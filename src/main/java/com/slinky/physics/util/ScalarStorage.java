package com.slinky.physics.util;

import com.slinky.physics.base.EntityManager;
import com.slinky.physics.components.Component;
import com.slinky.physics.components.ComponentManager;

/**
 * A high-performance data structure designed to store and manage scalar
 * floating-point data for entities within an ECS (Entity Component System)
 * framework. This class is optimised for scenarios that require rapid access to
 * floating-point values, making it ideal for use in real-time simulations,
 * physics engines, and games.
 *
 * <p>
 * The {@code ScalarStorage} class organises scalar data in a flat format, where
 * each entity's value is stored consecutively in a {@link FloatList}. This layout
 * enhances cache locality, allowing for faster access times and reduced memory
 * overhead, especially when large numbers of entities are being processed. Each
 * scalar value is associated with an entity ID, which is efficiently managed
 * using a {@link SparseSet}.
 * </p>
 *
 * <p>
 * This class is <b>final</b>, preventing further inheritance and ensuring
 * that its behavior remains consistent. All methods in this class are declared
 * as <b>final</b>, allowing the JVM to aggressively optimise them. This
 * finality guarantees that methods will not be overridden, facilitating faster
 * execution by enabling method inlining and other optimisations that improve
 * runtime performance.
 * </p>
 *
 * <h2>Core Features</h2>
 * <ul>
 *   <li><b>Efficient ID Management:</b> The {@code SparseSet} tracks entity IDs,
 *       allowing for fast addition, removal, and lookup of entities.</li>
 *   <li><b>Flat Data Layout:</b> Scalar values are stored in a contiguous
 *       {@link FloatList}, which improves memory access patterns and cache
 *       efficiency.</li>
 *   <li><b>Memory Efficiency:</b> Designed to handle thousands of
 *       entities with minimal memory overhead and supports dynamic resizing of
 *       the underlying data arrays.</li>
 *   <li><b>Swap and Pop Removal:</b> When a scalar is removed, the last value in
 *       the list is swapped into its position, ensuring that data access remains
 *       contiguous and efficient without leaving gaps in memory.</li>
 * </ul>
 *
 * <h2>Usage</h2>
 * <p>
 * {@code ScalarStorage} is primarily used for storing and manipulating scalar
 * values of entities. It supports basic operations such as adding new entities,
 * retrieving values, updating scalars, and removing entities.
 * </p>
 *
 * <pre>{@code
 *     // Create a ScalarStorage instance with an initial capacity of 100 entities
 *     ScalarStorage storage = new ScalarStorage(Component.MASS, entityManager, 100, 1000);
 *
 *     // Add a scalar for entity 1
 *     storage.add(1, 10.0f);
 *
 *     // Retrieve the value of entity 1
 *     float value = storage.get(1);
 *
 *     // Update the scalar for entity 1
 *     storage.set(1, 20.0f);
 *
 *     // Remove the scalar associated with entity 1
 *     storage.remove(1);
 * }</pre>
 *
 * <h2>Resizing Behavior</h2>
 * <p>
 * The internal {@link FloatList} resizes dynamically based on the number of
 * stored scalars. When new scalars are added and the list reaches its capacity,
 * it grows by 1.5x to accommodate more data. Similarly, the {@link SparseSet}
 * ensures that entity IDs are managed efficiently, even as entities are added
 * and removed.
 * </p>
 *
 * @version 2.0
 * @since   0.1.0
 *
 * @see     FloatList
 * @see     IntList
 * @see     SparseSet
 * @see     Component
 * @see     ComponentManager
 * @see     EntityManager
 *
 * @author  
 * Kheagen Haskins
 */
public class ScalarStorage implements ComponentManager<Float> {

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
     * The internal array that stores the scalar data in a flat format. Each
     * entity's value is stored consecutively within this {@link FloatList}.
     * This design ensures that values are stored contiguously in memory,
     * optimising cache performance and access times during large-scale
     * simulations.
     *
     * <p>
     * For example, an entity's value is stored at index {@code entityIndex}.
     * </p>
     */
    private final FloatList scalarData;

    /**
     * The maximum number of entities that can be stored in this
     * {@code ScalarStorage}. This value defines the upper limit on how many
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
     * Constructs a {@code ScalarStorage} with the specified component type,
     * initial capacity for entities, and a defined maximum capacity.
     *
     * <p>
     * The {@code initialEntityCapacity} is used to initialise the size of the
     * internal {@link FloatList}, which holds the scalar data. If the number of
     * entities exceeds this capacity, the list will resize dynamically, but it
     * will never exceed the {@code maxEntityCapacity}.
     * </p>
     *
     * @param componentType           the type of component managed by this storage, e.g., {@code Component.MASS}
     * @param entityManager           the {@code EntityManager} managing this component's entities
     * @param initialEntityCapacity   the initial number of entities the storage can hold
     * @param maxEntityCapacity       the maximum number of entities this storage can manage
     *
     * @throws IllegalArgumentException if {@code initialEntityCapacity} is less
     *         than or equal to 0, or if {@code initialEntityCapacity} exceeds
     *         {@code maxEntityCapacity}
     */
    public ScalarStorage(Component componentType, EntityManager entityManager, int initialEntityCapacity, int maxEntityCapacity) {
        if (initialEntityCapacity <= 0) {
            throw new IllegalArgumentException("Initial capacity must be positive");
        }

        if (initialEntityCapacity > maxEntityCapacity) {
            throw new IllegalArgumentException("Initial capacity cannot exceed maximum capacity");
        }

        this.componentType = componentType;
        this.entityManager = entityManager;
        this.maxCap        = maxEntityCapacity;
        this.scalarData    = new FloatList(initialEntityCapacity);
        this.sparseSet     = new SparseSet(maxEntityCapacity);
    }

