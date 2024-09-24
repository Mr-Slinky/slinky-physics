package com.slinky.physics.components;

import com.slinky.physics.components.data.SpringData;
import com.slinky.physics.base.EntityManager;
import com.slinky.physics.util.FloatList;
import com.slinky.physics.util.IntList;
import com.slinky.physics.util.SparseSet;

/**
 * A complex sparse set data structure that maintains two interleaved flat
 * arrays. One of these interleaved arrays contains the spring related data,
 * being the rest length and spring constant, respectively. The other stores the
 * entity IDs of two PointMass components, which is slightly different to the
 * more simpler sparse set data structures in ECS.
 * 
 * @author Kheagen Haskins
 */
public final class SpringManager implements ComponentManager<SpringData> {
    
    // ================================[ Fields ]================================ \\
    private EntityManager entityManager;
    private SparseSet     sparseSet;
    
    // Two interleaved flat arrays. 
    private IntList   pointMassIds; // Stores PointMass A and B
    private FloatList springData;   // Stores rest length then spring constant
    
    private int maxCap;
    
    // =============================[ Constructors ]============================= \\
    public SpringManager(EntityManager entityManager, int initialCapacity, int maxCapacity) {
        this.entityManager = entityManager;
        this.pointMassIds  = new IntList  (initialCapacity << 1); 
        this.springData    = new FloatList(initialCapacity << 1);     
        this.sparseSet     = new SparseSet(maxCapacity);
        this.maxCap        = maxCapacity;
    }
    
    // ===========================[ Accessor Methods ]=========================== \\
    public int size() {
        return sparseSet.size();
    }
    
    public int getMaxCapacity() {
        return maxCap;
    }
    
    public IntList getPointMassIds() {
        return pointMassIds;
    }
    
    public FloatList getSpringData() {
        return springData;
    }
    
    public IntList getEntityIds() {
        return sparseSet.dense();
    }
    
    public int getFirstPointMassIdOf(int entityId) {
        throwIfDoesNotExist(entityId);
        return pointMassIds.get(sparseSet.getIndexOf(entityId) << 1);
    }
    
    public int getSecondPointMassIdOf(int entityId) {
        throwIfDoesNotExist(entityId);
        return pointMassIds.get((sparseSet.getIndexOf(entityId) << 1) + 1);
    }
    
    public float getRestLengthOf(int entityId) {
        throwIfDoesNotExist(entityId);
        return springData.get(sparseSet.getIndexOf(entityId) << 1);
    }
    
    public float getSpringConstantOf(int entityId) {
        throwIfDoesNotExist(entityId);
        return springData.get((sparseSet.getIndexOf(entityId) << 1) + 1);
    }

    public void getPointMassIdsOf(int entityId, int[] dest) {
        throwIfDoesNotExist(entityId);
        if (dest == null || dest.length < 2) {
            throw new IllegalArgumentException("Destination array must have at least two elements.");
        }

        int index = sparseSet.getIndexOf(entityId) << 1;
        dest[0]   = pointMassIds.get(index);
        dest[1]   = pointMassIds.get(index + 1);
    }

    public int[] getPointMassIdsOf(int entityId) {
        throwIfDoesNotExist(entityId);
        int[] outp = new int[2];

        int index = sparseSet.getIndexOf(entityId) << 1;
        outp[0]   = pointMassIds.get(index);
        outp[1]   = pointMassIds.get(index + 1);

        return outp;
    }
    
    public void getSpringDataOf(int entityId, float[] dest) {
        throwIfDoesNotExist(entityId);
        if (dest == null || dest.length < 2) {
            throw new IllegalArgumentException("Destination array must have at least two elements.");
        }
        
        int index = sparseSet.getIndexOf(entityId) << 1;
        dest[0]   = springData.get(index);
        dest[1]   = springData.get(index + 1);
    }
    
    public float[] getSpringDataOf(int entityId) {
        throwIfDoesNotExist(entityId);

        float[] outp = new float[2];
            
        int index = sparseSet.getIndexOf(entityId) << 1;
        outp[0]   = springData.get(index);
        outp[1]   = springData.get(index + 1);
        
        return outp;
    }
    
    public Component getComponent() {
        return Component.SPRING;
    }
    
    // =============================[ API Methods ]============================== \\
    @Override
    public void add(int entityId) {
        throw new UnsupportedOperationException("Spring data cannot be created with default data; PointMass entities are required");
    }

    @Override
    public void add(int entityId, Object... data) {
        if (size() >= maxCap) {
            throw new IllegalStateException("Maximum capacity reached: " + maxCap);
        }

        if (entityId < 0 || entityId >= maxCap) {
            throw new IllegalArgumentException("Spring entity ID out of bounds: " + entityId);
        }

        if (!sparseSet.add(entityId)) {
            throw new IllegalArgumentException("Spring entity already exists: " + entityId);
        }

        pointMassIds.add((int)   data[0]);
        pointMassIds.add((int)   data[1]);
        springData  .add((float) data[2]);
        springData  .add((float) data[3]);

        // Update entity's component bitmask
        entityManager.addComponentTo(entityId, Component.SPRING);
    }
    
    @Override
    public void add(int entityId, SpringData data) {
        add(entityId, data.pointMassId1(), data.pointMassId2(), data.restLength(), data.springConstant());
    }
    
    /**
     * {@inheritDoc}
     */
    @Override
    public void remove(int entityId) {
        throwIfDoesNotExist(entityId);

        int indexToRemove = sparseSet.getIndexOf(entityId) << 1;
        int lastIndex     = (size() - 1) << 1;

        if (indexToRemove != lastIndex) {
            // Swap point mass IDs
            pointMassIds.set(indexToRemove,     pointMassIds.get(lastIndex));
            pointMassIds.set(indexToRemove + 1, pointMassIds.get(lastIndex + 1));

            // Swap rest length and spring constant
            springData.set(indexToRemove,     springData.get(lastIndex));
            springData.set(indexToRemove + 1, springData.get(lastIndex + 1));
        }

        // Remove the last elements
        pointMassIds.pop();
        pointMassIds.pop();
        springData  .pop();
        springData  .pop();
        sparseSet   .remove(entityId); // will do a swap and pop internally

        // Update entity's component bitmask
        entityManager.removeComponent(entityId, Component.SPRING);
    }
    
    public boolean contains(int entityId) {
        return sparseSet.contains(entityId);
    }
    
    // ============================[ Helper Methods ]============================ \\
    private void throwIfDoesNotExist(int entityId) {
        if (!sparseSet.contains(entityId)) {
            throw new IllegalArgumentException("Spring entity does not exist: " + entityId);
        }
    }

}

/* 
Possible Future Additions:
  - Make max capacity dynamic  
*/