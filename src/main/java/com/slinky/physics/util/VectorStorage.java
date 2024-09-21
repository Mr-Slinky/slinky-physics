package com.slinky.physics.util;

import java.util.Arrays;

/**
 * A high-performance data structure designed to store and manage 2D vector data
 * (x, y coordinates) for entities within an ECS (Entity Component System)
 * framework. This class is optimised for scenarios that require rapid access to
 * floating-point data, making it ideal for use in real-time simulations,
 * physics engines, and games.
 *
 * <p>
 * The {@code VectorStorage} class organises 2D vector data in an interleaved
 * format, where the x and y components of each vector are stored consecutively
 * in a {@link com.slinky.physics.util.FloatList}. This interleaving enhances
 * cache locality, allowing for faster access times and reduced memory overhead,
 * especially when large numbers of entities are being processed. Each vector is
 * associated with an entity ID, which is efficiently managed using a
 * {@link com.slinky.physics.util.SparseSet}.
 * </p>
 *
 * <h2>Core Features</h2>
 * <ul>
 *   <li><b>Efficient ID management:</b> The {@code SparseSet} tracks entity IDs, 
 *   allowing for fast addition, removal, and lookup of entities.</li>
 *   <li><b>Interleaved data layout:</b> The x and y components of vectors are stored 
 *   next to each other in memory, which improves memory access patterns and cache efficiency.</li>
 *   <li><b>Memory efficiency:</b> The class is designed to handle thousands of entities 
 *   with minimal memory overhead and supports dynamic resizing of the underlying data arrays.</li>
 *   <li><b>Swap and pop removal:</b> When a vector is removed, the last vector in the 
 *   list is swapped into its position, ensuring that data access remains contiguous and 
 *   efficient without leaving gaps in memory.</li>
 * </ul>
 *
 * <h2>Usage</h2>
 * <p>
 * {@code VectorStorage} is primarily used for storing and manipulating the
 * positions of entities in 2D space. It supports basic operations such as
 * adding new entities, retrieving x/y coordinates, updating vectors, and
 * removing entities.
 * </p>
 *
 * <pre><code>
 *     // Create a VectorStorage instance with an initial capacity of 100 entities
 *     VectorStorage storage = new VectorStorage(100, 1000);
 *
 *     // Add a vector for entity 1
 *     storage.add(1, 10.0f, 15.0f);
 *
 *     // Retrieve the x and y coordinates of entity 1
 *     float x = storage.getX(1);
 *     float y = storage.getY(1);
 *
 *     // Update the vector for entity 1
 *     storage.setVector(1, 20.0f, 25.0f);
 *
 *     // Remove the vector associated with entity 1
 *     storage.remove(1);
 * </code></pre>
 *
 * <h2>Resizing Behaviour</h2>
 * <p>
 * The internal {@link com.slinky.physics.util.FloatList} resizes dynamically
 * based on the number of stored vectors. When new vectors are added and the
 * list reaches its capacity, it grows by 1.5x to accommodate more data.
 * Similarly, the {@link com.slinky.physics.util.SparseSet} ensures that entity
 * IDs are managed efficiently, even as entities are added and removed.
 * </p>
 *
 * <h2>Thread Safety</h2>
 * <p>
 * This class is not thread-safe. If multiple threads access a
 * {@code VectorStorage} instance concurrently, external synchronisation must be
 * provided to ensure safe operations. Failure to do so may result in data
 * corruption or undefined behaviour.
 * </p>
 *
 * <h2>Exception Handling</h2>
 * <ul>
 *   <li>{@code IllegalArgumentException}: Thrown if an invalid entity ID is passed 
 *   to methods like {@link #add}, {@link #remove}, or {@link #getX}.</li>
 *   <li>{@code IllegalStateException}: Thrown if the storage exceeds its maximum 
 *   entity capacity.</li>
 * </ul>
 * 
 * @version 2.0
 * @since   0.1.0
 * 
 * @see com.slinky.physics.util.FloatList
 * @see com.slinky.physics.util.IntList
 * @see com.slinky.physics.util.SparseSet
 * 
 * @author Kheagen Haskins
 */
public final class VectorStorage {
    
    // ============================== Fields ================================ //
    /**
     * The {@link SparseSet} responsible for managing entity IDs in an efficient
     * manner. This structure provides quick access to entities while ensuring
     * minimal overhead in terms of memory usage and entity lookup times.
     *
     * <p>
     * The {@code SparseSet} allows for fast addition, removal, and checking of
     * entity existence, making it well-suited for dynamic systems with
     * frequently changing entities.
     * </p>
     */
    private final SparseSet sparseSet;

