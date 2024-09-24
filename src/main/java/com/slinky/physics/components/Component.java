package com.slinky.physics.components;

/**
 * The {@code Component} enum represents various physical properties 
 * that can be associated with entities in the physics engine. 
 * Each enum constant corresponds to a specific component that influences 
 * the physical behaviour of entities such as position, velocity, and forces.
 *
 * <p>Components in this enum are assigned bit flags (powers of two) 
 * which allow them to be efficiently combined and checked using bitwise operations. 
 * This approach enables the system to handle multiple components 
 * on an entity in a compact and performant way.</p>
 *
 * <p>The following components are currently available:</p>
 * <ul>
 *   <li>{@link #POSITION}: Represents the position of an entity in the 2D space.</li>
 *   <li>{@link #VELOCITY}: Represents the linear velocity of the entity.</li>
 *   <li>{@link #FORCE}: Represents the accumulated forces acting on the entity.</li>
 *   <li>{@link #MASS}: Represents the mass of the entity, affecting its inertia.</li>
 *   <li>{@link #DAMPING}: Represents the damping factor applied to the entity, 
 *       which reduces its velocity over time due to drag or friction.</li>
 *   <li>{@link #RESTITUTION}: Represents the bounciness or elasticity of the entity, 
 *       influencing how it responds to collisions.</li>
 *   <li>{@link #LIFE_TIME}: Represents the lifespan of the entity, 
 *       allowing it to exist for a finite number of frames.</li>
 * </ul>
 * 
 * @version 1.0
 * @since   0.1.0
 * 
 * @author  Kheagen Haskins
 */
public enum Component {

    /**
     * Represents the position of an entity in the 2D space. This component is
     * used to track and update the coordinates (x, y) of an entity in the
     * simulation.
     *
     * <p>
     * Bit flag value: {@code 0b1} (1)
     * </p>
     */
    POSITION(1),
    
    /**
     * Represents the velocity of an entity, specifically its linear velocity.
     * Velocity defines the rate of change of position over time and is
     * influenced by forces and damping. This component is essential for moving
     * entities.
     *
     * <p>
     * Bit flag value: {@code 0b10} (2)
     * </p>
     */
    VELOCITY(1 << 1),
    
    /**
     * Represents the accumulated forces acting on an entity. These forces are
     * used to calculate the acceleration of the entity according to Newton's
     * second law (F = ma).
     *
     * <p>
     * Bit flag value: {@code 0b100} (4)
     * </p>
     */
    FORCE(1 << 2),
    
    /**
     * Represents the mass of an entity. Mass affects how much an entity resists
     * acceleration when subjected to forces. It is an essential component for
     * any entity that needs realistic physical behaviour.
     *
     * <p>
     * Bit flag value: {@code 0b1000} (8)
     * </p>
     */
    MASS(1 << 3),
    
    /**
     * Represents the damping factor applied to an entity's velocity. Damping
     * reduces the entity's velocity over time, simulating drag or friction. It
     * prevents the entity from moving indefinitely and adds stability to the
     * simulation.
     *
     * <p>
     * Bit flag value: {@code 0b10000} (16)
     * </p>
     */
    DAMPING(1 << 4),
    
    /**
     * Represents the restitution or bounciness of an entity. Restitution is
     * used in collision responses to determine how much energy is retained
     * after a collision. A value of 1.0 means the entity is perfectly elastic
     * (no energy loss), while a value less than 1.0 indicates some energy is
     * lost in the collision.
     *
     * <p>
     * Bit flag value: {@code 0b100000} (32)
     * </p>
     */
    RESTITUTION(1 << 5),
    
    /**
     * Represents the lifespan of an entity in the simulation. The lifespan
     * determines how long an entity should remain active before being removed.
     * This component is useful for entities that should exist for a finite
     * number of frames, such as projectiles or temporary effects.
     *
     * <p>
     * Bit flag value: {@code 0b1000000} (64)
     * </p>
     */
    LIFE_TIME(1 << 6),
    
    
    SPRING(1 << 7);

    /**
     * The bit flag associated with the component. Each component is represented
     * by a unique power of two, allowing for efficient bitwise operations to
     * check if an entity has a specific set of components.
     */
    private int bit;
    
    /**
     * Constructs a {@code Component} with the specified bit flag.
     *
     * @param bit the bit flag representing the component
     */
    private Component(int bit) {
        this.bit = bit;
    }
      
    /**
     * Returns the bit flag associated with this component. The bit flag is used
     * in bitwise operations to efficiently check or modify which components an
     * entity possesses.
     *
     * @return the bit flag representing this component
     */
    public int bit() {
        return bit;
    }
    
}