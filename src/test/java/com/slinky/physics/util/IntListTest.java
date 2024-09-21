package com.slinky.physics.util;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.DisplayName;

import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;
import org.junit.jupiter.params.provider.CsvSource;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Unit Test for IntList class
 *
 * @author 
 */
public class IntListTest {

    private IntList testList;

    @BeforeEach
    void setUp() {
        testList = new IntList();
    }
  
    // =========================== Constructors Tests =========================== //
    @Test
    @DisplayName("Test Default Constructor")
    void testDefaultConstructor() {
        IntList list = new IntList();
        assertAll("Default constructor",
                () -> assertNotNull(list, "List should not be null"),
                () -> assertEquals(0, list.size(), "Initial size should be 0"),
                () -> assertEquals(IntList.MIN_CAPACITY, list.array().length, "Default capacity should be " + IntList.MIN_CAPACITY),
                () -> assertTrue(list.isEmpty(), "List should be empty")
        );
    }

    @ParameterizedTest
    @ValueSource(ints = {0, 1, 10, 100, 1000, 10_000, 10_000_000})
    @DisplayName("Test Parameterized Constructor with Various Capacities")
    void testParameterizedConstructor(int size) {
        IntList customList = new IntList(size);
        assertAll("Parameterized constructor",
                () -> assertNotNull(customList, "Custom list should not be null"),
                () -> assertEquals(0, customList.size(), "Initial size should be 0"),
                () -> assertTrue(customList.isEmpty(), "Custom list should be empty"),
                () -> assertTrue(customList.array().length >= IntList.MIN_CAPACITY, "Custom list should not have a capacity lower than " + IntList.MIN_CAPACITY)
        );
    }

    @Test
    @DisplayName("Test Array Constructor - Not Null and Size")
    void testArrayConstructor_notNullAndSize() {
        int[] initialArray = {1, 2, 3, 4, 5};
        IntList arrayList  = new IntList(initialArray);

        assertAll("Array-based list constructor and size",
                () -> assertNotNull(arrayList, "Array-based list should not be null"),
                () -> assertEquals(initialArray.length, arrayList.size(), "Size should match the initial array length")
        );
    }

    @Test
    @DisplayName("Test Array Constructor - Elements Match")
    void testArrayConstructor_elementsMatch() {
        int[] initialArray = {1, 2, 3, 4, 5};
        IntList arrayList = new IntList(initialArray);

        for (int i = 0; i < initialArray.length; i++) {
            assertEquals(initialArray[i], arrayList.get(i), "Element at index " + i + " should match");
        }
    }

    // =========================== Add Methods Tests =========================== //
    @ParameterizedTest
    @ValueSource(ints = {0, 1, 10, 100, 1_000_000})
    @DisplayName("Test Add Element")
    void testAddElement(int startingSize) {
        IntList list = new IntList(startingSize);
        assertAll("Add element to list",
                () -> assertTrue(list.add(10), "Adding an element should return true"),
                () -> assertEquals(1, list.size(), "Size should be 1 after adding an element"),
                () -> assertEquals(10, list.get(0), "Element at index 0 should be 10")
        );
    }

    @ParameterizedTest
    @ValueSource(ints = {0, 1, 10, 100, 1_000_000})
    @DisplayName("Test Add Multiple Elements")
    void testAddMultipleElements(int startingSize) {
        IntList list = new IntList(startingSize);
        for (int i = 0; i < 15; i++) {
            list.add(i);
        }
        assertEquals(15, list.size(), "Size should be 15 after adding 15 elements");
        for (int i = 0; i < 15; i++) {
            assertEquals(i, list.get(i), "Element at index " + i + " should be " + i);
        }
    }

    @Test
    @DisplayName("Test Insert Element at Specific Index")
    void testAddAtIndex() {
        IntList list = new IntList();
        list.add(10);
        list.add(20);
        list.add(30);
        list.insert(1, 15);

        assertAll("Insert element at specific index",
                () -> assertEquals(4,  list.size(), "Size should be 4 after insertion"),
                () -> assertEquals(10, list.get(0), "Element at index 0 should be 10"),
                () -> assertEquals(15, list.get(1), "Element at index 1 should be 15"),
                () -> assertEquals(20, list.get(2), "Element at index 2 should be 20"),
                () -> assertEquals(30, list.get(3), "Element at index 3 should be 30")
        );
    }

