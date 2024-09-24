package com.slinky.physics.util;

import com.slinky.physics.components.data.Vec2D;
import com.slinky.physics.base.EntityManager;
import com.slinky.physics.components.Component;
import com.slinky.physics.components.ComponentManager;

/**
 * A high-performance data structure designed to store and manage 2D vector data
 * (x and y coordinates) for entities within an ECS (Entity Component System)
 * framework. This class is optimised for scenarios that require rapid access to
 * floating-point data, making it ideal for use in real-time simulations,
 * physics engines, and games.
 *
 * <p>
 * The {@code VectorStorage} class organises 2D vector data in an interleaved
 * format, where the x and y components of each vector are stored consecutively
 * in a {@link FloatList}. This interleaving enhances cache locality, allowing
 * for faster access times and reduced memory overhead, especially when large
 * numbers of entities are being processed. Each vector is associated with an
 * entity ID, which is efficiently managed using a {@link SparseSet}.
 * </p>
 *
 * <h2>Key Features:</h2>
 * <ul>
 *   <li><b>Efficient Entity Management:</b> Uses a {@link SparseSet} for fast
 *   addition, removal, and lookup of entities.</li>
 *   <li><b>Interleaved Data Layout:</b> Stores x and y components contiguously in
 *   memory to optimise cache performance.</li>
 *   <li><b>Dynamic Resizing:</b> Supports dynamic resizing up to a specified
 *   maximum capacity, handling thousands of entities with minimal memory
 *   overhead.</li>
 *   <li><b>Swap-and-Pop Removal:</b> Maintains data contiguity by swapping the
 *   last vector into the position of a removed vector.</li>
 * </ul>
 *
 * <h2>Usage Example:</h2>
 * <pre><code>
 *     // Initialise VectorStorage with initial capacity of 100 and max capacity of 1000
 *     VectorStorage positionStorage = new VectorStorage(Component.POSITION, entityManager, 100, 1000);
 *
 *     // Add a vector for entity ID 1
 *     positionStorage.add(1, 10.0f, 15.0f);
 *
 *     // Retrieve x and y components for entity ID 1
 *     float x = positionStorage.getX(1);
 *     float y = positionStorage.getY(1);
 *
 *     // Update the vector for entity ID 1
 *     positionStorage.setVectorOf(1, 20.0f, 25.0f);
 *
 *     // Remove the vector for entity ID 1
 *     positionStorage.remove(1);
 * </code></pre>
 *
 * <h2>Thread Safety:</h2>
 * <p>
 * Methods in this class are synchronised to ensure thread safety in concurrent
 * environments. This allows multiple threads to interact with the storage
 * without causing data corruption.
 * </p>
 *
 * <h2>Performance Considerations:</h2>
 * <p>
 * By storing data in flat arrays and using primitive types, this class
 * minimises memory overhead and maximises data locality. The use of final
 * methods enables the JVM to perform aggressive optimisations like method
 * inlining, further enhancing performance.
 * </p>
 *
 * @version 3.0
 * @since   0.1.0
 *
 * @author  Kheagen Haskins
 * 
 * @see     com.slinky.physics.util.FloatList
 * @see     com.slinky.physics.util.IntList
 * @see     com.slinky.physics.util.SparseSet
 * @see     Component
 * @see     ComponentManager
 * @see     EntityManager
 */
public class VectorStorage implements ComponentManager<Vec2D> {
    
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
     * A utility object to hold vector data from the varargs add() method call.
     */
    private final Vec2D utilVector = Vec2D.zero();

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
    
    /**
     * The specific {@link Component} type that this manager is responsible for.
     * Each component type is uniquely identified by its associated bitmask.
     */
    private final Component componentType;

