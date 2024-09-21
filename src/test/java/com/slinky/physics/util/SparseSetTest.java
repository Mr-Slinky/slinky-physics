package com.slinky.physics.util;

import com.slinky.physics.base.EntityManager;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;

import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;
import org.junit.jupiter.params.provider.CsvSource;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Unit Test for SparseSet class
 *
 * @author Kheagen Haskins
 */
public class SparseSetTest {

    private static final int ENTITY_LIMIT = 100_000;
    private SparseSet sparseSet;
    
    
    @BeforeEach
    void setUp() {
        sparseSet = new SparseSet(ENTITY_LIMIT);
    }

    // =========================== Constructors Tests =========================== //
    /**
     * Tests that the constructor initializes the SparseSet correctly with valid
     * entity limits.
     *
     * @param entityLimit the valid entity limit to test
     */
    @ParameterizedTest(name = "Valid entityLimit: {0}")
    @ValueSource(ints = {1, 10, 100, 1000, 1000000})
    @DisplayName("Should initialize correctly with valid entity limits")
    void testConstructorWithValidEntityLimits(int entityLimit) {
        // Arrange & Act
        SparseSet set = new SparseSet(entityLimit);

        // Assert
        assertAll("Valid Constructor Initialization",
                // Verify that the sparse array has the correct length
                () -> assertEquals(entityLimit, set.sparse().length,
                        "Sparse array length should match entityLimit"),
                // Verify that all entries in the sparse array are initialized to -1
                () -> {
                    int[] sparse = set.sparse();
                    // Collect all mismatches for detailed reporting
                    var mismatches = IntStream.range(0, sparse.length)
                            .filter(i -> sparse[i] != -1)
                            .boxed()
                            .collect(Collectors.toList());

                    // If there are mismatches, fail the test with detailed information
                    assertTrue(mismatches.isEmpty(),
                            () -> "All sparse array entries should be initialized to -1. Mismatches at indices: " + mismatches);
                },
                // Verify that the dense list is empty upon initialization
                () -> assertEquals(0, set.size(),
                        "Dense list should be empty after construction")
        );
    }

    /**
     * Tests that the constructor throws an IllegalArgumentException when
     * provided with non-positive entity limits.
     *
     * @param entityLimit the invalid entity limit to test
     */
    @ParameterizedTest(name = "Invalid entityLimit: {0}")
    @ValueSource(ints = {0, -1, -100, Integer.MIN_VALUE})
    @DisplayName("Should throw IllegalArgumentException for non-positive entity limits")
    void testConstructorWithInvalidEntityLimits(int entityLimit) {
        assertThrows(
                IllegalArgumentException.class,
                () -> new SparseSet(entityLimit),
                "Constructor should throw IllegalArgumentException for non-positive entityLimit"
        );
    }

    // =========================== Add Method Tests =========================== //
    @ParameterizedTest
    @ValueSource(ints = {0, 1, 10, 100, 1000})
    @DisplayName("Add Single Entity")
    void testAddSingleEntity(int entityId) {
        boolean added = sparseSet.add(entityId);
        assertAll("Add single entity",
                () -> assertTrue(added, "Adding a new entity should return true"),
                () -> assertEquals(1, sparseSet.size(), "Size should be 1 after adding an entity"),
                () -> assertTrue(sparseSet.contains(entityId), "SparseSet should contain the added entity"),
                () -> assertEquals(0, sparseSet.getIndexOf(entityId), "Dense index should be 0")
        );
    }

    @Test
    @DisplayName("Add Duplicate Entity")
    void testAddDuplicateEntity() {
        int entityId = 5;
        sparseSet.add(entityId);
        boolean addedAgain = sparseSet.add(entityId);
        assertAll("Add duplicate entity",
                () -> assertFalse(addedAgain, "Adding the same entity again should return false"),
                () -> assertEquals(1, sparseSet.size(), "Size should remain 1 after adding duplicate"),
                () -> assertTrue(sparseSet.contains(entityId), "SparseSet should contain the entity")
        );
    }

    @Test
    @DisplayName("Add Multiple Entities")
    void testAddMultipleEntities() {
        int[] entityIds = {1, 2, 3, 4, 5};
        for (int entityId : entityIds) {
            sparseSet.add(entityId);
        }
        assertAll("Add multiple entities",
                () -> assertEquals(entityIds.length, sparseSet.size(), "Size should match the number of added entities"),
                () -> {
                    for (int i = 0; i < entityIds.length; i++) {
                        int entityId = entityIds[i];
                        int index = i;
                        assertAll("Entity " + entityId,
                                () -> assertTrue(sparseSet.contains(entityId), "SparseSet should contain entity " + entityId),
                                () -> assertEquals(index, sparseSet.getIndexOf(entityId), "Dense index should be correct"),
                                () -> assertEquals(entityId, sparseSet.getEntityAt(index), "Entity at dense index should be correct")
                        );
                    }
                }
        );
    }

