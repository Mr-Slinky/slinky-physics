package com.slinky.physics.util;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.DisplayName;

import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;
import org.junit.jupiter.params.provider.CsvSource;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Unit Test for FloatList class
 *
 * @author 
 */
public class FloatListTest {

    private FloatList testList;

    @BeforeEach
    void setUp() {
        testList = new FloatList();
    }
  
    // =========================== Constructors Tests =========================== //
    @Test
    @DisplayName("Test Default Constructor")
    void testDefaultConstructor() {
        FloatList list = new FloatList();
        assertAll("Default constructor",
                () -> assertNotNull(list, "List should not be null"),
                () -> assertEquals(0, list.size(), "Initial size should be 0"),
                () -> assertEquals(FloatList.MIN_CAPACITY, list.array().length, "Default capacity should be " + FloatList.MIN_CAPACITY),
                () -> assertTrue(list.isEmpty(), "List should be empty")
        );
    }

    @ParameterizedTest
    @ValueSource(ints = {0, 1, 10, 100, 1000, 10_000, 10_000_000})
    @DisplayName("Test Parameterized Constructor with Various Capacities")
    void testParameterizedConstructor(int size) {
        FloatList customList = new FloatList(size);
        assertAll("Parameterized constructor",
                () -> assertNotNull(customList, "Custom list should not be null"),
                () -> assertEquals(0, customList.size(), "Initial size should be 0"),
                () -> assertTrue(customList.isEmpty(), "Custom list should be empty"),
                () -> assertTrue(customList.array().length >= FloatList.MIN_CAPACITY, "Custom list should not have a capacity lower than " + FloatList.MIN_CAPACITY)
        );
    }

    @Test
    @DisplayName("Test Array Constructor - Not Null and Size")
    void testArrayConstructor_notNullAndSize() {
        float[] initialArray = {1.0f, 2.0f, 3.0f, 4.0f, 5.0f};
        FloatList arrayList  = new FloatList(initialArray);

        assertAll("Array-based list constructor and size",
                () -> assertNotNull(arrayList, "Array-based list should not be null"),
                () -> assertEquals(initialArray.length, arrayList.size(), "Size should match the initial array length")
        );
    }

    @Test
    @DisplayName("Test Array Constructor - Elements Match")
    void testArrayConstructor_elementsMatch() {
        float[] initialArray = {1.0f, 2.0f, 3.0f, 4.0f, 5.0f};
        FloatList arrayList = new FloatList(initialArray);

        for (int i = 0; i < initialArray.length; i++) {
            assertEquals(initialArray[i], arrayList.get(i), 0.0001f, "Element at index " + i + " should match");
        }
    }

    // =========================== Add Methods Tests =========================== //
    @ParameterizedTest
    @ValueSource(ints = {0, 1, 10, 100, 1_000_000})
    @DisplayName("Test Add Element")
    void testAddElement(int startingSize) {
        FloatList list = new FloatList(startingSize);
        assertAll("Add element to list",
                () -> assertTrue(list.add(10.0f), "Adding an element should return true"),
                () -> assertEquals(1, list.size(), "Size should be 1 after adding an element"),
                () -> assertEquals(10.0f, list.get(0), 0.0001f, "Element at index 0 should be 10.0f")
        );
    }

    @ParameterizedTest
    @ValueSource(ints = {0, 1, 10, 100, 1_000_000})
    @DisplayName("Test Add Multiple Elements")
    void testAddMultipleElements(int startingSize) {
        FloatList list = new FloatList(startingSize);
        for (int i = 0; i < 15; i++) {
            list.add((float) i);
        }
        assertEquals(15, list.size(), "Size should be 15 after adding 15 elements");
        for (int i = 0; i < 15; i++) {
            assertEquals((float) i, list.get(i), 0.0001f, "Element at index " + i + " should be " + i + ".0f");
        }
    }

    @Test
    @DisplayName("Test Insert Element at Specific Index")
    void testAddAtIndex() {
        FloatList list = new FloatList();
        list.add(10.0f);
        list.add(20.0f);
        list.add(30.0f);
        list.insert(1, 15.0f);

        assertAll("Insert element at specific index",
                () -> assertEquals(4,  list.size(), "Size should be 4 after insertion"),
                () -> assertEquals(10.0f, list.get(0), 0.0001f, "Element at index 0 should be 10.0f"),
                () -> assertEquals(15.0f, list.get(1), 0.0001f, "Element at index 1 should be 15.0f"),
                () -> assertEquals(20.0f, list.get(2), 0.0001f, "Element at index 2 should be 20.0f"),
                () -> assertEquals(30.0f, list.get(3), 0.0001f, "Element at index 3 should be 30.0f")
        );
    }

