package com.slinky.physics.util;

import com.slinky.physics.base.EntityManager;
import com.slinky.physics.components.Component;
import java.util.Arrays;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;

import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;
import org.junit.jupiter.params.provider.CsvSource;

import org.junit.jupiter.api.function.Executable;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Test class for {@link VectorStorage}.
 * 
 * @author Kheagen Haskins
 */
public class VectorStorageTest {
    
    private Component testComponent = Component.POSITION; // arbitrary getComponent
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
        @DisplayName("Should create VectorStorage with valid capacities")
        void testConstructorWithValidCapacities(int initialCapacity, int maxCapacity) {
            VectorStorage storage = new VectorStorage(testComponent, entityManager, initialCapacity, maxCapacity);

            assertAll("Validating VectorStorage initialization",
                () -> assertEquals(maxCapacity, storage.getMaxEntityCapacity(), "Max capacity not set correctly"),
                () -> assertNotNull(storage.getVectorData(), "Vector data should not be null"),
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
            Executable constructor = () -> new VectorStorage(testComponent, entityManager, initialCapacity, maxCapacity);
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
            Executable constructor = () -> new VectorStorage(testComponent, entityManager, initialCapacity, maxCapacity);
            IllegalArgumentException exception = assertThrows(IllegalArgumentException.class, constructor);

            assertEquals("Initial capacity cannot exceed maximum capacity", exception.getMessage());
        }

        @Test
        @DisplayName("Should correctly initialize internal structures")
        void testConstructorInitializesInternalStructures() {
            int initialCapacity = 10;
            int maxCapacity = 100;
            VectorStorage storage = new VectorStorage(testComponent, entityManager, initialCapacity, maxCapacity);

            assertAll("Validating internal structures",
                () -> assertEquals(maxCapacity, storage.getMaxEntityCapacity(), "Max capacity mismatch"),
                () -> assertEquals(0, storage.size(), "Size should be zero upon initialization"),
                () -> assertNotNull(storage.getVectorData(), "Vector data should not be null"),
                () -> assertEquals(initialCapacity * 2, storage.getVectorData().capacity(), "Vector data capacity mismatch"),
                () -> assertNotNull(storage.getEntityIds(), "Entity IDs should not be null"),
                () -> assertEquals(0, storage.getEntityIds().size(), "Entity IDs list should be empty")
            );
        }
    }
    
    // =========================[ Getter Method Tests ]========================== \\
    /**
     * Tests for the getter methods of {@link VectorStorage}.
     */
    @Nested
    @DisplayName("Getter Tests")
    class GetterTests {

        @Test
        @DisplayName("Should return correct size when storage is empty")
        void testSizeWhenEmpty() {
            VectorStorage storage = new VectorStorage(testComponent, entityManager, 10, 100);

            assertEquals(0, storage.size(), "Size should be zero when storage is empty");
        }

        @Test
        @DisplayName("Should return correct size after adding entities")
        void testSizeAfterAddingEntities() {
            VectorStorage storage = new VectorStorage(testComponent, entityManager, 10, 100);
            storage.add(1, 10.0f, 15.0f);
            storage.add(2, 20.0f, 25.0f);

            assertEquals(2, storage.size(), "Size should reflect the number of added entities");
        }

        @Test
        @DisplayName("Should return correct max capacity")
        void testGetMaxEntityCapacity() {
            int initialCapacity = 10;
            int maxCapacity = 50;
            VectorStorage storage = new VectorStorage(testComponent, entityManager, initialCapacity, maxCapacity);

            assertEquals(maxCapacity, storage.getMaxEntityCapacity(), "Max capacity should match the value set in constructor");
        }

        @Test
        @DisplayName("Should return non-null vector data")
        void testGetVectorData() {
            VectorStorage storage = new VectorStorage(testComponent, entityManager, 10, 100);

            assertNotNull(storage.getVectorData(), "getVectorData() should not return null");
            assertEquals(0, storage.getVectorData().size(), "Vector data size should be zero when storage is empty");
        }

        @Test
        @DisplayName("Should return vector data with correct values after adding entities")
        void testGetVectorDataAfterAddingEntities() {
            VectorStorage storage = new VectorStorage(testComponent, entityManager, 5, 50);
            storage.add(1, 10.0f, 15.0f);
            storage.add(2, 20.0f, 25.0f);

            FloatList vectorData = storage.getVectorData();

            assertAll("Validating vector data contents",
                () -> assertEquals(4, vectorData.size(), "Vector data size should be twice the number of entities"),
                () -> assertEquals(10.0f, vectorData.get(0), "First entity x-coordinate mismatch"),
                () -> assertEquals(15.0f, vectorData.get(1), "First entity y-coordinate mismatch"),
                () -> assertEquals(20.0f, vectorData.get(2), "Second entity x-coordinate mismatch"),
                () -> assertEquals(25.0f, vectorData.get(3), "Second entity y-coordinate mismatch")
            );
        }

