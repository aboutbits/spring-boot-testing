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
    public static <ID extends EntityId<?>, E extends Identified<ID> & ChangeAware, M extends Identified<ID> & ChangeAware> WriteOperationAsserter<ID, E, M> assertThatEntity(
            @NonNull E before,
            @NonNull Class<M> modelClass
    ) {
        return new WriteOperationAsserter<>(getEntityManager(), before, modelClass);
    }

    @SuppressWarnings("unused")
    public static <ID extends EntityId<?>, M extends Identified<ID>> WriteOperationIdAsserter<ID, M> assertThatEntity(
            @NonNull ID id,
            @NonNull Class<M> modelClass
    ) {
        return new WriteOperationIdAsserter<>(getEntityManager(), id, modelClass);
    }

    @RequiredArgsConstructor(access = AccessLevel.PRIVATE)
    public static final class WriteOperationAsserter<ID extends EntityId<?>, E extends Identified<ID> & ChangeAware, M extends Identified<ID> & ChangeAware> {
        private final EntityManager entityManager;
        private final E entity;
        private final Class<M> modelClass;

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

        private M getSavedInstance() {
            return entityManager.find(
                    modelClass,
                    entity.getId()
            );
        }
    }

    @RequiredArgsConstructor(access = AccessLevel.PRIVATE)
    public static final class WriteOperationIdAsserter<ID extends EntityId<?>, M extends Identified<ID>> {
        private final EntityManager entityManager;
        private final ID id;
        private final Class<M> modelClass;

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

        private M getSavedInstance() {
            return entityManager.find(
                    modelClass,
                    id
            );
        }
    }
}
