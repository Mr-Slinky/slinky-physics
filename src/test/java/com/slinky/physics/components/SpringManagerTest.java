package com.slinky.physics.components;

import com.slinky.physics.base.EntityManager;
import com.slinky.physics.entities.Archetype;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.DisplayName;


import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;

/**
 * @author Kheagen Haskins
 */
public class SpringManagerTest {

    private static final int E_LIMIT          = 10_000;
    private static final int INITIAL_CAPACITY = 16;
    
    private EntityManager entMan;
    private int pointId1, pointId2;
    private SpringManager springMan;
    private int springEntityId;
    
    @BeforeEach
    void setUp() {
        entMan         = new EntityManager(E_LIMIT);
        pointId1       = entMan.createEntity(Archetype.POINT_MASS);
        pointId2       = entMan.createEntity(Archetype.POINT_MASS);
        springEntityId = entMan.createEntity();
        springMan      = new SpringManager(entMan, INITIAL_CAPACITY, E_LIMIT);
        
    }

    // ==========================[ Constructor Tests ]=========================== \\
    @Test
    @DisplayName("SpringManager Constructor Test")
    void testConstructor() {
        assertDoesNotThrow(()->new SpringManager(entMan, INITIAL_CAPACITY, E_LIMIT));
    }
    
    @Test
    @DisplayName("Pseudo End to End Test")
    void testClass() {
        springMan.add(springEntityId, pointId1, pointId2, 1f, 1f);
    }

    // ========================[ Accessor Method Tests ]========================= \\
    // ===========================[ Add Method Tests ]=========================== \\
    // =========================[ Remove Method Tests ]========================== \\
    // ===========================[ API Method Tests ]=========================== \\
}
