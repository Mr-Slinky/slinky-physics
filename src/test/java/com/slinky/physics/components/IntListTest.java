package com.slinky.physics.components;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.DisplayName;

import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;

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
                () -> assertTrue(list.isEmpty(), "List should be empty")
        );
    }

    @ParameterizedTest
    @ValueSource(ints = {0, 1, 10, 100})
    @DisplayName("Test Parameterized Constructor with Various Capacities")
    void testParameterizedConstructor(int size) {
        IntList customList = new IntList(size);
        assertAll("Parameterized constructor",
                () -> assertNotNull(customList, "Custom list should not be null"),
                () -> assertEquals(0, customList.size(), "Initial size should be 0"),
                () -> assertTrue(customList.isEmpty(), "Custom list should be empty")
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

    // Removed tests that expect exceptions from constructors as data validation is skipped

    // =========================== Add Methods Tests =========================== //

    @ParameterizedTest
    @ValueSource(ints = {0, 1, 10, 100})
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
    @ValueSource(ints = {0, 1, 10, 100})
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

    @Test
    @DisplayName("Test Insert Element at Boundary Indices")
    void testAddAtIndexBoundary() {
        IntList list = new IntList();
        
        list.add(100);
        list.add(200);

        // Adding at index equal to size (end of the list)
        list.insert(2, 300);
        assertEquals(3, list.size(), "Size should be 3 after adding at the end");
        assertEquals(300, list.get(2), "Element at index 2 should be 300");

        // Adding at the beginning
        list.insert(0, 50);
        assertEquals(4, list.size(), "Size should be 4 after adding at the beginning");
        assertEquals(50, list.get(0), "Element at index 0 should be 50");
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
        assertEquals(5, testList.get(0),  "Element at index 0 should be 5");
        assertEquals(10, testList.get(1), "Element at index 1 should be 10");
        assertEquals(15, testList.get(2), "Element at index 2 should be 15");
    }

    @Test
    @DisplayName("Test Get Invalid Index")
    void testGetInvalidIndex() {
        testList.add(1);

        // Negative index
        assertThrows(IndexOutOfBoundsException.class, () -> {
            testList.get(-1);
        }, "Getting element at negative index should throw IndexOutOfBoundsException");

        // Index equal to size
        assertThrows(IndexOutOfBoundsException.class, () -> {
            testList.get(1);
        }, "Getting element at index equal to size should throw IndexOutOfBoundsException");
    }

    // =========================== Set Method Tests =========================== //

    @Test
    @DisplayName("Test Set Valid Index")
    void testSetValidIndex() {
        testList.add(100);
        testList.add(200);
        int oldValue = testList.set(1, 250);
        assertEquals(200, oldValue, "Old value at index 1 should be 200");
        assertEquals(250, testList.get(1), "New value at index 1 should be 250");
    }

    @Test
    @DisplayName("Test Set Invalid Index")
    void testSetInvalidIndex() {
        testList.add(1);

        // Negative index
        assertThrows(
                IndexOutOfBoundsException.class,
                () -> testList.set(-1, 10),
                "Setting element at negative index should throw IndexOutOfBoundsException"
        );

        // Index equal to size
        assertThrows(
                IndexOutOfBoundsException.class,
                () -> testList.set(1, 10),
                "Setting element at index equal to size should throw IndexOutOfBoundsException"
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
                () -> assertEquals(20, removed, "Removed element should be 20"),
                () -> assertEquals(2, testList.size(), "Size should be 2 after removal"),
                () -> assertEquals(10, testList.get(0), "Element at index 0 should be 10"),
                () -> assertEquals(30, testList.get(1), "Element at index 1 should be 30")
        );
    }

    @Test
    @DisplayName("Test Remove by Index at Boundaries")
    void testRemoveByIndexBoundary() {
        testList.add(50);
        testList.add(60);

        // Remove first element
        int removedFirst = testList.remove(0);
        assertEquals(50, removedFirst, "Removed first element should be 50");
        assertEquals(1, testList.size(), "Size should be 1 after removal");
        assertEquals(60, testList.get(0), "Element at index 0 should now be 60");

        // Remove last element
        int removedLast = testList.remove(0);
        assertEquals(60, removedLast, "Removed last element should be 60");
        assertEquals(0, testList.size(), "Size should be 0 after removal");
        assertTrue(testList.isEmpty(), "List should be empty after removing all elements");
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

        assertEquals(4, testList.size(), "Initial size should be 4");

        assertEquals(10, testList.remove(0), "Removed element should be 10");
        assertEquals(3, testList.size(), "Size should be 3 after removal");

        assertEquals(20, testList.remove(0), "Removed element should be 20");
        assertEquals(2, testList.size(), "Size should be 2 after removal");

        assertEquals(30, testList.remove(0), "Removed element should be 30");
        assertEquals(1, testList.size(), "Size should be 1 after removal");

        assertEquals(40, testList.remove(0), "Removed element should be 40");
        assertEquals(0, testList.size(), "Size should be 0 after removal");

        assertTrue(testList.isEmpty(), "List should be empty after removing all elements");
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

}