    @ParameterizedTest
    @DisplayName("Test Insert Element at the End with Various Initial Sizes")
    @ValueSource(ints = {0, 1, 10, 100, 1_000_000})
    void testAddAtIndexEnd(int initialSize) {
        FloatList list = new FloatList();

        // Adding initial elements to match the specified size
        for (int i = 0; i < initialSize; i++) {
            list.add(100.0f + i * 100.0f);
        }

        // Adding at index equal to size (end of the list)
        list.insert(initialSize, 300.0f);
        assertEquals(initialSize + 1, list.size(), "Size should increase by 1 after adding at the end");
        assertEquals(300.0f, list.get(initialSize), 0.0001f, "Element at the new last index should be 300.0f");
    }

    @ParameterizedTest
    @ValueSource(ints = {0, 1, 10, 100, 1_000_000})
    @DisplayName("Test Insert Element at the Beginning with Various Initial Sizes")
    void testAddAtIndexBeginning(int initialSize) {
        FloatList list = new FloatList();

        // Adding initial elements to match the specified size
        for (int i = 0; i < initialSize; i++) {
            list.add(100.0f + i * 100.0f);
        }

        // Adding at the beginning
        list.insert(0, 50.0f);
        assertEquals(initialSize + 1, list.size(), "Size should increase by 1 after adding at the beginning");
        assertEquals(50.0f, list.get(0), 0.0001f, "Element at index 0 should be 50.0f");
    }
    
    @ParameterizedTest
    @ValueSource(ints = {0, 1, 10, 100, 1_000_000})
    @DisplayName("Test Add To Capacity - Resizes")
    void testFillThenAdd(int initialSize) {
        FloatList list = new FloatList(initialSize);
        initialSize = Math.max(FloatList.MIN_CAPACITY, initialSize);
        for (int i = 0; i < initialSize + 1; i++) {
            list.add((float) i);
        }
        
        assertEquals(initialSize + 1, list.size(), "Size should be " + (initialSize + 1) + " after adding elements");
        assertEquals(initialSize + initialSize / 2, list.array().length, "Array should increase by one when adding beyond initial capacity");
    }

    @Test
    @DisplayName("Test Insert at Invalid Indices")
    void testInsertAtInvalidIndex() {
        FloatList list = new FloatList();
        list.add(1.0f);

        // Negative index
        assertThrows(IndexOutOfBoundsException.class, () -> {
            list.insert(-1, 10.0f);
        }, "Inserting at negative index should throw IndexOutOfBoundsException");

        // Index greater than size
        assertThrows(IndexOutOfBoundsException.class, () -> {
            list.insert(3, 10.0f);
        }, "Inserting at index greater than size should throw IndexOutOfBoundsException");
    }

    // =========================== Get Method Tests =========================== //
    @Test
    @DisplayName("Test Get Valid Index")
    void testGetValidIndex() {
        testList.add(5.0f);
        testList.add(10.0f);
        testList.add(15.0f);
        
        assertAll(
            ()-> assertEquals(5.0f,  testList.get(0), 0.0001f, "Element at index 0 should be 5.0f"),
            ()-> assertEquals(10.0f, testList.get(1), 0.0001f, "Element at index 1 should be 10.0f"),
            ()-> assertEquals(15.0f, testList.get(2), 0.0001f, "Element at index 2 should be 15.0f")
        );
    }

    @Test
    @DisplayName("Test Get Invalid Index")
    void testGetInvalidIndex() {
        testList.add(1.0f);

        assertAll("Invalid index access",
                // Negative index
                () -> assertThrows(
                        IndexOutOfBoundsException.class,
                        () -> testList.get(-1),
                        "Getting element at negative index should throw IndexOutOfBoundsException"
                ),
                // Index equal to size
                () -> assertThrows(
                        IndexOutOfBoundsException.class,
                        () -> testList.get(1),
                        "Getting element at index equal to size should throw IndexOutOfBoundsException"
                )
        );
    }


    // =========================== Set Method Tests =========================== //
    @Test
    @DisplayName("Test Set Valid Index")
    void testSetValidIndex() {
        testList.add(100.0f);
        testList.add(200.0f);
        float oldValue = testList.set(1, 250.0f);
        assertAll(
            ()-> assertEquals(200.0f, oldValue,        0.0001f, "Old value at index 1 should be 200.0f"),
            ()-> assertEquals(250.0f, testList.get(1), 0.0001f, "New value at index 1 should be 250.0f")
        );
    }

