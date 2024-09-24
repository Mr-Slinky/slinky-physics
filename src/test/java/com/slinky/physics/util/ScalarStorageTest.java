package com.slinky.physics.util;

import com.slinky.physics.base.EntityManager;
import com.slinky.physics.components.Component;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.function.Executable;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvSource;

import static org.junit.jupiter.api.Assertions.*;
import org.junit.jupiter.api.BeforeEach;

/**
 * Unit tests for the {@link ScalarStorage} class.
 * 
 * <p>
 * This test class focuses on verifying the correctness of the {@code ScalarStorage} constructor.
 * It ensures that the class behaves as expected when initialized with various parameters,
 * including valid and invalid capacities.
 * </p>
 * 
 * @version 1.0
 * @since   0.1.0
 * 
 * @see     ScalarStorage
 * 
 * @author
 */
public class ScalarStorageTest {
    
    private Component testComponent = Component.POSITION;
    private EntityManager entityManager;
    
    @BeforeEach
    void setup() {
        int size = 10_000;
        entityManager = new EntityManager(size);
        for (int i = 0; i < size; i++) {
            entityManager.createEntity();
        }
    }
    
    // ==========================[ Constructor Tests ]=========================== \\
    @Nested
    @DisplayName("Constructor Tests")
    class ConstructorTests {

        @ParameterizedTest(name = "initialCapacity={0}, maxCapacity={1}")
        @CsvSource({
            "1, 10",
            "5, 5",
            "10, 100",
            "100, 1000"
        })
        @DisplayName("Should create ScalarStorage with valid capacities")
        void testConstructorWithValidCapacities(int initialCapacity, int maxCapacity) {
            ScalarStorage storage = new ScalarStorage(testComponent, entityManager, initialCapacity, maxCapacity);

            assertAll("Validating ScalarStorage initialization",
                () -> assertEquals(maxCapacity, storage.getMaxEntityCapacity(), "Max capacity not set correctly"),
                () -> assertNotNull(storage.getScalarData(), "Scalar data should not be null"),
                () -> assertEquals(0, storage.size(), "Initial size should be zero"),
                () -> assertNotNull(storage.getEntityIds(), "Entity IDs should not be null")
            );
        }

        @ParameterizedTest(name = "initialCapacity={0}, maxCapacity={1}")
        @CsvSource({
            "0, 10",
            "-1, 10",
            "-5, 5"
        })
        @DisplayName("Should throw IllegalArgumentException for non-positive initial capacities")
        void testConstructorWithNonPositiveInitialCapacity(int initialCapacity, int maxCapacity) {
            Executable constructor = () -> new ScalarStorage(testComponent, entityManager, initialCapacity, maxCapacity);
            IllegalArgumentException exception = assertThrows(IllegalArgumentException.class, constructor);

            assertEquals("Initial capacity must be positive", exception.getMessage());
        }

        @ParameterizedTest(name = "initialCapacity={0}, maxCapacity={1}")
        @CsvSource({
            "10, 5",
            "100, 50",
            "1000, 999"
        })
        @DisplayName("Should throw IllegalArgumentException when initial capacity exceeds max capacity")
        void testConstructorWithInitialCapacityExceedingMaxCapacity(int initialCapacity, int maxCapacity) {
            Executable constructor = () -> new ScalarStorage(testComponent, entityManager, initialCapacity, maxCapacity);
            IllegalArgumentException exception = assertThrows(IllegalArgumentException.class, constructor);

            assertEquals("Initial capacity cannot exceed maximum capacity", exception.getMessage());
        }

        @Test
        @DisplayName("Should correctly initialize internal structures")
        void testConstructorInitializesInternalStructures() {
            int initialCapacity = 16;
            int maxCapacity = 100;
            ScalarStorage storage = new ScalarStorage(testComponent, entityManager, initialCapacity, maxCapacity);

            assertAll("Validating internal structures",
                () -> assertEquals(maxCapacity, storage.getMaxEntityCapacity(), "Max capacity mismatch"),
                () -> assertEquals(0, storage.size(), "Size should be zero upon initialization"),
                () -> assertNotNull(storage.getScalarData(), "Scalar data should not be null"),
                () -> assertEquals(initialCapacity, storage.getScalarData().capacity(), "Scalar data capacity mismatch"),
                () -> assertNotNull(storage.getEntityIds(), "Entity IDs should not be null"),
                () -> assertEquals(0, storage.getEntityIds().size(), "Entity IDs list should be empty")
            );
        }
    }
    
    // =============================[ Getter Tests ]============================== \\
    @Nested
    @DisplayName("Getter Tests")
    class GetterTests {