        @Test
        @DisplayName("Should return empty entity IDs list when storage is empty")
        void testGetEntityIdsWhenEmpty() {
            VectorStorage storage = new VectorStorage(testComponent, entityManager, 10, 100);

            IntList entityIds = storage.getEntityIds();

            assertNotNull(entityIds, "Entity IDs list should not be null");
            assertEquals(0, entityIds.size(), "Entity IDs list should be empty when storage is empty");
        }

        @Test
        @DisplayName("Should return correct entity IDs after adding entities")
        void testGetEntityIdsAfterAddingEntities() {
            VectorStorage storage = new VectorStorage(testComponent, entityManager, 5, 50);
            storage.add(1, 10.0f, 15.0f);
            storage.add(3, 20.0f, 25.0f);
            storage.add(5, 30.0f, 35.0f);

            IntList entityIds = storage.getEntityIds();

            assertAll("Validating entity IDs list",
                () -> assertNotNull(entityIds, "Entity IDs list should not be null"),
                () -> assertEquals(3, entityIds.size(), "Entity IDs list size should match the number of added entities"),
                () -> assertTrue(entityIds.contains(1), "Entity IDs should contain 1"),
                () -> assertTrue(entityIds.contains(3), "Entity IDs should contain 3"),
                () -> assertTrue(entityIds.contains(5), "Entity IDs should contain 5")
            );
        }

        @Test
        @DisplayName("Should maintain correct state after removing entities")
        void testGettersAfterRemovingEntities() {
            VectorStorage storage = new VectorStorage(testComponent, entityManager, 5, 50);
            storage.add(1, 10.0f, 15.0f);
            storage.add(2, 20.0f, 25.0f);
            storage.add(3, 30.0f, 35.0f);

            storage.remove(2);

            assertAll("Validating state after removal",
                () -> assertEquals(2, storage.size(), "Size should reflect the number of remaining entities"),
                () -> assertEquals(4, storage.getVectorData().size(), "Vector data size should be updated after removal"),
                () -> assertFalse(storage.getEntityIds().contains(2), "Entity IDs should not contain removed entity"),
                () -> assertTrue(storage.getEntityIds().contains(1), "Entity IDs should still contain existing entity"),
                () -> assertTrue(storage.getEntityIds().contains(3), "Entity IDs should still contain existing entity")
            );
        }
    }
   
    // ===========================[ Add Method Tests ]=========================== \\
    /**
     * Tests for the add method of {@link VectorStorage}.
     */
    @Nested
    @DisplayName("Add Method Tests")
    class AddMethodTests {

        @ParameterizedTest
        @ValueSource(ints = {0, 1, 100, 1000, 9_999})
        @DisplayName("Should add entity successfully when capacity allows")
        void testAddEntitySuccessfully(int entityId) {
            VectorStorage storage = new VectorStorage(testComponent, entityManager, 5, entityId + 100);
            storage.add(entityId, 10.0f, 15.0f);

            assertAll("Validating successful addition of entity",
                    () -> assertEquals(1, storage.size(), "Size should be 1 after adding one entity"),
                    () -> assertTrue(storage.contains(entityId), "Storage should contain entity ID 1"),
                    () -> assertEquals(10.0f, storage.getX(entityId), "X-coordinate mismatch"),
                    () -> assertEquals(15.0f, storage.getY(entityId), "Y-coordinate mismatch")
            );
        }

        @Test
        @DisplayName("Should throw IllegalStateException when adding entity beyond max capacity")
        void testAddEntityBeyondMaxCapacity() {
            VectorStorage storage = new VectorStorage(testComponent, entityManager, 2, 2);
            storage.add(0, 10.0f, 15.0f);
            storage.add(1, 20.0f, 25.0f);

            Executable addOperation = () -> storage.add(3, 30.0f, 35.0f);
            IllegalStateException exception = assertThrows(IllegalStateException.class, addOperation);

            assertEquals("Maximum capacity reached: 2", exception.getMessage());
        }

        @ParameterizedTest(name = "entityId={0}")
        @CsvSource({
            "-1",
            "-5",
            "10"
        })
        @DisplayName("Should throw IllegalArgumentException for invalid entity IDs")
        void testAddEntityWithInvalidEntityId(int entityId) {
            VectorStorage storage = new VectorStorage(testComponent, entityManager, 5, 10);

            Executable addOperation = () -> storage.add(entityId, 10.0f, 15.0f);
            IllegalArgumentException exception = assertThrows(IllegalArgumentException.class, addOperation);

            assertEquals("Entity ID out of bounds: " + entityId, exception.getMessage());
        }