    // =========================== Remove Method Tests =========================== //
    @Test
    @DisplayName("Remove Existing Entity")
    void testRemoveExistingEntity() {
        int entityId = 10;
        sparseSet.add(entityId);
        boolean removed = sparseSet.remove(entityId);
        assertAll("Remove existing entity",
                () -> assertTrue(removed, "Removing existing entity should return true"),
                () -> assertEquals(0, sparseSet.size(), "Size should be 0 after removal"),
                () -> assertFalse(sparseSet.contains(entityId), "SparseSet should not contain the entity after removal")
        );
    }

    @Test
    @DisplayName("Remove Non-Existing Entity")
    void testRemoveNonExistingEntity() {
        int entityId = 20;
        boolean removed = sparseSet.remove(entityId);
        assertAll("Remove non-existing entity",
                () -> assertFalse(removed, "Removing non-existing entity should return false"),
                () -> assertEquals(0, sparseSet.size(), "Size should remain 0"),
                () -> assertFalse(sparseSet.contains(entityId), "SparseSet should not contain the entity")
        );
    }

    @Test
    @DisplayName("Remove Entities and Check Dense Array")
    void testRemoveEntitiesAndCheckDenseArray() {
        int[] entityIds = {10, 20, 30, 40, 50};
        for (int entityId : entityIds) {
            sparseSet.add(entityId);
        }
        // Remove entity with ID 30
        sparseSet.remove(30);

        // Expected dense array after removal
        int[] expectedEntities = {10, 20, 50, 40};

        assertAll("After removing entity 30",
                () -> assertFalse(sparseSet.contains(30), "SparseSet should not contain 30"),
                () -> assertEquals(4, sparseSet.size(), "Size should be 4 after removal"),
                () -> {
                    for (int i = 0; i < sparseSet.size(); i++) {
                        int entityIdAtIndex = sparseSet.getEntityAt(i);
                        assertEquals(expectedEntities[i], entityIdAtIndex, "Entity at index " + i + " should be " + expectedEntities[i]);
                    }
                }
        );
    }

    // =========================== Contains Method Tests =========================== //
    @Test
    @DisplayName("Contains Method")
    void testContains() {
        int entityId = 5;
        assertFalse(sparseSet.contains(entityId), "SparseSet should not contain entity before adding");
        sparseSet.add(entityId);
        assertTrue(sparseSet.contains(entityId), "SparseSet should contain entity after adding");
    }

    // ============================ GetIndexOf Method Tests =========================== //
    /**
     * Tests that getIndexOf returns the correct index for existing entity IDs.
     *
     * @param entityId the existing entity ID to retrieve the index for
     * @param expectedIndex the expected index of the entity ID
     */
    @ParameterizedTest(name = "Entity ID {0} should be at index {1}")
    @CsvSource({
        "15, 0",
        "25, 1",
        "35, 2",
        "45, 3",
        "55, 4"
    })
    @DisplayName("getIndexOf should return correct index for existing entity IDs")
    void testGetIndexOfExistingEntities(int entityId, int expectedIndex) {
        sparseSet.add(15);
        sparseSet.add(25);
        sparseSet.add(35);
        sparseSet.add(45);
        sparseSet.add(55);
        int actualIndex = sparseSet.getIndexOf(entityId);

        assertAll("getIndexOf with existing entity IDs",
                () -> assertEquals(expectedIndex, actualIndex,
                        "Index of entity ID " + entityId + " should be " + expectedIndex)
        );
    }

    /**
     * Tests that getIndexOf throws IllegalArgumentException for non-existing
     * entity IDs.
     *
     * @param nonExistingEntityId the non-existing entity ID to test
     */
    @ParameterizedTest(name = "Non-existing Entity ID {0} should throw IllegalArgumentException")
    @ValueSource(ints = {5, 16, 26, 36, 46, 56, 1000})
    @DisplayName("getIndexOf should throw IllegalArgumentException for non-existing entity IDs")
    void testGetIndexOfNonExistingEntities(int nonExistingEntityId) {
        assertThrows(
                IllegalArgumentException.class,
                () -> sparseSet.getIndexOf(nonExistingEntityId),
                "getIndexOf should throw IllegalArgumentException for non-existing entity ID " + nonExistingEntityId
        );
    }

    // =========================== GetDenseIndex Method Tests =========================== //
    @Test
    @DisplayName("Get Dense Index of Existing Entity")
    void testGetDenseIndexExistingEntity() {
        int entityId = 5;
        sparseSet.add(entityId);
        int index = sparseSet.getIndexOf(entityId);
        assertEquals(0, index, "Dense index should be 0 for the first added entity");
    }

