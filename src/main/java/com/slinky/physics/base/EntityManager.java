package com.slinky.physics.base;

import com.slinky.physics.components.Component;
import com.slinky.physics.entities.Archetype;
import com.slinky.physics.util.IntList;
import java.util.Arrays;

/**
 * <p>
 * Manages the creation and deletion of entities within the ECS (Entity
 * Component System) architecture, using a <em>SparseSet</em> design pattern.
 * This pattern ensures constant time (O(1)) operations for both entity creation
 * and component retrieval, while maintaining optimal data locality by using
 * flat primitive arrays.
 * </p>
 *
 * <p>
 * The implementation comprises a dense array (represented by {@code IntList})
 * that stores active entity IDs and their corresponding component bitmasks. A
 * sparse array ({@code int[]}) is employed to map entity IDs to indices within
 * the dense array, allowing for efficient entity lookup and access.
 * </p>
 *
 * <p>
 * Additionally, this design enables efficient entity ID recycling through a
 * stack-based system of free IDs. When entities are destroyed, their IDs are
 * added to the stack, allowing these IDs to be reused in subsequent entity
 * creation, thus avoiding ID exhaustion.
 * </p>
 *
 * <p>
 * <strong>Performance:</strong></p>
 * <ul>
 * <li>O(1) entity creation and deletion</li>
 * <li>O(1) component access</li>
 * </ul>
 *
 * <p>
 * <strong>Design Notes:</strong></p>
 * <ul>
 * <li>This class is package-private to restrict direct access. It is intended
 * to be used internally by the {@code Engine} class to manage entities
 * efficiently.</li>
 * </ul>
 *
 * <p>
 * <strong>Usage:</strong></p>
 * <p>
 * This class is designed to be used in conjunction with the {@code Engine}
 * class, which handles the higher-level logic of the ECS framework.</p>
 *
 * <p><strong>Component Management:</strong></p>
 * <p>
 * Adding or removing components from entities in this class only updates the 
 * bitmask associated with the entity. This means that while the component bitmask 
 * reflects the components an entity possesses, the actual component data removal 
 * is deferred to the {@code ComponentManager}. This separation of responsibilities 
 * allows for efficient updates to entity-component relationships without 
 * immediately impacting the underlying component storage.
 * </p>
 * <p>
 * As the {@code EntityManager} is coordinated by the {@code Engine} class, 
 * it is the responsibility of the {@code Engine} to ensure that when an 
 * entity’s bitmask is updated (i.e., a component is added or removed), 
 * the corresponding component data is properly handled by the {@code ComponentManager}. 
 * This ensures that component data is only removed when appropriate and in line 
 * with the entity's updated component configuration.
 * </p>
 * 
 * @version 1.1
 * @since   0.1.0
 * 
 * @author  Kheagen Haskins
 * 
 * @see     Engine
 */
public final class EntityManager {

    // ============================== Constants ============================= //
    /**
     * The maximum number of entities that can be managed by the
     * {@code EntityManager}. This constant defines an upper limit to prevent
     * memory over-allocation and ensure the system operates within reasonable
     * bounds.
     */
    public static final int ENTITY_LIMIT = 1_000_000;

    // ============================== Fields ================================ //
    /**
     * A sparse array mapping entity IDs to indices in the dense arrays. This
     * array allows constant time (O(1)) lookups to determine if an entity is
     * active and retrieve its position in the dense arrays. Unused slots are
     * initialised to -1.
     */
    private final int[] sparse;

    /**
     * A dense array of active entity IDs, stored in {@code IntList} to ensure
     * efficient iteration and data locality. The indices in this list map
     * directly to active entities.
     */
    private final IntList denseEntityIDs;

    /**
     * A dense array of component masks corresponding to active entities, stored
     * in {@code IntList}. Each entry is a bitmask representing the components
     * an entity possesses.
     */
    private final IntList denseComponentMasks;

    /**
     * A stack of free entity IDs that have been recycled. When entities are
     * deleted, their IDs are pushed onto this stack for reuse. This ensures
     * that IDs are reused efficiently, reducing the likelihood of ID
     * exhaustion.
     */
    private final IntList freeEntityIDs;

    /**
     * The next available entity ID to assign when creating new entities. This
     * value is incremented sequentially until the pool of IDs is exhausted,
     * after which the system reuses IDs from {@code freeEntityIDs}.
     */
    private int nextEntityID;

    /**
     * The maximum number of entities that this {@code EntityManager} instance
     * can handle. This value is set upon instantiation and must be a positive
     * number less than or equal to {@code ENTITY_LIMIT}.
     */
    private final int entityCapacity;

