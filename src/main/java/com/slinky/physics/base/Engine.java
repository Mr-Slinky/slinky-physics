package com.slinky.physics.base;

import com.slinky.physics.components.Component;
import com.slinky.physics.components.ComponentManager;
import com.slinky.physics.components.ForceManager;
import com.slinky.physics.components.MassManager;
import com.slinky.physics.components.PositionManager;
import com.slinky.physics.components.RestitutionManager;
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
 * @since  0.1.0
 * 
 * @author Kheagen Haskins
 */
public class Engine {

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
    private EntityManager entityManager;
    private List<ISystem> systems                     = new ArrayList<>();
    private Map<Component, ComponentManager> managers = new HashMap<>();

    // =========================== Constructors ============================= //
    public Engine(EntityManager entityManager) {
        this.entityManager = entityManager;
        
        // This is going to become incredibly bloated as more components are added... Hmmmm!
        managers.put(Component.POSITION, new PositionManager   (entityManager, 16, entityManager.getEntityCapacity()));
        managers.put(Component.VELOCITY, new VelocityManager   (entityManager, 16, entityManager.getEntityCapacity()));
        managers.put(Component.FORCE,    new ForceManager      (entityManager, 16, entityManager.getEntityCapacity()));
        managers.put(Component.MASS,     new MassManager       (entityManager, 16, entityManager.getEntityCapacity()));
        managers.put(Component.MASS,     new RestitutionManager(entityManager, 16, entityManager.getEntityCapacity()));
    }
    
    // ============================== Getters =============================== //
    public int createEntity(Archetype archetype) {
        int entityId = entityManager.createEntity(archetype);
        
        ComponentManager compMan;
        for (Component comp : archetype.components()) {
            compMan = managers.get(comp);
            compMan.add(entityId);
        }
        
        return entityId;
    }
    
    public boolean destroyEntity(int entityId) {
        int components       = entityManager.getComponentMask(entityId);
        boolean wasDestroyed = entityManager.destroyEntity(entityId);
        
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
    @Override
    public String toString() {
        return super.toString();
    }
    
}