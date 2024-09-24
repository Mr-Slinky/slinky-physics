package com.slinky.physics.util;

import java.util.Arrays;

import static java.lang.Integer.max;

/**
 * A self-resizing flat array of integers designed for use within the
 * {@code com.slinky.physics.components} package of the ECS-based physics
 * engine. This class optimises memory layout and access by avoiding object
 * overhead and operating directly on primitive arrays.
 *
 * <p>
 * The {@code IntList} automatically grows to accommodate additional elements
 * and shrinks when its size falls below 25% of its current capacity. Its
 * memory-efficient design is aligned with the broader system's data-oriented
 * architecture, ensuring high performance in scenarios involving frequent
 * resizing of integer-based components.
 * </p>
 *
 * <p>
 * This class is not intended to implement the {@link java.util.List} interface,
 * as it focuses purely on optimising low-level operations within the ECS
 * framework. It plays a key role in efficiently managing large collections of
 * entities and components, where minimal object overhead and fast access
 * patterns are critical.
 * </p>
 *
 * <h2>Usage Example</h2>
 * <pre><code>
 * IntList entityIds = new IntList();
 * entityIds.add(5);
 * entityIds.add(10);
 * int firstEntity = entityIds.get(0); // Returns 5
 * </code></pre>
 *
 * <h2>Resizing Behaviour</h2>
 * <p>
 * The list increases by halve when its capacity when full and contracts to half
 * its size when it falls below 25% of its capacity, ensuring efficient memory
 * usage across varying workloads. The minimum capacity is defined by
 * {@link #MIN_CAPACITY}.
 * </p>
 *
 * <h2>Thread Safety</h2>
 * <p>
 * This class is not thread-safe. If multiple threads access an instance of
 * {@code IntList} concurrently and at least one thread modifies the list,
 * synchronization must be handled externally.
 * </p>
 *
 * @version  2.0
 * @since    0.1.0
 *
 * @author   Kheagen Haskins
 */
public final class IntList {

    // ============================== Static ================================ //
    /**
     * The default initial capacity of the list, used when no specific capacity
     * is provided by the user. This constant defines the minimum size for the
     * internal array buffer to prevent unnecessary memory reallocations when
     * the list is expected to remain small. The value is chosen to align with
     * typical cache line sizes for performance optimisation. Given that each
     * `int` occupies 4 bytes and most cache lines are 64 bytes, a capacity of
     * 16 ensures that a full cache line can be utilised efficiently (64 bytes /
     * 4 bytes per `int` = 16 `int` values).
     */
    public static final int MIN_CAPACITY = 16;

    // ============================== Fields ================================ //
    /**
     * The internal array buffer that stores the elements of this list. The 
     * length of this array defines the current capacity of the list, 
     * which automatically grows or shrinks based on the list's size.
     */
    private int[] data; 

    /**
     * The current number of elements stored in the list. This value represents
     * the actual size of the list, not the capacity, which refers to the 
     * length of the {@code data} array.
     */
    private int size;
    
    /**
     * The threshold at which the list shrinks its capacity. If the size of 
     * the list falls below this threshold, the internal array buffer is 
     * halved to reclaim unused memory. This value is calculated as a 
     * percentage of the current capacity.
     */
    private int shrinkThreshold;
    
    /**
     * The percentage at which the list shrinks relative to its current 
     * capacity. For example, a value of 25% means the list will shrink when 
     * its size falls below 25% of its capacity.
     */
    private int shrinkPercentage = 25;

    // =========================== Constructors ============================= //
    /**
     * Constructs an empty list with the specified initial capacity. 
     * This constructor is useful when the expected size of the list is known 
     * in advance, preventing unnecessary resizing operations.
     *
     * @param initialCapacity the initial capacity of the list
     * @throws IllegalArgumentException if the specified initial capacity is
     * negative
     */
    public IntList(int initialCapacity) {
        if (initialCapacity < 0) {
            throw new IllegalArgumentException("Illegal Capacity: " + initialCapacity);
        }
        initialCapacity      = Math.max(MIN_CAPACITY, initialCapacity);
        this.data            = new int[initialCapacity];
        this.shrinkThreshold = initialCapacity * shrinkPercentage / 100;
        this.size            = 0;
    }