        @ParameterizedTest(name = "initialCapacity={0}, maxCapacity={1}")
        @CsvSource({
            "16, 16",
            "32, 100",
            "64, 1000"
        })
        @DisplayName("Should correctly return getter values after initialization")
        void testGettersAfterInitialization(int initialCapacity, int maxCapacity) {
            ScalarStorage storage = new ScalarStorage(testComponent, entityManager, initialCapacity, maxCapacity);

            assertAll("Validating getters after initialization",
                    () -> assertEquals(0, storage.size(), "Size should be zero after initialization"),
                    () -> assertEquals(maxCapacity, storage.getMaxEntityCapacity(), "Max capacity mismatch"),
                    () -> assertNotNull(storage.getScalarData(), "Scalar data should not be null"),
                    () -> assertTrue(storage.getScalarData().capacity() >= Math.max(initialCapacity, 16),
                            "Scalar data capacity should be at least the maximum of initialCapacity and 16"),
                    () -> assertNotNull(storage.getEntityIds(), "Entity IDs should not be null"),
                    () -> assertEquals(0, storage.getEntityIds().size(), "Entity IDs list should be empty")
            );
        }

        @Test
        @DisplayName("Should correctly return getter values after adding entities")
        void testGettersAfterAddingEntities() {
            int initialCapacity = 32;
            int maxCapacity = 100;
            ScalarStorage storage = new ScalarStorage(testComponent, entityManager, initialCapacity, maxCapacity);

            // Add entities
            for (int entityId = 0; entityId < 10; entityId++) {
                storage.add(entityId, entityId * 1.0f);
            }

            assertAll("Validating getters after adding entities",
                    () -> assertEquals(10, storage.size(), "Size should reflect number of added entities"),
                    () -> assertEquals(maxCapacity, storage.getMaxEntityCapacity(), "Max capacity mismatch"),
                    () -> assertNotNull(storage.getScalarData(), "Scalar data should not be null"),
                    () -> assertTrue(storage.getScalarData().capacity() >= Math.max(initialCapacity, 16),
                            "Scalar data capacity should be at least the maximum of initialCapacity and 16"),
                    () -> assertNotNull(storage.getEntityIds(), "Entity IDs should not be null"),
                    () -> assertEquals(10, storage.getEntityIds().size(), "Entity IDs list should reflect number of added entities"),
                    () -> {
                        // Verify that all added entity IDs are present
                        IntList entityIds = storage.getEntityIds();
                        for (int entityId = 0; entityId < 10; entityId++) {
                            assertTrue(entityIds.contains(entityId), "Entity ID " + entityId + " should be present");
                        }
                    }
            );
        }

        @Test
        @DisplayName("Should correctly return getter values after removing entities")
        void testGettersAfterRemovingEntities() {
            int initialCapacity = 32;
            int maxCapacity = 100;
            ScalarStorage storage = new ScalarStorage(testComponent, entityManager, initialCapacity, maxCapacity);

            // Add entities
            for (int entityId = 0; entityId < 10; entityId++) {
                storage.add(entityId, entityId * 1.0f);
            }

            // Remove some entities
            for (int entityId = 0; entityId < 5; entityId++) {
                storage.remove(entityId);
            }

            assertAll("Validating getters after removing entities",
                    () -> assertEquals(5, storage.size(), "Size should reflect number of remaining entities"),
                    () -> assertEquals(maxCapacity, storage.getMaxEntityCapacity(), "Max capacity mismatch"),
                    () -> assertNotNull(storage.getScalarData(), "Scalar data should not be null"),
                    () -> assertTrue(storage.getScalarData().capacity() >= 16,
                            "Scalar data capacity should be at least the maximum of initialCapacity and 16"),
                    () -> assertNotNull(storage.getEntityIds(), "Entity IDs should not be null"),
                    () -> assertEquals(5, storage.getEntityIds().size(), "Entity IDs list should reflect number of remaining entities"),
                    () -> {
                        // Verify that only the remaining entity IDs are present
                        IntList entityIds = storage.getEntityIds();
                        for (int entityId = 0; entityId < 5; entityId++) {
                            assertFalse(entityIds.contains(entityId), "Entity ID " + entityId + " should have been removed");
                        }
                        for (int entityId = 5; entityId < 10; entityId++) {
                            assertTrue(entityIds.contains(entityId), "Entity ID " + entityId + " should be present");
                        }
                    }
            );
        }
    }
    
    // ===========================[ Add Method Tests ]=========================== \\
    @Nested
    @DisplayName("Add Method Tests")
    class AddTests {

        @ParameterizedTest(name = "initialCapacity={0}, maxCapacity={1}, entityId={2}, value={3}")
        @CsvSource({
            "16, 16, 0, 10.0",
            "32, 100, 15, 15.5",
            "64, 1000, 63, 63.3"
        })
        @DisplayName("Should add valid entities correctly")
        void testAddValidEntities(int initialCapacity, int maxCapacity, int entityId, float value) {
            ScalarStorage storage = new ScalarStorage(testComponent, entityManager, initialCapacity, maxCapacity);

            storage.add(entityId, value);

            assertAll("Validating addition of a valid entity",
                    () -> assertEquals(1, storage.size(), "Size should be 1 after adding an entity"),
                    () -> assertTrue(storage.contains(entityId), "Storage should contain the added entity ID"),
                    () -> assertEquals(value, storage.get(entityId), "Stored value should match the added value"),
                    () -> assertEquals(1, storage.getEntityIds().size(), "Entity IDs list should have one entry"),
                    () -> assertEquals(entityId, storage.getEntityIds().get(0), "Entity ID should be correctly stored")
            );
        }

