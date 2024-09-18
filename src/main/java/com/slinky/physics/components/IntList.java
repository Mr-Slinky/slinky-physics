package com.slinky.physics.components;

/**
 * A dynamic array for primitive integers, designed for performance and memory
 * efficiency compared to {@code ArrayList<Integer>}. This class provides
 * similar functionality to {@code ArrayList}, but avoids the overhead of
 * auto-boxing by storing integers in a contiguous memory layout.
 *
 * <p>
 * {@code IntList} allows for dynamic resizing as data are added, maintaining
 * amortized constant time complexity for appends. The internal capacity grows
 * automatically when necessary to accommodate additional data.
 * </p>
 *
 * <h3>Key Features:</h3>
 * <ul>
 * <li>Efficient handling of primitive {@code int} values without boxing</li>
 * <li>Supports typical list operations: insert, remove, get, set, and
 * contains</li>
 * <li>Automatic resizing with a default initial capacity of 10 data</li>
 * <li>Provides indexed access to data</li>
 * <li>Optimised for space and time efficiency in scenarios with frequent
 * integer manipulations</li>
 * </ul>
 *
 * <h3>Usage Example:</h3>
 * <pre><code>
 *    IntList list = new IntList();
 *    list.add(42);
 *    list.add(7);
 *    System.out.println(list.get(0)); // Output: 42
 * </code></pre>
 *
 * <p>
 * This class is not thread-safe and should be externally synchronised if used
 * in a multithreaded environment.
 * </p>
 *
 * @author Kheagen Haskins
 */
public final class IntList {

    // ============================== Static ================================ //
    /**
     * The default initial capacity of the list, used when no capacity is
     * specified.
     */
    public static final int DEFAULT_CAPACITY = 10;

    // ============================== Fields ================================ //
    /**
     * The array buffer where elements are stored. The capacity of the list is
     * the length of this array.
     */
    protected int[] data;

    /**
     * The current number of data in the list.
     */
    private int size;

    // =========================== Constructors ============================= //
    /**
     * Constructs an empty list with the specified initial capacity.
     *
     * @param initialCapacity the initial capacity of the list
     * @throws IllegalArgumentException if the specified initial capacity is
     * negative
     */
    public IntList(int initialCapacity) {
        if (initialCapacity < 0) {
            throw new IllegalArgumentException("Illegal Capacity: " + initialCapacity);
        }

        this.data = new int[initialCapacity];
        this.size = 0;
    }

    /**
     * Constructs an empty list with an initial capacity of ten.
     */
    public IntList() {
        this(DEFAULT_CAPACITY);
    }

    /**
     * Constructs a list containing the data of the specified array, in the
     * order they are returned by the array.
     *
     * @param array the array whose data are to be placed into this list
     * @throws NullPointerException if the specified array is null
     */
    public IntList(int[] array) {
        if (array == null) {
            throw new NullPointerException("Input array cannot be null");
        }

        this.data = array.clone();
        this.size = array.length;
    }

    // ============================== Getters =============================== //
    /**
     * Returns the element at the specified position in this list.
     *
     * @param index index of the element to return
     * @return the element at the specified position in this list
     * @throws IndexOutOfBoundsException if the index is out of range
     * {@code (index < 0 || index >= size())}
     */
    public int get(int index) {
        checkBounds(index);
        return data[index];
    }

    /**
     * Returns the current number of data in the list.
     *
     * @return the number of data in the list
     */
    public int size() {
        return this.size;
    }

    /**
     * Returns true if the list contains no data.
     *
     * @return true if the list contains no data
     */
    public boolean isEmpty() {
        return this.size == 0;
    }

    // ============================== Setters =============================== //
    /**
     * Replaces the element at the specified position in this list with the
     * specified element.
     *
     * @param index index of the element to replace
     * @param element element to be stored at the specified position
     * @return the element previously at the specified position
     * @throws IndexOutOfBoundsException if the index is out of range (index < 0 || index
     * >= size())
     */
    public int set(int index, int element) {
        checkBounds(index);
        int oldValue = data[index];
        data[index] = element;
        return oldValue;
    }

    // ============================ API Methods ============================= //
    /**
     * Appends the specified element to the end of this list.
     *
     * @param element element to be appended to this list
     * @return true (as specified by Collection.insert)
     */
    public boolean add(int element) {
        ensureCapacity(size + 1);
        data[size++] = element;
        return true;
    }

