package com.slinky.physics.components;

import com.slinky.physics.util.VectorStorage;

/**
 * {@code ForceManager} is a specialised extension of {@link VectorStorage}
 * designed to handle the storage and management of force vectors for
 * entities within the ECS (Entity Component System) architecture. This class
 * does not introduce any additional functionality beyond {@code VectorStorage},
 * but it represents a meaningful abstraction for managing force-related data
 * within the system.
 *
 * <p>
 * The class enforces an "is-a" relationship, where {@code ForceManager}
 * operates identically to {@code VectorStorage}, but is explicitly named for
 * handling force vectors. This ensures semantic clarity and type safety when
 * differentiating between position, velocity, force, or other similar vector
 * data in the ECS system.</p>
 *
 * <h2>Use Case</h2>
 * <p>
 * {@code ForceManager} is used in systems that require
 * managing and processing the force data of entities. For example, it can
 * store and apply forces acting on an entity in a 2D or 3D physics
 * simulation. By using {@code ForceManager}, developers can avoid mixing up
 * force vectors with other types of vector data like position or velocity,
 * enhancing both code readability and safety.
 * </p>
 * 
 * @version 1.0
 * @since   0.1.0
 * 
 * @author  Kheagen Haskins
 *
 * @see     com.slinky.physics.util.VectorStorage
 */
public final class ForceManager extends VectorStorage {

    // =============================[ Constructors ]============================= \\
    /**
     * Constructs a {@code ForceStorage} with the specified initial capacity
     * for entities and a defined maximum capacity. These parameters are passed
     * directly to the internal {@link VectorStorage} instance.
     *
     * @param initialEntityCapacity the initial number of entities the storage can hold
     * @param maxEntityCapacity the maximum number of entities this storage can manage
     * @throws IllegalArgumentException if {@code initialEntityCapacity} is less
     *         than or equal to 0, or if {@code initialEntityCapacity} exceeds
     *         {@code maxEntityCapacity}
     */
    public ForceManager(int initialEntityCapacity, int maxEntityCapacity) {
        super(initialEntityCapacity, maxEntityCapacity);
    }

}