        @ParameterizedTest(name = "initialCapacity={0}, maxCapacity={1}")
        @CsvSource({
            "16, 16",
            "32, 32",
            "64, 64",
            "100, 100"
        })
        @DisplayName("Should throw IllegalStateException when adding beyond max capacity")
        void testAddBeyondMaxCapacity(int initialCapacity, int maxCapacity) {
            ScalarStorage storage = new ScalarStorage(testComponent, entityManager, initialCapacity, maxCapacity);

            // Add entities up to max capacity
            for (int entityId = 0; entityId < maxCapacity; entityId++) {
                storage.add(entityId, entityId * 1.0f);
            }

            // Attempt to add one more entity beyond max capacity
            int extraEntityId = maxCapacity;
            Executable addBeyondCapacity = () -> storage.add(extraEntityId, 999.9f);

            IllegalStateException exception = assertThrows(IllegalStateException.class, addBeyondCapacity);
            assertEquals("Maximum capacity reached: " + maxCapacity, exception.getMessage(),
                    "Adding beyond max capacity should throw IllegalStateException with correct message");
        }

        @ParameterizedTest(name = "initialCapacity={0}, maxCapacity={1}, entityId={2}")
        @CsvSource({
            "16, 16, 0",
            "32, 100, 50",
            "64, 1000, 999"
        })
        @DisplayName("Should throw IllegalArgumentException when adding duplicate entity IDs")
        void testAddDuplicateEntities(int initialCapacity, int maxCapacity, int entityId) {
            ScalarStorage storage = new ScalarStorage(testComponent, entityManager, initialCapacity, maxCapacity);

            // Add the entity for the first time
            storage.add(entityId, entityId * 1.0f);

            // Attempt to add the same entity again
            Executable addDuplicate = () -> storage.add(entityId, entityId * 2.0f);

            IllegalArgumentException exception = assertThrows(IllegalArgumentException.class, addDuplicate);
            assertEquals("Entity already exists: " + entityId, exception.getMessage(),
                    "Adding a duplicate entity should throw IllegalArgumentException with correct message");
        }

        @ParameterizedTest(name = "initialCapacity={0}, maxCapacity={1}, entityId={2}, value={3}")
        @CsvSource({
            "16, 16, -1, 10.0",
            "32, 100, 100, 20.0",
            "64, 1000, 1001, 30.0"
        })
        @DisplayName("Should throw IllegalArgumentException when adding invalid entity IDs")
        void testAddInvalidEntityIds(int initialCapacity, int maxCapacity, int entityId, float value) {
            ScalarStorage storage = new ScalarStorage(testComponent, entityManager, initialCapacity, maxCapacity);

            Executable addInvalid = () -> storage.add(entityId, value);

            IllegalArgumentException exception = assertThrows(IllegalArgumentException.class, addInvalid);
            assertEquals("Entity ID out of bounds: " + entityId, exception.getMessage(),
                    "Adding an entity with invalid ID should throw IllegalArgumentException with correct message");
        }

        @Test
        @DisplayName("Should correctly add multiple valid entities")
        void testAddMultipleValidEntities() {
            int initialCapacity = 32;
            int maxCapacity = 100;
            ScalarStorage storage = new ScalarStorage(testComponent, entityManager, initialCapacity, maxCapacity);

            // Add multiple entities
            for (int entityId = 0; entityId < 20; entityId++) {
                storage.add(entityId, entityId * 1.0f);
            }

            assertAll("Validating addition of multiple entities",
                    () -> assertEquals(20, storage.size(), "Size should be 20 after adding entities"),
                    () -> assertEquals(20, storage.getEntityIds().size(), "Entity IDs list should have 20 entries"),
                    () -> {
                        // Verify each entity is present with correct value
                        for (int entityId = 0; entityId < 20; entityId++) {
                            assertTrue(storage.contains(entityId), "Storage should contain entity ID " + entityId);
                            assertEquals(entityId * 1.0f, storage.get(entityId),
                                    "Stored value for entity ID " + entityId + " should match the added value");
                        }
                    }
            );
        }

        @Test
        @DisplayName("Should correctly handle adding entities up to maximum capacity")
        void testAddEntitiesUpToMaxCapacity() {
            int initialCapacity = 16;
            int maxCapacity     = 50;
            ScalarStorage storage = new ScalarStorage(testComponent, entityManager, initialCapacity, maxCapacity);

            // Add entities up to max capacity
            for (int entityId = 0; entityId < maxCapacity; entityId++) {
                storage.add(entityId, entityId * 2.0f);
            }

            assertAll("Validating addition of entities up to max capacity",
                    () -> assertEquals(maxCapacity, storage.size(), "Size should be equal to max capacity"),
                    () -> assertEquals(maxCapacity, storage.getEntityIds().size(), "Entity IDs list should have max capacity entries"),
                    () -> {
                        // Verify each entity is present with correct value
                        for (int entityId = 0; entityId < maxCapacity; entityId++) {
                            assertTrue(storage.contains(entityId), "Storage should contain entity ID " + entityId);
                            assertEquals(entityId * 2.0f, storage.get(entityId),
                                    "Stored value for entity ID " + entityId + " should match the added value");
                        }
                    }
            );
        }
    }

    
    // =========================[ Remove Method Tests ]========================== \\
    @Nested
    @DisplayName("Remove Method Tests")
    class RemoveTests {