    @Test
    @DisplayName("Test Set Invalid Index")
    void testSetInvalidIndex() {
        testList.add(1.0f);

        assertAll("Invalid index set operations",
                // Negative index
                () -> assertThrows(
                        IndexOutOfBoundsException.class,
                        () -> testList.set(-1, 10.0f),
                        "Setting element at negative index should throw IndexOutOfBoundsException"
                ),
                // Index equal to size
                () -> assertThrows(
                        IndexOutOfBoundsException.class,
                        () -> testList.set(1, 10.0f),
                        "Setting element at index equal to size should throw IndexOutOfBoundsException"
                )
        );
    }

    // =========================== Remove Methods Tests =========================== //
    @Test
    @DisplayName("Test Remove by Index")
    void testRemoveByIndex() {
        testList.add(10.0f);
        testList.add(20.0f);
        testList.add(30.0f);
        float removed = testList.remove(1);

        assertAll("Remove element by index",
                () -> assertEquals(20.0f, removed, 0.0001f, "Removed element should be 20.0f"),
                () -> assertEquals(2,  testList.size(), "Size should be 2 after removal"),
                () -> assertEquals(10.0f, testList.get(0), 0.0001f, "Element at index 0 should be 10.0f"),
                () -> assertEquals(30.0f, testList.get(1), 0.0001f, "Element at index 1 should be 30.0f")
        );
    }

    @ParameterizedTest
    @DisplayName("Test Remove First Element by Index with Various List Sizes")
    @ValueSource(ints = {2, 3, 4}) // Different list sizes
    void testRemoveFirstElementByIndex(int initialSize) {
        // Populate list with initialSize elements
        for (int i = 0; i < initialSize; i++) {
            testList.add(50.0f + i * 10.0f); // Adds 50.0f, 60.0f, 70.0f, etc.
        }

        // Remove first element
        float removedFirst = testList.remove(0);

        assertAll("Remove first element",
            () -> assertEquals(50.0f, removedFirst, 0.0001f, "Removed first element should be 50.0f"),
            () -> assertEquals(initialSize - 1, testList.size(), "Size should decrease by 1 after removal"),
            () -> assertEquals(60.0f, testList.get(0), 0.0001f, "Element at index 0 should now be 60.0f")
        );
    }

    @ParameterizedTest
    @DisplayName("Test Remove Last Element by Index with Various List Sizes")
    @ValueSource(ints = {2, 3, 4}) // Different list sizes
    void testRemoveLastElementByIndex(int initialSize) {
        // Populate list with initialSize elements
        for (int i = 0; i < initialSize; i++) {
            testList.add(50.0f + i * 10.0f); // Adds 50.0f, 60.0f, 70.0f, etc.
        }

        // Remove last element
        float removedLast = testList.remove(initialSize - 1);

        assertAll("Remove last element",
            () -> assertEquals(50.0f + (initialSize - 1) * 10.0f, removedLast, 0.0001f, "Removed last element should be " + (50.0f + (initialSize - 1) * 10.0f) + "f"),
            () -> assertEquals(initialSize - 1, testList.size(), "Size should decrease by 1 after removal"),
            () -> assertEquals(50.0f, testList.get(0), 0.0001f, "Element at index 0 should still be 50.0f")
        );
    }

    @Test
    @DisplayName("Test Remove by Index Invalid")
    void testRemoveByIndexInvalid() {
        testList.add(1.0f);

        // Negative index
        assertThrows(IndexOutOfBoundsException.class, () -> {
            testList.remove(-1);
        }, "Removing element at negative index should throw IndexOutOfBoundsException");

        // Index equal to size
        assertThrows(IndexOutOfBoundsException.class, () -> {
            testList.remove(1);
        }, "Removing element at index equal to size should throw IndexOutOfBoundsException");
    }

    @Test
    @DisplayName("Test Remove Existing Element")
    void testRemoveElementExisting() {
        testList.add(5.0f);
        testList.add(10.0f);
        testList.add(15.0f);
        boolean removed = testList.removeElement(10.0f);

        assertAll("Remove existing element",
                () -> assertTrue(removed,               "removeElement should return true when element is removed"),
                () -> assertEquals(2,  testList.size(), "Size should be 2 after removal"),
                () -> assertEquals(5.0f,  testList.get(0), 0.0001f, "Element at index 0 should be 5.0f"),
                () -> assertEquals(15.0f, testList.get(1), 0.0001f, "Element at index 1 should be 15.0f")
        );
    }

