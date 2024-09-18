package com.slinky.physics.components;

import com.slinky.physics.base.EntityManager;
import java.util.Arrays;

/**
 * A sparse set collection implemented with primitive arrays, that should be
 * attached to a component.
 * 
 * @author Kheagen Haskins
 */
public class SparseSet {

    // ============================== Fields ================================ //
    private int     cap;
    private int[]   entityIdIndex;
    private IntList entityIds; // resizable primitive array
    
    // =========================== Constructors ============================= //
    public SparseSet(int entityLimit) {
        cap           = entityLimit;
        entityIdIndex = new int[entityLimit];
        entityIds     = new IntList(0);
        
        Arrays.fill(entityIdIndex, -1);
    }
    
    public SparseSet() {
        this(EntityManager.ENTITY_LIMIT);
    }
    
    // ============================== Getters =============================== //
    /**
     * Returns the current number of entities that have the component.
     *
     * @return the size of the dense array
     */
    public int size() {
        return entityIds.size();
    }

    /**
     * Retrieves the entity ID at the specified index in the dense array.
     *
     * @param  index the index in the dense array
     * @return the entity ID at the given index
     * @throws IndexOutOfBoundsException if the index is invalid
     */
    public int getEntityAt(int index) {
        return entityIds.get(index);
    }
    
    /**
     * Retrieves the dense index for a given entity ID. Assumes that the entity
     * has the component.
     *
     * @param  entityId the ID of the entity
     * @return the index in the dense array
     * @throws IllegalArgumentException if the entity does not have the
     *         component
     */
    public int getDenseIndex(int entityId) {
        if (!contains(entityId)) {
            throw new IllegalArgumentException("Entity does not have the component.");
        }
        
        return entityIdIndex[entityId];
    }
    
    // ============================ API Methods ============================= //
    /**
     * Adds an entity to the SparseSet if it doesn't already have the component.
     *
     * @param eID the ID of the entity to add
     * @return {@code true} if the entity was added, {@code false} if it was
     *         already present
     */
    public boolean add(int eID) {
        if (eID >= cap) {
            throw new IllegalArgumentException(String.format("%d cannot be above SparseSet's capacity: %d", eID, cap));
        }
        
        if (contains(eID)) {
            return false; // Already present
        } 
        
        entityIds.add(eID);
        entityIdIndex[eID] = entityIds.size() - 1;
        
        return true;
    }

    /**
     * Removes an entity from the SparseSet if it has the component.
     *
     * @param  entityId the ID of the entity to remove
     * @return {@code true} if the entity was removed, {@code false} if it was
     *         not present
     */
    public boolean remove(int entityId) {
        if (!contains(entityId)) {
            return false; // Not present
        }
        
        int indexToRemove = entityIdIndex[entityId];
        int lastEntityId  = entityIds.get(entityIds.size() - 1);
        // Swap the last entity with the one to remove
        entityIds.set(indexToRemove, lastEntityId);
        entityIdIndex[lastEntityId] =  indexToRemove;
        // Remove the last entity from dense
        entityIds.remove(entityIds.size() - 1);
        // Reset the sparse entry for the removed entity
        entityIdIndex[entityId] = -1;
        
        return true;
    }

    /**
     * Checks if an entity has the component.
     *
     * @param  entityId the ID of the entity to check
     * @return {@code true} if the entity has the component, {@code false}
     *         otherwise
     */
    public boolean contains(int entityId) {
        if (entityId < 0 || entityId >= cap) {
            return false;
        }
        
        int i = entityIdIndex[entityId];
        return i >= 0 && i < entityIds.size() && entityIds.get(i) == entityId;
    }


    @Override
    public String toString() {
        StringBuilder outp = new StringBuilder("SparseSet:\n");
        String[] ids = new String[cap];
        for (int i = 0; i < cap; i++) {
            ids[i] = entityIdIndex[i] == -1 ? "0" + i : "" + i;
        }
        
        return outp.append("IDs:\t\t")     .append(Arrays.toString(ids))          .append("\n")
                   .append("Dense Index\t").append(Arrays.toString(entityIdIndex)).append("\n")
                   .append("Sparse Arr:\t").append(entityIds)
                   .toString();
    }
    
}