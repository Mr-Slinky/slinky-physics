package com.slinky.physics.components;

import com.slinky.physics.base.EntityManager;
import com.slinky.physics.util.ScalarStorage;

/**
 * @version 1.0
 * @since   0.1.0
 * 
 * @author  Kheagen Haskins
 * 
 * @see     EntityManager
 * @see     ScalarStorage
 */
public class RestitutionManager extends ScalarStorage {

    // =============================[ Constructors ]============================= \\
    public RestitutionManager(EntityManager entityManager, int initialEntityCapacity, int maxEntityCapacity) {
        super(Component.MASS, entityManager, initialEntityCapacity, maxEntityCapacity);
    }
    
}