    // ============================== Getters =============================== //

    /**
     * Returns the current number of entities stored in this
     * {@code ScalarStorage}.
     *
     * <p>
     * This method provides the current size of the {@link SparseSet}, which
     * represents the number of active entities being tracked. The size
     * corresponds to the number of entities that have scalar values stored in
     * the {@link FloatList}.
     * </p>
     *
     * @return the number of entities in this storage
     */
    public final synchronized int size() {
        return sparseSet.size();
    }

    /**
     * Returns the maximum number of entities that this {@code ScalarStorage}
     * can manage.
     *
     * <p>
     * This method provides the upper limit of entities that can be stored in
     * the {@code ScalarStorage}, as defined during its construction. Once this
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
     * scalar data.
     *
     * <p>
     * This method returns the underlying {@code FloatList}, which contains the
     * scalar values of each entity. Modifications to this list should be made
     * with caution to avoid corrupting the internal data layout.
     * </p>
     *
     * @return the internal {@code FloatList} storing the scalar data
     */
    public final synchronized FloatList getScalarData() {
        return scalarData;
    }

    /**
     * Returns a list of all active entity IDs in this {@code ScalarStorage}.
     *
     * <p>
     * This method retrieves the {@link IntList} of entity IDs from the
     * {@link SparseSet}, representing all entities that currently have scalar
     * values stored. This list can be used for iteration or querying purposes
     * when working with the dense array of entities.
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
    public Component getComponent() {
        return componentType;
    }

    // ============================ API Methods ============================= //

    /**
     * Adds a new scalar value for the specified entity.
     *
     * <p>
     * This method associates the specified {@code entityId} with a scalar
     * value, storing it in the {@link FloatList}. If the entity already exists
     * or if the {@code entityId} is out of the valid range, an exception is
     * thrown. The storage dynamically grows to accommodate new entities up to
     * the maximum capacity.
     * </p>
     *
     * <p>
     * Note: The {@code entityId} must be unique and within the bounds of the
     * current storage capacity. If the maximum capacity is reached, no more
     * entities can be added.
     * </p>
     *
     * @param entityId the ID of the entity to be added
     * @param value    the scalar value to associate with the entity
     *
     * @throws IllegalArgumentException if the {@code entityId} is out of bounds
     *         or already exists
     * @throws IllegalStateException    if the storage has reached maximum capacity
     */
    @Override
    public final synchronized void add(int entityId, Float value) {
        if (size() >= maxCap) {
            throw new IllegalStateException("Maximum capacity reached: " + maxCap);
        }

        if (entityId < 0 || entityId >= maxCap) {
            throw new IllegalArgumentException("Entity ID out of bounds: " + entityId);
        }

        if (!sparseSet.add(entityId)) {
            throw new IllegalArgumentException("Entity already exists: " + entityId);
        }

        scalarData.add(value);
    }

    /**
     * Adds a new scalar value for the specified entity using a variable-length argument list.
     *
     * <p>
     * This method provides flexibility by allowing the addition of scalar data
     * without explicitly creating a {@code Float} object. It internally casts
     * the first element of the {@code values} array to a {@code float} and adds it
     * as the scalar value for the entity.
     * </p>
     *
     * @param entityId the ID of the entity to be added
     * @param values   a variable-length argument list containing the scalar value
     *
     * @throws IllegalArgumentException if the {@code entityId} is out of bounds,
     *         already exists, or if {@code values} is null or empty
     * @throws IllegalStateException    if the storage has reached maximum capacity
     */
    @Override
    public final synchronized void add(int entityId, Object... values) {
        if (values == null || values.length < 1) {
            throw new IllegalArgumentException("ScalarStorage requires at least one value.");
        }

        if (!(values[0] instanceof Float)) {
            throw new IllegalArgumentException("First value must be of type Float.");
        }

        add(entityId, (Float) values[0]);
    }

    /**
     * Assigns the component with a default scalar value to the specified entity.
     *
     * <p>
     * This method initialises the component for the given {@code entityId} using a
     * default scalar value of {@code 0.0f}. This is useful when the specific scalar
     * value is not immediately known or will be set later.
     * </p>
     *
     * @param entityId the ID of the entity to which the component will be added with a default value
     *
     * @throws IllegalArgumentException if the {@code entityId} is out of bounds
     *         or already exists
     * @throws IllegalStateException    if the storage has reached maximum capacity
     */
    public final synchronized void add(int entityId) {
        add(entityId, 0.0f);
    }