    // =========================== Constructors ============================= //
    /**
     * Constructs a new {@code EntityManager} with a specified capacity for
     * entities.
     *
     * <p>
     * The capacity determines the maximum number of entities that can be
     * managed by this instance. If the provided capacity is less than or equal
     * to zero, or exceeds the {@code ENTITY_LIMIT}, an
     * {@code IllegalArgumentException} is thrown.
     * </p>
     *
     * <p>
     * During initialisation, the sparse array is filled with {@code -1} to
     * indicate that no entities are mapped at any given index.
     * </p>
     *
     * @param capacity the maximum number of entities this manager can handle
     * @throws IllegalArgumentException if the capacity is less than or equal to
     * zero or exceeds {@code ENTITY_LIMIT}
     */
    public EntityManager(int capacity) {
        if (capacity <= 0 || capacity > ENTITY_LIMIT) {
            throw new IllegalArgumentException(
                    "EntityManager#constructor(int capacity): capacity must be a positive number less than or equal to "
                    + ENTITY_LIMIT + ", but was " + capacity
            );
        }

        this.entityCapacity      = capacity;
        this.denseEntityIDs      = new IntList(capacity);
        this.denseComponentMasks = new IntList(capacity);
        this.sparse              = new int[capacity];
        this.freeEntityIDs       = new IntList();
        this.nextEntityID        = 0;

        Arrays.fill(sparse, -1);
    }

    // ============================== Getters =============================== //
    /**
     * Checks whether the given {@code entityID} corresponds to an active
     * entity.
     *
     * @param entityID the ID of the entity to check
     * @return {@code true} if the entity exists, {@code false} otherwise
     */
    public boolean hasEntity(int entityID) {
        return entityID >= 0 && entityID < entityCapacity && sparse[entityID] >= 0;
    }

    /**
     * Retrieves the component bitmask for the specified {@code entityID}.
     *
     * <p>
     * The bitmask represents all the components an entity currently possesses.
     * If the entity does not exist, an {@code IllegalArgumentException} is
     * thrown.
     * </p>
     *
     * @param entityID the ID of the entity whose component mask is requested
     * @return the component bitmask of the entity
     * @throws IllegalArgumentException if the entity does not exist
     */
    public int getComponentMask(int entityID) {
        if (!hasEntity(entityID)) {
            throw new IllegalArgumentException(
                    "EntityManager#getComponentMask: Entity ID " + entityID + " does not exist."
            );
        }
        int index = sparse[entityID];
        return denseComponentMasks.get(index);
    }

    public int getEntityCapacity() {
        return entityCapacity;
    }
    
    // ============================ API Methods ============================= //
    /**
     * Creates a new entity and returns its unique {@code entityID}.
     *
     * <p>
     * If there are any previously deleted entities, their IDs are recycled.
     * Otherwise, a new ID is generated sequentially. If the maximum entity
     * capacity is reached, an {@code IllegalStateException} is thrown.
     * </p>
     *
     * <p>
     * The newly created entity is initialised with no components, which is
     * represented by a component bitmask of 0.
     * </p>
     *
     * @return the unique {@code entityID} of the newly created entity
     * @throws IllegalStateException if the entity capacity has been reached
     */
    public int createEntity() {
        int entityID = nextId();
        int index    = denseEntityIDs.size();

        denseEntityIDs.add(entityID);
        denseComponentMasks.add(0); // Initialise with no components
        sparse[entityID] = index;

        return entityID;
    }
    
    /**
     * Creates a new entity based on the provided {@code Archetype} and returns
     * its unique {@code entityID}.
     * <p>
     * The method assigns components to the entity according to the bitmask
     * defined by the archetype. It checks each {@code Component} and, if the
     * component is present in the archetype's bitmask, the component is added
     * to the entity. The entity ID is unique and generated internally.
     * </p>
     * 
     * @param archetype the archetype that defines which components the new
     * entity should have
     * @return the unique {@code entityID} of the newly created entity
     * @throws IllegalStateException if the entity capacity has been reached and
     * no new entities can be created
     */
    public int createEntity(Archetype archetype) {
        int entityId = createEntity();
        int index, maskBefore, maskAfter;
        for (Component comp : archetype.components()) {
            index      = sparse[entityId];
            maskBefore = denseComponentMasks.get(index);
            maskAfter  = maskBefore | comp.bit();
            
            denseComponentMasks.set(index, maskAfter);
        }
        return entityId;
    }

