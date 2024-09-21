package com.slinky.physics.util;

import java.util.Arrays;

/**
 * A highly efficient sparse set implementation that leverages primitive arrays,
 * optimised for use within an Entity Component System (ECS) framework.
 * <p>
 * The `SparseSet` class manages a collection of entity IDs associated with
 * a specific component. It employs a sparse array (`sparse`) and a dense list
 * (`dense`) to ensure constant-time operations for adding, removing, and
 * verifying the presence of entities. This design minimises memory overhead
 * and maximises performance by avoiding unnecessary object allocations.
 * </p>
 * <p>
 * Key Features:
 * </p>
 * <ul>
 * <li><strong>Constant-Time Operations:</strong> Add, remove, and contains
 * checks are performed in O(1) time.</li>
 * <li><strong>Primitive Data Structures:</strong> Utilises primitive `int`
 * arrays and a custom `IntList` to avoid the overhead associated with object wrappers.</li>
 * <li><strong>Efficient Memory Usage:</strong> The sparse array maps entity IDs
 * directly to their indices in the dense list, ensuring minimal memory
 * usage.</li>
 * <li><strong>Optimised Removal:</strong> The removal operation swaps the
 * entity to be removed with the last entity in the dense list, maintaining
 * continuity without leaving gaps.</li>
 * </ul>
 * <p><strong>Example Usage:</strong></p>
 * <pre>{@code
 *     // Initialise a SparseSet with a capacity of 1000 entities
 *     SparseSet positionComponents = new SparseSet(1000);
 *
 *     // Add an entity with ID 42
 *     positionComponents.add(42);
 *
 *     // Check if entity 42 has the component
 *     if (positionComponents.contains(42)) {
 *         int index = positionComponents.getIndexOf(42);
 *         // Perform operations using the index
 *     }
 *
 *     // Remove entity 42
 *     positionComponents.remove(42);
 * }</pre>
 *
 * @version 1.1
 * @since   0.1.0
 *
 * @author Kheagen Haskins
 */
public class SparseSet {

    // ============================== Fields ================================ //
    /**
     * The maximum number of entities that can be managed by this SparseSet.
     */
    private final int cap;

    /**
     * The sparse array mapping entity IDs to their indices in the dense list.
     * Initialized with -1 for all entries to indicate the absence of entities.
     */
    private final int[] sparse;

    /**
     * The dense list storing active entity IDs that possess the component.
     * Utilizes a custom resizable primitive array to minimize memory overhead.
     */
    private final IntList dense; // resizable primitive array

    // =========================== Constructors ============================= //
    /**
     * Constructs a new SparseSet with the specified entity limit.
     *
     * @param entityLimit the maximum number of entities the set can accommodate
     * @throws IllegalArgumentException if {@code entityLimit} is non-positive
     */
    public SparseSet(int entityLimit) {
        if (entityLimit <= 0) {
            throw new IllegalArgumentException("Entity limit must be positive.");
        }
        
        this.cap    = entityLimit;
        this.sparse = new int[entityLimit];
        this.dense  = new IntList(); 

        Arrays.fill(sparse, -1);
    }

    // ============================== Getters =============================== //
    /**
     * Returns the current number of entities that possess the component.
     *
     * @return the size of the dense list
     */
    public int size() {
        return dense.size();
    }

    /**
     * Retrieves the entity ID at the specified index in the dense list.
     *
     * @param index the index in the dense list
     * @return the entity ID at the given index
     * @throws IndexOutOfBoundsException if the index is out of range (index
     * &lt; 0 || index &ge; size())
     */
    public int getEntityAt(int index) {
        return dense.get(index);
    }

    /**
     * Retrieves the index in the dense list for the specified entity ID.
     * Assumes that the entity has the component; otherwise, an exception is
     * thrown.
     *
     * @param entityId the ID of the entity
     * @return the index in the dense list where the entity ID is stored
     * @throws IllegalArgumentException if the entity does not possess the
     * component
     */
    public int getIndexOf(int entityId) {
        if (!contains(entityId)) {
            throw new IllegalArgumentException("Entity ID " + entityId + " does not have the component.");
        }

        return sparse[entityId];
    }

