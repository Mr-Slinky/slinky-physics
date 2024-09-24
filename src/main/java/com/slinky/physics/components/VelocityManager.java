package com.slinky.physics.components;

import com.slinky.physics.base.EntityManager;
import com.slinky.physics.util.VectorStorage;

/**
 * {@code VelocityManager} is a specialised extension of {@link VectorStorage}
 * designed to handle the storage and management of velocity vectors for
 * entities within the ECS (Entity Component System) architecture. This class
 * does not introduce any additional functionality beyond {@code VectorStorage},
 * but it represents a meaningful abstraction for managing velocity-related data
 * within the system.
 *
 * <p>
 * The class enforces an "is-a" relationship, where {@code VelocityManager}
 * operates identically to {@code VectorStorage}, but is explicitly named for
 * handling velocity vectors. This ensures semantic clarity and type safety when
 * differentiating between position, velocity, force, or other similar vector
 * data in the ECS system.</p>
 *
 * <h2>Use Case</h2>
 * <p>
 * {@code VelocityManager} is used in systems that require managing and
 * processing the velocity data of entities. For example, it can store and
 * update the speed and direction of an entity in a 2D or 3D physics simulation.
 * By using {@code VelocityManager}, developers can avoid mixing up velocity
 * vectors with other types of vector data like position or force, enhancing
 * both code readability and safety.
 * </p>
 * 
 * @version 1.0
 * @since   0.1.0
 * 
 * @author  Kheagen Haskins
 *
 * @see     com.slinky.physics.util.VectorStorage
 */
public final class VelocityManager extends VectorStorage {

    // =============================[ Constructors ]============================= \\
    /**
     * Constructs a {@code VelocityStorage} with the specified initial capacity
     * for entities and a defined maximum capacity. These parameters are passed
     * directly to the internal {@link VectorStorage} instance.
     *
     * @param entityManager the EntityManager managing this components entitites
     * @param initialEntityCapacity the initial number of entities the storage
     *        can hold
     * @param maxEntityCapacity the maximum number of entities this storage can manage
     * @throws IllegalArgumentException if {@code initialEntityCapacity} is less
     *         than or equal to 0, or if {@code initialEntityCapacity} exceeds
     *         {@code maxEntityCapacity}
     */
    public VelocityManager(EntityManager entityManager, int initialEntityCapacity, int maxEntityCapacity) {
        super(Component.VELOCITY, entityManager, initialEntityCapacity, maxEntityCapacity);
    }

}