    @ParameterizedTest
    @DisplayName("Test Insert Element at the End with Various Initial Sizes")
    @ValueSource(ints = {0, 1, 10, 100, 1_000_000})
    void testAddAtIndexEnd(int initialSize) {
        IntList list = new IntList();

        // Adding initial elements to match the specified size
        for (int i = 0; i < initialSize; i++) {
            list.add(100 + i * 100);
        }

        // Adding at index equal to size (end of the list)
        list.insert(initialSize, 300);
        assertEquals(initialSize + 1, list.size(), "Size should increase by 1 after adding at the end");
        assertEquals(300, list.get(initialSize),   "Element at the new last index should be 300");
    }

    @ParameterizedTest
    @ValueSource(ints = {0, 1, 10, 100, 1_000_000})
    @DisplayName("Test Insert Element at the Beginning with Various Initial Sizes")
    void testAddAtIndexBeginning(int initialSize) {
        IntList list = new IntList();

        // Adding initial elements to match the specified size
        for (int i = 0; i < initialSize; i++) {
            list.add(100 + i * 100);
        }

        // Adding at the beginning
        list.insert(0, 50);
        assertEquals(initialSize + 1, list.size(), "Size should increase by 1 after adding at the beginning");
        assertEquals(50, list.get(0), "Element at index 0 should be 50");
    }
    
    @ParameterizedTest
    @ValueSource(ints = {0, 1, 10, 100, 1_000_000})
    @DisplayName("Test Add To Capacity - Resizes")
    void testFillThenAdd(int initialSize) {
        IntList list = new IntList(initialSize);
        initialSize = Math.max(IntList.MIN_CAPACITY, initialSize);
        for (int i = 0; i < initialSize + 1; i++) {
            list.add(i);
        }
        
        assertEquals(initialSize + initialSize / 2, list.array().length, "Array should increase by half its size when full");
    }

    @Test
    @DisplayName("Test Insert at Invalid Indices")
    void testInsertAtInvalidIndex() {
        IntList list = new IntList();
        list.add(1);

        // Negative index
        assertThrows(IndexOutOfBoundsException.class, () -> {
            list.insert(-1, 10);
        }, "Inserting at negative index should throw IndexOutOfBoundsException");

        // Index greater than size
        assertThrows(IndexOutOfBoundsException.class, () -> {
            list.insert(3, 10);
        }, "Inserting at index greater than size should throw IndexOutOfBoundsException");
    }

    // =========================== Get Method Tests =========================== //
    @Test
    @DisplayName("Test Get Valid Index")
    void testGetValidIndex() {
        testList.add(5);
        testList.add(10);
        testList.add(15);
        
        assertAll(
            ()-> assertEquals(5,  testList.get(0), "Element at index 0 should be 5"),
            ()-> assertEquals(10, testList.get(1), "Element at index 1 should be 10"),
            ()-> assertEquals(15, testList.get(2), "Element at index 2 should be 15")
        );
    }

    @Test
    @DisplayName("Test Get Invalid Index")
    void testGetInvalidIndex() {
        testList.add(1);

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
        testList.add(100);
        testList.add(200);
        int oldValue = testList.set(1, 250);
        assertAll(
            ()-> assertEquals(200, oldValue,        "Old value at index 1 should be 200"),
            ()-> assertEquals(250, testList.get(1), "New value at index 1 should be 250")
        );
    }

    @Test
    @DisplayName("Test Set Invalid Index")
    void testSetInvalidIndex() {
        testList.add(1);

        assertAll("Invalid index set operations",
                // Negative index
                () -> assertThrows(
                        IndexOutOfBoundsException.class,
                        () -> testList.set(-1, 10),
                        "Setting element at negative index should throw IndexOutOfBoundsException"
                ),
                // Index equal to size
                () -> assertThrows(
                        IndexOutOfBoundsException.class,
                        () -> testList.set(1, 10),
                        "Setting element at index equal to size should throw IndexOutOfBoundsException"
                )
        );
    }