    // ============================ API Methods ============================= //
    /**
     * Adds an entity to the SparseSet if it does not already possess the
     * component.
     *
     * @param eID the ID of the entity to add
     * @return {@code true} if the entity was successfully added, {@code false}
     * if it was already present
     * @throws IllegalArgumentException if {@code eID} is negative or exceeds
     * the set's capacity
     */
    public boolean add(int eID) {
        if (eID < 0 || eID >= cap) {
            throw new IndexOutOfBoundsException(String.format("Entity ID %d is out of bounds (0 to %d).", eID, cap - 1));
        }

        if (contains(eID)) {
            return false; // Entity already present
        }

        dense.add(eID);
        sparse[eID] = dense.size() - 1;

        return true;
    }

    /**
     * Removes an entity from the SparseSet if it possesses the component.
     *
     * @param entityId the ID of the entity to remove
     * @return {@code true} if the entity was successfully removed,
     * {@code false} if it was not present
     */
    public boolean remove(int entityId) {
        if (!contains(entityId)) {
            return false; // Entity not present
        }

        int indexToRemove = sparse[entityId];
        int lastIndex     = dense.size() - 1;
        int lastEntityId  = dense.get(lastIndex);

        if (entityId != lastEntityId) {
            // Swap the entity to remove with the last entity in the dense list
            dense.set(indexToRemove, lastEntityId);
            sparse[lastEntityId] = indexToRemove;
        }

        // Remove the last entity from the dense list
        dense.remove(lastIndex);
        // Reset the sparse entry for the removed entity
        sparse[entityId] = -1;

        return true;
    }

    /**
     * Checks whether the specified entity possesses the component.
     *
     * @param entityId the ID of the entity to check
     * @return {@code true} if the entity has the component, {@code false}
     * otherwise
     */
    public boolean contains(int entityId) {
        if (entityId < 0 || entityId >= cap) {
            return false;
        }

        int i = sparse[entityId];
        return i >= 0 && i < dense.size() && dense.get(i) == entityId;
    }
    
    /**
     * Clears the SparseSet by resetting all entries in the sparse array to -1
     * and clearing the dense list. This effectively removes all entities from
     * the set.
     * <p>
     * After calling this method, the SparseSet will be empty and ready for
     * reuse.
     * </p>
     */
    public void clear() {
        Arrays.fill(sparse, -1);
        dense.clear();
    }

    /**
     * Returns the sparse array used by the SparseSet. The sparse array maps
     * entity IDs directly to their indices in the dense list. Any entity ID not
     * present in the set will have a corresponding value of -1 in the sparse
     * array.
     *
     * @return the sparse array containing the mapping of entity IDs to their
     * indices in the dense list
     */
    int[] sparse() {
        return sparse;
    } // for testing

    /**
     * Returns the dense list used by the SparseSet. The dense list stores all
     * active entity IDs that are currently in the set. This list can be used to
     * iterate over all entities in the set.
     *
     * @return the dense list containing active entity IDs
     */
    IntList dense() {
        return dense;
    } // for testing


    // ============================ Overrides ================================ //
    /**
     * Compares this SparseSet to the specified object for equality. Two
     * SparseSets are considered equal if they have the same capacity, identical
     * sparse arrays, and equivalent dense lists.
     *
     * @param obj the object to compare with
     * @return {@code true} if the specified object is equal to this SparseSet,
     * {@code false} otherwise
     */
    @Override
    public boolean equals(Object obj) {
        if (this == obj) {
            return true;
        }
        if (!(obj instanceof SparseSet)) {
            return false;
        }
        SparseSet other = (SparseSet) obj;
        return this.cap == other.cap
                && Arrays.equals(this.sparse, other.sparse)
                && this.dense.equals(other.dense);
    }

    /**
     * Returns the hash code value for this SparseSet.
     *
     * @return the hash code value
     */
    @Override
    public int hashCode() {
        int result = Integer.hashCode(cap);
        result = 31 * result + Arrays.hashCode(sparse);
        result = 31 * result + dense.hashCode();
        return result;
    }

    /**
     * Returns a string representation of the SparseSet, including its capacity,
     * sparse array, and dense list.
     *
     * @return a string detailing the contents of the SparseSet
     */
    @Override
    public String toString() {
        StringBuilder outp = new StringBuilder("SparseSet:\n");
        String[] ids = new String[cap];
        for (int i = 0; i < cap; i++) {
            ids[i] = sparse[i] == -1 ? "None" : String.valueOf(i);
        }

        outp.append("Entity IDs:\t").append(Arrays.toString(ids)).append("\n")
            .append("Sparse Indices:\t").append(Arrays.toString(sparse)).append("\n")
            .append("Dense List:\t").append(dense);

        return outp.toString();
    }

}