    /**
     * The internal array that stores the 2D vector data in an interleaved
     * format. Each entity's x and y coordinates are stored consecutively within
     * this {@link FloatList}. This design ensures that x and y values are
     * stored contiguously in memory, optimising cache performance and access
     * times during large-scale simulations.
     *
     * <p>
     * For example, an entity's x value is stored at index {@code 2 * entityId}
     * and its y value is stored at {@code 2 * entityId + 1}.
     * </p>
     */
    private final FloatList vectorData;

    /**
     * The maximum number of entities that can be stored in this
     * {@code VectorStorage}. This value defines the upper limit on how many
     * entities can be managed at a given time.
     *
     * <p>
     * This maximum capacity is set during construction and cannot be exceeded.
     * Once the capacity is reached, attempting to add more entities will result
     * in an {@code IllegalStateException}.
     * </p>
     */
    private final int maxCap;

    // =========================== Constructors ============================= //
    /**
     * Constructs a {@code VectorStorage} with the specified initial capacity
     * for entities and a defined maximum capacity. The initial capacity
     * determines the starting size of the internal data structures, while the
     * maximum capacity defines the upper limit of how many entities can be
     * stored.
     *
     * <p>
     * The {@code initialEntityCapacity} is used to initialise the size of the
     * internal {@link FloatList}, which holds the 2D vector data. If the number
     * of entities exceeds this capacity, the list will resize dynamically, but
     * it will never exceed the {@code maxEntityCapacity}.
     * </p>
     *
     * @param initialEntityCapacity the initial number of entities the storage
     * can hold
     * @param maxEntityCapacity the maximum number of entities this storage can
     * manage
     * @throws IllegalArgumentException if {@code initialEntityCapacity} is less
     * than or equal to 0, or if {@code initialEntityCapacity} exceeds
     * {@code maxEntityCapacity}
     */
    public VectorStorage(int initialEntityCapacity, int maxEntityCapacity) {
        if (initialEntityCapacity <= 0) {
            throw new IllegalArgumentException("Initial capacity must be positive");
        }

        if (initialEntityCapacity > maxEntityCapacity) {
            throw new IllegalArgumentException("Initial capacity cannot exceed maximum capacity");
        }

        this.maxCap     = maxEntityCapacity;
        this.vectorData = new FloatList(initialEntityCapacity * 2);
        this.sparseSet  = new SparseSet(maxEntityCapacity);
    }

    // ============================== Getters =============================== //
    /**
     * Returns the current number of entities stored in this
     * {@code VectorStorage}.
     *
     * <p>
     * This method provides the current size of the {@link SparseSet}, which
     * represents the number of active entities being tracked. The size
     * corresponds to the number of entities that have vectors stored in the
     * interleaved {@link FloatList}.
     * </p>
     *
     * @return the number of entities in this storage
     */
    public synchronized int size() {
        return sparseSet.size();
    }

    /**
     * Returns the maximum number of entities that this {@code VectorStorage}
     * can manage.
     *
     * <p>
     * This method provides the upper limit of entities that can be stored in
     * the {@code VectorStorage}, as defined during its construction. Once this
     * limit is reached, no additional entities can be added without removing
     * others first.
     * </p>
     *
     * @return the maximum entity capacity of this storage
     */
    public synchronized int getMaxEntityCapacity() {
        return maxCap;
    }

    /**
     * Provides direct access to the internal {@link FloatList} that stores the
     * 2D vector data.
     *
     * <p>
     * This method returns the underlying {@code FloatList}, which contains the
     * interleaved x and y coordinates of each entity. Modifications to this
     * list should be made with caution to avoid corrupting the internal data
     * layout.
     * </p>
     *
     * @return the internal {@code FloatList} storing the vector data
     */
    public synchronized FloatList getVectorData() {
        return vectorData;
    }

    /**
     * Returns a list of all active entity IDs in this {@code VectorStorage}.
     *
     * <p>
     * This method retrieves the {@link IntList} of entity IDs from the
     * {@link SparseSet}, representing all entities that currently have vectors
     * stored. This list can be used for iteration or querying purposes when
     * working with the dense array of entities.
     * </p>
     *
     * @return an {@code IntList} containing the active entity IDs
     */
    public synchronized IntList getEntityIds() {
        return sparseSet.dense();
    }
    
