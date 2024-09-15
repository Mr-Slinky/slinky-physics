package com.slinky.physics.components;

import static java.lang.Math.round;
import java.util.Arrays;

/**
 * The {@code VectorStorage} class provides an optimised data structure for
 * storing and managing 2D vector data in the context of an
 * Entity-Component-System (ECS) framework. The class is designed to handle
 * large data sets using an interleaved array, and allows efficient access to
 * vectors by associating each entity with a corresponding index in the internal
 * array.
 *
 * <p>
 * For example, an entity with an ID of 10 would have its associated vector's x
 * and y coordinates stored at positions 20 and 21 in the underlying array
 * (since each entity occupies two consecutive array slots for x and y values).
 * The internal array thus holds all vectors in a linear, interleaved format,
 * providing fast, direct access based on entity ID.
 * </p>
 *
 * <h2>Design Considerations:</h2>
 * <ul>
 *   <li>
 *   Internal array uses {@code float} over {@code double} to reduce memory
 *   footprint.
 *   </li>
 * 
 *   <li>
 *   This class is declared {@code final}, meaning it cannot be subclassed. This
 *   allows the JVM to apply various performance optimisations such as method
 *   inlining and devirtualisation, which are particularly important for
 *   performance-critical environments like real-time physics simulations.
 *   </li>
 *
 *   <li>
 *   The class relies on interleaved arrays for data storage. Each entity's x and
 *   y vector values are stored in consecutive blocks within the array, allowing
 *   efficient retrieval and update operations. This structure also reduces the
 *   overhead associated with using individual objects for each vector component,
 *   optimising both memory usage and CPU cache locality.
 *   </li>
 *
 *   <li>
 *   Components such as {@code Position} and {@code Velocity} will hold their own
 *   reference to an instance of this class, which links their vector data to this
 *   storage. Each component manages its data through the {@code VectorStorage}
 *   instance by using entity IDs to access or modify vector values.
 *   </li>
 *
 *   <li>
 *   Accessors (getters) in this class perform no input validation to maximise
 *   performance. It is assumed that the entity IDs provided are valid and within
 *   bounds. Any validation or error-checking must be done externally before
 *   invoking methods on this class. This design choice prioritises speed, making
 *   this class suitable for tight loops or inner performance-critical sections of
 *   the application.
 *   </li>
 * </ul>
 *
 * <h2>Resizing Strategy:</h2>
 * <p>
 * {@code VectorStorage} uses a simple resizing strategy where the internal
 * array increases its capacity by 50%. This ensures that the array grows
 * dynamically as new entities are added. However, this also means that if the
 * client starts off with a small initial capacity and the number of entities
 * increases rapidly, resizing may incur performance penalties. It is
 * recommended that the client class chooses an appropriate initial capacity
 * based on expected use to mitigate the cost of frequent resizing.
 * </p>
 * 
 * <p>
 * Additionally, {@code VectorStorage} does not manage its resizing by itself.
 * The containing class is responsible for invoking the {@code trim()} and
 * {@code grow()} methods to reduce or increase the internal array capacity if
 * necessary.
 * </p>
 *
 * <h2>Thread-Safety:</h2>
 * <p>
 * This class does not implement any thread-safety mechanisms. It is intended to
 * be wrapped by a more specialised component class, which may implement
 * thread-safety if required. Therefore, clients should ensure that if multiple
 * threads are interacting with this class, proper synchronisation or locking
 * mechanisms are implemented in the containing class.
 * </p>
 *
 * <h2>Usage Context:</h2>
 * <p>
 * {@code VectorStorage} is typically used in conjunction with vector-related
 * components, such as {@code Position}, {@code Velocity}, or {@code Force} in a
 * physics engine. It provides a highly efficient way to store and manipulate
 * large quantities of vector data, especially in situations where entities
 * frequently interact with spatial calculations.
 * </p>
 *
 * <h2>Performance Optimisations:</h2>
 * <ul>
 *   <li>
 *   Minimises memory allocations by using a single interleaved array for x
 *   and y data.
 *   </li>
 *
 *   <li>  
 *   Resizing is done conservatively, with the array capacity increasing by 50%
 *   when needed.
 *   </li>
 * 
 *   <li>
 *   No runtime checks for bounds or entity validation are included in the
 *   getter/setter methods to maximise speed.
 *   </li>
 * </ul>
 * 
 * @version 1.0
 * @since   0.1.0
 * 
 * @author Kheagen Haskins
 */
public final class VectorStorage {
    
    // ============================== Static ================================ //
    /**
     * The maximum capacity that the internal array can reach. This value
     * represents the number of float values (x and y components) that can be
     * stored in the array, as each vector consists of two components (x and y).
     * Once this limit is reached, no further resizing will occur.
     *
     * <p>
     * While the array can theoretically reach this capacity, practical limits
     * may be imposed by the JVM's available memory. A fully populated
     * {@code VectorStorage} instance will occupy approximately 8MB of memory
     * due to the use of float values.
     * </p>
     */
    public static final int MAX_CAPACITY = 1_000_000;