    @Test
    @DisplayName("Get Dense Index of Non-Existing Entity")
    void testGetDenseIndexNonExistingEntity() {
        int entityId = 5;
        assertThrows(IllegalArgumentException.class, () -> {
            sparseSet.getIndexOf(entityId);
        }, "getDenseIndex should throw IllegalArgumentException for non-existing entity");
    }

    // =========================== GetEntityAt Method Tests =========================== //
    /**
     * Tests that getEntityAt returns the correct entity ID for valid indices.
     *
     * @param index the valid index to retrieve
     * @param expectedEntityId the expected entity ID at the given index
     */
    @ParameterizedTest(name = "Index {0} should return entity ID {1}")
    @CsvSource({
        "0, 10",
        "1, 20",
        "2, 30",
        "3, 40",
        "4, 50"
    })
    @DisplayName("getEntityAt should return correct entity ID for valid indices")
    void testGetEntityAtValidIndices(int index, int expectedEntityId) {
        // Adding entities with IDs 10, 20, 30, 40, 50
        sparseSet.add(10);
        sparseSet.add(20);
        sparseSet.add(30);
        sparseSet.add(40);
        sparseSet.add(50);
        int actualEntityId = sparseSet.getEntityAt(index);

        // Assert
        assertAll("getEntityAt with valid indices",
                () -> assertEquals(expectedEntityId, actualEntityId,
                        "Entity ID at index " + index + " should be " + expectedEntityId)
        );
    }

    /**
     * Tests that getEntityAt throws IndexOutOfBoundsException for invalid
     * indices.
     *
     * @param invalidIndex the invalid index to test
     */
    @ParameterizedTest(name = "Invalid index {0} should throw IndexOutOfBoundsException")
    @ValueSource(ints = {-1, 5, 10, 100})
    @DisplayName("getEntityAt should throw IndexOutOfBoundsException for invalid indices")
    void testGetEntityAtInvalidIndices(int invalidIndex) {
        // Adding entities with IDs 10, 20, 30, 40, 50
        sparseSet.add(10);
        sparseSet.add(20);
        sparseSet.add(30);
        sparseSet.add(40);
        sparseSet.add(50);
        assertThrows(
                IndexOutOfBoundsException.class,
                () -> sparseSet.getEntityAt(invalidIndex),
                "getEntityAt should throw IndexOutOfBoundsException for invalid index " + invalidIndex
        );
    }

    // =========================== Size Method Tests =========================== //
    /**
     * Tests that the size method returns 0 upon initialization.
     */
    @Test
    @DisplayName("Size should be 0 upon initialization")
    void testSizeAfterInitialization() {
        assertAll("Size after initialization",
                () -> assertEquals(0, sparseSet.size(),
                        "Size should be 0 immediately after construction")
        );
    }

    /**
     * Tests that the size method returns the correct count after adding
     * entities.
     *
     * @param entitiesToAdd the number of entities to add
     */
    @ParameterizedTest(name = "Adding {0} entities should result in size {0}")
    @ValueSource(ints = {1, 5, 10, 50, 99, 100, 100_000})
    @DisplayName("Size should reflect the number of added entities")
    void testSizeAfterAddingEntities(int entitiesToAdd) {
        // Arrange
        IntStream.range(0, entitiesToAdd).forEach(sparseSet::add);

        // Act
        int actualSize = sparseSet.size();

        // Assert
        assertAll("Size after adding entities",
                () -> assertEquals(entitiesToAdd, actualSize,
                        "Size should equal the number of added entities")
        );
    }

    /**
     * Tests that the size method returns the correct count after adding and
     * removing entities.
     *
     * @param entitiesToAdd the number of entities to add
     * @param entitiesToRemove the number of entities to remove
     */
    @ParameterizedTest(name = "Add {0}, remove {1}, expected size {2}")
    @CsvSource({
        "10, 5, 5",
        "50, 20, 30",
        "100, 100, 0",
        "99, 1, 98",
        "1, 0, 1"
    })
    @DisplayName("Size should reflect additions and removals")
    void testSizeAfterAddingAndRemovingEntities(int entitiesToAdd, int entitiesToRemove, int expectedSize) {
        // Arrange
        IntStream.range(0, entitiesToAdd).forEach(sparseSet::add);
        IntStream.range(0, entitiesToRemove).forEach(sparseSet::remove);

        // Act
        int actualSize = sparseSet.size();

        // Assert
        assertAll("Size after adding and removing entities",
                () -> assertEquals(expectedSize, actualSize,
                        "Size should equal entities added minus entities removed")
        );
    }

