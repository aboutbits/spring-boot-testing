package it.aboutbits.springboot.testing.persistance;

import it.aboutbits.springboot.testing.spring.BeanAccessor;
import it.aboutbits.springboot.toolbox.persistence.ChangeAware;
import it.aboutbits.springboot.toolbox.type.identity.EntityId;
import it.aboutbits.springboot.toolbox.type.identity.Identified;
import jakarta.persistence.EntityManager;
import lombok.AccessLevel;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;

import static org.assertj.core.api.Assertions.assertThat;

public final class PersistenceAssert {
    private PersistenceAssert() {
    }

    private static EntityManager getEntityManager() {
        return BeanAccessor.getBean(EntityManager.class);
    }

    @SuppressWarnings("unused")
    public static <E extends Identified<?> & ChangeAware> WriteOperationAsserter<E> assertThatEntity(@NonNull E before) {
        return new WriteOperationAsserter<>(getEntityManager(), before);
    }

    @SuppressWarnings("unused")
    public static <ID extends EntityId<?>> WriteOperationIdAsserter<ID> assertThatEntity(@NonNull ID id) {
        return new WriteOperationIdAsserter<>(getEntityManager(), id);
    }

    @RequiredArgsConstructor(access = AccessLevel.PRIVATE)
    public static final class WriteOperationAsserter<E extends Identified<?> & ChangeAware> {
        private final EntityManager entityManager;
        private final E entity;

        @SuppressWarnings("unused")
        public void hasBeenCreatedInDatabase() {
            entityManager.clear();
            var savedInstance = getSavedInstance();

            assertThat(
                    savedInstance.getId()
            ).isNotNull();

            assertThat(
                    savedInstance.getCreatedAt()
            ).isNotNull();
        }

        @SuppressWarnings("unused")
        public void hasBeenUpdatedInDatabase() {
            entityManager.clear();
            var savedInstance = getSavedInstance();

            assertThat(
                    savedInstance.getUpdatedAt()
            ).isAfter(
                    entity.getUpdatedAt()
            );
        }

        @SuppressWarnings("unused")
        public void hasNotChangedInDatabase() {
            entityManager.clear();
            var savedInstance = getSavedInstance();

            assertThat(savedInstance).isNotNull();

            assertThat(
                    savedInstance.getUpdatedAt()
            ).isEqualTo(
                    entity.getUpdatedAt()
            );
        }

        @SuppressWarnings("unused")
        public void isAbsentInDatabase() {
            entityManager.clear();
            var savedInstance = getSavedInstance();

            assertThat(savedInstance).isNull();
        }

        @SuppressWarnings("unused")
        public void isPresentInDatabase() {
            entityManager.clear();
            var savedInstance = getSavedInstance();

            assertThat(savedInstance).isNotNull();
        }

        @SuppressWarnings("unchecked")
        private E getSavedInstance() {
            return (E) entityManager.find(
                    entity.getClass(),
                    entity.getId()
            );
        }
    }

    public static final class WriteOperationIdAsserter<ID extends EntityId<?>> {
        private final EntityManager entityManager;
        private final ID id;
        private final Class<? extends Identified<? extends ID>> clazz;

        @SuppressWarnings("unchecked")
        private WriteOperationIdAsserter(
                EntityManager entityManager,
                ID id
        ) {
            this.entityManager = entityManager;
            this.id = id;

            var enclosingClass = id.getClass().getEnclosingClass();
            if (Identified.class.isAssignableFrom(enclosingClass)) {
                this.clazz = (Class<? extends Identified<? extends ID>>) enclosingClass;
            } else {
                throw new IllegalArgumentException("EntityId is not a subclass of Identified!");
            }
        }

        @SuppressWarnings("unused")
        public void isAbsentInDatabase() {
            entityManager.clear();
            var savedInstance = getSavedInstance();

            assertThat(savedInstance).isNull();
        }

        @SuppressWarnings("unused")
        public void isPresentInDatabase() {
            entityManager.clear();
            var savedInstance = getSavedInstance();

            assertThat(savedInstance).isNotNull();
        }

        private Object getSavedInstance() {
            return entityManager.find(
                    clazz,
                    id
            );
        }
    }
}