        @Test
        @DisplayName("Should throw IllegalArgumentException when adding duplicate entity")
        void testAddDuplicateEntity() {
            VectorStorage storage = new VectorStorage(testComponent, entityManager, 5, 10);
            storage.add(1, 10.0f, 15.0f);

            Executable addOperation = () -> storage.add(1, 20.0f, 25.0f);
            IllegalArgumentException exception = assertThrows(IllegalArgumentException.class, addOperation);

            assertEquals("Entity already exists: 1", exception.getMessage());
        }

        @Test
        @DisplayName("Should store multiple entities correctly")
        void testAddMultipleEntities() {
            VectorStorage storage = new VectorStorage(testComponent, entityManager, 5, 10);
            storage.add(1, 10.0f, 15.0f);
            storage.add(2, 20.0f, 25.0f);
            storage.add(3, 30.0f, 35.0f);

            assertAll("Validating multiple entities",
                    () -> assertEquals(3, storage.size(), "Size should be 3 after adding three entities"),
                    () -> assertEquals(10.0f, storage.getX(1), "Entity 1 X-coordinate mismatch"),
                    () -> assertEquals(15.0f, storage.getY(1), "Entity 1 Y-coordinate mismatch"),
                    () -> assertEquals(20.0f, storage.getX(2), "Entity 2 X-coordinate mismatch"),
                    () -> assertEquals(25.0f, storage.getY(2), "Entity 2 Y-coordinate mismatch"),
                    () -> assertEquals(30.0f, storage.getX(3), "Entity 3 X-coordinate mismatch"),
                    () -> assertEquals(35.0f, storage.getY(3), "Entity 3 Y-coordinate mismatch")
            );
        }

        @Test
        @DisplayName("Should dynamically resize vectorData when capacity exceeded")
        void testDynamicResizingOfVectorData() {
            VectorStorage storage = new VectorStorage(testComponent, entityManager, 2, 10);
            storage.add(1, 10.0f, 15.0f);
            storage.add(2, 20.0f, 25.0f);
            storage.add(3, 30.0f, 35.0f); // This should trigger a resize

            FloatList vectorData = storage.getVectorData();

            assertAll("Validating vectorData after dynamic resizing",
                    () -> assertEquals(6, vectorData.size(), "vectorData size should be 6 after adding three entities"),
                    () -> assertEquals(10.0f, vectorData.get(0), "First entity X-coordinate mismatch"),
                    () -> assertEquals(15.0f, vectorData.get(1), "First entity Y-coordinate mismatch"),
                    () -> assertEquals(20.0f, vectorData.get(2), "Second entity X-coordinate mismatch"),
                    () -> assertEquals(25.0f, vectorData.get(3), "Second entity Y-coordinate mismatch"),
                    () -> assertEquals(30.0f, vectorData.get(4), "Third entity X-coordinate mismatch"),
                    () -> assertEquals(35.0f, vectorData.get(5), "Third entity Y-coordinate mismatch")
            );
        }
    }
    // =========================[ Remove Method Tests ]========================== \\
    @Nested
    @DisplayName("Remove Method Tests")
    class RemoveMethodTests {

        @Test
        @DisplayName("Should remove existing entity successfully")
        void testRemoveExistingEntity() {
            VectorStorage storage = new VectorStorage(testComponent, entityManager, 5, 10);
            storage.add(0, 10.0f, 15.0f);
            storage.add(1, 20.0f, 25.0f);

            storage.remove(0);

            assertAll("Validating storage after removal",
                    () -> assertEquals(1, storage.size(), "Size should be 1 after removing one entity"),
                    () -> assertFalse(storage.contains(0), "Storage should not contain entity ID 0"),
                    () -> assertTrue(storage.contains(1), "Storage should still contain entity ID 1"),
                    () -> assertEquals(20.0f, storage.getX(1), "Entity 1 X-coordinate mismatch"),
                    () -> assertEquals(25.0f, storage.getY(1), "Entity 1 Y-coordinate mismatch")
            );
        }

        @Test
        @DisplayName("Should throw IllegalArgumentException when removing non-existent entity")
        void testRemoveNonExistentEntity() {
            VectorStorage storage = new VectorStorage(testComponent, entityManager, 5, 10);
            storage.add(0, 10.0f, 15.0f);

            Executable removeOperation = () -> storage.remove(1);
            IllegalArgumentException exception = assertThrows(IllegalArgumentException.class, removeOperation);

            assertEquals("Entity does not exist: 1", exception.getMessage());
        }