    /**
     * Destroys the entity with the specified {@code entityID}.
     *
     * <p>
     * The method removes the entity from the dense array and updates the sparse
     * array accordingly. If the entity to be removed is not the last entity in
     * the dense array, the last entity is swapped into its position to maintain
     * array compactness. The destroyed entity's ID is added to the
     * {@code freeEntityIDs} stack for reuse.
     * </p>
     *
     * @param entityID the ID of the entity to destroy
     * @return {@code true} if the entity was successfully destroyed,
     * {@code false} if the entity did not exist
     */
    public boolean destroyEntity(int entityID) {
        if (!hasEntity(entityID)) {
            return false;
        }

        int index        = sparse[entityID];
        int lastIndex    = denseEntityIDs.size() - 1;
        int lastEntityID = denseEntityIDs.get(lastIndex);

        // Swap the last entity with the one to remove if they are not the same
        if (index != lastIndex) {
            denseEntityIDs     .set(index, lastEntityID);
            denseComponentMasks.set(index, denseComponentMasks.get(lastIndex));
            sparse[lastEntityID] = index;
        }

        // Remove the last entity
        denseEntityIDs.pop();
        denseComponentMasks.pop();
        sparse[entityID] = -1;
        freeEntityIDs.add(entityID);

        return true;
    }

    /**
     * Adds a component to the specified entity.
     *
     * <p>
     * The component is added by updating the entity's component bitmask. If the
     * entity does not already have the component, the bitmask is modified to
     * include the component's bit. If the entity does not exist, an
     * {@code IllegalArgumentException} is thrown.
     * </p>
     *
     * @param entityID the ID of the entity to which the component will be added
     * @param comp the component to add
     * @return {@code true} if the component was successfully added,
     * {@code false} if the entity already had the component
     * @throws IllegalArgumentException if the entity does not exist
     */
    public boolean addComponentTo(int entityID, Component comp) {
        if (!hasEntity(entityID)) {
            throw new IllegalArgumentException(
                    "EntityManager#addComponent: Entity ID " + entityID + " does not exist."
            );
        }
        
        int index      = sparse[entityID];
        int maskBefore = denseComponentMasks.get(index);
        int maskAfter  = maskBefore | comp.bit();
        denseComponentMasks.set(index, maskAfter);
        return maskBefore != maskAfter;
    }

    /**
     * Removes a component from the specified entity.
     *
     * <p>
     * The component is removed by updating the entity's component bitmask. If
     * the entity does not have the component, no change is made. If the entity
     * does not exist, an {@code IllegalArgumentException} is thrown.
     * </p>
     *
     * @param entityID the ID of the entity from which the component will be
     * removed
     * @param comp the component to remove
     * @return {@code true} if the component was successfully removed,
     * {@code false} if the entity did not have the component
     * @throws IllegalArgumentException if the entity does not exist
     */
    public boolean removeComponent(int entityID, Component comp) {
        if (!hasEntity(entityID)) {
            throw new IllegalArgumentException(
                    "EntityManager#removeComponent: Entity ID " + entityID + " does not exist."
            );
        }
        
        int index = sparse[entityID];
        int maskBefore = denseComponentMasks.get(index);
        int maskAfter = maskBefore & ~comp.bit();
        denseComponentMasks.set(index, maskAfter);
        return maskBefore != maskAfter;
    }

    /**
     * Checks if the specified entity has the given component.
     *
     * @param entityID the ID of the entity to check
     * @param comp the component to check for
     * @return {@code true} if the entity has the component, {@code false}
     * otherwise
     */
    public boolean hasComponent(int entityID, Component comp) {
        if (!hasEntity(entityID)) {
            return false;
        }
        
        int index = sparse[entityID];
        int mask  = denseComponentMasks.get(index);
        return (mask & comp.bit()) != 0;
    }
    
    /**
     * Generates the next available entity ID.
     * <p>
     * If there are any previously freed entity IDs, one will be reused by
     * popping from the `freeEntityIDs` stack. If no free IDs are available, a
     * new one is generated by incrementing `nextEntityID`. If the entity
     * capacity has been reached, an exception is thrown.
     *
     * @return the next available entity ID
     * @throws IllegalStateException if the maximum entity capacity has been
     *         reached
     */
    private int nextId() {
        int entityId;
        if (!freeEntityIDs.isEmpty()) {
            entityId = freeEntityIDs.pop();
        } else {
            if (nextEntityID >= entityCapacity) {
                throw new IllegalStateException(
                        "EntityManager#createEntity: Reached maximum entity capacity of " + entityCapacity
                );
            }
            entityId = nextEntityID++;
        }
        
        return entityId;
    }
    
}