        /**
         * Tests the removal of a single existing entity.
         */
        @Test
        @DisplayName("Should remove an existing entity correctly")
        void testRemoveExistingEntity() {
            int initialCapacity = 32;
            int maxCapacity = 100;
            ScalarStorage storage = new ScalarStorage(testComponent, entityManager, initialCapacity, maxCapacity);

            // Add an entity
            int entityId = 10;
            float value = 10.5f;
            storage.add(entityId, value);

            // Remove the entity
            storage.remove(entityId);

            assertAll("Validating removal of an existing entity",
                    () -> assertEquals(0, storage.size(), "Size should be zero after removing the entity"),
                    () -> assertFalse(storage.contains(entityId), "Storage should not contain the removed entity ID"),
                    () -> assertEquals(0, storage.getEntityIds().size(), "Entity IDs list should be empty after removal"),
                    () -> assertEquals(0, storage.getScalarData().size(), "Scalar data should be empty after removal")
            );
        }

        /**
         * Tests the removal of multiple existing entities correctly.
         */
        @ParameterizedTest(name = "initialCapacity={0}, maxCapacity={1}, entityId1={2}, entityId2={3}")
        @CsvSource({
            "16, 16, 0, 1",
            "32, 100, 5, 9",
            "64, 1000, 2, 7"
        })
        @DisplayName("Should remove multiple existing entities correctly")
        void testRemoveMultipleExistingEntities(int initialCapacity, int maxCapacity, int entityId1, int entityId2) {
            ScalarStorage storage = new ScalarStorage(testComponent, entityManager, initialCapacity, maxCapacity);

            // Add multiple entities
            for (int i = 0; i < 10; i++) {
                storage.add(i, i * 1.0f);
            }

            // Remove specified entities
            storage.remove(entityId1);
            storage.remove(entityId2);

            assertAll("Validating removal of multiple existing entities",
                    () -> assertEquals(8, storage.size(), "Size should reflect the number of remaining entities"),
                    () -> assertFalse(storage.contains(entityId1), "Storage should not contain the first removed entity ID"),
                    () -> assertFalse(storage.contains(entityId2), "Storage should not contain the second removed entity ID"),
                    () -> assertEquals(8, storage.getEntityIds().size(), "Entity IDs list should have correct number of entries"),
                    () -> {
                        // Verify that removed entities are not present
                        IntList entityIds = storage.getEntityIds();
                        assertFalse(entityIds.contains(entityId1), "Entity ID " + entityId1 + " should have been removed");
                        assertFalse(entityIds.contains(entityId2), "Entity ID " + entityId2 + " should have been removed");
                    },
                    () -> {
                        // Verify that remaining entities have correct values
                        for (int i = 0; i < 10; i++) {
                            if (i != entityId1 && i != entityId2) {
                                assertTrue(storage.contains(i), "Storage should contain entity ID " + i);
                                assertEquals(i * 1.0f, storage.get(i), "Stored value for entity ID " + i + " should match the added value");
                            }
                        }
                    }
            );
        }

        /**
         * Tests that attempting to remove a non-existing entity throws an
         * exception.
         */
        @Test
        @DisplayName("Should throw IllegalArgumentException when removing a non-existing entity")
        void testRemoveNonExistingEntity() {
            int initialCapacity = 32;
            int maxCapacity = 100;
            ScalarStorage storage = new ScalarStorage(testComponent, entityManager, initialCapacity, maxCapacity);

            // Attempt to remove an entity that doesn't exist
            int nonExistingEntityId = 50;
            Executable removeNonExisting = () -> storage.remove(nonExistingEntityId);

            IllegalArgumentException exception = assertThrows(IllegalArgumentException.class, removeNonExisting);
            assertEquals("Entity does not exist: " + nonExistingEntityId, exception.getMessage(),
                    "Removing a non-existing entity should throw IllegalArgumentException with correct message");
        }

        /**
         * Tests the removal of the last entity to ensure swap and pop are
         * handled correctly.
         */
        @Test
        @DisplayName("Should correctly handle removing the last entity")
        void testRemoveLastEntity() {
            int initialCapacity = 32;
            int maxCapacity = 100;
            ScalarStorage storage = new ScalarStorage(testComponent, entityManager, initialCapacity, maxCapacity);

            // Add multiple entities
            for (int i = 0; i < 5; i++) {
                storage.add(i, i * 2.0f);
            }

            // Remove the last entity
            int lastEntityId = 4;
            storage.remove(lastEntityId);

            assertAll("Validating removal of the last entity",
                    () -> assertEquals(4, storage.size(), "Size should decrease by one after removal"),
                    () -> assertFalse(storage.contains(lastEntityId), "Storage should not contain the removed last entity ID"),
                    () -> assertEquals(4, storage.getEntityIds().size(), "Entity IDs list should reflect the correct number of entries"),
                    () -> {
                        // Verify that remaining entities are present with correct values
                        for (int i = 0; i < 4; i++) {
                            assertTrue(storage.contains(i), "Storage should contain entity ID " + i);
                            assertEquals(i * 2.0f, storage.get(i), "Stored value for entity ID " + i + " should match the added value");
                        }
                    }
            );
        }