    // =========================== Edge Cases and Additional Tests =========================== //
    @Test
    @DisplayName("Add Entities Beyond Entity Limit")
    void testAddEntitiesBeyondEntityLimit() {
        int entityLimit = EntityManager.ENTITY_LIMIT;
        SparseSet set = new SparseSet(entityLimit);
        for (int i = 0; i < entityLimit; i++) {
            set.add(i);
        }

        assertAll(
                () -> assertEquals(entityLimit, set.size(), "Size should be equal to entity limit after adding entities up to limit"),
                // Attempt to add one more entity beyond the limit
                () -> assertThrows(
                        IndexOutOfBoundsException.class,
                        () -> set.add(entityLimit),
                        "Adding entity beyond the limit should throw IndexOutOfBoundsException"
                )
        );
    }

    @Test
    @DisplayName("Add and Remove All Entities")
    void testAddAndRemoveAllEntities() {
        int[] entityIds = {5, 10, 15, 20, 25};
        for (int entityId : entityIds) {
            sparseSet.add(entityId);
        }
        assertEquals(entityIds.length, sparseSet.size(), "Size should be equal to the number of added entities");

        for (int entityId : entityIds) {
            sparseSet.remove(entityId);
        }
        assertEquals(0, sparseSet.size(), "Size should be 0 after removing all entities");
        for (int entityId : entityIds) {
            assertFalse(sparseSet.contains(entityId), "SparseSet should not contain entity " + entityId);
        }
    }

    @Test
    @DisplayName("Contains Method with Invalid Entity ID")
    void testContainsWithInvalidEntityId() {
        assertFalse(sparseSet.contains(-1), "SparseSet should return false for negative entity ID");
        assertFalse(sparseSet.contains(Integer.MAX_VALUE), "SparseSet should return false for large entity ID not added");
    }

    @Test
    @DisplayName("Add Entities with Negative and Zero IDs")
    void testAddEntitiesWithNegativeAndZeroIds() {
        assertAll("Add entities with negative and zero IDs",
                () -> assertThrows(
                        IndexOutOfBoundsException.class,
                        () -> sparseSet.add(-1),
                        "Adding negative entity ID should throw IndexOutOfBoundsException"),
                () -> assertDoesNotThrow(
                        () -> sparseSet.add(0),
                        "Adding zero as entity ID should not throw an exception"),
                () -> assertTrue(
                        sparseSet.contains(0),
                        "SparseSet should contain entity with ID 0")
        );
    }

    @Test
    @DisplayName("Remove Entities in Reverse Order")
    void testRemoveEntitiesInReverseOrder() {
        int[] entityIds = {1, 2, 3, 4, 5};
        for (int entityId : entityIds) {
            sparseSet.add(entityId);
        }
        for (int i = entityIds.length - 1; i >= 0; i--) {
            sparseSet.remove(entityIds[i]);
        }
        assertEquals(0, sparseSet.size(), "Size should be 0 after removing all entities");
    }

    @Test
    @DisplayName("Stress Test with Many Entities")
    void testStressWithManyEntities() {
        int entityCount = 1000;
        for (int i = 0; i < entityCount; i++) {
            sparseSet.add(i);
        }
        assertEquals(entityCount, sparseSet.size(), "Size should be " + entityCount + " after adding entities");

        for (int i = 0; i < entityCount; i++) {
            assertTrue(sparseSet.contains(i), "SparseSet should contain entity " + i);
        }
    }

    @Test
    @DisplayName("Add and Remove Random Entities")
    void testAddAndRemoveRandomEntities() {
        int[] entityIdsToAdd = {100, 200, 300, 400, 500};
        int[] entityIdsToRemove = {300, 500, 100};

        for (int entityId : entityIdsToAdd) {
            sparseSet.add(entityId);
        }
        for (int entityId : entityIdsToRemove) {
            sparseSet.remove(entityId);
        }

        assertAll("After adding and removing random entities",
                () -> assertFalse(sparseSet.contains(300), "SparseSet should not contain 300"),
                () -> assertFalse(sparseSet.contains(500), "SparseSet should not contain 500"),
                () -> assertFalse(sparseSet.contains(100), "SparseSet should not contain 100"),
                () -> assertTrue(sparseSet.contains(200), "SparseSet should contain 200"),
                () -> assertTrue(sparseSet.contains(400), "SparseSet should contain 400"),
                () -> assertEquals(2, sparseSet.size(), "Size should be 2 after removals")
        );
    }

    @Test
    @DisplayName("GetEntityAt with Out of Bounds Index")
    void testGetEntityAtOutOfBoundsIndex() {
        sparseSet.add(10);
        assertThrows(IndexOutOfBoundsException.class, () -> {
            sparseSet.getEntityAt(1);
        }, "getEntityAt should throw IndexOutOfBoundsException for index out of bounds");
    }

    @Test
    @DisplayName("GetDenseIndex After Removing Entity")
    void testGetDenseIndexAfterRemovingEntity() {
        sparseSet.add(10);
        sparseSet.remove(10);
        assertThrows(IllegalArgumentException.class, () -> {
            sparseSet.getIndexOf(10);
        }, "getDenseIndex should throw IllegalArgumentException after entity is removed");
    }