    // =========================== Remove Methods Tests =========================== //
    @Test
    @DisplayName("Test Remove by Index")
    void testRemoveByIndex() {
        testList.add(10);
        testList.add(20);
        testList.add(30);
        int removed = testList.remove(1);

        assertAll("Remove element by index",
                () -> assertEquals(20, removed,         "Removed element should be 20"),
                () -> assertEquals(2,  testList.size(), "Size should be 2 after removal"),
                () -> assertEquals(10, testList.get(0), "Element at index 0 should be 10"),
                () -> assertEquals(30, testList.get(1), "Element at index 1 should be 30")
        );
    }

    @ParameterizedTest
    @DisplayName("Test Remove First Element by Index with Various List Sizes")
    @ValueSource(ints = {2, 3, 4}) // Different list sizes
    void testRemoveFirstElementByIndex(int initialSize) {
        // Populate list with initialSize elements
        for (int i = 0; i < initialSize; i++) {
            testList.add(50 + i * 10); // Adds 50, 60, 70, etc.
        }

        // Remove first element
        int removedFirst = testList.remove(0);

        assertAll("Remove first element",
            () -> assertEquals(50, removedFirst, "Removed first element should be 50"),
            () -> assertEquals(initialSize - 1, testList.size(), "Size should decrease by 1 after removal"),
            () -> assertEquals(60, testList.get(0), "Element at index 0 should now be 60")
        );
    }

    @ParameterizedTest
    @DisplayName("Test Remove Last Element by Index with Various List Sizes")
    @ValueSource(ints = {2, 3, 4}) // Different list sizes
    void testRemoveLastElementByIndex(int initialSize) {
        // Populate list with initialSize elements
        for (int i = 0; i < initialSize; i++) {
            testList.add(50 + i * 10); // Adds 50, 60, 70, etc.
        }

        // Remove last element
        int removedLast = testList.remove(initialSize - 1);

        assertAll("Remove last element",
            () -> assertEquals(50 + (initialSize - 1) * 10, removedLast, "Removed last element should be " + (50 + (initialSize - 1) * 10)),
            () -> assertEquals(initialSize - 1, testList.size(), "Size should decrease by 1 after removal"),
            () -> assertEquals(50, testList.get(0), "Element at index 0 should still be 50")
        );
    }

    @Test
    @DisplayName("Test Remove by Index Invalid")
    void testRemoveByIndexInvalid() {
        testList.add(1);

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
        testList.add(5);
        testList.add(10);
        testList.add(15);
        boolean removed = testList.removeElement(10);

        assertAll("Remove existing element",
                () -> assertTrue(removed,               "removeElement should return true when element is removed"),
                () -> assertEquals(2,  testList.size(), "Size should be 2 after removal"),
                () -> assertEquals(5,  testList.get(0), "Element at index 0 should be 5"),
                () -> assertEquals(15, testList.get(1), "Element at index 1 should be 15")
        );
    }

    @Test
    @DisplayName("Test Remove Non-Existing Element")
    void testRemoveElementNonExisting() {
        testList.add(1);
        testList.add(2);
        boolean removed = testList.removeElement(3);

        assertAll("Remove non-existing element",
                () -> assertFalse(removed,             "removeElement should return false when element is not found"),
                () -> assertEquals(2, testList.size(), "Size should remain unchanged when element is not found")
        );
    }

    // =========================== Clear Method Test =========================== //
    @Test
    @DisplayName("Test Clear Method")
    void testClear() {
        testList.add(100);
        testList.add(200);
        testList.add(300);
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
        testList.add(7);
        testList.add(14);
        testList.add(21);
        assertTrue(testList.contains(14), "List should contain 14");
    }

    @Test
    @DisplayName("Test Contains - Element Absent")
    void testContainsFalse() {
        testList.add(7);
        testList.add(14);
        testList.add(21);
        assertFalse(testList.contains(10), "List should not contain 10");
    }

    // =========================== IndexOf Method Tests =========================== //
    @Test
    @DisplayName("Test IndexOf Existing Element")
    void testIndexOfExistingElement() {
        testList.add(5);
        testList.add(10);
        testList.add(15);
        testList.add(10);
        assertEquals(1, testList.indexOf(10), "indexOf should return first occurrence index");
    }

    @Test
    @DisplayName("Test IndexOf Non-Existing Element")
    void testIndexOfNonExistingElement() {
        testList.add(1);
        testList.add(2);
        testList.add(3);
        assertEquals(-1, testList.indexOf(4), "indexOf should return -1 for non-existing element");
    }