        /**
         * Tests the removal of all entities one by one to ensure complete
         * cleanup.
         */
        @Test
        @DisplayName("Should correctly handle removing all entities one by one")
        void testRemoveAllEntities() {
            int initialCapacity = 64;
            int maxCapacity = 1000;
            ScalarStorage storage = new ScalarStorage(testComponent, entityManager, initialCapacity, maxCapacity);

            // Add multiple entities
            for (int i = 0; i < 20; i++) {
                storage.add(i, i * 3.0f);
            }

            // Remove all entities one by one
            for (int i = 0; i < 20; i++) {
                storage.remove(i);
            }

            assertAll("Validating removal of all entities",
                    () -> assertEquals(0, storage.size(), "Size should be zero after removing all entities"),
                    () -> assertEquals(0, storage.getEntityIds().size(), "Entity IDs list should be empty after removing all entities"),
                    () -> assertEquals(0, storage.getScalarData().size(), "Scalar data should be empty after removing all entities")
            );
        }

        /**
         * Tests the swap and pop functionality by removing entities from
         * various positions.
         */
        @ParameterizedTest(name = "initialCapacity={0}, maxCapacity={1}, removeEntityId={2}")
        @CsvSource({
            "32, 100, 0", // Remove first entity
            "32, 100, 5", // Remove a middle entity
            "32, 100, 8" // Remove last entity
        })
        @DisplayName("Should correctly handle swap and pop when removing entities from various positions")
        void testRemoveEntitiesWithSwapAndPop(int initialCapacity, int maxCapacity, int removeEntityId) {
            ScalarStorage storage = new ScalarStorage(testComponent, entityManager, initialCapacity, maxCapacity);

            // Add multiple entities
            for (int i = 0; i < 10; i++) {
                storage.add(i, i * 1.0f);
            }

            // Ensure the entity to remove exists
            assertTrue(storage.contains(removeEntityId), "Storage should contain entity ID " + removeEntityId + " before removal");

            // Capture the last entity's ID before removal
            int lastEntityId = 9;

            // Remove the specified entity
            storage.remove(removeEntityId);

            // After removal, the last entity should now occupy the position of the removed entity
            // Thus, entityId at 'removeEntityId' should now have the value of 'lastEntityId'
            assertAll("Validating swap and pop during removal",
                    () -> assertEquals(9, storage.size(), "Size should decrease by one after removal"),
                    () -> assertFalse(storage.contains(removeEntityId), "Storage should not contain the removed entity ID"),
                    () -> assertTrue(storage.contains(lastEntityId), "Storage should still contain the last entity ID"),
                    () -> assertEquals(lastEntityId * 1.0f, storage.get(lastEntityId),
                            "Value for the last entity ID should remain correct"),
                    () -> assertEquals(9, storage.getEntityIds().size(), "Entity IDs list should have one less entry after removal")
            );

            // Additionally, verify that the last entity's ID is now in the position of the removed entity
            // This requires checking the internal mapping if possible, or verifying that all expected entities are present
            for (int i = 0; i < 10; i++) {
                if (i == removeEntityId) {
                    // The removed position should now contain the last entity's value, but since get() uses entityId,
                    // we ensure that the last entity's value is still correct
                    continue;
                }
                assertTrue(storage.contains(i), "Storage should contain entity ID " + i);
                assertEquals(i * 1.0f, storage.get(i), "Stored value for entity ID " + i + " should match the added value");
            }
        }

        /**
         * Tests attempting to remove an entity from an empty storage.
         */
        @Test
        @DisplayName("Should handle removing entities when storage is empty")
        void testRemoveFromEmptyStorage() {
            int initialCapacity = 32;
            int maxCapacity = 100;
            ScalarStorage storage = new ScalarStorage(testComponent, entityManager, initialCapacity, maxCapacity);

            // Attempt to remove an entity from an empty storage
            int entityId = 10;
            Executable removeFromEmpty = () -> storage.remove(entityId);

            IllegalArgumentException exception = assertThrows(IllegalArgumentException.class, removeFromEmpty);
            assertEquals("Entity does not exist: " + entityId, exception.getMessage(),
                    "Removing from an empty storage should throw IllegalArgumentException with correct message");
        }
    }
    
    // =======================[ Getter and Setter Tests ]======================== \\
    @Nested
    @DisplayName("Getter and Setter Method Tests")
    class GetterSetterTests {