    @Test
    @DisplayName("Remove and Re-Add Entity")
    void testRemoveAndReAddEntity() {
        sparseSet.add(15);
        sparseSet.remove(15);
        boolean addedAgain = sparseSet.add(15);
        assertAll("Remove and re-add entity",
                () -> assertTrue(addedAgain, "Re-adding a removed entity should return true"),
                () -> assertTrue(sparseSet.contains(15), "SparseSet should contain the re-added entity"),
                () -> assertEquals(0, sparseSet.getIndexOf(15), "Dense index should be reset to 0")
        );
    }

    @Test
    @DisplayName("Add Entities with Gaps in IDs")
    void testAddEntitiesWithGapsInIds() {
        int[] entityIds = {0, 5, 10, 15, 20};
        for (int entityId : entityIds) {
            sparseSet.add(entityId);
        }
        assertAll("Add entities with gaps in IDs",
                () -> assertEquals(entityIds.length, sparseSet.size(), "Size should match the number of added entities"),
                () -> {
                    for (int i = 0; i < entityIds.length; i++) {
                        int entityId = entityIds[i];
                        assertTrue(sparseSet.contains(entityId), "SparseSet should contain entity " + entityId);
                    }
                }
        );
    }

    @Test
    @DisplayName("Add Max Integer Entity ID")
    void testAddMaxIntegerEntityId() {
        int maxID = EntityManager.ENTITY_LIMIT - 1;
        sparseSet.add(maxID);
        assertAll("Add max integer entity ID",
                () -> assertThrows(IndexOutOfBoundsException.class, () -> sparseSet.add(maxID + 1)),
                () -> assertTrue(sparseSet.contains(maxID), "SparseSet should contain max integer ID"),
                () -> assertEquals(0, sparseSet.getIndexOf(maxID), "Dense index should be 0")
        );
    }

    @Test
    @DisplayName("Remove From Empty SparseSet")
    void testRemoveFromEmptySparseSet() {
        boolean removed = sparseSet.remove(10);
        assertFalse(removed, "Removing from empty SparseSet should return false");
    }

    @Test
    @DisplayName("Contains After Clearing SparseSet")
    void testContainsAfterClearingSparseSet() {
        sparseSet.add(10);
        sparseSet.remove(10);
        assertFalse(sparseSet.contains(10), "SparseSet should not contain entity after it is removed");
    }

    @Test
    @DisplayName("Get Dense Index After Clearing SparseSet")
    void testGetDenseIndexAfterClearingSparseSet() {
        sparseSet.add(10);
        sparseSet.remove(10);
        assertThrows(IllegalArgumentException.class, () -> {
            sparseSet.getIndexOf(10);
        }, "getDenseIndex should throw IllegalArgumentException after entity is removed");
    }

    @Test
    @DisplayName("Add and Remove Entities Repeatedly")
    void testAddAndRemoveEntitiesRepeatedly() {
        for (int i = 0; i < 10; i++) {
            sparseSet.add(i);
            sparseSet.remove(i);
        }
        assertEquals(0, sparseSet.size(), "Size should be 0 after adding and removing entities repeatedly");
    }

    @Test
    @DisplayName("Remove Entities Not in SparseSet")
    void testRemoveEntitiesNotInSparseSet() {
        sparseSet.add(1);
        boolean removed = sparseSet.remove(2);
        assertAll("Remove entities not in SparseSet",
                () -> assertFalse(removed, "Removing non-existing entity should return false"),
                () -> assertEquals(1, sparseSet.size(), "Size should remain unchanged"),
                () -> assertTrue(sparseSet.contains(1), "SparseSet should still contain entity 1")
        );
    }

    @Test
    @DisplayName("Add Entity After Max Integer ID")
    void testAddEntityAfterMaxIntegerId() {
        assertThrows(IndexOutOfBoundsException.class, () -> {
            sparseSet.add(Integer.MAX_VALUE + 1);
        }, "Adding entity ID beyond Integer.MAX_VALUE should throw IndexOutOfBoundsException");
    }

    @Test
    @DisplayName("Get Entity At Negative Index")
    void testGetEntityAtNegativeIndex() {
        sparseSet.add(10);
        assertThrows(IndexOutOfBoundsException.class, () -> {
            sparseSet.getEntityAt(-1);
        }, "getEntityAt should throw IndexOutOfBoundsException for negative index");
    }

    @Test
    @DisplayName("Ensure Internal Arrays Do Not Leak")
    void testInternalArraysDoNotLeak() {
        // Assuming SparseSet does not expose internal arrays
        assertThrows(NoSuchMethodException.class, () -> {
            sparseSet.getClass().getMethod("getDense");
        }, "SparseSet should not have a method to expose internal dense array");
    }