    /**
     * The maximum allowed initial capacity for this storage, set to half of
     * {@code MAX_CAPACITY}. Since the initial capacity passed to the
     * constructor represents the number of vectors (where each vector consists
     * of two float values: x and y), this ensures that the storage does not
     * exceed the overall maximum capacity of the underlying array.
     *
     * <p>
     * By limiting the initial capacity to half of {@code MAX_CAPACITY}, this
     * constraint allows the client to treat the internal arrays as parallel
     * arrays, ensuring that the number of vectors stored does not surpass the
     * total possible capacity of the internal data array.
     * </p>
     */
    public static final int MAX_STARTING_CAPACITY = MAX_CAPACITY / 2;

    // ============================== Fields ================================ //
    /**
     * The internal array used to store the 2D vector data in an interleaved
     * format. Each entity's x and y coordinates are stored consecutively in
     * this array. The array's capacity dynamically grows when more space is
     * needed.
     *
     * <p>
     * For example, the x-coordinate of entity ID 0 is stored at index 0, and
     * the y-coordinate is stored at index 1. For entity ID 1, the x-coordinate
     * is stored at index 2, and the y-coordinate is stored at index 3, and so
     * on.
     * </p>
     */
    protected float[] data; // protected for access when testing

    /**
     * The current capacity of the {@code data} array. This represents the total
     * number of floats the array can hold (which is twice the number of
     * entities, since each entity requires two slots for x and y).
     */
    private int capacity;

    // =========================== Constructors ============================= //
     /**
     * Constructs a new {@code VectorStorage} instance with the specified
     * initial capacity. The initial capacity defines how many entities can be
     * stored initially before resizing is required.
     *
     * <p>
     * The internal capacity is multiplied by 2 to account for the x and y
     * coordinates of each entity. 
     * </p>
     *
     * @param initialCapacity the number of entities that can be stored
     * initially
     * @throws IllegalArgumentException if {@code initialCapacity} is less than
     * or equal to zero, or if the number of components exceeds the maximum
     * capacity defined by {@code MAX_STARTING_CAPACITY}.
     */
    public VectorStorage(int initialCapacity) {
        if (initialCapacity <= 0) {
            throw new IllegalArgumentException("VectorStorage#Constructor(int initialCapacity): Initial capacity must be positive");
        }
        
        if (initialCapacity > MAX_STARTING_CAPACITY) {
            throw new IllegalArgumentException("VectorStorage#Constructor(int initialCapacity): Initial capacity too large: " + initialCapacity);
        }
        
        this.capacity = initialCapacity * 2; // Account for x and y values
        this.data     = new float[capacity];
    }

    // ============================== Getters =============================== //
    /**
     * Retrieves the x-coordinate for the entity with the specified ID.
     *
     * <p>
     * Note: This method does not perform bounds checking, so it assumes that
     * the {@code entityId} provided is valid and within the range of the
     * current capacity. External validation must ensure the {@code entityId} is
     * correct before calling this method.
     * </p>
     *
     * @param entityId the ID of the entity whose x-coordinate is to be
     * retrieved
     * @return the x-coordinate for the specified entity
     */
    public float xAt(int entityId) {
        return data[entityId << 1];
    }

    /**
     * Retrieves the y-coordinate for the entity with the specified ID.
     *
     * <p>
     * Note: This method does not perform bounds checking, so it assumes that
     * the {@code entityId} provided is valid and within the range of the
     * current capacity. External validation must ensure the {@code entityId} is
     * correct before calling this method.
     * </p>
     *
     * @param entityId the ID of the entity whose y-coordinate is to be
     * retrieved
     * @return the y-coordinate for the specified entity
     */
    public float yAt(int entityId) {
        return data[(entityId << 1) + 1];
    }

    /**
     * Returns the number of components (x and y values) currently stored in
     * this {@code VectorStorage}. This value represents the total number of
     * floats that have been added to the storage, where each entity's vector
     * consists of two components (x and y).
     *
     * <p>
     * Note that the capacity returned is the total count of x and y values, not
     * the number of entities. To get the number of entities (i.e., the number
     * of vectors), you should use the {@code vectorCount()} method.
     * </p>
     *
     * @return the total number of x and y components currently in the storage
     */
    public int capacity() {
        return capacity;
    }