        // Tests for the contains method
        @ParameterizedTest(name = "initialCapacity={0}, maxCapacity={1}, entityId={2}, expectedContains={3}")
        @CsvSource({
            "16, 16, 0, true",
            "32, 100, 50, true",
            "64, 1000, 999, true",
            "16, 16, 1, false",
            "32, 100, 100, false",
            "64, 1000, 1001, false"
        })
        @DisplayName("Should correctly determine if storage contains specific entities")
        void testContains(int initialCapacity, int maxCapacity, int entityId, boolean expectedContains) {
            ScalarStorage storage = new ScalarStorage(testComponent, entityManager, initialCapacity, maxCapacity);

            if (expectedContains) {
                storage.add(entityId, entityId * 1.0f);
            }

            assertAll("Validating contains method",
                    () -> assertEquals(expectedContains, storage.contains(entityId),
                            "Contains method should return " + expectedContains + " for entity ID " + entityId),
                    () -> {
                        if (expectedContains) {
                            assertTrue(storage.contains(entityId), "Storage should contain entity ID " + entityId);
                        } else {
                            assertFalse(storage.contains(entityId), "Storage should not contain entity ID " + entityId);
                        }
                    }
            );
        }

        // Tests for the get method
        @ParameterizedTest(name = "initialCapacity={0}, maxCapacity={1}, entityId={2}, value={3}")
        @CsvSource({
            "16, 16, 0, 10.0",
            "32, 100, 50, 50.5",
            "64, 1000, 999, 999.9"
        })
        @DisplayName("Should retrieve correct scalar values for existing entities")
        void testGetExistingEntity(int initialCapacity, int maxCapacity, int entityId, float value) {
            ScalarStorage storage = new ScalarStorage(testComponent, entityManager, initialCapacity, maxCapacity);
            storage.add(entityId, value);

            float retrievedValue = storage.get(entityId);

            assertAll("Validating get method for existing entity",
                    () -> assertEquals(value, retrievedValue, 0.0001f, "Retrieved value should match the added value"),
                    () -> assertTrue(storage.contains(entityId), "Storage should contain the entity ID")
            );
        }

        @ParameterizedTest(name = "initialCapacity={0}, maxCapacity={1}, entityId={2}")
        @CsvSource({
            "16, 16, -1",
            "32, 100, 100",
            "64, 1000, 1001"
        })
        @DisplayName("Should throw IllegalArgumentException when getting a non-existing entity")
        void testGetNonExistingEntity(int initialCapacity, int maxCapacity, int entityId) {
            ScalarStorage storage = new ScalarStorage(testComponent, entityManager, initialCapacity, maxCapacity);

            Executable getNonExisting = () -> storage.get(entityId);

            assertThrows(IllegalArgumentException.class, getNonExisting);
        }

        // Tests for the set method
        @ParameterizedTest(name = "initialCapacity={0}, maxCapacity={1}, entityId={2}, newValue={3}")
        @CsvSource({
            "16, 16, 0, 20.0",
            "32, 100, 50, 60.5",
            "64, 1000, 999, 1000.9"
        })
        @DisplayName("Should correctly set scalar values for existing entities")
        void testSetExistingEntity(int initialCapacity, int maxCapacity, int entityId, float newValue) {
            ScalarStorage storage = new ScalarStorage(testComponent, entityManager, initialCapacity, maxCapacity);
            storage.add(entityId, newValue - 10.0f); // Add with initial value

            storage.set(entityId, newValue);

            assertAll("Validating set method for existing entity",
                    () -> assertEquals(newValue, storage.get(entityId), 0.0001f, "Value should be updated to the new value"),
                    () -> assertTrue(storage.contains(entityId), "Storage should still contain the entity ID")
            );
        }

        @ParameterizedTest(name = "initialCapacity={0}, maxCapacity={1}, entityId={2}, newValue={3}")
        @CsvSource({
            "16, 16, -1, 20.0",
            "32, 100, 100, 60.5",
            "64, 1000, 1001, 1000.9"
        })
        @DisplayName("Should throw IllegalArgumentException when setting a non-existing entity")
        void testSetNonExistingEntity(int initialCapacity, int maxCapacity, int entityId, float newValue) {
            ScalarStorage storage = new ScalarStorage(testComponent, entityManager, initialCapacity, maxCapacity);

            Executable setNonExisting = () -> storage.set(entityId, newValue);

            assertThrows(IllegalArgumentException.class, setNonExisting);
        }

        @Test
        @DisplayName("Should correctly add multiple valid entities")
        void testSetMultipleEntities() {
            int initialCapacity = 32;
            int maxCapacity = 100;
            ScalarStorage storage = new ScalarStorage(testComponent, entityManager, initialCapacity, maxCapacity);

            // Add multiple entities
            for (int i = 0; i < 10; i++) {
                storage.add(i, i * 1.0f);
            }

            // Set new values
            for (int i = 0; i < 10; i++) {
                storage.set(i, i * 2.0f);
            }

            assertAll("Validating setting of multiple entities",
                    () -> assertEquals(10, storage.size(), "Size should remain unchanged after setting entities"),
                    () -> {
                        // Verify that all entities have updated values
                        for (int i = 0; i < 10; i++) {
                            assertEquals(i * 2.0f, storage.get(i), 0.0001f, "Entity ID " + i + " should have updated value");
                            assertTrue(storage.contains(i), "Storage should contain entity ID " + i);
                        }
                    }
            );
        }

