package it.aboutbits.springboot.testing.persistence;

import it.aboutbits.springboot.testing.spring.BeanAccessor;
import it.aboutbits.springboot.toolbox.persistence.ChangeAware;
import it.aboutbits.springboot.toolbox.type.identity.EntityId;
import it.aboutbits.springboot.toolbox.type.identity.Identified;
import jakarta.persistence.EntityManager;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import org.jspecify.annotations.NullMarked;

import java.util.Collection;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.stream.Collectors;

import static it.aboutbits.springboot.toolbox.util.CollectUtil.collectToSet;
import static org.assertj.core.api.Assertions.assertThat;

@NullMarked
public final class PersistenceAssert {
    private PersistenceAssert() {
    }

    private static EntityManager getEntityManager() {
        return BeanAccessor.getBean(EntityManager.class);
    }

    @SuppressWarnings("unused")
    public static <ID extends EntityId<?>, E extends Identified<ID> & ChangeAware, M extends Identified<ID> & ChangeAware> WriteOperationAsserter<ID, E, M> assertThatEntity(
            E before,
            Class<M> modelClass
    ) {
        return new WriteOperationAsserter<>(getEntityManager(), List.of(before), modelClass);
    }

    @SuppressWarnings("unused")
    public static <ID extends EntityId<?>, E extends Identified<ID> & ChangeAware, M extends Identified<ID> & ChangeAware> WriteOperationAsserter<ID, E, M> assertThatEntity(
            Collection<E> before,
            Class<M> modelClass
    ) {
        return new WriteOperationAsserter<>(getEntityManager(), before, modelClass);
    }

    @SuppressWarnings("unused")
    public static <ID extends EntityId<?>, M extends Identified<ID>> WriteOperationIdAsserter<ID, M> assertThatEntityId(
            ID id,
            Class<M> modelClass
    ) {
        return new WriteOperationIdAsserter<>(getEntityManager(), List.of(id), modelClass);
    }

    @SuppressWarnings("unused")
    public static <ID extends EntityId<?>, M extends Identified<ID>> WriteOperationIdAsserter<ID, M> assertThatEntityId(
            Collection<ID> id,
            Class<M> modelClass
    ) {
        return new WriteOperationIdAsserter<>(getEntityManager(), id, modelClass);
    }

    /**
     * Batch query entities by their IDs using JPQL
     */
    private static <ID extends EntityId<?>, M extends Identified<ID>> List<M> batchFindByIds(
            EntityManager entityManager,
            Collection<ID> ids,
            Class<M> modelClass
    ) {
        if (ids.isEmpty()) {
            return List.of();
        }

        return ids.stream()
                .map(
                        id -> entityManager.find(modelClass, id)
                )
                .filter(Objects::nonNull)
                .toList();
    }

    /**
     * Check if entities with given IDs exist in the database using a count query
     */
    private static <ID extends EntityId<?>, M extends Identified<ID>> Map<ID, Boolean> batchCheckExistence(
            EntityManager entityManager,
            Collection<ID> ids,
            Class<M> modelClass
    ) {
        if (ids.isEmpty()) {
            return Map.of();
        }

        // Get existing entities
        var existingEntities = batchFindByIds(entityManager, ids, modelClass);
        var existingIds = collectToSet(existingEntities, Identified::getId);

        // Map each ID to its existence status
        return ids.stream()
                .collect(Collectors.toMap(
                        id -> id,
                        existingIds::contains
                ));
    }

    @RequiredArgsConstructor(access = AccessLevel.PRIVATE)
    public static final class WriteOperationAsserter<ID extends EntityId<?>, E extends Identified<ID> & ChangeAware, M extends Identified<ID> & ChangeAware> {
        private final EntityManager entityManager;
        private final Collection<E> entity;
        private final Class<M> modelClass;

