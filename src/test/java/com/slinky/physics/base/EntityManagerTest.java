// File: com/slinky/physics/base/EntityManagerTest.java
package com.slinky.physics.base;

import com.slinky.physics.components.Component;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;

import static org.junit.jupiter.api.Assertions.*;

class EntityManagerTest {

    private EntityManager entityManager;
    private static final int ENTITY_CAPACITY = 100;

    @BeforeEach
    void setUp() {
        entityManager = new EntityManager(ENTITY_CAPACITY);
    }

    @Test
    void testCreateEntity() {
        int entityID = entityManager.createEntity();
        assertTrue(entityManager.hasEntity(entityID), "Entity should exist after creation");
    }

    @Test
    void testDestroyEntity() {
        int entityID      = entityManager.createEntity();
        boolean destroyed = entityManager.destroyEntity(entityID);
        assertAll(
            () -> assertTrue(destroyed, "Entity should be destroyed successfully"),
            () -> assertFalse(entityManager.hasEntity(entityID), "Entity should not exist after destruction")
        );
    }

    @Test
    void testAddComponentTo() {
        int entityID  = entityManager.createEntity();
        boolean added = entityManager.addComponentTo(entityID, Component.POSITION);
        assertAll(
            () -> assertTrue(added, "Component should be added successfully"),
            () -> assertTrue(entityManager.hasComponent(entityID, Component.POSITION), "Entity should have the POSITION component")
        );
    }

    @Test
    void testRemoveComponent() {
        int entityID = entityManager.createEntity();
        entityManager.addComponentTo(entityID, Component.POSITION);
        boolean removed = entityManager.removeComponent(entityID, Component.POSITION);
        assertAll(
            () -> assertTrue(removed, "Component should be removed successfully"),
            () -> assertFalse(entityManager.hasComponent(entityID, Component.POSITION), "Entity should not have the POSITION component")
        );
    }

    @ParameterizedTest
    @ValueSource(ints = {0, 1, 2, 5, 10, 50})
    void testCreateMultipleEntities(int count) {
        int[] entityIDs = new int[count];
        for (int i = 0; i < count; i++) {
            entityIDs[i] = entityManager.createEntity();
        }

        for (int i = 0; i < count; i++) {
            int entityID = entityIDs[i];
            assertTrue(entityManager.hasEntity(entityID), "Entity " + entityID + " should exist");
        }
    }

    @ParameterizedTest
    @ValueSource(ints = {1, 1 << 1, 1 << 2, 1 << 3})
    void testAddMultipleComponents(int componentBit) {
        int entityID = entityManager.createEntity();
        Component component = getComponentByBit(componentBit);
        boolean added = entityManager.addComponentTo(entityID, component);

        assertAll(
            () -> assertTrue(added, "Component " + component + " should be added successfully"),
            () -> assertTrue(entityManager.hasComponent(entityID, component), "Entity should have the component " + component)
        );
    }

    @Test
    void testAddAndRemoveMultipleComponents() {
        int entityID = entityManager.createEntity();
        entityManager.addComponentTo(entityID, Component.POSITION);
        entityManager.addComponentTo(entityID, Component.VELOCITY);
        entityManager.addComponentTo(entityID, Component.MASS);

        boolean removed = entityManager.removeComponent(entityID, Component.VELOCITY);

        assertAll(
            () -> assertTrue(removed, "Component VELOCITY should be removed successfully"),
            () -> assertTrue(entityManager.hasComponent(entityID, Component.POSITION), "Entity should have POSITION component"),
            () -> assertFalse(entityManager.hasComponent(entityID, Component.VELOCITY), "Entity should not have VELOCITY component"),
            () -> assertTrue(entityManager.hasComponent(entityID, Component.MASS), "Entity should have MASS component")
        );
    }

    @Test
    void testDestroyEntityAndRecycleID() {
        int entityID1 = entityManager.createEntity();
        entityManager.destroyEntity(entityID1);
        int entityID2 = entityManager.createEntity();

        assertAll(
            () -> assertEquals(entityID1, entityID2, "Entity ID should be recycled"),
            () -> assertTrue(entityManager.hasEntity(entityID2),  "New entity should exist")
        );
    }