        @Test
        @DisplayName("Should correctly handle setting entities after removal")
        void testSetEntitiesAfterRemoval() {
            int initialCapacity = 32;
            int maxCapacity = 100;
            ScalarStorage storage = new ScalarStorage(testComponent, entityManager, initialCapacity, maxCapacity);

            // Add multiple entities
            for (int i = 0; i < 10; i++) {
                storage.add(i, i * 1.0f);
            }

            // Remove some entities
            storage.remove(2);
            storage.remove(5);

            // Set existing and non-existing entities
            storage.set(3, 30.0f);
            storage.set(7, 70.0f);

            Executable setRemovedEntity = () -> storage.set(2, 20.0f);

            assertThrows(IllegalArgumentException.class, setRemovedEntity);

            assertAll("Validating set method after removal",
                    () -> assertEquals(8, storage.size(), "Size should reflect the number of remaining entities"),
                    () -> assertEquals(30.0f, storage.get(3), 0.0001f, "Entity ID 3 should have updated value"),
                    () -> assertEquals(70.0f, storage.get(7), 0.0001f, "Entity ID 7 should have updated value"),
                    () -> assertThrows(IllegalArgumentException.class, setRemovedEntity, "Setting a removed entity should throw exception")
            );
        }
    }
    
    // ===========================[ GetValueOf Tests ]=========================== \\
    @Nested
    @DisplayName("GetValueOf Method Tests")
    class GetValueOfTests {

        /**
         * Tests retrieving scalar values for existing entities using
         * getValueOf(entityId).
         */
        @ParameterizedTest(name = "initialCapacity={0}, maxCapacity={1}, entityId={2}, value={3}")
        @CsvSource({
            "16, 16, 0, 10.0",
            "32, 100, 50, 50.5",
            "64, 1000, 999, 999.9"
        })
        @DisplayName("Should retrieve correct scalar values for existing entities using getValueOf(entityId)")
        void testGetValueOfExistingEntity(int initialCapacity, int maxCapacity, int entityId, float value) {
            ScalarStorage storage = new ScalarStorage(testComponent, entityManager, initialCapacity, maxCapacity);
            storage.add(entityId, value);

            float retrievedValue = storage.getValueOf(entityId);

            assertAll("Validating getValueOf(entityId) method for existing entity",
                    () -> assertEquals(value, retrievedValue, 0.0001f, "Retrieved value should match the added value"),
                    () -> assertTrue(storage.contains(entityId), "Storage should contain the entity ID")
            );
        }

        /**
         * Tests that attempting to retrieve a value for a non-existing entity
         * using getValueOf(entityId) throws an exception.
         */
        @ParameterizedTest(name = "initialCapacity={0}, maxCapacity={1}, entityId={2}")
        @CsvSource({
            "16, 16, -1",
            "32, 100, 100",
            "64, 1000, 1001"
        })
        @DisplayName("Should throw IllegalArgumentException when retrieving value for non-existing entity using getValueOf(entityId)")
        void testGetValueOfNonExistingEntity(int initialCapacity, int maxCapacity, int entityId) {
            ScalarStorage storage = new ScalarStorage(testComponent, entityManager, initialCapacity, maxCapacity);

            Executable getNonExisting = () -> storage.getValueOf(entityId);

            assertThrows(IllegalArgumentException.class, getNonExisting);
        }

        /**
         * Tests retrieving scalar values for existing entities using
         * getValueOf(entityId, dest).
         */
        @ParameterizedTest(name = "initialCapacity={0}, maxCapacity={1}, entityId={2}, value={3}")
        @CsvSource({
            "16, 16, 0, 10.0",
            "32, 100, 50, 50.5",
            "64, 1000, 999, 999.9"
        })
        @DisplayName("Should retrieve correct scalar values for existing entities using getValueOf(entityId, dest)")
        void testGetValueOfWithDestArray(int initialCapacity, int maxCapacity, int entityId, float value) {
            ScalarStorage storage = new ScalarStorage(testComponent, entityManager, initialCapacity, maxCapacity);
            storage.add(entityId, value);

            float[] dest = new float[1];
            storage.getValueOf(entityId, dest);

            assertAll("Validating getValueOf(entityId, dest) method for existing entity",
                    () -> assertEquals(value, dest[0], 0.0001f, "Destination array should contain the correct value"),
                    () -> assertTrue(storage.contains(entityId), "Storage should contain the entity ID")
            );
        }

        /**
         * Tests that attempting to retrieve a value for a non-existing entity
         * using getValueOf(entityId, dest) throws an exception.
         */
        @ParameterizedTest(name = "initialCapacity={0}, maxCapacity={1}, entityId={2}")
        @CsvSource({
            "16, 16, -1",
            "32, 100, 100",
            "64, 1000, 1001"
        })
        @DisplayName("Should throw IllegalArgumentException when retrieving value for non-existing entity using getValueOf(entityId, dest)")
        void testGetValueOfWithDestArrayNonExistingEntity(int initialCapacity, int maxCapacity, int entityId) {
            ScalarStorage storage = new ScalarStorage(testComponent, entityManager, initialCapacity, maxCapacity);
            float[] dest = new float[1];

            Executable getNonExisting = () -> storage.getValueOf(entityId, dest);

            assertThrows(IllegalArgumentException.class, getNonExisting);
        }