        @Test
        @DisplayName("Should remove multiple entities and maintain correct state")
        void testRemoveMultipleEntities() {
            VectorStorage storage = new VectorStorage(testComponent, entityManager, 5, 10);
            storage.add(0, 10.0f, 15.0f);
            storage.add(1, 20.0f, 25.0f);
            storage.add(2, 30.0f, 35.0f);

            storage.remove(1);
            storage.remove(0);

            assertAll("Validating storage after multiple removals",
                    () -> assertEquals(1, storage.size(), "Size should be 1 after removing two entities"),
                    () -> assertFalse(storage.contains(0), "Storage should not contain entity ID 0"),
                    () -> assertFalse(storage.contains(1), "Storage should not contain entity ID 1"),
                    () -> assertTrue(storage.contains(2), "Storage should still contain entity ID 2"),
                    () -> assertEquals(30.0f, storage.getX(2), "Entity 2 X-coordinate mismatch"),
                    () -> assertEquals(35.0f, storage.getY(2), "Entity 2 Y-coordinate mismatch")
            );
        }

        @Test
        @DisplayName("Should handle removing the last entity correctly")
        void testRemoveLastEntity() {
            VectorStorage storage = new VectorStorage(testComponent, entityManager, 5, 10);
            storage.add(0, 10.0f, 15.0f);

            storage.remove(0);

            assertAll("Validating storage after removing the last entity",
                    () -> assertEquals(0, storage.size(), "Size should be 0 after removing the last entity"),
                    () -> assertFalse(storage.contains(0), "Storage should not contain entity ID 0"),
                    () -> assertEquals(0, storage.getVectorData().size(), "Vector data should be empty")
            );
        }

        @Test
        @DisplayName("Should handle swap and pop correctly when removing entities")
        void testSwapAndPopOnRemove() {
            VectorStorage storage = new VectorStorage(testComponent, entityManager, 5, 10);
            storage.add(0, 10.0f, 15.0f);
            storage.add(1, 20.0f, 25.0f);
            storage.add(2, 30.0f, 35.0f);

            storage.remove(1); // This should swap entity 2 into position of entity 1

            assertAll("Validating swap and pop behavior",
                    () -> assertEquals(2, storage.size(), "Size should be 2 after removing one entity"),
                    () -> assertFalse(storage.contains(1), "Storage should not contain entity ID 1"),
                    () -> assertTrue(storage.contains(0), "Storage should contain entity ID 0"),
                    () -> assertTrue(storage.contains(2), "Storage should contain entity ID 2"),
                    () -> assertEquals(10.0f, storage.getX(0), "Entity 0 X-coordinate mismatch"),
                    () -> assertEquals(15.0f, storage.getY(0), "Entity 0 Y-coordinate mismatch"),
                    () -> assertEquals(30.0f, storage.getX(2), "Entity 2 X-coordinate mismatch after swap"),
                    () -> assertEquals(35.0f, storage.getY(2), "Entity 2 Y-coordinate mismatch after swap")
            );
        }

        @Test
        @DisplayName("Should handle removing all entities and reset storage")
        void testRemoveAllEntities() {
            VectorStorage storage = new VectorStorage(testComponent, entityManager, 5, 10);
            storage.add(0, 10.0f, 15.0f);
            storage.add(1, 20.0f, 25.0f);

            storage.remove(0);
            storage.remove(1);

            assertAll("Validating storage after removing all entities",
                    () -> assertEquals(0, storage.size(), "Size should be 0 after removing all entities"),
                    () -> assertFalse(storage.contains(0), "Storage should not contain entity ID 0"),
                    () -> assertFalse(storage.contains(1), "Storage should not contain entity ID 1"),
                    () -> assertEquals(0, storage.getVectorData().size(), "Vector data should be empty"),
                    () -> assertEquals(0, storage.getEntityIds().size(), "Entity IDs list should be empty")
            );
        }
    }

    // ====================[ Contains & GetSet Method Tests ]==================== \\
    @Nested
    @DisplayName("Contains and Get/Set Methods Tests")
    class ContainsAndGetSetMethodsTests {

        @Test
        @DisplayName("Should return true for existing entities in contains method")
        void testContainsExistingEntity() {
            VectorStorage storage = new VectorStorage(testComponent, entityManager, 5, 10);
            storage.add(0, 10.0f, 15.0f);
            storage.add(1, 20.0f, 25.0f);

            assertAll("Validating contains method for existing entities",
                    () -> assertTrue(storage.contains(0), "Storage should contain entity ID 0"),
                    () -> assertTrue(storage.contains(1), "Storage should contain entity ID 1")
            );
        }

