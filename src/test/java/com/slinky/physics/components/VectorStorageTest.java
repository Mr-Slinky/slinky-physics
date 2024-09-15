package com.slinky.physics.components;

import org.junit.jupiter.api.Test;

import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;

import static org.junit.jupiter.api.Assertions.assertAll;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;

/**
 * Comprehensive unit test for the {@code VectorStorage} class.
 *
 * @author Kheagen
 */
public class VectorStorageTest {
    
    // =========================================== Constructor Tests =========================================== //
    @ParameterizedTest
    @ValueSource(ints = {1, 2, 3, 4, 5, 10, 2000, VectorStorage.MAX_STARTING_CAPACITY, VectorStorage.MAX_STARTING_CAPACITY - 1})
    public void testConstructor_ValidInput_NoThrow(int cap) {
        assertDoesNotThrow(() -> new VectorStorage(cap));
    }

    @ParameterizedTest
    @ValueSource(ints = {0, -0, -1, -2, -3, -5, -10, -2000, Integer.MIN_VALUE, VectorStorage.MAX_STARTING_CAPACITY + 1, VectorStorage.MAX_STARTING_CAPACITY + 2})
    public void testConstructor_InvalidInput_NoThrow(int cap) {
        assertThrows(IllegalArgumentException.class, ()-> new VectorStorage(cap));
    }
    
    @ParameterizedTest
    @ValueSource(ints = {1, 2, 3, 4, 5, 10, 100, 2000, VectorStorage.MAX_STARTING_CAPACITY, VectorStorage.MAX_STARTING_CAPACITY - 1})
    public void testConstructor_ValidInput_InitialState(int cap) {
        VectorStorage vec = new VectorStorage(cap);
        assertAll(
                () -> assertEquals(cap,      vec.vectorCount()),
                () -> assertEquals(cap * 2,  vec.data.length),
                () -> assertEquals(cap * 2,  vec.capacity()),
                () -> assertEquals(0f,       vec.xAt(0)),
                () -> assertEquals(0f,       vec.yAt(0)),
                () -> assertEquals(0f,       vec.xAt(cap - 1)),
                () -> assertEquals(0f,       vec.yAt(cap - 1))
        );
    }
    
    // ======================================== Setter and Getter Tests ======================================== //
    @ParameterizedTest
    @ValueSource(ints = {1, 2, 3, 4, 5, 10, 100, 2000, VectorStorage.MAX_STARTING_CAPACITY, VectorStorage.MAX_STARTING_CAPACITY - 1})
    public void testGetAndSetComponents_InvalidInput_ThrowsOutOfBounds(int cap) {
        VectorStorage vec = new VectorStorage(cap);
        
        assertAll(
                () -> assertThrows(ArrayIndexOutOfBoundsException.class, () -> vec.xAt (cap)),
                () -> assertThrows(ArrayIndexOutOfBoundsException.class, () -> vec.yAt (cap)),
                () -> assertThrows(ArrayIndexOutOfBoundsException.class, () -> vec.setX(cap, 0)),
                () -> assertThrows(ArrayIndexOutOfBoundsException.class, () -> vec.setY(cap, 0))
        );
    }
    
    @ParameterizedTest
    @ValueSource(ints = {1, 2, 3, 4, 5, 10, 100, 2000, VectorStorage.MAX_STARTING_CAPACITY, VectorStorage.MAX_STARTING_CAPACITY - 1})
    public void testGetAndSetComponents_NegativeInput_ThrowsOutOfBounds(int cap) {
        VectorStorage vec = new VectorStorage(cap);
        
        assertAll(
                () -> assertThrows(ArrayIndexOutOfBoundsException.class, () -> vec.xAt (-1)),
                () -> assertThrows(ArrayIndexOutOfBoundsException.class, () -> vec.yAt (-1)),
                () -> assertThrows(ArrayIndexOutOfBoundsException.class, () -> vec.setX(-1, 0)),
                () -> assertThrows(ArrayIndexOutOfBoundsException.class, () -> vec.setY(-1, 0))
        );
    }
    
    @ParameterizedTest
    @ValueSource(ints = {1, 2, 3, 4, 5, 10, 100, 2000, VectorStorage.MAX_STARTING_CAPACITY, VectorStorage.MAX_STARTING_CAPACITY - 1})
    public void testSetComponents_ValidInput_InitialState(int cap) {
        VectorStorage vec = new VectorStorage(cap);
        
        float x = 3.14159f;
        float y = 3.14159f;
        
        for (int i = 0; i < cap; i++) {
            int index = i;
            vec.setComponents(index, x, y);
            assertAll("Testing component mutation with ID " + i,
                () -> assertEquals(cap,     vec.vectorCount()),
                () -> assertEquals(cap * 2, vec.data.length),
                () -> assertEquals(cap * 2, vec.capacity()),
                () -> assertEquals(x,       vec.xAt(index)),
                () -> assertEquals(y,       vec.yAt(index))
            );
        }
    }
    
