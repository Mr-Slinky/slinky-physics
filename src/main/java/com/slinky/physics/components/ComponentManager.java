package com.slinky.physics.components;

/**
 * A generic interface for managing components associated with entities within
 * the ECS framework.
 *
 * <p>
 * Implementations of this interface are responsible for handling the addition,
 * removal, and management of specific component types for entities. Components
 * are stored in flat, contiguous arrays of primitive data to optimise cache
 * performance and adhere to Data-Oriented Design (DOD) principles. The type
 * parameter {@code T} represents the data structure encapsulating the primitive
 * values relevant to the component.
 * </p>
 *
 * <p>
 * For example, a {@code ComponentManager<Float>} might manage components that
 * consist of single floating-point values. More complex components can
 * encapsulate multiple primitives or composite data types.
 * </p>
 *
 * @param <T> The type of data managed by the implementing class. This should
 * typically encapsulate primitive data types or structures composed of
 * primitives that can be efficiently stored and accessed in flat arrays.
 * 
 * @version 1.0
 * @since   0.1.0
 * 
 * @author  Kheagen Haskins
 * 
 * @see     com.slinky.components.data
 */
public interface ComponentManager<T> {

    /**
     * Assigns the component with default data to the specified entity.
     *
     * <p>
     * This method initialises the component for the given {@code entityId}
     * using predefined default values. Typically, default values are zeros or
     * other neutral values appropriate for the component type.
     * </p>
     *
     * @param entityId The unique identifier of the entity to which the
     * component will be added.
     *
     * @throws IllegalArgumentException if the {@code entityId} is invalid or if
     * the component already exists for the entity.
     * @throws IllegalStateException if the component manager has reached its
     * maximum capacity and cannot add more components.
     */
    void add(int entityId);

    /**
     * Adds a component with specified data to the entity identified by
     * {@code entityId}.
     *
     * <p>
     * This method associates the component data encapsulated in {@code T} with
     * the specified entity. The data should consist of primitive values or
     * structures that can be efficiently stored in the underlying flat arrays.
     * If the entity already possesses the component, this method may either
     * update the existing data or throw an exception based on the
     * implementation.
     * </p>
     *
     * <p>
     * The underlying data storage structure is dynamic, allowing it to grow as
     * new components are added. However, there is a predefined maximum
     * capacity. If {@code entityId} exceeds this capacity or if adding the
     * component would surpass the maximum allowed number of components, an
     * exception is thrown. Typically, capacity management is handled by a
     * higher-level controller, such as the {@code Engine}.
     * </p>
     *
     * @param entityId The unique identifier of the entity to which the
     * component will be added.
     * @param data The component data to be associated with the entity. This
     * data should align with the component's structure and storage schema.
     *
     * @throws IllegalArgumentException if {@code entityId} is invalid, exceeds
     * the allowed capacity, or if the component already exists for the entity.
     * @throws IllegalStateException if the component manager has reached its
     * maximum capacity and cannot add more components.
     */
    void add(int entityId, T data);

    /**
     * Adds multiple components with varying data types to the entity identified
     * by {@code entityId}.
     *
     * <p>
     * This method allows for the addition of multiple components in a single
     * call by accepting a variable number of arguments. The provided data
     * should correspond to the components' storage schema and order as expected
     * by the implementing class. This provides flexibility in initialising
     * multiple aspects of an entity simultaneously.
     * </p>
     *
     * <p>
     * Similar to other addition methods, the underlying data structure is
     * dynamic but subject to a predefined capacity limit. Exceeding this limit
     * or providing mismatched data types will result in exceptions. It is
     * advisable to ensure that the order and type of data arguments align
     * precisely with the component manager's expectations to prevent runtime
     * errors.
     * </p>
     *
     * @param entityId The unique identifier of the entity to which the
     * components will be added.
     * @param data A variable-length argument list containing the component data
     * to be added. The sequence and types of these arguments must match the
     * component manager's storage schema.
     *
     * @throws IllegalArgumentException if {@code entityId} is invalid, exceeds
     * the allowed capacity, if the provided data does not match the expected
     * types, or if components already exist for the entity.
     * @throws IllegalStateException if the component manager has reached its
     * maximum capacity and cannot add more components.
     */
    void add(int entityId, Object... data);

    /**
     * Removes the component associated with the specified entity.
     *
     * <p>
     * This method dissociates the component from the entity identified by
     * {@code entityId}, effectively removing all related data from the
     * underlying storage structures. After removal, the entity will no longer
     * possess the component, and any systems relying on this component will no
     * longer process the entity.
     * </p>
     *
     * <p>
     * If the entity does not possess the component, the method may either
     * perform no action or throw an exception based on the implementation.
     * </p>
     *
     * @param entityId The unique identifier of the entity from which the
     * component will be removed.
     *
     * @throws IllegalArgumentException if {@code entityId} is invalid or if the
     * entity does not possess the component.
     */
    void remove(int entityId);

    /**
     * Retrieves the {@link Component} that this manager is responsible for
     * handling.
     *
     * <p>
     * Each {@code ComponentManager} is dedicated to managing a specific type of
     * component. This method provides a way to query which component type the
     * manager is associated with, facilitating coordination and interactions
     * within the ECS framework.
     * </p>
     *
     * @return The {@code Component} enum constant that this manager is
     * responsible for.
     */
    Component getComponent();

}