    @Test
    @DisplayName("Test Remove Non-Existing Element")
    void testRemoveElementNonExisting() {
        testList.add(1.0f);
        testList.add(2.0f);
        boolean removed = testList.removeElement(3.0f);

        assertAll("Remove non-existing element",
                () -> assertFalse(removed,             "removeElement should return false when element is not found"),
                () -> assertEquals(2, testList.size(), "Size should remain unchanged when element is not found")
        );
    }

    // =========================== Clear Method Test =========================== //
    @Test
    @DisplayName("Test Clear Method")
    void testClear() {
        testList.add(100.0f);
        testList.add(200.0f);
        testList.add(300.0f);
        testList.clear();

        assertAll("Clear the list",
                () -> assertEquals(0, testList.size(), "Size should be 0 after clear"),
                () -> assertTrue(testList.isEmpty(),   "List should be empty after clear"),
                () -> assertThrows(
                        IndexOutOfBoundsException.class,
                        () -> testList.get(0),
                        "Getting element from cleared list should throw IndexOutOfBoundsException"
                )
        );
    }

    // =========================== Contains Method Tests =========================== //
    @Test
    @DisplayName("Test Contains - Element Present")
    void testContainsTrue() {
        testList.add(7.0f);
        testList.add(14.0f);
        testList.add(21.0f);
        assertTrue(testList.contains(14.0f), "List should contain 14.0f");
    }

    @Test
    @DisplayName("Test Contains - Element Absent")
    void testContainsFalse() {
        testList.add(7.0f);
        testList.add(14.0f);
        testList.add(21.0f);
        assertFalse(testList.contains(10.0f), "List should not contain 10.0f");
    }

    // =========================== IndexOf Method Tests =========================== //
    @Test
    @DisplayName("Test IndexOf Existing Element")
    void testIndexOfExistingElement() {
        testList.add(5.0f);
        testList.add(10.0f);
        testList.add(15.0f);
        testList.add(10.0f);
        assertEquals(1, testList.indexOf(10.0f), "indexOf should return first occurrence index");
    }

    @Test
    @DisplayName("Test IndexOf Non-Existing Element")
    void testIndexOfNonExistingElement() {
        testList.add(1.0f);
        testList.add(2.0f);
        testList.add(3.0f);
        assertEquals(-1, testList.indexOf(4.0f), "indexOf should return -1 for non-existing element");
    }

    // =========================== ToArray Method Test =========================== //
    @Test
    @DisplayName("Test toArray Method")
    void testToArray() {
        testList.add(100.0f);
        testList.add(200.0f);
        testList.add(300.0f);
        float[] array = testList.toArray();
        assertArrayEquals(new float[]{100.0f, 200.0f, 300.0f}, array, "toArray should return correct array");
    }

    @Test
    @DisplayName("Test toArray on Empty List")
    void testToArrayEmptyList() {
        float[] array = testList.toArray();
        assertArrayEquals(new float[]{}, array, "toArray should return empty array for empty list");
    }

    // =========================== IsEmpty Method Tests =========================== //
    @Test
    @DisplayName("Test isEmpty on Empty List")
    void testIsEmptyTrue() {
        assertTrue(testList.isEmpty(), "List should be empty initially");
    }

    @Test
    @DisplayName("Test isEmpty on Non-Empty List")
    void testIsEmptyFalse() {
        testList.add(10.0f);
        assertFalse(testList.isEmpty(), "List should not be empty after adding an element");
    }

    // =========================== ToString Method Test =========================== //
    @Test
    @DisplayName("Test toString on Empty List")
    void testToStringEmptyList() {
        assertEquals("[]", testList.toString(), "toString should return [] for empty list");
    }

    @Test
    @DisplayName("Test toString on Non-Empty List")
    void testToStringNonEmptyList() {
        testList.add(1.0f);
        testList.add(2.0f);
        testList.add(3.0f);
        assertEquals("[1.0, 2.0, 3.0]", testList.toString(), "toString should return correct string representation");
    }