    /**
     * Constructs an empty list with an initial capacity of {@link #MIN_CAPACITY}.
     * This default constructor is designed for situations where the expected 
     * size of the list is unknown or relatively small.
     */
    public IntList() {
        this(MIN_CAPACITY);
    }

    /**
     * Constructs a list containing the elements of the specified array. 
     * The list will have an initial capacity that matches the length of the 
     * provided array, and the elements will be added in the order they appear 
     * in the array.
     *
     * @param array the array whose elements are to be placed into this list
     * @throws NullPointerException if the specified array is null
     */
    public IntList(int[] array) {
        if (array == null) {
            throw new NullPointerException("Input array cannot be null");
        }

        this.data = array.clone();
        this.shrinkThreshold = data.length * shrinkPercentage / 100;
        this.size = array.length;
    }

    // ============================== Getters =============================== //
    /**
     * Returns the element at the specified position in this list.
     * 
     * <p>
     * This method retrieves the integer stored at the given index, providing 
     * direct access to the list's internal array. The index must be within 
     * the valid range, otherwise, an {@code IndexOutOfBoundsException} will 
     * be thrown.
     * </p>
     *
     * @param index the index of the element to return
     * @return the element at the specified position in this list
     * @throws IndexOutOfBoundsException if the index is out of range
     *         {@code (index < 0 || index >= size())}
     */
    public int get(int index) {
        checkBounds(index);
        return data[index];
    }

    /**
     * Returns the current number of elements in the list.
     *
     * <p>
     * This method provides the number of elements currently stored in the list. 
     * It is distinct from the capacity, which refers to the total number of 
     * available slots in the internal array.
     * </p>
     *
     * @return the number of elements in the list
     */
    public int size() {
        return this.size;
    }
    
    /**
     * Returns the current capacity of the list.
     *
     * <p>
     * This method provides the total number of elements that the internal array
     * can hold before it needs to grow. The capacity refers to the length of the
     * internal array and is distinct from the size of the list, which represents
     * the number of elements actually stored in the list.
     * </p>
     *
     * @return the current capacity of the list
     */
    public int capacity() {
        return data.length;
    }

    /**
     * Returns {@code true} if the list contains no elements.
     *
     * <p>
     * This method checks whether the list is empty by verifying if its size is 
     * zero. It is a quick way to determine if the list contains any elements.
     * </p>
     *
     * @return {@code true} if the list contains no elements, otherwise {@code false}
     */
    public boolean isEmpty() {
        return this.size == 0;
    }

    /**
     * Returns the current shrink percentage used by the list.
     * 
     * <p>
     * This value represents the threshold percentage of capacity at which 
     * the list will shrink. For example, a value of 25% means the list will 
     * shrink when its size falls below 25% of its current capacity.
     * </p>
     *
     * @return the current shrink percentage
     */
    public int getShrinkPercentage() {
        return shrinkPercentage;
    }
    
    /**
     * Exposes the internal array buffer for unit testing and other package-private access.
     * 
     * <p>
     * This method provides direct access to the internal array, bypassing size checks.
     * It is not meant for public use, but for testing or other operations that 
     * need direct access to the data array. Modifications made directly to the array 
     * may corrupt the list, so caution is advised.
     * </p>
     *
     * @return the internal array buffer
     */
    int[] array() {
        return data;
    }
    
    // ============================== Setters =============================== //
    /**
     * Replaces the element at the specified position in this list with the
     * specified element.
     *
     * <p>
     * This method allows for replacing an element at a specific index with a 
     * new value, returning the value previously stored at that position. 
     * Index bounds are checked to ensure the index is valid within the current 
     * list size.
     * </p>
     *
     * @param index the index of the element to replace
     * @param element the element to be stored at the specified position
     * @return the element previously at the specified position
     * @throws IndexOutOfBoundsException if the index is out of range 
     *         {@code (index < 0 || index >= size())}
     */
    public int set(int index, int element) {
        checkBounds(index);
        int oldValue = data[index];
        data[index] = element;
        return oldValue;
    }