    /**
     * Returns the total number of vectors currently stored in this
     * {@code VectorStorage}. Each vector consists of two components (x and y),
     * so this method divides the internal {@code capacity} by 2 to return the
     * number of vectors.
     *
     * <p>
     * This method provides a convenient way to determine how many entities (or
     * vectors) are currently represented in the storage without needing to
     * manually divide the component count.
     * </p>
     *
     * @return the number of vectors (entities) currently stored
     */
    public int vectorCount() {
        return capacity / 2;
    }


    // ============================== Setters =============================== //
    /**
     * Updates the x-coordinate for the entity with the specified ID.
     *
     * <p>
     * Note: This method does not perform bounds checking, so it assumes that
     * the {@code entityId} provided is valid and within the range of the
     * current capacity. External validation must ensure the {@code entityId} is
     * correct before calling this method.</p>
     *
     * @param entityId the ID of the entity whose x-coordinate is to be updated
     * @param x the new x-coordinate for the entity
     */
    public void setX(int entityId, float x) {
        data[entityId * 2] = x;
    }

    /**
     * Updates the y-coordinate for the entity with the specified ID.
     *
     * <p>
     * Note: This method does not perform bounds checking, so it assumes that
     * the {@code entityId} provided is valid and within the range of the
     * current capacity. External validation must ensure the {@code entityId} is
     * correct before calling this method.</p>
     *
     * @param entityId the ID of the entity whose y-coordinate is to be updated
     * @param y the new y-coordinate for the entity
     */
    public void setY(int entityId, float y) {
        data[entityId * 2 + 1] = y;
    }
    
    /**
     * Updates both the x and y components for the entity with the specified ID.
     *
     * <p>
     * This method directly sets the x and y values in the internal array for
     * the given entity. The x and y values are stored in consecutive positions
     * within the interleaved array, where the index is determined by the
     * entity's ID.
     * </p>
     *
     * <p>
     * Note: This method does not perform bounds checking on the
     * {@code entityId}. It assumes that the {@code entityId} provided is valid
     * and within the current capacity. External validation should ensure the
     * {@code entityId} is correct before invoking this method.
     * </p>
     *
     * @param entityId the ID of the entity whose x and y components are to be
     * updated
     * @param x the new x-coordinate of the entity
     * @param y the new y-coordinate of the entity
     */
    public void setComponents(int entityId, float x, float y) {
        data[entityId * 2] = x;
        data[entityId * 2 + 1] = y;
    }

    // ============================ API Methods ============================= //
    /**
     * Trims the internal array to reduce its capacity based on the specified
     * number of entities.
     *
     * <p>
     * This method reduces the capacity of the internal array by trimming any
     * excess space not needed for the specified number of entities. The new
     * capacity is calculated by removing twice the number of components (since
     * each entity has both x and y components) from the current array length.
     * </p>
     *
     * <p>
     * After trimming, the internal array is resized to match the new capacity,
     * effectively releasing any unused memory.
     * </p>
     *
     * @param trimSize the number of entities that should are removed from the array
     * after trimming. The total number of components (x and y values) is
     * calculated as {@code size * 2}.
     *
     * @throws IllegalArgumentException if the specified size is greater than
     * the current number of entities.
     */
    public void trim(int trimSize) {
        trimSize *= 2;
        if (trimSize > capacity) {
            throw new IllegalArgumentException("VectorStorage#trim(int size): size " + trimSize * 2 + " cannot exceed the current capacity (" + capacity + ")");
        }
        
        capacity = capacity - trimSize;
        data = Arrays.copyOf(data, capacity);
    }


    /**
     * Increases the capacity of the internal array by 50%.
     *
     * <p>
     * This method allocates a new array with a capacity 50% larger than the
     * current array and copies the existing data into the new array. The
     * capacity is updated to reflect the new capacity, but it will never exceed
     * the {@code MAX_STARTING_CAPACITY}.
     * </p>
     *
     * <p>
     * If the current capacity is already at {@code MAX_STARTING_CAPACITY}, an exception
     * will be thrown indicating that no further resizing is possible.
     * </p>
     *
     * <p>
     * This resizing strategy ensures that the internal array grows dynamically
     * while keeping reallocation operations relatively infrequent. However,
     * frequent resizing can still occur if the initial capacity is set too low
     * relative to the expected number of entities.
     * </p>
     *
     * @throws IllegalStateException if the internal storage reaches
     * {@code MAX_STARTING_CAPACITY}.
     */
    public void grow() {
        if (this.capacity == MAX_CAPACITY) {
            throw new IllegalStateException("VectorStorage#grow(): Internal vector storage is at capacity");
        }

        int newCapacity = Math.min(MAX_CAPACITY, capacity + (int) round(capacity / 2.0));
        // MAX_CAPACITY is even, so this should be safe from going above that maximum
        if (newCapacity % 2 != 0) {
            newCapacity += 1;
        }
        
        data            = Arrays.copyOf(data, newCapacity);
        this.capacity   = newCapacity;
    }

}