        @Test
        @DisplayName("Should return false for non-existing entities in contains method")
        void testContainsNonExistingEntity() {
            VectorStorage storage = new VectorStorage(testComponent, entityManager, 5, 10);
            storage.add(0, 10.0f, 15.0f);

            assertAll("Validating contains method for non-existing entities",
                    () -> assertFalse(storage.contains(1), "Storage should not contain entity ID 1"),
                    () -> assertFalse(storage.contains(2), "Storage should not contain entity ID 2")
            );
        }

        @Test
        @DisplayName("Should retrieve correct x and y components with getX and getY")
        void testGetXAndGetY() {
            VectorStorage storage = new VectorStorage(testComponent, entityManager, 5, 10);
            storage.add(0, 10.0f, 15.0f);
            storage.add(1, 20.0f, 25.0f);

            assertAll("Validating getX and getY methods",
                    () -> assertEquals(10.0f, storage.getX(0), "Entity 0 X-coordinate mismatch"),
                    () -> assertEquals(15.0f, storage.getY(0), "Entity 0 Y-coordinate mismatch"),
                    () -> assertEquals(20.0f, storage.getX(1), "Entity 1 X-coordinate mismatch"),
                    () -> assertEquals(25.0f, storage.getY(1), "Entity 1 Y-coordinate mismatch")
            );
        }

        @Test
        @DisplayName("getX and getY should throw IllegalArgumentException for non-existing entities")
        void testGetXAndGetYWithNonExistingEntity() {
            VectorStorage storage = new VectorStorage(testComponent, entityManager, 5, 10);
            storage.add(0, 10.0f, 15.0f);

            Executable getXOperation = () -> storage.getX(1);
            Executable getYOperation = () -> storage.getY(1);

            assertAll("Validating exceptions for getX and getY with non-existing entities",
                    () -> assertThrows(IllegalArgumentException.class, getXOperation, "getX should throw exception"),
                    () -> assertThrows(IllegalArgumentException.class, getYOperation, "getY should throw exception")
            );
        }

        @Test
        @DisplayName("Should update x and y components with setX and setY")
        void testSetXAndSetY() {
            VectorStorage storage = new VectorStorage(testComponent, entityManager, 5, 10);
            storage.add(0, 10.0f, 15.0f);

            storage.setX(0, 50.0f);
            storage.setY(0, 55.0f);

            assertAll("Validating setX and setY methods",
                    () -> assertEquals(50.0f, storage.getX(0), "Entity 0 X-coordinate should be updated to 50.0f"),
                    () -> assertEquals(55.0f, storage.getY(0), "Entity 0 Y-coordinate should be updated to 55.0f")
            );
        }

        @Test
        @DisplayName("setX and setY should throw IllegalArgumentException for non-existing entities")
        void testSetXAndSetYWithNonExistingEntity() {
            VectorStorage storage = new VectorStorage(testComponent, entityManager, 5, 10);
            storage.add(0, 10.0f, 15.0f);

            Executable setXOperation = () -> storage.setX(1, 50.0f);
            Executable setYOperation = () -> storage.setY(1, 55.0f);

            assertAll("Validating exceptions for setX and setY with non-existing entities",
                    () -> assertThrows(IllegalArgumentException.class, setXOperation, "setX should throw exception"),
                    () -> assertThrows(IllegalArgumentException.class, setYOperation, "setY should throw exception")
            );
        }

        @ParameterizedTest(name = "entityId={0}, x={1}, y={2}")
        @CsvSource({
            "0, 10.0, 15.0",
            "1, 20.0, 25.0",
            "2, 30.0, 35.0"
        })
        @DisplayName("Should correctly add and retrieve multiple entities")
        void testAddAndRetrieveMultipleEntities(int entityId, float x, float y) {
            VectorStorage storage = new VectorStorage(testComponent, entityManager, 5, 10);
            storage.add(entityId, x, y);

            assertAll("Validating addition and retrieval of multiple entities",
                    () -> assertTrue(storage.contains(entityId), "Storage should contain entity ID " + entityId),
                    () -> assertEquals(x, storage.getX(entityId), "Entity " + entityId + " X-coordinate mismatch"),
                    () -> assertEquals(y, storage.getY(entityId), "Entity " + entityId + " Y-coordinate mismatch")
            );
        }