        @SuppressWarnings("unused")
        public void hasBeenCreatedInDatabase() {
            entityManager.clear();

            var ids = collectToSet(entity, Identified::getId);
            var savedInstances = batchFindByIds(entityManager, ids, modelClass);

            for (var savedInstance : savedInstances) {
                assertThat(
                        savedInstance.getId()
                ).isNotNull();

                assertThat(
                        savedInstance.getCreatedAt()
                ).isNotNull();
            }

            // Verify all entities were found
            assertThat(savedInstances).hasSize(entity.size());
        }

        @SuppressWarnings("unused")
        public void hasBeenUpdatedInDatabase() {
            entityManager.clear();

            var ids = collectToSet(entity, Identified::getId);
            var savedInstances = batchFindByIds(entityManager, ids, modelClass);

            // Create a map for easy lookup of original entities by ID
            var originalByIdMap = entity.stream()
                    .collect(Collectors.toMap(Identified::getId, e -> e));

            for (var savedInstance : savedInstances) {
                var originalEntity = originalByIdMap.get(savedInstance.getId());
                Objects.requireNonNull(originalEntity);

                assertThat(originalEntity).isNotNull();

                assertThat(
                        savedInstance.getUpdatedAt()
                ).isAfter(
                        originalEntity.getUpdatedAt()
                );
            }
        }

        @SuppressWarnings("unused")
        public void hasNotChangedInDatabase() {
            entityManager.clear();

            var ids = collectToSet(entity, Identified::getId);
            var savedInstances = batchFindByIds(entityManager, ids, modelClass);

            // Create a map for easy lookup of original entities by ID
            var originalByIdMap = entity.stream()
                    .collect(Collectors.toMap(Identified::getId, e -> e));

            for (var savedInstance : savedInstances) {
                var originalEntity = originalByIdMap.get(savedInstance.getId());
                Objects.requireNonNull(originalEntity);

                assertThat(savedInstance).isNotNull();
                assertThat(originalEntity).isNotNull();

                assertThat(
                        savedInstance.getUpdatedAt()
                ).isEqualTo(
                        originalEntity.getUpdatedAt()
                );
            }
        }

        @SuppressWarnings("unused")
        public void isAbsentInDatabase() {
            entityManager.clear();

            var ids = collectToSet(entity, Identified::getId);
            var existenceMap = batchCheckExistence(entityManager, ids, modelClass);

            for (var id : ids) {
                assertThat(existenceMap.get(id))
                        .as("Entity with ID %s should be absent from database", id)
                        .isFalse();
            }
        }

        @SuppressWarnings("unused")
        public void isPresentInDatabase() {
            entityManager.clear();

            var ids = collectToSet(entity, Identified::getId);
            var existenceMap = batchCheckExistence(entityManager, ids, modelClass);

            for (var id : ids) {
                assertThat(existenceMap.get(id))
                        .as("Entity with ID %s should be present in database", id)
                        .isTrue();
            }
        }
    }

    @RequiredArgsConstructor(access = AccessLevel.PRIVATE)
    public static final class WriteOperationIdAsserter<ID extends EntityId<?>, M extends Identified<ID>> {
        private final EntityManager entityManager;
        private final Collection<ID> id;
        private final Class<M> modelClass;

        @SuppressWarnings("unused")
        public void isAbsentInDatabase() {
            entityManager.clear();

            var existenceMap = batchCheckExistence(entityManager, id, modelClass);

            for (var entityId : id) {
                assertThat(existenceMap.get(entityId))
                        .as("Entity with ID %s should be absent from database", entityId)
                        .isFalse();
            }
        }

        @SuppressWarnings("unused")
        public void isPresentInDatabase() {
            entityManager.clear();

            var existenceMap = batchCheckExistence(entityManager, id, modelClass);

            for (var entityId : id) {
                assertThat(existenceMap.get(entityId))
                        .as("Entity with ID %s should be present in database", entityId)
                        .isTrue();
            }
        }
    }
}
