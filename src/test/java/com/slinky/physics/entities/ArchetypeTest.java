package com.slinky.physics.entities;

import static com.slinky.physics.components.Component.*;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.DisplayName;

import static org.junit.jupiter.api.Assertions.assertAll;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotEquals;

/**
 *
 * @author Kheagen Haskins
 */
public class ArchetypeTest {
    
    @Test
    @DisplayName("Archetype Non-Zero BitMask Test")
    public void testBitMask_NotZero() {
        for (Archetype value : Archetype.values()) {
            assertNotEquals(0, value.bitmask()); 
        }
    }
    
    @Test
    @DisplayName("PointMass Archetype BitMask Test")
    public void testPointMass_BitMask_ExpectedBitMask() {
        int expectedBitmask = POSITION.bit() | VELOCITY.bit() | FORCE.bit() | MASS.bit() | RESTITUTION.bit();
        
        assertAll(
                () -> assertEquals(0b101111,        Archetype.POINT_MASS.bitmask()),
                () -> assertEquals(expectedBitmask, Archetype.POINT_MASS.bitmask())
        );
    }
    
}