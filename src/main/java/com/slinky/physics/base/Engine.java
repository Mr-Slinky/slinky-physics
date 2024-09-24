package com.slinky.physics.base;

import com.slinky.physics.components.Component;
import com.slinky.physics.components.ComponentManager;
import com.slinky.physics.components.ForceManager;
import com.slinky.physics.components.MassManager;
import com.slinky.physics.components.PositionManager;
import com.slinky.physics.components.RestitutionManager;
import com.slinky.physics.components.SpringManager;
import com.slinky.physics.components.VelocityManager;

import com.slinky.physics.entities.Archetype;

import com.slinky.physics.systems.ISystem;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import java.util.Map;

/**
 * A facade acting as the main controller of all physics systems.
 *
 * @version 0.1
 * @since   0.1.0
 * 
 * @author Kheagen Haskins
 */
public final class Engine {

    // ============================== Static ================================ //
    public static final int VECTOR_MASK = Component.POSITION.bit()
                                        | Component.VELOCITY.bit()
                                        | Component.FORCE   .bit();
    
    public static final int SCALAR_MASK = Component.MASS       .bit()
                                        | Component.DAMPING    .bit()
                                        | Component.RESTITUTION.bit()
                                        | Component.LIFE_TIME  .bit();

    // ============================== Fields ================================ //
    // Component Manager is a marker interface; it has no methods defined by that can change
    private EntityManager entMan;
    private List<ISystem> systems = new ArrayList<>();
    private Map<Component, ComponentManager> managers = new HashMap<>();

    // =========================== Constructors ============================= //
    public Engine(EntityManager entityManager) {
        this.entMan = entityManager;
        
        // This is going to become incredibly bloated as more components are added... Hmmmm!
        managers.put(Component.POSITION,    new PositionManager   (entityManager, 16, entityManager.getEntityCapacity()));
        managers.put(Component.VELOCITY,    new VelocityManager   (entityManager, 16, entityManager.getEntityCapacity()));
        managers.put(Component.FORCE,       new ForceManager      (entityManager, 16, entityManager.getEntityCapacity()));
        // scalar managers
        managers.put(Component.MASS,        new MassManager       (entityManager, 16, entityManager.getEntityCapacity()));
        managers.put(Component.RESTITUTION, new RestitutionManager(entityManager, 16, entityManager.getEntityCapacity()));
        // special
        managers.put(Component.SPRING,      new SpringManager(entityManager, 16, entityManager.getEntityCapacity()));
    }
    
    // ============================== Getters =============================== //
    public int createPointMass() {
        return createEntity(Archetype.POINT_MASS);
    }
    
    public int createSpring(int pointMassId1, int pointMassId2, float restLength, float springConstant) {
        boolean validEntity = isPointMass(pointMassId1) && isPointMass(pointMassId2);
        if (!validEntity) {
            throw new IllegalArgumentException(String.format("Entity %d and %d must both be Point Mass archetypes", pointMassId1, pointMassId2));
        }
        
        int entId = entMan.createEntity();
        SpringManager springMan = (SpringManager) managers.get(Component.SPRING);
        springMan.add(entId, pointMassId1, pointMassId2, restLength, springConstant);
        entMan.addComponentTo(entId, Component.SPRING);
        return entId;
    }
    
    public int createEntity(Archetype archetype) {
        int entId = entMan.createEntity(archetype);
        
        ComponentManager compMan;
        for (Component comp : archetype.components()) {
            compMan = managers.get(comp);
            compMan.add(entId);
        }
        
        return entId;
    }
    
    public boolean destroyEntity(int entityId) {
        int components       = entMan.getComponentMask(entityId);
        boolean wasDestroyed = entMan.destroyEntity(entityId);
        
        if (wasDestroyed) {
            for (ComponentManager compMan : managers.values()) {
                int bit = compMan.getComponent().bit();
                if ((bit & components) == bit) {
                    compMan.remove(entityId);
                }
            }
        }
        
        return wasDestroyed;
    }
    
    // ============================ API Methods ============================= //
    public boolean addSystem(ISystem system) {
        return systems.add(system);
    }
    
    // ========================== Helper Methods ============================ //
    private boolean isPointMass(int entityID) {
        return Archetype.POINT_MASS.contains(entMan.getComponentMask(entityID));
    }
    
}