    @Test
    @DisplayName("Add Null Entity ID")
    void testAddNullEntityId() {
        // Since entityId is a primitive int, it cannot be null; this test ensures type safety
        // This test can be omitted if not applicable
    }

    @Test
    @DisplayName("Add Maximum Number of Entities")
    void testAddMaximumNumberOfEntities() {
        SparseSet set = new SparseSet(2000);
        int maxEntities = 1000; // Adjust based on system limits
        for (int i = 0; i < maxEntities; i++) {
            set.add(i);
        }
        assertEquals(maxEntities, set.size(), "Size should be equal to maxEntities after adding");
    }

    @Test
    @DisplayName("Remove and Add Entities to Test Reuse")
    void testRemoveAndAddEntitiesToTestReuse() {
        sparseSet.add(10);
        sparseSet.remove(10);
        sparseSet.add(20);
        assertAll("Remove and add entities to test reuse",
                () -> assertTrue(sparseSet.contains(20), "SparseSet should contain entity 20"),
                () -> assertFalse(sparseSet.contains(10), "SparseSet should not contain entity 10"),
                () -> assertEquals(1, sparseSet.size(), "Size should be 1 after operations")
        );
    }

    @Test
    @DisplayName("Stress Test Adding and Removing Entities")
    void testStressAddingAndRemovingEntities() {
        int operations = 1000;
        for (int i = 0; i < operations; i++) {
            sparseSet.add(i);
        }

        for (int i = 0; i < operations; i++) {
            sparseSet.remove(i);
        }
        assertEquals(0, sparseSet.size(), "Size should be 0 after adding and removing entities");
    }
    
    // ======================== Nested Test Classes For Add and Remove ======================== //
    @Nested
    @DisplayName("add(int eID) Method Tests")
    class AddMethodTests {

        /**
         * Tests that adding a new entity returns true and the entity is present
         * after addition.
         *
         * @param eID the entity ID to add
         */
        @ParameterizedTest(name = "Add new entity ID {0}")
        @ValueSource(ints = {0, 1, 50, 99})
        @DisplayName("Should add new entities successfully")
        void testAddNewEntities(int eID) {
            boolean wasAdded = sparseSet.add(eID);

            boolean contains = sparseSet.contains(eID);
            int index = sparseSet.getIndexOf(eID);
            int entityAtIndex = sparseSet.getEntityAt(index);

            assertAll("Adding new entities",
                    () -> assertTrue(wasAdded, "add() should return true when adding a new entity"),
                    () -> assertTrue(contains, "SparseSet should contain the added entity"),
                    () -> assertEquals(eID, entityAtIndex, "Entity ID at its index should match the added entity ID")
            );
        }

        /**
         * Tests that adding an existing entity returns false and does not alter
         * the set.
         *
         * @param eID the entity ID to add twice
         */
        @ParameterizedTest(name = "Add existing entity ID {0} twice")
        @ValueSource(ints = {0, 1, 50, 99})
        @DisplayName("Should not add duplicate entities")
        void testAddExistingEntities(int eID) {
            sparseSet.add(eID);
            boolean wasAddedAgain = sparseSet.add(eID);

            int currentSize = sparseSet.size();
            int index = sparseSet.getIndexOf(eID);
            int entityAtIndex = sparseSet.getEntityAt(index);

            assertAll("Adding existing entities",
                    () -> assertFalse(wasAddedAgain, "add() should return false when adding an existing entity"),
                    () -> assertEquals(1, currentSize, "Size should remain 1 after attempting to add a duplicate"),
                    () -> assertEquals(eID, entityAtIndex, "Entity ID at its index should remain unchanged")
            );
        }

        /**
         * Tests that adding an entity with a negative ID throws
         * IndexOutOfBoundsException.
         *
         * @param invalidEID the invalid (negative) entity ID to add
         */
        @ParameterizedTest(name = "Add invalid (negative) entity ID {0}")
        @ValueSource(ints = {-1, -50, Integer.MIN_VALUE})
        @DisplayName("Should throw IndexOutOfBoundsException for negative entity IDs")
        void testAddInvalidNegativeEntityIDs(int invalidEID) {
            IndexOutOfBoundsException exception = assertThrows(
                    IndexOutOfBoundsException.class,
                    () -> sparseSet.add(invalidEID),
                    "add() should throw IndexOutOfBoundsException for negative entity IDs"
            );

            assertAll("Exception Message Verification",
                    () -> assertNotNull(exception.getMessage(), "Exception message should not be null"),
                    () -> assertFalse(exception.getMessage().isEmpty(), "Exception message should not be empty"),
                    () -> assertTrue(exception.getMessage().contains("out of bounds"),
                            "Exception message should indicate that the entity ID is out of bounds")
            );
        }