    // ============================ API Methods ============================= //
    /**
     * Adds a new vector (x, y) for the specified entity.
     *
     * <p>
     * This method associates the specified {@code entityId} with a 2D vector,
     * storing the x and y components in the interleaved {@link FloatList}. If
     * the entity already exists or if the {@code entityId} is out of the valid
     * range, an exception is thrown. The storage dynamically grows to
     * accommodate new entities up to the maximum capacity.
     * </p>
     *
     * <p>
     * Note: The {@code entityId} must be unique and within the bounds of the
     * current storage capacity. If the maximum capacity is reached, no more
     * entities can be added.
     * </p>
     *
     * @param entityId the ID of the entity to be added
     * @param x the x-component of the vector
     * @param y the y-component of the vector
     * @throws IllegalArgumentException if the {@code entityId} is out of bounds
     *         or already exists
     * @throws IllegalStateException if the storage has reached maximum capacity
     */
    public synchronized void add(int entityId, float x, float y) {
        if (size() >= maxCap) {
            throw new IllegalStateException("Maximum capacity reached: " + maxCap);
        }
        
        if (entityId < 0 || entityId >= maxCap) {
            throw new IllegalArgumentException("Entity ID out of bounds: " + entityId);
        }

        if (!sparseSet.add(entityId)) {
            throw new IllegalArgumentException("Entity already exists: " + entityId);
        }

        vectorData.add(x);
        vectorData.add(y);
    }

    /**
     * Removes the vector associated with the specified entity.
     *
     * <p>
     * This method removes the 2D vector (x, y) data associated with the given
     * {@code entityId}. The removal is performed via a "swap and pop"
     * operation, where the last vector in the list is swapped into the position
     * of the removed vector. This approach keeps the internal storage compact
     * and prevents fragmentation of the data.
     * </p>
     *
     * <p>
     * If the entity does not exist in the storage, an
     * {@code IllegalArgumentException} is thrown.
     * </p>
     *
     * @param entityId the ID of the entity whose vector is to be removed
     * @throws IllegalArgumentException if the entity does not exist
     */
    public synchronized void remove(int entityId) {
        if (!sparseSet.contains(entityId)) {
            throw new IllegalArgumentException("Entity does not exist: " + entityId);
        }

        int indexToRemove = sparseSet.getIndexOf(entityId);
        int lastIndex = size() - 1;

        // Swap vector data if not removing the last element
        if (indexToRemove != lastIndex) {
            // Swap vector data
            vectorData.set(indexToRemove * 2, vectorData.get(lastIndex * 2));
            vectorData.set(indexToRemove * 2 + 1, vectorData.get(lastIndex * 2 + 1));
        }

        // Remove the entity from SparseSet
        sparseSet.remove(entityId);

        // Remove the last vector data (x, y)
        vectorData.pop();
        vectorData.pop();
    }

    /**
     * Checks if the storage contains the specified entity.
     *
     * <p>
     * This method checks whether a given {@code entityId} is currently stored
     * in the {@code VectorStorage}. It queries the internal {@link SparseSet}
     * to determine if the entity exists.
     * </p>
     *
     * @param entityId the ID of the entity to check
     * @return {@code true} if the entity exists, {@code false} otherwise
     */
    public synchronized boolean contains(int entityId) {
        return sparseSet.contains(entityId);
    }

    /**
     * Retrieves the x-component of the vector for the specified entity.
     *
     * <p>
     * This method returns the x-coordinate of the vector associated with the
     * given {@code entityId}. The index is calculated based on the entity's
     * position within the internal {@link FloatList}.
     * </p>
     *
     * @param entityId the ID of the entity whose x-coordinate is being
     * retrieved
     * @return the x-component of the vector for the specified entity
     * @throws IllegalArgumentException if the entity does not exist
     */
    public synchronized float getX(int entityId) {
        int index = sparseSet.getIndexOf(entityId);
        return vectorData.get(index * 2);
    }

    /**
     * Retrieves the y-component of the vector for the specified entity.
     *
     * <p>
     * This method returns the y-coordinate of the vector associated with the
     * given {@code entityId}. The index is calculated based on the entity's
     * position within the internal {@link FloatList}.
     * </p>
     *
     * @param entityId the ID of the entity whose y-coordinate is being
     * retrieved
     * @return the y-component of the vector for the specified entity
     * @throws IllegalArgumentException if the entity does not exist
     */
    public synchronized float getY(int entityId) {
        int index = sparseSet.getIndexOf(entityId);
        return vectorData.get(index * 2 + 1);
    }