    @Test
    void testExceedEntityCapacity() {
        assertThrows(IllegalStateException.class, () -> {
            for (int i = 0; i <= ENTITY_CAPACITY; i++) {
                entityManager.createEntity();
            }
        });
    }

    @Test
    void testHasComponentOnNonExistentEntity() {
        int invalidEntityID = ENTITY_CAPACITY + 1;
        assertFalse(entityManager.hasComponent(invalidEntityID, Component.POSITION), "Non-existent entity should not have any components");
    }

    @Test
    void testAddComponentToNonExistentEntity() {
        int invalidEntityID = ENTITY_CAPACITY + 1;
        Exception exception = assertThrows(IllegalArgumentException.class, () -> {
            entityManager.addComponentTo(invalidEntityID, Component.POSITION);
        });
        String expectedMessage = "Entity ID " + invalidEntityID + " does not exist";
        assertTrue(exception.getMessage().contains(expectedMessage), "Exception message should indicate invalid entity ID");
    }

    @Test
    void testRemoveComponentFromNonExistentEntity() {
        int invalidEntityID = ENTITY_CAPACITY + 1;
        Exception exception = assertThrows(IllegalArgumentException.class, () -> {
            entityManager.removeComponent(invalidEntityID, Component.POSITION);
        });
        String expectedMessage = "Entity ID " + invalidEntityID + " does not exist";
        assertTrue(exception.getMessage().contains(expectedMessage), "Exception message should indicate invalid entity ID");
    }

    // Helper method to get Component by bit
    private Component getComponentByBit(int bit) {
        for (Component comp : Component.values()) {
            if (comp.bit() == bit) {
                return comp;
            }
        }
        throw new IllegalArgumentException("No component found with bit: " + bit);
    }
    
      @Test
    void testGetComponentMaskForNewEntity() {
        int entityID = entityManager.createEntity();
        int componentMask = entityManager.getComponentMask(entityID);
        assertEquals(0, componentMask, "New entity should have a component mask of zero");
    }

    @Test
    void testGetComponentMaskAfterAddingComponents() {
        int entityID = entityManager.createEntity();
        entityManager.addComponentTo(entityID, Component.POSITION);
        entityManager.addComponentTo(entityID, Component.VELOCITY);
        int expectedMask = Component.POSITION.bit() | Component.VELOCITY.bit();
        int actualMask = entityManager.getComponentMask(entityID);
        assertEquals(expectedMask, actualMask, "Component mask should reflect added components");
    }

    @Test
    void testGetComponentMaskAfterRemovingComponents() {
        int entityID = entityManager.createEntity();
        entityManager.addComponentTo(entityID, Component.POSITION);
        entityManager.addComponentTo(entityID, Component.VELOCITY);
        entityManager.removeComponent(entityID, Component.POSITION);
        int expectedMask = Component.VELOCITY.bit();
        int actualMask = entityManager.getComponentMask(entityID);
        assertEquals(expectedMask, actualMask, "Component mask should reflect removed components");
    }

    @Test
    void testGetComponentMaskForNonExistentEntity() {
        int invalidEntityID = ENTITY_CAPACITY + 1;
        Exception exception = assertThrows(IllegalArgumentException.class, () -> {
            entityManager.getComponentMask(invalidEntityID);
        });
        String expectedMessage = "Entity ID " + invalidEntityID + " does not exist.";
        assertTrue(exception.getMessage().contains(expectedMessage), "Exception message should indicate invalid entity ID");
    }

    @Test
    void testGetComponentMaskAfterEntityDestruction() {
        int entityID = entityManager.createEntity();
        entityManager.addComponentTo(entityID, Component.POSITION);
        entityManager.destroyEntity(entityID);
        Exception exception = assertThrows(IllegalArgumentException.class, () -> {
            entityManager.getComponentMask(entityID);
        });
        String expectedMessage = "Entity ID " + entityID + " does not exist.";
        assertTrue(exception.getMessage().contains(expectedMessage), "Exception message should indicate entity no longer exists");
    }

}