        /**
         * Tests that adding an entity with an ID equal to or exceeding the
         * capacity throws IndexOutOfBoundsException.
         *
         * @param invalidEID the invalid (out-of-bounds) entity ID to add
         */
        @ParameterizedTest(name = "Add invalid (out-of-bounds) entity ID {0}")
        @ValueSource(ints = {ENTITY_LIMIT, ENTITY_LIMIT + 1, Integer.MAX_VALUE})
        @DisplayName("Should throw IndexOutOfBoundsException for out-of-bounds entity IDs")
        void testAddInvalidOutOfBoundsEntityIDs(int invalidEID) {
            IndexOutOfBoundsException exception = assertThrows(
                    IndexOutOfBoundsException.class,
                    () -> sparseSet.add(invalidEID),
                    "add() should throw IndexOutOfBoundsException for out-of-bounds entity IDs"
            );

            assertAll("Exception Message Verification",
                    () -> assertNotNull(exception.getMessage(), "Exception message should not be null"),
                    () -> assertFalse(exception.getMessage().isEmpty(), "Exception message should not be empty"),
                    () -> assertTrue(exception.getMessage().contains("out of bounds"),
                            "Exception message should indicate that the entity ID is out of bounds")
            );
        }

        /**
         * Tests adding multiple entities up to the capacity to ensure no
         * exceptions are thrown.
         */
        @Test
        @DisplayName("Should add multiple entities up to capacity successfully")
        void testAddMultipleEntitiesUpToCapacity() {
            // Arrange
            IntStream.range(0, ENTITY_LIMIT).forEach(eID -> {
                boolean wasAdded = sparseSet.add(eID);
                assertTrue(wasAdded, "add() should return true when adding a new entity");
            });

            // Act
            int currentSize = sparseSet.size();

            // Assert
            assertAll("Adding multiple entities up to capacity",
                    () -> assertEquals(ENTITY_LIMIT, currentSize, "Size should equal ENTITY_LIMIT after adding all entities"),
                    () -> {
                        // Verify that all entities are present
                        boolean allPresent = IntStream.range(0, ENTITY_LIMIT)
                                .allMatch(sparseSet::contains);
                        assertTrue(allPresent, "All added entities should be present in the SparseSet");
                    }
            );
        }

        /**
         * Tests that adding an entity after reaching capacity throws
         * IndexOutOfBoundsException.
         */
        @Test
        @DisplayName("Should throw IndexOutOfBoundsException when adding beyond capacity")
        void testAddBeyondCapacity() {
            IntStream.range(0, ENTITY_LIMIT).forEach(sparseSet::add);
            int beyondCapacityEID = ENTITY_LIMIT + 1;

            IndexOutOfBoundsException exception = assertThrows(
                    IndexOutOfBoundsException.class,
                    () -> sparseSet.add(beyondCapacityEID),
                    "add() should throw IndexOutOfBoundsException when adding beyond capacity"
            );

            assertAll("Exception Message Verification",
                    () -> assertNotNull(exception.getMessage(), "Exception message should not be null"),
                    () -> assertFalse(exception.getMessage().isEmpty(), "Exception message should not be empty"),
                    () -> assertTrue(exception.getMessage().contains("out of bounds"),
                            "Exception message should indicate that the entity ID is out of bounds")
            );
        }
    }

    @Nested
    @DisplayName("remove(int entityId) Method Tests")
    class RemoveMethodTests {

        /**
         * Sets up the SparseSet with a predefined set of entities before each
         * test in this nested class.
         */
        @BeforeEach
        void setupEntities() {
            // Adding entities with IDs 10, 20, 30, 40, 50
            sparseSet.add(10);
            sparseSet.add(20);
            sparseSet.add(30);
            sparseSet.add(40);
            sparseSet.add(50);
        }

        /**
         * Tests that removing an existing entity returns true and the entity is
         * no longer present.
         *
         * @param entityId the entity ID to remove
         */
        @ParameterizedTest(name = "Remove existing entity ID {0}")
        @ValueSource(ints = {10, 20, 30, 40, 50})
        @DisplayName("Should remove existing entities successfully")
        void testRemoveExistingEntities(int entityId) {
            // Arrange
            boolean wasRemoved = sparseSet.remove(entityId);

            // Act
            boolean contains = sparseSet.contains(entityId);
            int expectedSize = 4;

            // Assert
            assertAll("Removing existing entities",
                    () -> assertTrue(wasRemoved, "remove() should return true when removing an existing entity"),
                    () -> assertFalse(contains, "SparseSet should not contain the entity after removal"),
                    () -> assertEquals(expectedSize, sparseSet.size(),
                            "Size should decrement by one after removing an entity")
            );
        }

