package com.slinky.physics.components;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.DisplayName;

import static org.junit.jupiter.api.Assertions.*;

/**
 *
 * @author Kheagen Haskins
 */
public class ComponentTest {
    
    @Test
    @DisplayName("Test All Bits Are Unique")
    public void testBits_AllAreUnique() {

        for (Component compA : Component.values()) 
         for (Component compB : Component.values()) 
          if (compA != compB) {
              assertNotEquals(compA.bit(), compB.bit(), "Components found with matching bitmask bits");
          }

    }
    
}