    // =========================== ToArray Method Test =========================== //
    @Test
    @DisplayName("Test toArray Method")
    void testToArray() {
        testList.add(100);
        testList.add(200);
        testList.add(300);
        int[] array = testList.toArray();
        assertArrayEquals(new int[]{100, 200, 300}, array, "toArray should return correct array");
    }

    @Test
    @DisplayName("Test toArray on Empty List")
    void testToArrayEmptyList() {
        int[] array = testList.toArray();
        assertArrayEquals(new int[]{}, array, "toArray should return empty array for empty list");
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
        testList.add(10);
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
        testList.add(1);
        testList.add(2);
        testList.add(3);
        assertEquals("[1, 2, 3]", testList.toString(), "toString should return correct string representation");
    }

    // =========================== Capacity Expansion Test =========================== //
    @ParameterizedTest
    @ValueSource(ints = {0, 1, 10, 100})
    @DisplayName("Test Capacity Expansion")
    void testCapacityExpansion(int startingSize) {
        IntList list = new IntList(startingSize);
        
        // Adding more elements than initial capacity to test expansion
        for (int i = 0; i < 20; i++) {
            list.add(i);
        }
        assertEquals(20, list.size(), "Size should be 20 after adding 20 elements");
        for (int i = 0; i < 20; i++) {
            assertEquals(i, list.get(i), "Element at index " + i + " should be " + i);
        }
    }

    // =========================== Fill Method Tests =========================== //
    @Test
    @DisplayName("Test Fill Method on Non-Empty List")
    void testFill() {
        testList.add(1);
        testList.add(2);
        testList.add(3);
        testList.fill(9);

        assertEquals(3, testList.size(), "Size should remain the same after fill");
        for (int i = 0; i < testList.size(); i++) {
            assertEquals(9, testList.get(i), "Element at index " + i + " should be 9");
        }
    }

    @Test
    @DisplayName("Test Fill Method on Empty List")
    void testFillEmptyList() {
        testList.fill(10);

        assertEquals(0, testList.size(), "Size should remain 0 after fill on empty list");
        assertTrue(testList.isEmpty(), "List should remain empty after fill");
    }

    @ParameterizedTest
    @ValueSource(ints = {0, -1, 42, Integer.MAX_VALUE, Integer.MIN_VALUE})
    @DisplayName("Test Fill with Various Values")
    void testFillWithVariousValues(int value) {
        testList.add(10);
        testList.add(20);
        testList.add(30);

        testList.fill(value);

        for (int i = 0; i < testList.size(); i++) {
            assertEquals(value, testList.get(i), "Element at index " + i + " should be " + value);
        }
    }

    // =========================== Additional Edge Cases =========================== //
    @Test
    @DisplayName("Test Add All Elements and Clear")
    void testAddAllElementsAndClear() {
        for (int i = 0; i < 100; i++) {
            testList.add(i);
        }

        assertEquals(100, testList.size(), "Size should be 100 after adding 100 elements");
        testList.clear();
        assertEquals(0, testList.size(), "Size should be 0 after clearing the list");
        assertTrue(testList.isEmpty(), "List should be empty after clearing");
    }

    @Test
    @DisplayName("Test Remove All Elements One by One")
    void testRemoveAllElementsOneByOne() {
        testList.add(10);
        testList.add(20);
        testList.add(30);
        testList.add(40);

        assertAll("Remove elements and check size step by step",
                () -> assertEquals(4,  testList.size(),    "Initial size should be 4"),
                () -> assertEquals(10, testList.remove(0), "Removed element should be 10"),
                () -> assertEquals(3,  testList.size(),    "Size should be 3 after removal"),
                () -> assertEquals(20, testList.remove(0), "Removed element should be 20"),
                () -> assertEquals(2,  testList.size(),    "Size should be 2 after removal"),
                () -> assertEquals(30, testList.remove(0), "Removed element should be 30"),
                () -> assertEquals(1,  testList.size(),    "Size should be 1 after removal"),
                () -> assertEquals(40, testList.remove(0), "Removed element should be 40"),
                () -> assertEquals(0,  testList.size(),    "Size should be 0 after removal"),
                () -> assertTrue(testList.isEmpty(),       "List should be empty after removing all elements")
        );
    }