    /**
     * Sets the shrink percentage for the list, which determines when 
     * the internal array will contract.
     * 
     * <p>
     * The shrink percentage must be between 10% and 90%, inclusive. 
     * If an invalid value is provided, an {@code IllegalArgumentException} is thrown. 
     * This percentage is used to calculate the threshold at which the list 
     * will shrink its capacity when the size falls below this proportion of the 
     * current capacity.
     * </p>
     *
     * @param shrinkPercentage the new shrink percentage, must be between 10 and 90
     * @throws IllegalArgumentException if the provided shrink percentage is 
     *         not between 10 and 90
     */
    public void setShrinkPercentage(int shrinkPercentage) {
        if (shrinkPercentage < 10 || shrinkPercentage > 90) {
            throw new IllegalArgumentException("Shrink percentage must be between 10 and 90");
        }

        this.shrinkPercentage = shrinkPercentage;
        this.shrinkThreshold  = data.length * shrinkPercentage / 100;
    }

    
    // ============================ API Methods ============================= //

    /**
     * Truncates the list to the specified new size.
     * 
     * <p>
     * This method resizes the internal array to the specified {@code newSize}. 
     * If the new size is smaller than the current size, the list is truncated 
     * and excess elements are discarded. The {@code newSize} must be within the 
     * valid range of {@code 0 <= newSize <= size}. After truncation, the 
     * shrink threshold is recalculated based on the new capacity.
     * </p>
     *
     * @param newSize the new size to set for the list
     * @throws IllegalArgumentException if the specified new size is negative or 
     *         greater than the current size
     */
    public void truncate(int newSize) {
        if (newSize < 0 || newSize > size) {
            throw new IllegalArgumentException("Invalid new size: " + newSize);
        }

        data = Arrays.copyOf(data, newSize);
        size = newSize;
        shrinkThreshold = data.length / 4;
    }

    /**
     * Appends the specified element to the end of this list.
     * 
     * <p>
     * This method adds a new element to the end of the list, expanding 
     * the internal array if necessary to accommodate the new element. The 
     * internal capacity is automatically managed by the {@code ensureCapacity()} 
     * method.
     * </p>
     *
     * @param element the element to be appended to this list
     * @return {@code true} (as specified by {@code Collection.insert})
     */
    public boolean add(int element) {
        ensureCapacity(size + 1);
        data[size++] = element;
        return true;
    }
    
    /**
     * Appends all elements from the specified array to the end of this list.
     *
     * <p>
     * This method adds all elements from the provided array to the list in the 
     * order they appear in the array. The list's capacity is automatically expanded 
     * if necessary to accommodate the additional elements.
     * </p>
     *
     * @param elements the array of elements to be added to the list
     * @throws NullPointerException if the specified {@code elements} array is {@code null}
     */
    public void addAll(int[] elements) {
        if (elements == null) {
            throw new NullPointerException("Input array cannot be null");
        }
        
        ensureCapacity(size + elements.length);
        System.arraycopy(elements, 0, data, size, elements.length);
        size += elements.length;
    }
    
    /**
     * Inserts the specified element at the specified position in this list.
     * 
     * <p>
     * The element at the specified index and any subsequent elements are shifted 
     * to the right to make room for the new element. The internal array grows 
     * if necessary to accommodate the new element. The {@code insert()} operation 
     * may cause elements to be moved, so it can be costly if used frequently in 
     * performance-critical sections of code.
     * </p>
     *
     * @param index the position at which the specified element is to be inserted
     * @param element the element to be inserted
     * @throws IndexOutOfBoundsException if the index is out of range 
     *         {@code (index < 0 || index > size())}
     */
    public void insert(int index, int element) {
        if (index < 0 || index > size) {
            throw new IndexOutOfBoundsException(outOfBoundsMessage(index, true));
        }

        ensureCapacity(size + 1);
        System.arraycopy(data, index, data, index + 1, size - index); 
        data[index] = element;
        size++;
    }