    @ParameterizedTest
    @ValueSource(floats = {Integer.MAX_VALUE, Integer.MIN_VALUE, Float.MIN_VALUE, 0, 1, -1, 10, -10, 0.0001f, -0.00001f})
    public void testSetX_ValidInput_InitialState(float x) {
        int cap = 100;
        VectorStorage vec = new VectorStorage(cap);
        
        for (int i = 0; i < cap; i++) {
            int index = i;
            vec.setX(index, x);
            assertAll("Testing X mutation with ID " + i,
                    () -> assertEquals(x, vec.xAt(index)),
                    () -> assertEquals(0, vec.yAt(index))
            );
        }
    }
    
    @ParameterizedTest
    @ValueSource(floats = {Integer.MAX_VALUE, Integer.MIN_VALUE, Float.MIN_VALUE, 0, 1, -1, 10, -10, 0.0001f, -0.00001f})
    public void testSetY_ValidInput_InitialState(float y) {
        int cap = 100;
        VectorStorage vec = new VectorStorage(cap);
        
        for (int i = 0; i < cap; i++) {
            int index = i;
            vec.setY(index, y);
            assertAll("Testing Y mutation with ID " + i,
                    () -> assertEquals(0, vec.xAt(index)),
                    () -> assertEquals(y, vec.yAt(index))
            );
        }
    }

    // ========================================== Trim and Grow Tests ========================================== //
    @ParameterizedTest
    @ValueSource(ints = {10, 100, 2000, VectorStorage.MAX_STARTING_CAPACITY, VectorStorage.MAX_STARTING_CAPACITY - 1})
    public void testTrim_EdgeCases_HandlesCorrectly(int cap) {
        VectorStorage vec = new VectorStorage(cap);
        assertAll(
                () -> assertThrows(IllegalArgumentException.class, () -> vec.trim(cap + 1)),
                () -> assertDoesNotThrow(() -> vec.trim(cap))
        );
    }
    
    @ParameterizedTest
    @ValueSource(ints = {1, 10, 100, 200, 250})
    public void testTrim_ValidInput_FunctionsCorrectly(int trimCount) {
        int cap = 250;
        VectorStorage vec = new VectorStorage(cap);
        vec.trim(trimCount);
        
        int lengthAfterTrim = cap * 2 - trimCount * 2;
        
        assertAll(
                () -> assertEquals(cap - trimCount, vec.vectorCount()),
                () -> assertEquals(lengthAfterTrim, vec.data.length),
                () -> assertEquals(lengthAfterTrim, vec.capacity())
        );
    }
    
    @Test
    public void testGrow_EdgeCases_HandlesEdgeValuesCorrectly() {
        int cap1 = 1;
        int cap2 = VectorStorage.MAX_STARTING_CAPACITY;
        VectorStorage vecMin = new VectorStorage(cap1);
        VectorStorage vecMax = new VectorStorage(cap2);
        
        vecMin.grow();
        
        assertAll(
                () -> assertTrue  (vecMin.data.length % 2 == 0),
                () -> assertTrue  (vecMin.capacity()  % 2 == 0),
                () -> assertEquals(vecMin.capacity(), vecMin.data.length),
                () -> assertEquals(4,                 vecMin.data.length),
                
                // ensure grow fails when at capacity
                () -> assertEquals(VectorStorage.MAX_CAPACITY,  vecMax.capacity()),
                () -> assertThrows(IllegalStateException.class, () -> vecMax.grow())
        );
    }
    
    @ParameterizedTest
    @ValueSource(ints = {222_222, 230_000, 240_000, 250_000})
    public void testGrow_EdgeCases_MultipleGrows(int cap) {
        VectorStorage vecMin = new VectorStorage(cap);
        VectorStorage vec = new VectorStorage(cap);
        
        // Can only grow twice before hitting capacity
        assertAll(
                () -> assertDoesNotThrow(() -> vec.grow()),
                () -> assertDoesNotThrow(() -> vec.grow()),
                () -> assertEquals(VectorStorage.MAX_CAPACITY, vec.data.length),
                () -> assertEquals(VectorStorage.MAX_CAPACITY, vec.capacity()),
                () -> assertThrows(IllegalStateException.class, () -> vec.grow())
        );
    }
    
}