        /**
         * Tests that providing a null destination array to getValueOf(entityId,
         * dest) throws an exception.
         */
        @Test
        @DisplayName("Should throw IllegalArgumentException when destination array is null in getValueOf(entityId, dest)")
        void testGetValueOfWithNullDestArray() {
            int initialCapacity = 32;
            int maxCapacity = 100;
            int entityId = 10;
            float value = 10.5f;

            ScalarStorage storage = new ScalarStorage(testComponent, entityManager, initialCapacity, maxCapacity);
            storage.add(entityId, value);

            Executable getWithNullDest = () -> storage.getValueOf(entityId, null);

            assertThrows(IllegalArgumentException.class, getWithNullDest);
        }

        /**
         * Tests that providing a destination array of insufficient length to
         * getValueOf(entityId, dest) throws an exception.
         */
        @Test
        @DisplayName("Should throw IllegalArgumentException when destination array is too small in getValueOf(entityId, dest)")
        void testGetValueOfWithSmallDestArray() {
            int initialCapacity = 32;
            int maxCapacity = 100;
            int entityId = 10;
            float value = 10.5f;

            ScalarStorage storage = new ScalarStorage(testComponent, entityManager, initialCapacity, maxCapacity);
            storage.add(entityId, value);

            float[] dest = new float[0]; // Insufficient length

            Executable getWithSmallDest = () -> storage.getValueOf(entityId, dest);

            assertThrows(IllegalArgumentException.class, getWithSmallDest);
        }

        /**
         * Tests retrieving values for multiple existing entities using
         * getValueOf(entityId) and getValueOf(entityId, dest).
         */
        @Test
        @DisplayName("Should correctly retrieve values for multiple existing entities")
        void testGetValueOfMultipleEntities() {
            int initialCapacity = 32;
            int maxCapacity = 100;
            ScalarStorage storage = new ScalarStorage(testComponent, entityManager, initialCapacity, maxCapacity);

            // Add multiple entities
            for (int i = 0; i < 10; i++) {
                storage.add(i, i * 2.0f);
            }

            // Retrieve values using getValueOf(entityId)
            for (int i = 0; i < 10; i++) {
                float retrievedValue = storage.getValueOf(i);
                assertEquals(i * 2.0f, retrievedValue, 0.0001f, "Retrieved value should match the added value for entity ID " + i);
            }

            // Retrieve values using getValueOf(entityId, dest)
            float[] dest = new float[1];
            for (int i = 0; i < 10; i++) {
                storage.getValueOf(i, dest);
                assertEquals(i * 2.0f, dest[0], 0.0001f, "Destination array should contain the correct value for entity ID " + i);
            }
        }

        /**
         * Tests retrieving values after removing some entities to ensure proper
         * exception handling.
         */
        @Test
        @DisplayName("Should handle getValueOf methods correctly after removing entities")
        void testGetValueOfAfterRemoval() {
            int initialCapacity = 32;
            int maxCapacity = 100;
            ScalarStorage storage = new ScalarStorage(testComponent, entityManager, initialCapacity, maxCapacity);

            // Add multiple entities
            for (int i = 0; i < 5; i++) {
                storage.add(i, i * 3.0f);
            }

            // Remove some entities
            storage.remove(1);
            storage.remove(3);

            // Attempt to retrieve values for existing and removed entities using getValueOf(entityId)
            assertEquals(0.0f, storage.getValueOf(0), 0.0001f, "Value for entity ID 0 should be correct");
            assertEquals(6.0f, storage.getValueOf(2), 0.0001f, "Value for entity ID 2 should be correct");
            assertEquals(12.0f, storage.getValueOf(4), 0.0001f, "Value for entity ID 4 should be correct");

            Executable getRemovedEntity1 = () -> storage.getValueOf(1);
            Executable getRemovedEntity3 = () -> storage.getValueOf(3);

            assertThrows(IllegalArgumentException.class, getRemovedEntity1);
            assertThrows(IllegalArgumentException.class, getRemovedEntity3);

            // Attempt to retrieve values for existing and removed entities using getValueOf(entityId, dest)
            float[] dest = new float[1];
            storage.getValueOf(0, dest);
            assertEquals(0.0f, dest[0], 0.0001f, "Destination array should contain correct value for entity ID 0");

            storage.getValueOf(2, dest);
            assertEquals(6.0f, dest[0], 0.0001f, "Destination array should contain correct value for entity ID 2");

            storage.getValueOf(4, dest);
            assertEquals(12.0f, dest[0], 0.0001f, "Destination array should contain correct value for entity ID 4");

            Executable getRemovedEntity1WithDest = () -> storage.getValueOf(1, dest);
            Executable getRemovedEntity3WithDest = () -> storage.getValueOf(3, dest);

            assertThrows(IllegalArgumentException.class, getRemovedEntity1WithDest);
            assertThrows(IllegalArgumentException.class, getRemovedEntity3WithDest);
        }
    }

}