    @Test
    @DisplayName("Test Add After Clear")
    void testAddAfterClear() {
        testList.add(100);
        testList.add(200);
        testList.clear();
        testList.add(300);

        assertAll("Add after clearing the list",
                () -> assertEquals(1, testList.size(), "Size should be 1 after adding after clear"),
                () -> assertEquals(300, testList.get(0), "Element at index 0 should be 300")
        );
    }

    @Test
    @DisplayName("Test toArray Does Not Expose Internal Data")
    void testToArrayIsolation() {
        testList.add(1);
        testList.add(2);
        testList.add(3);

        int[] array = testList.toArray();
        array[0] = 100; // Modify the returned array

        assertEquals(1, testList.get(0), "Modifying the returned array should not affect the IntList");
    }

    @Test
    @DisplayName("Test Large Number of Elements")
    void testLargeNumberOfElements() {
        int largeSize = 1000000;
        IntList largeList = new IntList();

        for (int i = 0; i < largeSize; i++) {
            largeList.add(i);
        }

        assertEquals(largeSize, largeList.size(), "Size should be " + largeSize + " after adding " + largeSize + " elements");

        // Verify a few random elements
        assertEquals(0, largeList.get(0), "First element should be 0");
        assertEquals(largeSize - 1, largeList.get(largeSize - 1), "Last element should be " + (largeSize - 1));
        assertEquals(500000, largeList.get(500000), "Middle element should be 500000");
    }

    @ParameterizedTest(name = "Test Add with startingCapacity={0} and element={1}")
    @CsvSource({
        "0, 10",
        "1, 20",
        "10, 30",
        "100, 40"
    })
    @DisplayName("Test Add Element with Various Starting Capacities and Elements")
    void testAddElementWithVariousParameters(int startingCapacity, int element) {
        IntList list = new IntList(startingCapacity);
        assertTrue(list.add(element), "Adding an element should return true");
        assertEquals(1, list.size(), "Size should be 1 after adding an element");
        assertEquals(element, list.get(0), "Element at index 0 should match the added element");
    }

    // ============================[Add All Method Tests]============================ //
    @Test
    @DisplayName("Test addAll with Non-Empty Array on Empty List")
    void testAddAllWithNonEmptyArrayOnEmptyList() {
        int[] elementsToAdd = {10, 20, 30};
        testList.addAll(elementsToAdd);

        assertAll("AddAll with non-empty array on empty list",
                () -> assertEquals(3, testList.size(), "Size should be 3 after adding 3 elements"),
                () -> assertEquals(10, testList.get(0), "Element at index 0 should be 10"),
                () -> assertEquals(20, testList.get(1), "Element at index 1 should be 20"),
                () -> assertEquals(30, testList.get(2), "Element at index 2 should be 30")
        );
    }

    @Test
    @DisplayName("Test addAll with Non-Empty Array on Non-Empty List")
    void testAddAllWithNonEmptyArrayOnNonEmptyList() {
        testList.add(1);
        testList.add(2);
        int[] elementsToAdd = {3, 4, 5};
        testList.addAll(elementsToAdd);

        assertAll("AddAll with non-empty array on non-empty list",
                () -> assertEquals(5, testList.size(), "Size should be 5 after adding 3 elements"),
                () -> assertEquals(1, testList.get(0), "Element at index 0 should be 1"),
                () -> assertEquals(2, testList.get(1), "Element at index 1 should be 2"),
                () -> assertEquals(3, testList.get(2), "Element at index 2 should be 3"),
                () -> assertEquals(4, testList.get(3), "Element at index 3 should be 4"),
                () -> assertEquals(5, testList.get(4), "Element at index 4 should be 5")
        );
    }