    /**
     * Removes the element at the specified position in this list.
     * 
     * <p>
     * The element at the given index is removed, and any subsequent elements 
     * are shifted to the left to fill the gap. The size of the list is reduced, 
     * and the internal array may be shrunk if the size falls below the shrink 
     * threshold. This operation also returns the value that was removed.
     * </p>
     *
     * @param index the index of the element to be removed
     * @return the element previously at the specified position
     * @throws IndexOutOfBoundsException if the index is out of range 
     *         {@code (index < 0 || index >= size())}
     */
    public int remove(int index) {
        checkBounds(index);
        
        int oldValue = data[index];
        int numMoved = size - index - 1;
        if (numMoved > 0) {
            System.arraycopy(data, index + 1, data, index, numMoved);
        }

        size--;
        shrinkCapacityIfNecessary();

        return oldValue;
    }
    
    /**
     * Removes and returns the last element of the list.
     *
     * <p>
     * This method removes the element at the last position in the list,
     * reducing its size by one. The removed element is returned to the caller.
     * If the list is empty, an {@code IndexOutOfBoundsException} will be
     * thrown.
     * </p>
     *
     * @return the element previously at the last position in the list
     * @throws IndexOutOfBoundsException if the list is empty
     */
    public int pop() {
        return remove(size() - 1);
    }
    
    /**
     * Removes all occurrences of the specified elements from this list.
     *
     * <p>
     * This method removes every occurrence of each element in the provided array 
     * from the list. If any elements are removed, the method returns {@code true}; 
     * otherwise, it returns {@code false}. If an element from the array is not 
     * present in the list, it is ignored.
     * </p>
     *
     * @param elements the array of elements to be removed from the list
     * @return {@code true} if the list was modified as a result of this operation, 
     *         otherwise {@code false}
     */
    public boolean removeAll(int[] elements) {
        boolean modified = false;
        for (int element : elements) {
            while (removeElement(element)) {
                modified = true;
            }
        }
        return modified;
    }

    /**
     * Removes the first occurrence of the specified element from this list, if
     * it is present.
     *
     * <p>
     * This method searches the list for the first occurrence of the specified
     * element and removes it if found. If the element is removed, all
     * subsequent elements are shifted left to fill the gap. If the element is
     * not present, the list remains unchanged.
     * </p>
     *
     * @param element the element to be removed from this list, if present
     * @return {@code true} if the list contained the specified element,
     * otherwise {@code false}
     */
    public boolean removeElement(int element) {
        for (int index = 0; index < size; index++) {
            if (data[index] == element) {
                remove(index);
                return true;
            }
        }

        return false;
    }

    /**
     * Removes all of the elements from this list.
     * 
     * <p>
     * This method clears the list by resetting the internal array to its default 
     * minimum capacity, effectively removing all elements. The list will be empty 
     * after this call, but the internal array is still allocated to the 
     * {@code MIN_CAPACITY}.
     * </p>
     */
    public void clear() {
        data = new int[MIN_CAPACITY];
        size = 0;
        shrinkThreshold = MIN_CAPACITY * shrinkPercentage / 100;
    }

    /**
     * Returns {@code true} if this list contains the specified element.
     * 
     * <p>
     * This method checks if the given element is present in the list by 
     * searching through its contents. If the element is found, the method 
     * returns {@code true}; otherwise, it returns {@code false}.
     * </p>
     *
     * @param element the element whose presence in this list is to be tested
     * @return {@code true} if this list contains the specified element, otherwise {@code false}
     */
    public boolean contains(int element) {
        return indexOf(element) >= 0;
    }

    /**
     * Fills the list with the specified value.
     * 
     * <p>
     * This method assigns the given {@code value} to each element in the 
     * list, replacing all existing values up to the current size. The capacity 
     * of the list is unchanged, but the content is replaced.
     * </p>
     *
     * @param value the value to assign to each element in the list
     */
    public void fill(int value) {
        Arrays.fill(data, 0, size, value);
    }