        @Test
        @DisplayName("Should correctly update and retrieve x and y components for multiple entities")
        void testSetXAndSetYForMultipleEntities() {
            VectorStorage storage = new VectorStorage(testComponent, entityManager, 5, 10);
            storage.add(0, 10.0f, 15.0f);
            storage.add(1, 20.0f, 25.0f);

            storage.setX(0, 50.0f);
            storage.setY(1, 55.0f);

            assertAll("Validating setX and setY for multiple entities",
                    () -> assertEquals(50.0f, storage.getX(0), "Entity 0 X-coordinate should be updated to 50.0f"),
                    () -> assertEquals(15.0f, storage.getY(0), "Entity 0 Y-coordinate should remain 15.0f"),
                    () -> assertEquals(20.0f, storage.getX(1), "Entity 1 X-coordinate should remain 20.0f"),
                    () -> assertEquals(55.0f, storage.getY(1), "Entity 1 Y-coordinate should be updated to 55.0f")
            );
        }
    }

    
    // ======================[ GetSet Vector Method Tests ]====================== \\
    @Nested
    @DisplayName("Get/Set VectorOf Methods Tests")
    class GetSetVectorOfMethodsTests {

        @Test
        @DisplayName("Should retrieve vector into provided array with getVectorOf(entityId, dest)")
        void testGetVectorOfWithDestArray() {
            VectorStorage storage = new VectorStorage(testComponent, entityManager, 5, 10);
            storage.add(0, 10.0f, 15.0f);

            float[] dest = new float[2];
            storage.getVectorOf(0, dest);

            assertAll("Validating getVectorOf with destination array",
                    () -> assertEquals(10.0f, dest[0], "X-coordinate mismatch in dest array"),
                    () -> assertEquals(15.0f, dest[1], "Y-coordinate mismatch in dest array")
            );
        }

        @Test
        @DisplayName("getVectorOf(entityId, dest) should throw exception for non-existing entity")
        void testGetVectorOfWithDestArrayNonExistingEntity() {
            VectorStorage storage = new VectorStorage(testComponent, entityManager, 5, 10);

            float[] dest = new float[2];
            Executable operation = () -> storage.getVectorOf(0, dest);

            IllegalArgumentException exception = assertThrows(IllegalArgumentException.class, operation);
            // Optionally check exception message if specified
        }

        @Test
        @DisplayName("getVectorOf(entityId, dest) should throw exception for insufficient dest array size")
        void testGetVectorOfWithInsufficientDestArray() {
            VectorStorage storage = new VectorStorage(testComponent, entityManager, 5, 10);
            storage.add(0, 10.0f, 15.0f);

            float[] dest = new float[1]; // Insufficient size
            Executable operation = () -> storage.getVectorOf(0, dest);

            IllegalArgumentException exception = assertThrows(IllegalArgumentException.class, operation);
            assertEquals("Destination array must have at least two elements.", exception.getMessage());
        }

        @Test
        @DisplayName("Should retrieve vector as new array with getVectorOf(entityId)")
        void testGetVectorOfReturnsArray() {
            VectorStorage storage = new VectorStorage(testComponent, entityManager, 5, 10);
            storage.add(0, 20.0f, 25.0f);

            float[] vector = storage.getVectorOf(0);

            assertAll("Validating getVectorOf returns correct array",
                    () -> assertNotNull(vector, "Returned vector array should not be null"),
                    () -> assertEquals(2, vector.length, "Returned vector array should have length 2"),
                    () -> assertEquals(20.0f, vector[0], "X-coordinate mismatch in returned array"),
                    () -> assertEquals(25.0f, vector[1], "Y-coordinate mismatch in returned array")
            );
        }

        @Test
        @DisplayName("getVectorOf(entityId) should throw exception for non-existing entity")
        void testGetVectorOfReturnsArrayNonExistingEntity() {
            VectorStorage storage = new VectorStorage(testComponent, entityManager, 5, 10);

            Executable operation = () -> storage.getVectorOf(0);

            IllegalArgumentException exception = assertThrows(IllegalArgumentException.class, operation);
            // Optionally check exception message if specified
        }

        @Test
        @DisplayName("Should set vector components with setVectorOf(entityId, x, y)")
        void testSetVectorOf() {
            VectorStorage storage = new VectorStorage(testComponent, entityManager, 5, 10);
            storage.add(0, 10.0f, 15.0f);

            storage.setVectorOf(0, 50.0f, 55.0f);

            assertAll("Validating setVectorOf method",
                    () -> assertEquals(50.0f, storage.getX(0), "X-coordinate should be updated to 50.0f"),
                    () -> assertEquals(55.0f, storage.getY(0), "Y-coordinate should be updated to 55.0f")
            );
        }

        @Test
        @DisplayName("setVectorOf(entityId, x, y) should throw exception for non-existing entity")
        void testSetVectorOfNonExistingEntity() {
            VectorStorage storage = new VectorStorage(testComponent, entityManager, 5, 10);

            Executable operation = () -> storage.setVectorOf(0, 50.0f, 55.0f);

            IllegalArgumentException exception = assertThrows(IllegalArgumentException.class, operation);
            // Optionally check exception message if specified
        }

