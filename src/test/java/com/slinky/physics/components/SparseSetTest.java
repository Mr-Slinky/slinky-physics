package com.slinky.physics.components;

import com.slinky.physics.base.EntityManager;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.DisplayName;

import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Unit Test for SparseSet class
 *
 * @author Khe
 */
public class SparseSetTest {

    private SparseSet sparseSet;

    @BeforeEach
    void setUp() {
        sparseSet = new SparseSet();
    }

    // =========================== Constructors Tests =========================== //
    @Test
    @DisplayName("Default Constructor")
    void testDefaultConstructor() {
        SparseSet set = new SparseSet();
        assertAll("Default constructor",
                () -> assertNotNull(set, "SparseSet should not be null"),
                () -> assertEquals(0, set.size(), "Initial size should be 0")
        );
    }

    @ParameterizedTest
    @ValueSource(ints = {10, 100, 1000})
    @DisplayName("Parameterized Constructor with Entity Limit")
    void testParameterizedConstructor(int entityLimit) {
        SparseSet set = new SparseSet(entityLimit);
        assertAll("Parameterized constructor",
                () -> assertNotNull(set, "SparseSet should not be null"),
                () -> assertEquals(0, set.size(), "Initial size should be 0")
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
                () -> assertEquals(0, sparseSet.getDenseIndex(entityId), "Dense index should be 0")
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
                                () -> assertEquals(index, sparseSet.getDenseIndex(entityId), "Dense index should be correct"),
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

    // =========================== GetDenseIndex Method Tests =========================== //
    @Test
    @DisplayName("Get Dense Index of Existing Entity")
    void testGetDenseIndexExistingEntity() {
        int entityId = 5;
        sparseSet.add(entityId);
        int index = sparseSet.getDenseIndex(entityId);
        assertEquals(0, index, "Dense index should be 0 for the first added entity");
    }

    @Test
    @DisplayName("Get Dense Index of Non-Existing Entity")
    void testGetDenseIndexNonExistingEntity() {
        int entityId = 5;
        assertThrows(IllegalArgumentException.class, () -> {
            sparseSet.getDenseIndex(entityId);
        }, "getDenseIndex should throw IllegalArgumentException for non-existing entity");
    }

    // =========================== GetEntityAt Method Tests =========================== //
    @Test
    @DisplayName("Get Entity At Valid Index")
    void testGetEntityAtValidIndex() {
        sparseSet.add(10);
        sparseSet.add(20);
        int entityId = sparseSet.getEntityAt(1);
        assertEquals(20, entityId, "getEntityAt(1) should return the second entity added");
    }

    @Test
    @DisplayName("Get Entity At Invalid Index")
    void testGetEntityAtInvalidIndex() {
        assertThrows(IndexOutOfBoundsException.class, () -> {
            sparseSet.getEntityAt(0);
        }, "getEntityAt should throw IndexOutOfBoundsException for empty SparseSet");
    }

    // =========================== Size Method Tests =========================== //
    @Test
    @DisplayName("Size Method")
    void testSize() {
        assertEquals(0, sparseSet.size(), "Initial size should be 0");
        sparseSet.add(1);
        assertEquals(1, sparseSet.size(), "Size should be 1 after adding an entity");
        sparseSet.add(2);
        assertEquals(2, sparseSet.size(), "Size should be 2 after adding another entity");
        sparseSet.remove(1);
        assertEquals(1, sparseSet.size(), "Size should be 1 after removing an entity");
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
                        IllegalArgumentException.class,
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
            sparseSet.getDenseIndex(10);
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
                () -> assertEquals(0, sparseSet.getDenseIndex(15), "Dense index should be reset to 0")
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
                () -> assertThrows(IllegalArgumentException.class, () -> sparseSet.add(maxID + 1)),
                () -> assertTrue(sparseSet.contains(maxID), "SparseSet should contain max integer ID"),
                () -> assertEquals(0, sparseSet.getDenseIndex(maxID), "Dense index should be 0")
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
            sparseSet.getDenseIndex(10);
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
        SparseSet set = new SparseSet();
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
}