    // =========================== Capacity Expansion Test =========================== //
    @ParameterizedTest
    @ValueSource(ints = {0, 1, 10, 100})
    @DisplayName("Test Capacity Expansion")
    void testCapacityExpansion(int startingSize) {
        FloatList list = new FloatList(startingSize);
        
        // Adding more elements than initial capacity to test expansion
        for (int i = 0; i < 20; i++) {
            list.add((float) i);
        }
        assertEquals(20, list.size(), "Size should be 20 after adding 20 elements");
        for (int i = 0; i < 20; i++) {
            assertEquals((float) i, list.get(i), 0.0001f, "Element at index " + i + " should be " + i + ".0f");
        }
    }

    // =========================== Fill Method Tests =========================== //
    @Test
    @DisplayName("Test Fill Method on Non-Empty List")
    void testFill() {
        testList.add(1.0f);
        testList.add(2.0f);
        testList.add(3.0f);
        testList.fill(9.0f);

        assertEquals(3, testList.size(), "Size should remain the same after fill");
        for (int i = 0; i < testList.size(); i++) {
            assertEquals(9.0f, testList.get(i), 0.0001f, "Element at index " + i + " should be 9.0f");
        }
    }

    @Test
    @DisplayName("Test Fill Method on Empty List")
    void testFillEmptyList() {
        testList.fill(10.0f);

        assertEquals(0, testList.size(), "Size should remain 0 after fill on empty list");
        assertTrue(testList.isEmpty(), "List should remain empty after fill");
    }

    @ParameterizedTest
    @ValueSource(ints = {0, -1, 42, Integer.MAX_VALUE, Integer.MIN_VALUE})
    @DisplayName("Test Fill with Various Values")
    void testFillWithVariousValues(int value) {
        testList.add(10.0f);
        testList.add(20.0f);
        testList.add(30.0f);

        testList.fill((float) value);

        for (int i = 0; i < testList.size(); i++) {
            assertEquals((float) value, testList.get(i), 0.0001f, "Element at index " + i + " should be " + (float) value + "f");
        }
    }

    // ============================[Add All Method Tests]============================ //
    @Test
    @DisplayName("Test addAll with Non-Empty Array on Empty List")
    void testAddAllWithNonEmptyArrayOnEmptyList() {
        float[] elementsToAdd = {10.0f, 20.0f, 30.0f};
        testList.addAll(elementsToAdd);

        assertAll("AddAll with non-empty array on empty list",
                () -> assertEquals(3, testList.size(), "Size should be 3 after adding 3 elements"),
                () -> assertEquals(10.0f, testList.get(0), 0.0001f, "Element at index 0 should be 10.0f"),
                () -> assertEquals(20.0f, testList.get(1), 0.0001f, "Element at index 1 should be 20.0f"),
                () -> assertEquals(30.0f, testList.get(2), 0.0001f, "Element at index 2 should be 30.0f")
        );
    }

    @Test
    @DisplayName("Test addAll with Non-Empty Array on Non-Empty List")
    void testAddAllWithNonEmptyArrayOnNonEmptyList() {
        testList.add(1.0f);
        testList.add(2.0f);
        float[] elementsToAdd = {3.0f, 4.0f, 5.0f};
        testList.addAll(elementsToAdd);

        assertAll("AddAll with non-empty array on non-empty list",
                () -> assertEquals(5, testList.size(), "Size should be 5 after adding 3 elements"),
                () -> assertEquals(1.0f, testList.get(0), 0.0001f, "Element at index 0 should be 1.0f"),
                () -> assertEquals(2.0f, testList.get(1), 0.0001f, "Element at index 1 should be 2.0f"),
                () -> assertEquals(3.0f, testList.get(2), 0.0001f, "Element at index 2 should be 3.0f"),
                () -> assertEquals(4.0f, testList.get(3), 0.0001f, "Element at index 3 should be 4.0f"),
                () -> assertEquals(5.0f, testList.get(4), 0.0001f, "Element at index 4 should be 5.0f")
        );
    }

    @Test
    @DisplayName("Test addAll with Empty Array")
    void testAddAllWithEmptyArray() {
        testList.add(100.0f);
        testList.add(200.0f);
        float[] emptyArray = {};
        testList.addAll(emptyArray);

        assertAll("AddAll with empty array",
                () -> assertEquals(2, testList.size(), "Size should remain 2 after adding empty array"),
                () -> assertEquals(100.0f, testList.get(0), 0.0001f, "Element at index 0 should be 100.0f"),
                () -> assertEquals(200.0f, testList.get(1), 0.0001f, "Element at index 1 should be 200.0f")
        );
    }