    /**
     * Sets the x-component of the vector for the specified entity.
     *
     * <p>
     * This method updates the x-coordinate of the vector associated with the
     * given {@code entityId} in the internal {@link FloatList}.
     * </p>
     *
     * @param entityId the ID of the entity whose x-coordinate is being set
     * @param x the new x-component to assign to the vector
     * @throws IllegalArgumentException if the entity does not exist
     */
    public synchronized void setX(int entityId, float x) {
        int index = sparseSet.getIndexOf(entityId);
        vectorData.set(index * 2, x);
    }

    /**
     * Sets the y-component of the vector for the specified entity.
     *
     * <p>
     * This method updates the y-coordinate of the vector associated with the
     * given {@code entityId} in the internal {@link FloatList}.
     * </p>
     *
     * @param entityId the ID of the entity whose y-coordinate is being set
     * @param y the new y-component to assign to the vector
     * @throws IllegalArgumentException if the entity does not exist
     */
    public synchronized void setY(int entityId, float y) {
        int index = sparseSet.getIndexOf(entityId);
        vectorData.set(index * 2 + 1, y);
    }

    /**
     * Retrieves the full 2D vector (x, y) for the specified entity and stores
     * it in the provided array.
     *
     * <p>
     * This method copies the x and y coordinates of the vector associated with
     * the given {@code entityId} into the provided {@code dest} array. The
     * array must have at least two elements to store the coordinates.
     * </p>
     *
     * @param entityId the ID of the entity whose vector is being retrieved
     * @param dest the destination array to store the x and y components
     * @throws IllegalArgumentException if the entity does not exist or if the
     * {@code dest} array has fewer than 2 elements
     */
    public synchronized void getVectorOf(int entityId, float[] dest) {
        if (dest == null || dest.length < 2) {
            throw new IllegalArgumentException("Destination array must have at least two elements.");
        }

        int index = sparseSet.getIndexOf(entityId);
        dest[0] = vectorData.get(index * 2);
        dest[1] = vectorData.get(index * 2 + 1);
    }

    /**
     * Retrieves the full 2D vector (x, y) for the specified entity and returns
     * it as a new array of floats.
     *
     * <p>
     * This method creates a new array of size 2, where the x and y coordinates
     * of the vector associated with the given {@code entityId} are stored. The
     * first element of the array will contain the x-coordinate, and the second
     * element will contain the y-coordinate.
     * </p>
     *
     * @param entityId the ID of the entity whose vector is being retrieved
     * @return an array of floats where the first element is the x-coordinate
     * and the second element is the y-coordinate of the vector
     * @throws IllegalArgumentException if the entity does not exist
     */    
    public synchronized float[] getVectorOf(int entityId) {
        float[] vectorComponents = new float[2];
        
        int index           = sparseSet.getIndexOf(entityId);
        vectorComponents[0] = vectorData.get(index * 2);
        vectorComponents[1] = vectorData.get(index * 2 + 1);
        
        return vectorComponents;
    }

    /**
     * Sets both the x and y components of the vector for the specified entity.
     *
     * <p>
     * This method updates the x and y coordinates of the vector associated with
     * the given {@code entityId} in the internal {@link FloatList}.
     * </p>
     *
     * @param entityId the ID of the entity whose vector is being updated
     * @param x the new x-component to assign to the vector
     * @param y the new y-component to assign to the vector
     * @throws IllegalArgumentException if the entity does not exist
     */
    public synchronized void setVectorOf(int entityId, float x, float y) {
        int index = sparseSet.getIndexOf(entityId);
        vectorData.set(index * 2, x);
        vectorData.set(index * 2 + 1, y);
    }
    
    /**
     * For debugging only; to visualise the state.
     * 
     * @return 
     */
    @Override
    public String toString() {
        StringBuilder outp = new StringBuilder();
        outp.append("IDs:\t").append(Arrays.toString(sparseSet.sparse())).append("\n");

        outp.append("Dense:\t[");
        for (int i = 0; i < sparseSet.dense().size(); i++) {
            outp.append(sparseSet.dense().array()[i]).append(", ");
        }
        outp.append("]\n");
        outp.delete(outp.length() - 4, outp.length() - 2);
        
        outp.append("Data:\t[");
        for (int i = 0; i < vectorData.size(); i++) {
            outp.append(vectorData.array()[i]).append(", ");
        }
        outp.append("]");
        outp.delete(outp.length() - 3, outp.length() - 1);
        
        return outp.toString();
    }
    
}