    /**
     * Returns the index of the first occurrence of the specified element in
     * this list, or {@code -1} if the element is not present.
     * 
     * <p>
     * This method searches the list for the specified element and returns its 
     * index if found. If the element is not present in the list, the method 
     * returns {@code -1}.
     * </p>
     *
     * @param element the element to search for
     * @return the index of the first occurrence of the specified element, or {@code -1} if not found
     */
    public int indexOf(int element) {
        for (int i = 0; i < size; i++) {
            if (data[i] == element) {
                return i;
            }
        }
        
        return -1;
    }

    /**
     * Returns a cloned array containing all of the elements in this list 
     * in proper sequence (from first to last element).
     * 
     * <p>
     * This method creates a new array containing the current elements of the 
     * list, preserving their order. The size of the returned array will match 
     * the size of the list.
     * </p>
     *
     * @return an array containing all of the elements in this list in proper sequence
     */
    public int[] toArray() {
        int[] array = new int[size];
        System.arraycopy(data, 0, array, 0, size);
        return array;
    }

    /**
     * Returns a string representation of the list.
     * 
     * <p>
     * This method provides a string representation of the list, where the 
     * elements are represented in a comma-separated format enclosed in square 
     * brackets (e.g., {@code [1, 2, 3]}). The string includes only the elements 
     * up to the current size of the list.
     * </p>
     *
     * @return a string representation of the list
     */
    @Override
    public String toString() {
        if (size == 0) {
            return "[]";
        }

        StringBuilder sb = new StringBuilder();
        sb.append('[');
        for (int i = 0; i < size; i++) {
            sb.append(data[i]);
            if (i < size - 1) {
                sb.append(", ");
            }
        }
        sb.append(']');
        return sb.toString();
    }

    // ========================== Helper Methods ============================ //
    /**
     * Ensures that the list has enough capacity to hold at least the specified
     * {@code minCapacity}. If the current capacity is insufficient, the internal
     * array is expanded.
     *
     * @param minCapacity the desired minimum capacity
     */
    private void ensureCapacity(int minCapacity) {
        if (minCapacity > data.length) {
            int newCapacity;
            if (data.length == 0) {
                newCapacity = MIN_CAPACITY;
            } else {
                newCapacity = data.length + data.length / 2; // 1.5x growth
                if (newCapacity < minCapacity) {
                    newCapacity = minCapacity;
                }
            }
            data = Arrays.copyOf(data, newCapacity);
            shrinkThreshold = (newCapacity * shrinkPercentage) / 100;
        }
    }
    
    /**
     * Shrinks the list's capacity if necessary, based on the current size 
     * and shrink threshold. The internal array is halved if the size falls 
     * below the shrink threshold and the current capacity is above the 
     * minimum capacity.
     */
    private void shrinkCapacityIfNecessary() {
        if (size < shrinkThreshold && data.length > MIN_CAPACITY) {
            adjustCapacity(max(MIN_CAPACITY, data.length / 2));
        }
    }

    /**
     * Checks if the specified index is within the valid range for the list.
     * If the index is out of bounds, an {@code IndexOutOfBoundsException} is thrown.
     *
     * @param index the index to check
     */
    private void checkBounds(int index) {
        if (index < 0 || index >= size) {
            throw new IndexOutOfBoundsException(outOfBoundsMessage(index, false));
        }
    }
    
    /**
     * Adjusts the list's capacity to the specified {@code newCapacity}. 
     * The new capacity must be greater than or equal to the current size.
     *
     * @param newCapacity the new capacity to set
     * @throws IllegalArgumentException if the new capacity is less than the current size
     */
    private void adjustCapacity(int newCapacity) {
        if (newCapacity < size) {
            throw new IllegalArgumentException("New capacity cannot be less than current size");
        }
        
        data = Arrays.copyOf(data, newCapacity);
        shrinkThreshold = newCapacity * shrinkPercentage / 100;
    }

    /**
     * Constructs an error message for an out-of-bounds index.
     *
     * @param index the index that is out of bounds
     * @param insertion {@code true} if the error occurred during insertion, otherwise {@code false}
     * @return a formatted string describing the out-of-bounds condition
     */
    private String outOfBoundsMessage(int index, boolean insertion) {
        String str = insertion ? "above" : "equal to \\ above";
        return String.format("Index %d cannot be negative, or %s the current size of the IntList (%d)", index, str, size);
    }

}