    @Test
    @DisplayName("Test addAll with Null Array")
    void testAddAllWithNullArray() {
        assertThrows(NullPointerException.class, () -> {
            testList.addAll(null);
        }, "Adding a null array should throw NullPointerException");
    }

    @Test
    @DisplayName("Test addAll Triggering Capacity Expansion")
    void testAddAllTriggeringCapacityExpansion() {
        // Initialize list with small capacity
        FloatList smallList = new FloatList(5);
        for (int i = 0; i < 5; i++) {
            smallList.add((float) i);
        }

        // Add an array that exceeds the current capacity
        float[] largeArray = {5.0f, 6.0f, 7.0f, 8.0f, 9.0f, 10.0f};
        smallList.addAll(largeArray);

        assertAll("AddAll triggering capacity expansion",
                () -> assertEquals(11, smallList.size(), "Size should be 11 after adding 6 elements"),
                () -> assertEquals(0.0f, smallList.get(0), 0.0001f, "Element at index 0 should be 0.0f"),
                () -> assertEquals(10.0f, smallList.get(10), 0.0001f, "Element at index 10 should be 10.0f"),
                () -> assertTrue(smallList.array().length >= 11, "Internal array should have expanded to accommodate new elements")
        );
    }

    @Test
    @DisplayName("Test addAll on Initially Empty List")
    void testAddAllOnInitiallyEmptyList() {
        float[] elementsToAdd = {100.0f, 200.0f, 300.0f, 400.0f};
        testList.addAll(elementsToAdd);

        assertAll("AddAll on initially empty list",
                () -> assertEquals(4, testList.size(), "Size should be 4 after adding 4 elements"),
                () -> assertEquals(100.0f, testList.get(0), 0.0001f, "Element at index 0 should be 100.0f"),
                () -> assertEquals(200.0f, testList.get(1), 0.0001f, "Element at index 1 should be 200.0f"),
                () -> assertEquals(300.0f, testList.get(2), 0.0001f, "Element at index 2 should be 300.0f"),
                () -> assertEquals(400.0f, testList.get(3), 0.0001f, "Element at index 3 should be 400.0f")
        );
    }

    @Test
    @DisplayName("Test addAll with Very Large Array")
    void testAddAllWithVeryLargeArray() {
        float[] largeArray = new float[1000];
        for (int i = 0; i < largeArray.length; i++) {
            largeArray[i] = (float) i;
        }
        testList.addAll(largeArray);

        assertAll("AddAll with very large array",
                () -> assertEquals(1000, testList.size(), "Size should be 1000 after adding 1000 elements"),
                () -> assertEquals(0.0f, testList.get(0), 0.0001f, "Element at index 0 should be 0.0f"),
                () -> assertEquals(999.0f, testList.get(999), 0.0001f, "Element at index 999 should be 999.0f"),
                () -> assertTrue(testList.array().length >= 1000, "Internal array should have sufficient capacity after adding large array")
        );
    }

    @Test
    @DisplayName("Test addAll with Multiple Sequential Adds")
    void testAddAllWithMultipleSequentialAdds() {
        float[] firstBatch = {1.0f, 2.0f, 3.0f};
        float[] secondBatch = {4.0f, 5.0f};
        float[] thirdBatch = {6.0f, 7.0f, 8.0f, 9.0f};

        testList.addAll(firstBatch);
        testList.addAll(secondBatch);
        testList.addAll(thirdBatch);

        assertAll("AddAll with multiple sequential adds",
                () -> assertEquals(9, testList.size(), "Size should be 9 after adding all batches"),
                () -> assertArrayEquals(new float[]{1.0f, 2.0f, 3.0f, 4.0f, 5.0f, 6.0f, 7.0f, 8.0f, 9.0f}, testList.toArray(), "All elements should be correctly added in order")
        );
    }

    @Test
    @DisplayName("Test addAll and Verify No Data Loss")
    void testAddAllAndVerifyNoDataLoss() {
        float[] elementsToAdd = {5.0f, 10.0f, 15.0f, 20.0f, 25.0f};
        testList.addAll(elementsToAdd);

        for (int i = 0; i < elementsToAdd.length; i++) {
            assertEquals(elementsToAdd[i], testList.get(i), 0.0001f, "Element at index " + i + " should be " + elementsToAdd[i] + "f");
        }
        assertEquals(elementsToAdd.length, testList.size(), "Size should match the number of added elements");
    }