    /**
     * Inserts the specified element at the specified position in this list.
     * Shifts the element currently at that position (if any) and any subsequent
     * elements to the right.
     *
     * @param index index at which the specified element is to be inserted
     * @param element element to be inserted
     * @throws IndexOutOfBoundsException if the index is out of range (index < 0 || index
     * > size())
     */
    public void insert(int index, int element) {
        ensureCapacity(size + 1);
        System.arraycopy(data, index, data, index + 1, size - index);
        data[index] = element;
        size++;
    }

    /**
     * Removes the element at the specified position in this list. Shifts any
     * subsequent elements to the left. Returns the element that was removed
     * from the list.
     *
     * @param index the index of the element to be removed
     * @return the element previously at the specified position
     * @throws IndexOutOfBoundsException if the index is out of range (index < 0 || index
     * >= size())
     */
    public int remove(int index) {
        checkBounds(index);

        int oldValue = data[index];

        int numMoved = size - index - 1;
        if (numMoved > 0) {
            System.arraycopy(data, index + 1, data, index, numMoved);
        }

        size--;
        return oldValue;
    }

    /**
     * Removes the first occurrence of the specified element from this list, if
     * it is present. If the list does not contain the element, it is unchanged.
     *
     * @param eID element to be removed from this list, if present
     * @return true if the list contained the specified element
     */
    public boolean removeElement(int eID) {
        for (int index = 0; index < size; index++) {
            if (data[index] == eID) {
                remove(index);
                return true;
            }
        }
        
        return false;
    }

    /**
     * Removes all of the elements from this list. The list will be empty after
     * this call returns.
     */
    public void clear() {
        // Clear all data to zero
        for (int i = 0; i < size; i++) {
            data[i] = 0;
        }
        size = 0;
    }

    /**
     * Returns true if this list contains the specified element.
     *
     * @param element element whose presence in this list is to be tested
     * @return true if this list contains the specified element
     */
    public boolean contains(int element) {
        return indexOf(element) >= 0;
    }

    /**
     * Fills the entire array with the specified value.
     *
     * <p>
     * This method assigns the given {@code value} to each element in the
     * {@code data} array, effectively replacing all existing values.
     * </p>
     *
     * @param value the value to assign to each element in the array
     */
    public void fill(int value) {
        for (int i = 0; i < data.length; i++) {
            data[i] = value;
        }
    }

    /**
     * Returns the index of the first occurrence of the specified element in
     * this list, or -1 if this list does not contain the element.
     *
     * @param element element to search for
     * @return the index of the first occurrence of the specified element in
     * this list, or -1 if this list does not contain the element
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
     * Returns a cloned array containing all of the data in this list in proper
     * sequence (from first to last element).
     *
     * @return an array containing all of the data in this list in proper
     * sequence
     */
    public int[] toArray() {
        int[] array = new int[size];
        System.arraycopy(data, 0, array, 0, size);
        return array;
    }

    /**
     * Provides a common string representation of the list.
     *
     * @return a string representation of the list
     */
    @Override
    public String toString() {
        if (size == 0) {
            return "[]";
        }

        StringBuilder bob = new StringBuilder();
        bob.append('[');
        for (int i = 0; i < size; i++) {
            bob.append(data[i]);
            if (i != size - 1) {
                bob.append(", ");
            }
        }
        bob.append(']');
        return bob.toString();
    }

    // ========================== Helper Methods ============================ //
    /**
     * Increases the capacity of this list, if necessary, to ensure that it can
     * hold at least the number of data specified by the minimum capacity
     * argument.
     *
     * @param minCapacity the desired minimum capacity
     */
    private void ensureCapacity(int minCapacity) {
        if (minCapacity > data.length) {
            int newCapacity = data.length * 2;
            if (newCapacity < minCapacity) {
                newCapacity = minCapacity;
            }
            // Copy data to new array
            int[] newElements = new int[newCapacity];
            System.arraycopy(data, 0, newElements, 0, size);
            data = newElements;
        }
    }

    private void checkBounds(int index) {
        if (index >= size) {
            throw new ArrayIndexOutOfBoundsException(outOfBoundsMessage(index));
        }
    }

    private String outOfBoundsMessage(int index) {
        return String.format("Index %d cannot be above or equal to the current size of the IntList (%d)", index, size);
    }
}