        /**
         * Tests that removing a non-existing entity returns false and does not
         * alter the set.
         *
         * @param entityId the non-existing entity ID to remove
         */
        @ParameterizedTest(name = "Remove non-existing entity ID {0}")
        @ValueSource(ints = {5, 15, 25, 35, 45, 55, 100})
        @DisplayName("Should not remove non-existing entities")
        void testRemoveNonExistingEntities(int entityId) {
            // Arrange
            boolean wasRemoved = sparseSet.remove(entityId);
            int expectedSize = 5;

            // Act
            boolean contains = sparseSet.contains(entityId);

            // Assert
            assertAll("Removing non-existing entities",
                    () -> assertFalse(wasRemoved, "remove() should return false when removing a non-existing entity"),
                    () -> assertEquals(expectedSize, sparseSet.size(),
                            "Size should remain unchanged when removing a non-existing entity"),
                    () -> assertFalse(contains, "SparseSet should not contain the non-existing entity")
            );
        }

        /**
         * Tests that removing from an empty SparseSet returns false.
         */
        @Test
        @DisplayName("Should not remove entities from an empty SparseSet")
        void testRemoveFromEmptySparseSet() {
            // Arrange
            SparseSet emptySet = new SparseSet(ENTITY_LIMIT);
            boolean wasRemoved = emptySet.remove(10);

            // Act
            boolean contains = emptySet.contains(10);
            int expectedSize = 0;

            // Assert
            assertAll("Removing from an empty SparseSet",
                    () -> assertFalse(wasRemoved, "remove() should return false when removing from an empty SparseSet"),
                    () -> assertEquals(expectedSize, emptySet.size(),
                            "Size should remain 0 after attempting to remove from an empty SparseSet"),
                    () -> assertFalse(contains, "SparseSet should not contain any entities after construction")
            );
        }

        /**
         * Tests the internal consistency after removing an entity, especially
         * verifying the swap logic.
         */
        @Test
        @DisplayName("Should maintain internal consistency after removing an entity")
        void testInternalConsistencyAfterRemoval() {
            // Arrange
            // Initial Dense List: [10, 20, 30, 40, 50]
            // Remove entity 20: swap with last element (50)
            boolean wasRemoved = sparseSet.remove(20);

            // Act
            boolean containsRemoved = sparseSet.contains(20);
            boolean containsSwapped = sparseSet.contains(50);
            int size = sparseSet.size();
            int indexOf50 = sparseSet.getIndexOf(50);
            int entityAtIndexOf50 = sparseSet.getEntityAt(indexOf50);

            // Assert
            assertAll("Internal consistency after removal",
                    () -> assertTrue(wasRemoved, "remove() should return true when removing an existing entity"),
                    () -> assertFalse(containsRemoved, "SparseSet should not contain the removed entity"),
                    () -> assertTrue(containsSwapped, "SparseSet should still contain the swapped entity"),
                    () -> assertEquals(4, size, "Size should decrement by one after removal"),
                    () -> assertEquals(50, entityAtIndexOf50, "Swapped entity should be correctly placed in the dense list")
            );
        }

        /**
         * Tests removing all entities one by one to ensure the SparseSet
         * handles emptying correctly.
         */
        @Test
        @DisplayName("Should remove all entities sequentially without issues")
        void testRemoveAllEntitiesSequentially() {
            // Arrange
            IntStream.rangeClosed(10, 50).filter(eID -> eID % 10 == 0).forEach(eID -> {
                boolean wasRemoved = sparseSet.remove(eID);
                assertTrue(wasRemoved, "remove() should return true when removing an existing entity");
            });

            // Act
            int finalSize = sparseSet.size();

            // Assert
            assertAll("Removing all entities sequentially",
                    () -> assertEquals(0, finalSize, "Size should be 0 after removing all entities"),
                    () -> {
                        // Verify that no entities are present
                        boolean anyPresent = IntStream.rangeClosed(10, 50).filter(eID -> eID % 10 == 0)
                                .anyMatch(sparseSet::contains);
                        assertFalse(anyPresent, "SparseSet should not contain any entities after all removals");
                    }
            );
        }

        /**
         * Tests removing an entity multiple times to ensure idempotency.
         */
        @Test
        @DisplayName("Should handle multiple removals of the same entity gracefully")
        void testMultipleRemovalsOfSameEntity() {
            // Arrange
            int entityId = 30;
            boolean firstRemoval = sparseSet.remove(entityId);
            boolean secondRemoval = sparseSet.remove(entityId);

            // Act
            boolean contains = sparseSet.contains(entityId);
            int expectedSize = 4;

            // Assert
            assertAll("Multiple removals of the same entity",
                    () -> assertTrue(firstRemoval, "First removal should return true"),
                    () -> assertFalse(secondRemoval, "Second removal should return false"),
                    () -> assertFalse(contains, "SparseSet should not contain the entity after removal"),
                    () -> assertEquals(expectedSize, sparseSet.size(),
                            "Size should decrement only once after multiple removals")
            );
        }
    }

}