    @Test
    @DisplayName("Test addAll When List is Near Capacity")
    void testAddAllWhenListIsNearCapacity() {
        // Initialize list with capacity 10 and add 9 elements
        FloatList nearCapacityList = new FloatList(10);
        for (int i = 0; i < 9; i++) {
            nearCapacityList.add((float) i);
        }

        // Add an array of 5 elements to exceed the current capacity
        float[] elementsToAdd = {9.0f, 10.0f, 11.0f, 12.0f, 13.0f};
        nearCapacityList.addAll(elementsToAdd);

        assertAll("AddAll when list is near capacity",
                () -> assertEquals(14, nearCapacityList.size(), "Size should be 14 after adding 5 elements"),
                () -> assertEquals(9.0f, nearCapacityList.get(9), 0.0001f, "Element at index 9 should be 9.0f"),
                () -> assertEquals(13.0f, nearCapacityList.get(13), 0.0001f, "Element at index 13 should be 13.0f"),
                () -> assertTrue(nearCapacityList.array().length >= 14, "Internal array should have expanded to accommodate new elements")
        );
    }

    // =========================== RemoveAll Method Tests =========================== //
    @Test
    @DisplayName("Test RemoveAll Existing Elements")
    void testRemoveAllExistingElements() {
        testList.add(10.0f);
        testList.add(20.0f);
        testList.add(30.0f);
        testList.add(20.0f);
        testList.add(40.0f);

        float[] elementsToRemove = {20.0f, 30.0f};
        testList.removeAll(elementsToRemove);

        assertAll("RemoveAll existing elements",
                () -> assertEquals(2, testList.size(), "Size should be 2 after removals"),
                () -> assertEquals(10.0f, testList.get(0), 0.0001f, "Element at index 0 should be 10.0f"),
                () -> assertEquals(40.0f, testList.get(1), 0.0001f, "Element at index 1 should be 40.0f")
        );
    }

    @Test
    @DisplayName("Test RemoveAll Non-Existing Elements")
    void testRemoveAllNonExistingElements() {
        testList.add(10.0f);
        testList.add(20.0f);

        float[] elementsToRemove = {30.0f, 40.0f};
        testList.removeAll(elementsToRemove);

        assertAll("RemoveAll non-existing elements",
                () -> assertEquals(2, testList.size(), "Size should remain 2 after removals"),
                () -> assertEquals(10.0f, testList.get(0), 0.0001f, "Element at index 0 should be 10.0f"),
                () -> assertEquals(20.0f, testList.get(1), 0.0001f, "Element at index 1 should be 20.0f")
        );
    }

    @Test
    @DisplayName("Test RemoveAll with Empty Array")
    void testRemoveAllWithEmptyArray() {
        testList.add(100.0f);
        testList.add(200.0f);

        float[] emptyArray = {};
        testList.removeAll(emptyArray);

        assertAll("RemoveAll with empty array",
                () -> assertEquals(2, testList.size(), "Size should remain 2 after removing empty array"),
                () -> assertEquals(100.0f, testList.get(0), 0.0001f, "Element at index 0 should be 100.0f"),
                () -> assertEquals(200.0f, testList.get(1), 0.0001f, "Element at index 1 should be 200.0f")
        );
    }

    @Test
    @DisplayName("Test RemoveAll with Null Array")
    void testRemoveAllWithNullArray() {
        testList.add(10.0f);
        float[] nullArray = null;

        assertThrows(NullPointerException.class, () -> {
            testList.removeAll(nullArray);
        }, "Removing with a null array should throw NullPointerException");
    }

    @Test
    @DisplayName("Test RemoveAll Duplicate Elements")
    void testRemoveAllDuplicateElements() {
        testList.add(10.0f);
        testList.add(20.0f);
        testList.add(20.0f);
        testList.add(30.0f);
        testList.add(20.0f);

        float[] elementsToRemove = {20.0f};
        testList.removeAll(elementsToRemove);

        assertAll("RemoveAll duplicate elements",
                () -> assertEquals(2, testList.size(), "Size should be 2 after removals"),
                () -> assertEquals(10.0f, testList.get(0), 0.0001f, "Element at index 0 should be 10.0f"),
                () -> assertEquals(30.0f, testList.get(1), 0.0001f, "Element at index 1 should be 30.0f")
        );
    }

    @Test
    @DisplayName("Test RemoveAll All Elements")
    void testRemoveAllAllElements() {
        testList.add(10.0f);
        testList.add(20.0f);
        testList.add(30.0f);

        float[] elementsToRemove = {10.0f, 20.0f, 30.0f};
        testList.removeAll(elementsToRemove);

        assertAll("RemoveAll all elements",
                () -> assertEquals(0, testList.size(), "Size should be 0 after removing all elements"),
                () -> assertTrue(testList.isEmpty(), "List should be empty after removing all elements")
        );
    }