    /**
     * Removes the scalar value associated with the specified entity.
     *
     * <p>
     * This method removes the scalar value associated with the given
     * {@code entityId}. The removal is performed via a "swap and pop"
     * operation, where the last value in the list is swapped into the position
     * of the removed scalar. This approach keeps the internal storage compact
     * and prevents fragmentation of the data.
     * </p>
     *
     * <p>
     * If the entity does not exist in the storage, an
     * {@code IllegalArgumentException} is thrown.
     * </p>
     *
     * @param entityId the ID of the entity whose scalar is to be removed
     *
     * @throws IllegalArgumentException if the entity does not exist
     */
    @Override
    public final synchronized void remove(int entityId) {
        if (!sparseSet.contains(entityId)) {
            throw new IllegalArgumentException("Entity does not exist: " + entityId);
        }

        int indexToRemove = sparseSet.getIndexOf(entityId);
        int lastIndex     = size() - 1;

        // Swap scalar data if not removing the last element
        if (indexToRemove != lastIndex) {
            float lastValue = scalarData.get(lastIndex);
            scalarData.set(indexToRemove, lastValue);
        }

        // Remove the entity from SparseSet
        sparseSet.remove(entityId);

        // Remove the last scalar data
        scalarData.pop();
    }

    /**
     * Checks if the storage contains the specified entity.
     *
     * <p>
     * This method checks whether a given {@code entityId} is currently stored
     * in the {@code ScalarStorage}. It queries the internal {@link SparseSet}
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
     * Retrieves the scalar value for the specified entity.
     *
     * <p>
     * This method returns the scalar value associated with the given
     * {@code entityId}. The index is calculated based on the entity's position
     * within the internal {@link FloatList}.
     * </p>
     *
     * @param entityId the ID of the entity whose scalar value is being
     *                 retrieved
     * @return the scalar value for the specified entity
     *
     * @throws IllegalArgumentException if the entity does not exist
     */
    public final synchronized float get(int entityId) {
        int index = sparseSet.getIndexOf(entityId);
        return scalarData.get(index);
    }

    /**
     * Sets the scalar value for the specified entity.
     *
     * <p>
     * This method updates the scalar value associated with the given
     * {@code entityId} in the internal {@link FloatList}.
     * </p>
     *
     * @param entityId the ID of the entity whose scalar value is being set
     * @param value    the new scalar value to assign to the entity
     *
     * @throws IllegalArgumentException if the entity does not exist
     */
    public final synchronized void set(int entityId, float value) {
        int index = sparseSet.getIndexOf(entityId);
        scalarData.set(index, value);
    }

    /**
     * Retrieves the scalar value for the specified entity and stores it in the
     * provided array.
     *
     * <p>
     * This method copies the scalar value associated with the given
     * {@code entityId} into the provided {@code dest} array. The array must
     * have at least one element to store the value.
     * </p>
     *
     * @param entityId the ID of the entity whose scalar value is being
     *                 retrieved
     * @param dest     the destination array to store the scalar value
     *
     * @throws IllegalArgumentException if the entity does not exist or if the
     *                                  {@code dest} array has fewer than one element
     */
    public final synchronized void getValueOf(int entityId, float[] dest) {
        if (dest == null || dest.length < 1) {
            throw new IllegalArgumentException("Destination array must have at least one element.");
        }

        int index = sparseSet.getIndexOf(entityId);
        dest[0] = scalarData.get(index);
    }

    /**
     * Retrieves the scalar value for the specified entity and returns it as a
     * new float.
     *
     * <p>
     * This method returns the scalar value associated with the given
     * {@code entityId}.
     * </p>
     *
     * @param entityId the ID of the entity whose scalar value is being
     *                 retrieved
     * @return the scalar value of the specified entity
     *
     * @throws IllegalArgumentException if the entity does not exist
     */
    public final synchronized float getValueOf(int entityId) {
        int index = sparseSet.getIndexOf(entityId);
        return scalarData.get(index);
    }

    /**
     * Returns a string representation of the {@code ScalarStorage} state.
     *
     * <p>
     * This method is intended for debugging purposes. It provides a textual
     * representation of the current state of the storage, including the
     * {@link SparseSet} and the scalar data contained within the
     * {@link FloatList}.
     * </p>
     *
     * @return a string representation of the {@code ScalarStorage} state
     */
    @Override
    public final synchronized String toString() {
        StringBuilder outp = new StringBuilder();
        outp.append(sparseSet).append("\n");

        outp.append("Data:\t[");
        for (int i = 0; i < scalarData.size(); i++) {
            outp.append(scalarData.get(i)).append(", ");
        }

        if (scalarData.size() > 0) {
            outp.delete(outp.length() - 2, outp.length());
        }
        outp.append("]");

        return outp.toString();
    }

}