    @Test
    @DisplayName("Test addAll with Empty Array")
    void testAddAllWithEmptyArray() {
        testList.add(100);
        testList.add(200);
        int[] emptyArray = {};
        testList.addAll(emptyArray);

        assertAll("AddAll with empty array",
                () -> assertEquals(2, testList.size(), "Size should remain 2 after adding empty array"),
                () -> assertEquals(100, testList.get(0), "Element at index 0 should be 100"),
                () -> assertEquals(200, testList.get(1), "Element at index 1 should be 200")
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
        IntList smallList = new IntList(5);
        for (int i = 0; i < 5; i++) {
            smallList.add(i);
        }

        // Add an array that exceeds the current capacity
        int[] largeArray = {5, 6, 7, 8, 9, 10};
        smallList.addAll(largeArray);

        assertAll("AddAll triggering capacity expansion",
                () -> assertEquals(11, smallList.size(), "Size should be 11 after adding 6 elements"),
                () -> assertEquals(0, smallList.get(0), "Element at index 0 should be 0"),
                () -> assertEquals(10, smallList.get(10), "Element at index 10 should be 10"),
                () -> assertTrue(smallList.array().length >= 11, "Internal array should have expanded to accommodate new elements")
        );
    }

    @Test
    @DisplayName("Test addAll on Initially Empty List")
    void testAddAllOnInitiallyEmptyList() {
        int[] elementsToAdd = {100, 200, 300, 400};
        testList.addAll(elementsToAdd);

        assertAll("AddAll on initially empty list",
                () -> assertEquals(4, testList.size(), "Size should be 4 after adding 4 elements"),
                () -> assertEquals(100, testList.get(0), "Element at index 0 should be 100"),
                () -> assertEquals(200, testList.get(1), "Element at index 1 should be 200"),
                () -> assertEquals(300, testList.get(2), "Element at index 2 should be 300"),
                () -> assertEquals(400, testList.get(3), "Element at index 3 should be 400")
        );
    }

    @Test
    @DisplayName("Test addAll with Very Large Array")
    void testAddAllWithVeryLargeArray() {
        int[] largeArray = new int[1000];
        for (int i = 0; i < largeArray.length; i++) {
            largeArray[i] = i;
        }
        testList.addAll(largeArray);

        assertAll("AddAll with very large array",
                () -> assertEquals(1000, testList.size(), "Size should be 1000 after adding 1000 elements"),
                () -> assertEquals(0, testList.get(0), "Element at index 0 should be 0"),
                () -> assertEquals(999, testList.get(999), "Element at index 999 should be 999"),
                () -> assertTrue(testList.array().length >= 1000, "Internal array should have sufficient capacity after adding large array")
        );
    }

    @Test
    @DisplayName("Test addAll with Multiple Sequential Adds")
    void testAddAllWithMultipleSequentialAdds() {
        int[] firstBatch = {1, 2, 3};
        int[] secondBatch = {4, 5};
        int[] thirdBatch = {6, 7, 8, 9};

        testList.addAll(firstBatch);
        testList.addAll(secondBatch);
        testList.addAll(thirdBatch);

        assertAll("AddAll with multiple sequential adds",
                () -> assertEquals(9, testList.size(), "Size should be 9 after adding all batches"),
                () -> assertArrayEquals(new int[]{1, 2, 3, 4, 5, 6, 7, 8, 9}, testList.toArray(), "All elements should be correctly added in order")
        );
    }

    @Test
    @DisplayName("Test addAll and Verify No Data Loss")
    void testAddAllAndVerifyNoDataLoss() {
        int[] elementsToAdd = {5, 10, 15, 20, 25};
        testList.addAll(elementsToAdd);

        for (int i = 0; i < elementsToAdd.length; i++) {
            assertEquals(elementsToAdd[i], testList.get(i), "Element at index " + i + " should be " + elementsToAdd[i]);
        }
        assertEquals(elementsToAdd.length, testList.size(), "Size should match the number of added elements");
    }

    @Test
    @DisplayName("Test addAll When List is Near Capacity")
    void testAddAllWhenListIsNearCapacity() {
        // Initialize list with capacity 10 and add 9 elements
        IntList nearCapacityList = new IntList(10);
        for (int i = 0; i < 9; i++) {
            nearCapacityList.add(i);
        }

        // Add an array of 5 elements to exceed the current capacity
        int[] elementsToAdd = {9, 10, 11, 12, 13};
        nearCapacityList.addAll(elementsToAdd);

        assertAll("AddAll when list is near capacity",
                () -> assertEquals(14, nearCapacityList.size(), "Size should be 14 after adding 5 elements"),
                () -> assertEquals(9, nearCapacityList.get(9), "Element at index 9 should be 9"),
                () -> assertEquals(13, nearCapacityList.get(13), "Element at index 13 should be 13"),
                () -> assertTrue(nearCapacityList.array().length >= 14, "Internal array should have expanded to accommodate new elements")
        );
    }

    
    // =========================== RemoveAll Method Tests =========================== //
    @Test
    @DisplayName("Test RemoveAll Existing Elements")
    void testRemoveAllExistingElements() {
        testList.add(10);
        testList.add(20);
        testList.add(30);
        testList.add(20);
        testList.add(40);

        int[] elementsToRemove = {20, 30};
        testList.removeAll(elementsToRemove);

        assertAll("RemoveAll existing elements",
                () -> assertEquals(2, testList.size(), "Size should be 2 after removals"),
                () -> assertEquals(10, testList.get(0), "Element at index 0 should be 10"),
                () -> assertEquals(40, testList.get(1), "Element at index 1 should be 40")
        );
    }

    @Test
    @DisplayName("Test RemoveAll Non-Existing Elements")
    void testRemoveAllNonExistingElements() {
        testList.add(10);
        testList.add(20);

        int[] elementsToRemove = {30, 40};
        testList.removeAll(elementsToRemove);

        assertAll("RemoveAll non-existing elements",
                () -> assertEquals(2, testList.size(), "Size should remain 2 after removals"),
                () -> assertEquals(10, testList.get(0), "Element at index 0 should be 10"),
                () -> assertEquals(20, testList.get(1), "Element at index 1 should be 20")
        );
    }

    @Test
    @DisplayName("Test RemoveAll with Empty Array")
    void testRemoveAllWithEmptyArray() {
        testList.add(10);
        testList.add(20);

        int[] emptyArray = {};
        testList.removeAll(emptyArray);

        assertAll("RemoveAll with empty array",
                () -> assertEquals(2, testList.size(), "Size should remain 2 after removing empty array"),
                () -> assertEquals(10, testList.get(0), "Element at index 0 should be 10"),
                () -> assertEquals(20, testList.get(1), "Element at index 1 should be 20")
        );
    }

    @Test
    @DisplayName("Test RemoveAll with Null Array")
    void testRemoveAllWithNullArray() {
        testList.add(10);
        int[] nullArray = null;

        assertThrows(NullPointerException.class, () -> {
            testList.removeAll(nullArray);
        }, "Removing with a null array should throw NullPointerException");
    }

    @Test
    @DisplayName("Test RemoveAll Duplicate Elements")
    void testRemoveAllDuplicateElements() {
        testList.add(10);
        testList.add(20);
        testList.add(20);
        testList.add(30);
        testList.add(20);

        int[] elementsToRemove = {20};
        testList.removeAll(elementsToRemove);

        assertAll("RemoveAll duplicate elements",
                () -> assertEquals(2, testList.size(), "Size should be 2 after removals"),
                () -> assertEquals(10, testList.get(0), "Element at index 0 should be 10"),
                () -> assertEquals(30, testList.get(1), "Element at index 1 should be 30")
        );
    }

    @Test
    @DisplayName("Test RemoveAll All Elements")
    void testRemoveAllAllElements() {
        testList.add(10);
        testList.add(20);
        testList.add(30);

        int[] elementsToRemove = {10, 20, 30};
        testList.removeAll(elementsToRemove);

        assertAll("RemoveAll all elements",
                () -> assertEquals(0, testList.size(), "Size should be 0 after removing all elements"),
                () -> assertTrue(testList.isEmpty(), "List should be empty after removing all elements")
        );
    }

    @Test
    @DisplayName("Test RemoveAll with Overlapping Elements")
    void testRemoveAllWithOverlappingElements() {
        testList.add(10);
        testList.add(20);
        testList.add(30);
        testList.add(40);

        int[] elementsToRemove = {20, 50};
        testList.removeAll(elementsToRemove);

        assertAll("RemoveAll with overlapping elements",
                () -> assertEquals(3, testList.size(), "Size should be 3 after removals"),
                () -> assertEquals(10, testList.get(0), "Element at index 0 should be 10"),
                () -> assertEquals(30, testList.get(1), "Element at index 1 should be 30"),
                () -> assertEquals(40, testList.get(2), "Element at index 2 should be 40")
        );
    }

    // =========================== Shrinkage Method Tests =========================== //
    @Test
    @DisplayName("Test Capacity Shrinking")
    void testCapacityShrinking() {
        int initialCapacity = 100;
        IntList list = new IntList(initialCapacity);

        // Add elements to fill the list beyond shrink threshold
        for (int i = 0; i < 75; i++) { // 75% of initialCapacity
            list.add(i);
        }
        assertEquals(75, list.size(), "Size should be 75 after additions");

        // Now removing elements to drop below 25% of capacity (which is 4)
        for (int i = 0; i < 51; i++) {
            list.remove(0);
        }
        assertEquals(24, list.size(), "Size should be 24 after removals");

        // Assuming shrinkThreshold is 25, after size drops to 24, capacity should shrink
        int[] internalArray = list.array();
        assertTrue(internalArray.length < initialCapacity,       "Internal array should have shrunk");
        assertTrue(internalArray.length >= IntList.MIN_CAPACITY, "Internal array should not shrink below MIN_CAPACITY");
    }
    
    @Test
    @DisplayName("Test Shrinking To Mininmum Capacity")
    void testShrinkUntilMinCapacity() {
        int initialCapacity = 100;
        IntList list = new IntList(initialCapacity);
        for (int i = 0; i < initialCapacity; i++) {
            list.add(i);
        }
        assertEquals(initialCapacity, list.size(), String.format("Size should be %d after %d additions", initialCapacity, initialCapacity));
        
        for (int i = 0; i < initialCapacity; i++) {
            list.removeElement(i);
        }
        assertEquals(0, list.size(), String.format("Size should be 0 after %d removals", initialCapacity));
        assertEquals(IntList.MIN_CAPACITY, list.array().length, String.format("Array capacity should be %d after all elements removed", IntList.MIN_CAPACITY));
    }
    
    @Test
    @DisplayName("Test SetShrinkPercentage Method")
    void testSetShrinkPercentage() {
        IntList list = new IntList(100);

        // Add elements to set up initial state
        for (int i = 0; i < 75; i++) {
            list.add(i);
        }

        // Change shrink percentage to 50%
        list.setShrinkPercentage(50);
        assertEquals(50, list.getShrinkPercentage(), "Shrink percentage should be updated to 50%");

        // Remove elements to trigger shrinking at 50%
        for (int i = 0; i < 26; i++) { // Size goes from 15 to 7
            list.remove(0);
        }
        assertEquals(49, list.size(), "Size should be 49 after removals");

        // Verify that internal capacity has shrunk appropriately (50% of 100 initially)
        // Since initial capacity was 100 and possibly increased, adjust accordingly
        int[] internalArray = list.array();
        assertEquals(50, internalArray.length, "Internal array should have shrunk based on new shrink percentage");
        assertTrue(internalArray.length >= IntList.MIN_CAPACITY, "Internal array should not shrink below MIN_CAPACITY");
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

    // ============================== Pop Method Tests ============================= //
    @Test
    void pop_ShouldReturnLastElement_WhenListIsNotEmpty() {
        testList.add(1);
        testList.add(2);
        testList.add(3);

        float result = testList.pop();

        assertEquals(3.0f, result, "The last element should be 3.0f");
        assertEquals(2, testList.size(), "The size should decrease by one");
    }

    @Test
    void pop_ShouldRemoveLastElement_WhenListIsNotEmpty() {
        testList.add(10);
        testList.add(20);
        testList.add(30);

        testList.pop();

        assertEquals(2, testList.size(), "The list size should decrease by one after pop");
        assertEquals(20, testList.get(testList.size() - 1), "The new last element should be 20.0f");
    }

    @Test
    void pop_ShouldThrowException_WhenListIsEmpty() {
        assertThrows(IndexOutOfBoundsException.class, () -> {
            testList.pop();
        }, "Popping from an empty list should throw IndexOutOfBoundsException");
    }

    @Test
    void pop_ShouldReturnOnlyElement_WhenListHasOneElement() {
        testList.add(5);

        float result = testList.pop();

        assertEquals(5.0f, result, "The only element in the list should be 5.0f");
        assertTrue(testList.isEmpty(), "The list should be empty after popping the only element");
    }

    @Test
    void pop_ShouldWorkCorrectly_WhenListIsResized() {
        for (int i = 0; i < 100; i++) {
            testList.add(i * 1);
        }

        float result = testList.pop();

        assertEquals(99.0f, result, "The last element should be 99.0f");
        assertEquals(99, testList.size(), "The list size should be 99 after popping one element");
    }
        
}