    @Test
    @DisplayName("Test RemoveAll with Overlapping Elements")
    void testRemoveAllWithOverlappingElements() {
        testList.add(10.0f);
        testList.add(20.0f);
        testList.add(30.0f);
        testList.add(40.0f);

        float[] elementsToRemove = {20.0f, 50.0f};
        testList.removeAll(elementsToRemove);

        assertAll("RemoveAll with overlapping elements",
                () -> assertEquals(3, testList.size(), "Size should be 3 after removals"),
                () -> assertEquals(10.0f, testList.get(0), 0.0001f, "Element at index 0 should be 10.0f"),
                () -> assertEquals(30.0f, testList.get(1), 0.0001f, "Element at index 1 should be 30.0f"),
                () -> assertEquals(40.0f, testList.get(2), 0.0001f, "Element at index 2 should be 40.0f")
        );
    }

    // =========================== Shrinkage Method Tests =========================== //
    @Test
    @DisplayName("Test Capacity Shrinking")
    void testCapacityShrinking() {
        int initialCapacity = 100;
        FloatList list = new FloatList(initialCapacity);

        // Add elements to fill the list beyond shrink threshold
        for (int i = 0; i < 75; i++) { // 75% of initialCapacity
            list.add((float) i);
        }
        assertEquals(75, list.size(), "Size should be 75 after additions");

        // Now removing elements to drop below 25% of capacity (which is 25)
        for (int i = 0; i < 51; i++) {
            list.remove(0);
        }
        assertEquals(24, list.size(), "Size should be 24 after removals");

        // Assuming shrinkThreshold is 25, after size drops to 24, capacity should shrink
        float[] internalArray = list.array();
        assertTrue(internalArray.length < initialCapacity,       "Internal array should have shrunk");
        assertTrue(internalArray.length >= FloatList.MIN_CAPACITY, "Internal array should not shrink below MIN_CAPACITY");
    }
    
    @Test
    @DisplayName("Test Shrinking To Minimum Capacity")
    void testShrinkUntilMinCapacity() {
        int initialCapacity = 100;
        FloatList list = new FloatList(initialCapacity);
        for (int i = 0; i < initialCapacity; i++) {
            list.add((float) i);
        }
        assertEquals(initialCapacity, list.size(), String.format("Size should be %d after %d additions", initialCapacity, initialCapacity));
        
        for (int i = 0; i < initialCapacity; i++) {
            list.removeElement((float) i);
        }
        assertEquals(0, list.size(), String.format("Size should be 0 after %d removals", initialCapacity));
        assertEquals(FloatList.MIN_CAPACITY, list.array().length, String.format("Array capacity should be %d after all elements removed", FloatList.MIN_CAPACITY));
    }
    
    @Test
    @DisplayName("Test SetShrinkPercentage Method")
    void testSetShrinkPercentage() {
        FloatList list = new FloatList(100);

        // Add elements to set up initial state
        for (int i = 0; i < 75; i++) {
            list.add((float) i);
        }

        // Change shrink percentage to 50%
        list.setShrinkPercentage(50);
        assertEquals(50, list.getShrinkPercentage(), "Shrink percentage should be updated to 50%");

        // Remove elements to trigger shrinking at 50%
        for (int i = 0; i < 26; i++) { // Size goes from 75 to 49
            list.remove(0);
        }
        assertEquals(49, list.size(), "Size should be 49 after removals");

        // Verify that internal capacity has shrunk appropriately (50% of 100 initially)
        float[] internalArray = list.array();
        assertEquals(50, internalArray.length, "Internal array should have shrunk based on new shrink percentage");
        assertTrue(internalArray.length >= FloatList.MIN_CAPACITY, "Internal array should not shrink below MIN_CAPACITY");
    }

    @Test
    @DisplayName("Test SetShrinkPercentage with Invalid Values")
    void testSetShrinkPercentageInvalidValues() {
        assertThrows(IllegalArgumentException.class, () -> {
            testList.setShrinkPercentage(5);
        }, "Setting shrink percentage below 10 should throw IllegalArgumentException");

        assertThrows(IllegalArgumentException.class, () -> {
            testList.setShrinkPercentage(95);
        }, "Setting shrink percentage above 90 should throw IllegalArgumentException");
    }
    
}