        @Test
        @DisplayName("Should handle multiple entities with getVectorOf and setVectorOf")
        void testGetSetVectorOfMultipleEntities() {
            VectorStorage storage = new VectorStorage(testComponent, entityManager, 5, 10);
            storage.add(0, 10.0f, 15.0f);
            storage.add(1, 20.0f, 25.0f);

            storage.setVectorOf(0, 50.0f, 55.0f);
            float[] vector1 = storage.getVectorOf(0);
            float[] vector2 = storage.getVectorOf(1);

            assertAll("Validating getVectorOf and setVectorOf with multiple entities",
                    () -> assertEquals(50.0f, vector1[0], "Entity 0 X-coordinate should be updated to 50.0f"),
                    () -> assertEquals(55.0f, vector1[1], "Entity 0 Y-coordinate should be updated to 55.0f"),
                    () -> assertEquals(20.0f, vector2[0], "Entity 1 X-coordinate should remain 20.0f"),
                    () -> assertEquals(25.0f, vector2[1], "Entity 1 Y-coordinate should remain 25.0f")
            );
        }

        @ParameterizedTest(name = "entityId={0}, x={1}, y={2}")
        @CsvSource({
            "0, 10.0, 15.0",
            "1, 20.0, 25.0",
            "2, 30.0, 35.0"
        })
        @DisplayName("Should correctly get and set vectors for multiple entities")
        void testGetSetVectorOfParameterized(int entityId, float x, float y) {
            VectorStorage storage = new VectorStorage(testComponent, entityManager, 5, 10);
            storage.add(entityId, x, y);

            storage.setVectorOf(entityId, x + 10.0f, y + 10.0f);
            float[] vector = storage.getVectorOf(entityId);

            assertAll("Validating getVectorOf and setVectorOf",
                    () -> assertEquals(x + 10.0f, vector[0], "X-coordinate should be updated"),
                    () -> assertEquals(y + 10.0f, vector[1], "Y-coordinate should be updated")
            );
        }
    }
    
    // =================[ Advanced Vector Storage Method Tests ]================= \\
    @Nested
    @DisplayName("Advanced Operations Tests")
    class AdvancedOperationsTests {

        @Test
        @DisplayName("Should correctly handle multiple add and remove operations")
        void testMultipleAddAndRemoveOperations() {
            VectorStorage storage = new VectorStorage(testComponent, entityManager, 5, 20);

            // Add entities 0 to 9
            for (int i = 0; i < 10; i++) {
                storage.add(i, i * 1.0f, i * 1.5f);
            }

            // Remove even entities
            for (int i = 0; i < 10; i += 2) {
                storage.remove(i);
            }

            // Add entities 10 to 14
            for (int i = 10; i < 15; i++) {
                storage.add(i, i * 2.0f, i * 2.5f);
            }

            // Check internal state
            FloatList vectorData = storage.getVectorData();
            IntList entityIds = storage.getEntityIds();

            assertAll("Validating internal state after multiple operations",
                    () -> assertEquals(10, storage.size(), "Storage size should be 10"),
                    () -> assertEquals(20, vectorData.size(), "Vector data size should be 20 (10 entities * 2 components)"),
                    () -> assertEquals(10, entityIds.size(), "Entity IDs size should be 10"),
                    () -> {
                        // Check that the entities in storage are odd numbers from 1 to 9 and 10 to 14
                        int[] expectedEntityIds = {1, 3, 5, 7, 9, 10, 11, 12, 13, 14};
                        int[] actualEntityIds = Arrays.copyOf(entityIds.array(), entityIds.size());
                        Arrays.sort(actualEntityIds);
                        assertArrayEquals(expectedEntityIds, actualEntityIds, "Entity IDs should match expected IDs");
                    },
                    () -> {
                        // Check that vectorData contains correct values
                        for (int idx = 0; idx < storage.size(); idx++) {
                            int entityId = entityIds.get(idx);
                            float expectedX, expectedY;
                            if (entityId < 10) {
                                expectedX = entityId * 1.0f;
                                expectedY = entityId * 1.5f;
                            } else {
                                expectedX = entityId * 2.0f;
                                expectedY = entityId * 2.5f;
                            }
                            float actualX = vectorData.get(idx * 2);
                            float actualY = vectorData.get(idx * 2 + 1);
                            assertEquals(expectedX, actualX, "X-coordinate mismatch for entity " + entityId);
                            assertEquals(expectedY, actualY, "Y-coordinate mismatch for entity " + entityId);
                        }
                    }
            );
        }