    /**
     * The {@code EntityManager} instance that this manager interacts with. The
     * {@code EntityManager} oversees the lifecycle of entities and their
     * associations with various components.
     */
    protected final EntityManager entityManager;
    
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
     * @param  componentType the type of component, for example {@link Component#POSITION}
     * @param  entityManager the {@code EntityManager} managing this component's entities
     * @param  initialEntityCapacity the initial number of entities the storage
     *         can hold
     * @param  maxEntityCapacity the maximum number of entities this storage can
     *         manage
     * @throws IllegalArgumentException if {@code initialEntityCapacity} is less
     *         than or equal to 0, or if {@code initialEntityCapacity} exceeds
     *         {@code maxEntityCapacity}
     */
    public VectorStorage(Component componentType, EntityManager entityManager, int initialEntityCapacity, int maxEntityCapacity) {
        if (initialEntityCapacity <= 0) {
            throw new IllegalArgumentException("Initial capacity must be positive");
        }

        if (initialEntityCapacity > maxEntityCapacity) {
            throw new IllegalArgumentException("Initial capacity cannot exceed maximum capacity");
        }
        
        this.componentType = componentType;
        this.entityManager = entityManager;
        this.maxCap        = maxEntityCapacity;
        this.vectorData    = new FloatList(initialEntityCapacity * 2);
        this.sparseSet     = new SparseSet(maxEntityCapacity);
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
    public final synchronized int size() {
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
    public final synchronized int getMaxEntityCapacity() {
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
    public final synchronized FloatList getVectorData() {
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
    public final synchronized IntList getEntityIds() {
        return sparseSet.dense();
    }
    
    /**
     * Returns the specific {@link Component} type that this manager is responsible for.
     *
     * @return the component type managed by this storage
     */
    @Override
    public final Component getComponent() {
        return componentType;
    }
    
    // ============================ API Methods ============================= //
    /**
     * Adds a vector to the storage for the specified entity.
     *
     * <p>
     * This method adds the provided {@link Vec2D} vector to the storage associated with
     * the given {@code entityId}. It ensures that the storage does not exceed its maximum
     * capacity and that the entity ID is within valid bounds. Upon successful addition,
     * the entity's bitmask is updated via the {@link EntityManager}.
     * </p>
     *
     * @param entityId the ID of the entity to be added
     * @param vector the 2D vector to associate with the entity
     * @throws IllegalArgumentException if the {@code entityId} is out of bounds
     *         or already exists
     * @throws IllegalStateException if the storage has reached maximum capacity
     */
    @Override
    public final synchronized void add(int entityId, Vec2D vector) {
        if (size() >= maxCap) {
            throw new IllegalStateException("Maximum capacity reached: " + maxCap);
        }
        
        if (entityId < 0 || entityId >= maxCap) {
            throw new IllegalArgumentException("Entity ID out of bounds: " + entityId);
        }
        
        if (!sparseSet.add(entityId)) {
            throw new IllegalArgumentException("Entity already exists: " + entityId);
        }
        
        // accepting the possible class cast exception here given the above checks; 
        // do not want more performance hits
        vectorData.add(vector.x()); 
        vectorData.add(vector.y());
        entityManager.addComponentTo(entityId, componentType); // ensures the entity's bitmask is updated
    }
    
     /**
     * Adds a vector to the storage for the specified entity using variable
     * arguments.
     *
     * <p>
     * This method expects at least two elements in {@code vectorComponents},
     * representing the x and y components of the vector. It converts the
     * provided components into a {@link Vec2D} object and delegates the
     * addition to the {@link #add(int, Vec2D)} method.
     * </p>
     *
     * @param entityId the ID of the entity to be added
     * @param vectorComponents the x and y components of the vector
     * @throws IllegalArgumentException if the {@code entityId} is out of bounds
     * or already exists, or if fewer than two vector components are provided
     * @throws IllegalStateException if the storage has reached maximum capacity
     */
    @Override
    public final synchronized void add(int entityId, Object... vectorComponents) {
        if (vectorComponents.length < 2) {
            throw new IllegalArgumentException("VectorStorage varargs must have at least two elements");
        }

        utilVector.setComponents((float) vectorComponents[0], (float) vectorComponents[1]);
        add(entityId, utilVector);
    }
    
    /**
     * Assigns the component with a default vector value to the specified entity.
     *
     * <p>
     * This method initialises the component for the given {@code entityId} using a
     * default vector value of {@code 0f, 0f}. This is useful when the specific scalar
     * value is not immediately known or will be set later.
     * </p>
     *
     * @param entityId the ID of the entity to which the component will be added with a default value
     *
     * @throws IllegalArgumentException if the {@code entityId} is out of bounds
     *         or already exists
     * @throws IllegalStateException    if the storage has reached maximum capacity
     */
    @Override
    public final synchronized void add(int entityId) {
        add(entityId, 0f, 0f);
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
    public final synchronized void remove(int entityId) {
        if (!sparseSet.contains(entityId)) {
            throw new IllegalArgumentException("Entity does not exist: " + entityId);
        }
        
        int indexToRemove = sparseSet.getIndexOf(entityId) << 1;
        int lastIndex     = (size() - 1) << 1;

        // Swap vector data if not removing the last element
        if (indexToRemove != lastIndex) {
            // Swap vector data
            vectorData.set(indexToRemove,     vectorData.get(lastIndex));
            vectorData.set(indexToRemove + 1, vectorData.get(lastIndex + 1));
        }

        // Remove the entity from SparseSet
        sparseSet.remove(entityId);

        // Remove the last vector data (x, y)
        vectorData.pop();
        vectorData.pop();
        entityManager.removeComponent(entityId, componentType); // ensures the entity's bitmask is updated
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
    public final synchronized boolean contains(int entityId) {
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
    public final synchronized float getX(int entityId) {
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
    public final synchronized float getY(int entityId) {
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
    public final synchronized void setX(int entityId, float x) {
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
    public final synchronized void setY(int entityId, float y) {
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
    public final synchronized void getVectorOf(int entityId, float[] dest) {
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
    public final synchronized float[] getVectorOf(int entityId) {
        float[] vectorComponents = new float[2];
        
        int index           = sparseSet.getIndexOf(entityId) << 1;
        vectorComponents[0] = vectorData.get(index);
        vectorComponents[1] = vectorData.get(index + 1);
        
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
    public final synchronized void setVectorOf(int entityId, float x, float y) {
        int index = sparseSet.getIndexOf(entityId) << 1; // * 2
        vectorData.set(index, x);
        vectorData.set(index + 1, y);
    }
    
    /**
     * Returns a string representation of the current state of the {@code VectorStorage}.
     * 
     * <p>
     * This method is intended for debugging purposes only. It visualises the state
     * by displaying the contents of the {@link SparseSet} and the interleaved vector
     * data stored in the {@link FloatList}.
     * </p>
     * 
     * @return a string representing the current state of the storage
     */
    @Override
    public String toString() {
        StringBuilder outp = new StringBuilder();
        outp.append(sparseSet).append("\n");
        
        outp.append("Data:\t[");
        for (int i = 0; i < vectorData.size(); i++) {
            outp.append(vectorData.array()[i]).append(", ");
        }
        outp.append("]");
        outp.delete(outp.length() - 3, outp.length() - 1);
        
        return outp.toString();
    }

}