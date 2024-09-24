package com.slinky.physics.systems;

/**
 * Represents a system within the ECS framework that performs operations on
 * entities possessing specific components. Systems encapsulate the logic that
 * processes and updates entity data each frame or tick, enabling modular and
 * maintainable game or simulation architectures.
 *
 * <p>
 * The {@code ISystem} interface is a {@code @FunctionalInterface}, allowing
 * systems to be implemented using lambda expressions or method references if
 * desired. Each system must provide an implementation of the {@link #update()}
 * method, which contains the core processing logic executed during the engine's
 * update cycle.
 * </p>
 *
 * <p>
 * Systems are responsible for:
 * </p>
 * <ul>
 * <li>
 * Iterating over relevant entities and their associated components.
 * </li>
 * <li>
 * Performing computations, updates, or actions based on component data.
 * </li>
 * <li>
 * Maintaining game or simulation state consistency and enforcing game rules.
 * </li>
 * </ul>
 *
 * <h2>Usage Example</h2>
 * <p>
 * Below is an example of how to implement a simple {@code MovementSystem} that
 * updates the positions of entities based on their velocities:
 * </p>
 *
 * <pre><code>
 * public class MovementSystem implements ISystem {
 *     private final PositionManager positionManager;
 *     private final VelocityManager velocityManager;
 *     private final float deltaTime;
 *
 *     public MovementSystem(PositionManager positionManager, VelocityManager velocityManager, float deltaTime) {
 *         this.positionManager = positionManager;
 *         this.velocityManager = velocityManager;
 *         this.deltaTime = deltaTime;
 *     }
 *
 *     @Override
 *     public void update() {
 *         for (int entityId : positionManager.getEntityIds()) {
 *             Vector2D position = positionManager.getPosition(entityId);
 *             Vector2D velocity = velocityManager.getVelocity(entityId);
 *
 *             // Update position based on velocity and deltaTime
 *             position.setX(position.getX() + velocity.getX() * deltaTime);
 *             position.setY(position.getY() + velocity.getY() * deltaTime);
 *
 *             // Persist the updated position
 *             positionManager.setPosition(entityId, position);
 *         }
 *     }
 * }
 * </code></pre>
 *
 * <h2>Integration with Engine</h2>
 * <p>
 * Systems are typically registered with the
 * {@link com.slinky.physics.base.Engine} class, which manages their lifecycle
 * and invokes their {@code update} methods during the engine's update cycle.
 * The order in which systems are added to the engine determines the sequence of
 * their execution, allowing for dependency management between systems.
 * </p>
 *
 * @version 1.0
 * @since   0.1.0
 *
 * @author  Kheagen Haskins
 *
 * @see     com.slinky.physics.base.Engine
 * @see     com.slinky.physics.components.ComponentManager
 */
@FunctionalInterface
public interface ISystem {

    // =============================[ API Methods ]============================== \\
    /**
     * Executes the system's core logic, processing and updating relevant
     * entities and their components.
     *
     * <p>
     * The {@code update} method is called by the engine during each update
     * cycle. Systems should implement this method to perform operations such as
     * physics calculations, input handling, AI processing, or rendering tasks.
     * </p>
     *
     * <p>
     * Implementations should strive for efficiency within this method to
     * maintain high performance, especially in real-time applications like
     * games or simulations.
     * </p>
     */
    void update();

}