        @Test
        @DisplayName("Should correctly resize internal arrays when capacity exceeded")
        void testInternalArrayResizing() {
            VectorStorage storage = new VectorStorage(testComponent, entityManager, 2, 100);

            int initialCapacity = storage.getVectorData().capacity();

            // Add entities to exceed initial capacity
            for (int i = 0; i < 50; i++) {
                storage.add(i, i * 1.0f, i * 2.0f);
            }

            int newCapacity = storage.getVectorData().capacity();

            assertTrue(newCapacity > initialCapacity, "Vector data capacity should have increased");

            assertEquals(50, storage.size(), "Storage size should be 50 after adding 50 entities");
            assertEquals(100, storage.getVectorData().size(), "Vector data size should be 100 (50 entities * 2 components)");
        }

        @Test
        @DisplayName("Should maintain data consistency after swap and pop operations")
        void testDataConsistencyAfterSwapAndPop() {
            VectorStorage storage = new VectorStorage(testComponent, entityManager, 5, 10);
            storage.add(0, 0.0f, 0.0f);
            storage.add(1, 1.0f, 1.0f);
            storage.add(2, 2.0f, 2.0f);
            storage.add(3, 3.0f, 3.0f);

            // Remove entity 1 (should swap with the last entity, which is entity 3)
            storage.remove(1);

            FloatList vectorData = storage.getVectorData();
            IntList entityIds = storage.getEntityIds();

            assertAll("Validating data consistency after swap and pop",
                    () -> assertEquals(3, storage.size(), "Storage size should be 3"),
                    () -> {
                        // Check that entity IDs reflect the swap
                        int[] expectedEntityIds = {0, 3, 2};
                        int[] actualEntityIds = Arrays.copyOf(entityIds.array(), entityIds.size());
                        // Order matters here because of swapping
                        assertArrayEquals(expectedEntityIds, actualEntityIds, "Entity IDs should reflect swap and pop");
                    },
                    () -> {
                        // Check that vectorData contains correct values
                        for (int idx = 0; idx < storage.size(); idx++) {
                            int entityId = entityIds.get(idx);
                            float expectedX = entityId * 1.0f;
                            float expectedY = entityId * 1.0f;
                            float actualX = vectorData.get(idx * 2);
                            float actualY = vectorData.get(idx * 2 + 1);
                            assertEquals(expectedX, actualX, "X-coordinate mismatch for entity " + entityId);
                            assertEquals(expectedY, actualY, "Y-coordinate mismatch for entity " + entityId);
                        }
                    }
            );
        }

        @Test
        @DisplayName("Should handle stress test of adding and removing entities")
        void testStressAddRemove() {
            VectorStorage storage = new VectorStorage(testComponent, entityManager, 10, 1000);

            // Add 500 entities
            for (int i = 0; i < 500; i++) {
                storage.add(i, i * 1.0f, i * 1.1f);
            }

            assertEquals(500, storage.size(), "Storage size should be 500 after adding 500 entities");

            // Remove every third entity
            for (int i = 0; i < 500; i += 4) {
                storage.remove(i);
            }

            int expectedSize = 500 - (500 / 4);
            assertEquals(expectedSize, storage.size(), "Storage size should reflect removed entities");

            // Check that remaining entities have correct data
            FloatList vectorData = storage.getVectorData();
            IntList entityIds = storage.getEntityIds();

            assertAll("Validating data consistency after stress add/remove",
                    () -> {
                        for (int idx = 0; idx < storage.size(); idx++) {
                            int entityId = entityIds.get(idx);
                            float expectedX = entityId * 1.0f;
                            float expectedY = entityId * 1.1f;
                            float actualX = vectorData.get(idx * 2);
                            float actualY = vectorData.get(idx * 2 + 1);
                            assertEquals(expectedX, actualX, "X-coordinate mismatch for entity " + entityId);
                            assertEquals(expectedY, actualY, "Y-coordinate mismatch for entity " + entityId);
                        }
                    }
            );
        }

        @Test
        @DisplayName("Should not have residual data after removing all entities")
        void testResidualDataAfterClearing() {
            VectorStorage storage = new VectorStorage(testComponent, entityManager, 5, 10);

            // Add entities
            for (int i = 0; i < 5; i++) {
                storage.add(i, i * 1.0f, i * 1.5f);
            }

            // Remove all entities
            for (int i = 0; i < 5; i++) {
                storage.remove(i);
            }

            assertAll("Validating storage after clearing all entities",
                    () -> assertEquals(0, storage.size(), "Storage size should be 0"),
                    () -> assertEquals(0, storage.getVectorData().size(), "Vector data should be empty"),
                    () -> assertEquals(0, storage.getEntityIds().size(), "Entity IDs should be empty")
            );
        }
    }

}