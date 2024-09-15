package com.slinky.physics.util;

import static java.lang.Math.abs;
import java.util.stream.Stream;

import org.junit.jupiter.api.Test;

import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.MethodSource;
import org.junit.jupiter.params.provider.Arguments;

import static org.junit.jupiter.api.Assertions.assertAll;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotSame;
import static org.junit.jupiter.api.Assertions.assertThrows;

/**
 *
 * @author Kheagen Haskins
 */
public class Vec2DTest {

    // ============================= Providers ============================== //
    private static final float PRECISION_THRESHOLD = 0.001f;

    static Stream<Arguments> provideNonZeroVectorArgs() {
        return Stream.of(
                Arguments.of( 0.0001f,   0.0001f),
                Arguments.of(-0.001f,    0.001f),
                Arguments.of( 0.01f,    -0.01f),
                Arguments.of(-0.1f,     -0.1f),
                Arguments.of( 1.5f,      2.5f),
                Arguments.of(-1f,       -2f),
                Arguments.of( 3.14159f,  3.14159f),
                Arguments.of( 3.14159f,  2.71828f),
                Arguments.of( 0.14159f,  0.71828f),
                Arguments.of( 8.2153f,   51564.7182f)
        );
    }

    static Stream<Arguments> provideScalarArgs() {
        return Stream.of(
                Arguments.of( 1f,    1f,    2f),
                Arguments.of( 0.5f,  0.5f,  4f),
                Arguments.of(-1f,   -1f,    3f),
                Arguments.of( 1.5f,  2.5f,  0.5f),
                Arguments.of( 1000f, 2000f, 0.001f)
        );
    }

    // =================== distanceBetween Static Method ==================== //
    @Test
    public void testDistanceBetween_Superficial() {
        Vec2D v1 = Vec2D.zero();
        Vec2D v2 = new Vec2D(3, 4);

        assertEquals(5, Vec2D.distanceBetween(v1, v2));
    }

    // =================== Constructor & Getter & Setter ==================== //
    @ParameterizedTest
    @MethodSource("provideNonZeroVectorArgs")
    public void testConstructorAndGetters(float x, float y) {
        // Arrange & Act
        Vec2D vector = new Vec2D(x, y);

        // Assert
        assertAll(
                () -> assertEquals(x, vector.x(), "X coordinate should match the value passed to the constructor."),
                () -> assertEquals(y, vector.y(), "Y coordinate should match the value passed to the constructor.")
        );
    }

    @ParameterizedTest
    @MethodSource("provideNonZeroVectorArgs")
    void testSetters(float x, float y) {
        // Arrange
        Vec2D v1 = new Vec2D(0, 0);
        Vec2D v2 = new Vec2D(0, 0);

        // Act
        v1.setX(x);
        v1.setY(y);
        v2.setComponents(x, y);

        // Assert
        assertAll("Testing Vector2D setters",
                () -> assertEquals(x, v1.x()),
                () -> assertEquals(y, v1.y()),
                () -> assertEquals(x, v2.x()),
                () -> assertEquals(y, v2.y())
        );
    }

    // ========================= Vector Equals Test ========================= //
    @ParameterizedTest
    @MethodSource("provideNonZeroVectorArgs")
    public void testEquals(float x, float y) {
        Vec2D v1 = new Vec2D(x, y);
        Vec2D v2 = new Vec2D(x, y);
        Vec2D v3 = new Vec2D(x + 10, y / 10);

        assertAll(
                () -> assertTrue(v1.matches(v2)),
                () -> assertFalse(v1.matches(v3)),
                () -> assertFalse(v2.matches(v3))
        );
    }

    // ======================== Vector Cosntant Tests ======================= //
    @Test
    public void testConstants_UnitVectorValues() {
        assertAll(
                () -> assertEquals(0,  Vec2D.DOWN.x),
                () -> assertEquals(0,  Vec2D.UP.x),
                () -> assertEquals(0,  Vec2D.LEFT.y),
                () -> assertEquals(0,  Vec2D.RIGHT.y),
                () -> assertEquals(-1, Vec2D.LEFT.x),
                () -> assertEquals(-1, Vec2D.UP.y),
                () -> assertEquals(1,  Vec2D.RIGHT.x),
                () -> assertEquals(1,  Vec2D.DOWN.y),
                () -> assertEquals(1,  Vec2D.UP.mag()),
                () -> assertEquals(1,  Vec2D.DOWN.mag()),
                () -> assertEquals(1,  Vec2D.LEFT.mag()),
                () -> assertEquals(1,  Vec2D.RIGHT.mag())
        );
    }

    @Test
    public void testConstants_Mutability() {
        assertAll(
                () -> assertThrows(UnsupportedOperationException.class, () -> Vec2D.UP.setX(1)),
                () -> assertThrows(UnsupportedOperationException.class, () -> Vec2D.UP.setY(1)),
                () -> assertThrows(UnsupportedOperationException.class, () -> Vec2D.UP.setComponents(1, 1)),
                () -> assertThrows(UnsupportedOperationException.class, () -> Vec2D.UP.setMag(2)),
                () -> assertThrows(UnsupportedOperationException.class, () -> Vec2D.DOWN.setX(1)),
                () -> assertThrows(UnsupportedOperationException.class, () -> Vec2D.DOWN.setY(1)),
                () -> assertThrows(UnsupportedOperationException.class, () -> Vec2D.DOWN.setComponents(1, 1)),
                () -> assertThrows(UnsupportedOperationException.class, () -> Vec2D.DOWN.setMag(2)),
                () -> assertThrows(UnsupportedOperationException.class, () -> Vec2D.LEFT.setX(1)),
                () -> assertThrows(UnsupportedOperationException.class, () -> Vec2D.LEFT.setY(1)),
                () -> assertThrows(UnsupportedOperationException.class, () -> Vec2D.LEFT.setComponents(1, 1)),
                () -> assertThrows(UnsupportedOperationException.class, () -> Vec2D.LEFT.setMag(2)),
                () -> assertThrows(UnsupportedOperationException.class, () -> Vec2D.RIGHT.setX(1)),
                () -> assertThrows(UnsupportedOperationException.class, () -> Vec2D.RIGHT.setY(1)),
                () -> assertThrows(UnsupportedOperationException.class, () -> Vec2D.RIGHT.setComponents(1, 1)),
                () -> assertThrows(UnsupportedOperationException.class, () -> Vec2D.RIGHT.setMag(2)),
                // Operations allowed but the return value is a new or different Vector
                () -> assertNotSame(Vec2D.UP,    Vec2D.UP.copy(new Vec2D(10, 10))),
                () -> assertNotSame(Vec2D.UP,    Vec2D.UP.cross(new Vec2D(10, 10))),
                () -> assertNotSame(Vec2D.UP,    Vec2D.UP.div(new Vec2D(10, 10))),
                () -> assertNotSame(Vec2D.UP,    Vec2D.UP.add(new Vec2D(10, 10))),
                () -> assertNotSame(Vec2D.UP,    Vec2D.UP.sub(new Vec2D(10, 10))),
                () -> assertNotSame(Vec2D.UP,    Vec2D.UP.div(7)),
                () -> assertNotSame(Vec2D.UP,    Vec2D.UP.scale(7)),
                () -> assertNotSame(Vec2D.DOWN,  Vec2D.DOWN.copy(new Vec2D(10, 10))),
                () -> assertNotSame(Vec2D.DOWN,  Vec2D.DOWN.cross(new Vec2D(10, 10))),
                () -> assertNotSame(Vec2D.DOWN,  Vec2D.DOWN.div(new Vec2D(10, 10))),
                () -> assertNotSame(Vec2D.DOWN,  Vec2D.DOWN.add(new Vec2D(10, 10))),
                () -> assertNotSame(Vec2D.DOWN,  Vec2D.DOWN.sub(new Vec2D(10, 10))),
                () -> assertNotSame(Vec2D.DOWN,  Vec2D.DOWN.div(7)),
                () -> assertNotSame(Vec2D.DOWN,  Vec2D.DOWN.scale(7)),
                () -> assertNotSame(Vec2D.LEFT,  Vec2D.LEFT.copy(new Vec2D(10, 10))),
                () -> assertNotSame(Vec2D.LEFT,  Vec2D.LEFT.cross(new Vec2D(10, 10))),
                () -> assertNotSame(Vec2D.LEFT,  Vec2D.LEFT.div(new Vec2D(10, 10))),
                () -> assertNotSame(Vec2D.LEFT,  Vec2D.LEFT.add(new Vec2D(10, 10))),
                () -> assertNotSame(Vec2D.LEFT,  Vec2D.LEFT.sub(new Vec2D(10, 10))),
                () -> assertNotSame(Vec2D.LEFT,  Vec2D.LEFT.div(7)),
                () -> assertNotSame(Vec2D.LEFT,  Vec2D.LEFT.scale(7)),
                () -> assertNotSame(Vec2D.RIGHT, Vec2D.RIGHT.copy(new Vec2D(10, 10))),
                () -> assertNotSame(Vec2D.RIGHT, Vec2D.RIGHT.cross(new Vec2D(10, 10))),
                () -> assertNotSame(Vec2D.RIGHT, Vec2D.RIGHT.div(new Vec2D(10, 10))),
                () -> assertNotSame(Vec2D.RIGHT, Vec2D.RIGHT.add(new Vec2D(10, 10))),
                () -> assertNotSame(Vec2D.RIGHT, Vec2D.RIGHT.sub(new Vec2D(10, 10))),
                () -> assertNotSame(Vec2D.RIGHT, Vec2D.RIGHT.div(7)),
                () -> assertNotSame(Vec2D.RIGHT, Vec2D.RIGHT.scale(7)),
                // Remain unchanged
                () -> assertEquals(0,  Vec2D.DOWN.x),
                () -> assertEquals(0,  Vec2D.UP.x),
                () -> assertEquals(0,  Vec2D.LEFT.y),
                () -> assertEquals(0,  Vec2D.RIGHT.y),
                () -> assertEquals(-1, Vec2D.LEFT.x),
                () -> assertEquals(-1, Vec2D.UP.y),
                () -> assertEquals(1,  Vec2D.RIGHT.x),
                () -> assertEquals(1,  Vec2D.DOWN.y),
                () -> assertEquals(1,  Vec2D.UP.mag()),
                () -> assertEquals(1,  Vec2D.DOWN.mag()),
                () -> assertEquals(1,  Vec2D.LEFT.mag()),
                () -> assertEquals(1,  Vec2D.RIGHT.mag())
        );
    }

    @ParameterizedTest
    @MethodSource("provideNonZeroVectorArgs")
    public void testConstants_AddOperation_RemainUnchanged(float x, float y) {
        Vec2D.UP   .add(new Vec2D(x, y));
        Vec2D.DOWN .add(new Vec2D(x, y));
        Vec2D.LEFT .add(new Vec2D(x, y));
        Vec2D.RIGHT.add(new Vec2D(x, y));
        
        assertAll(
                () -> assertEquals(0,  Vec2D.DOWN.x),
                () -> assertEquals(0,  Vec2D.UP.x),
                () -> assertEquals(0,  Vec2D.LEFT.y),
                () -> assertEquals(0,  Vec2D.RIGHT.y),
                () -> assertEquals(-1, Vec2D.LEFT.x),
                () -> assertEquals(-1, Vec2D.UP.y),
                () -> assertEquals(1,  Vec2D.RIGHT.x),
                () -> assertEquals(1,  Vec2D.DOWN.y),
                () -> assertEquals(1,  Vec2D.UP.mag()),
                () -> assertEquals(1,  Vec2D.DOWN.mag()),
                () -> assertEquals(1,  Vec2D.LEFT.mag()),
                () -> assertEquals(1,  Vec2D.RIGHT.mag())
        );
    }

    @ParameterizedTest
    @MethodSource("provideNonZeroVectorArgs")
    public void testConstants_SubOperation_RemainUnchanged(float x, float y) {
        Vec2D.UP   .sub(new Vec2D(x, y));
        Vec2D.DOWN .sub(new Vec2D(x, y));
        Vec2D.LEFT .sub(new Vec2D(x, y));
        Vec2D.RIGHT.sub(new Vec2D(x, y));
        
        assertAll(
                () -> assertEquals(0,  Vec2D.DOWN.x),
                () -> assertEquals(0,  Vec2D.UP.x),
                () -> assertEquals(0,  Vec2D.LEFT.y),
                () -> assertEquals(0,  Vec2D.RIGHT.y),
                () -> assertEquals(-1, Vec2D.LEFT.x),
                () -> assertEquals(-1, Vec2D.UP.y),
                () -> assertEquals(1,  Vec2D.RIGHT.x),
                () -> assertEquals(1,  Vec2D.DOWN.y),
                () -> assertEquals(1,  Vec2D.UP.mag()),
                () -> assertEquals(1,  Vec2D.DOWN.mag()),
                () -> assertEquals(1,  Vec2D.LEFT.mag()),
                () -> assertEquals(1,  Vec2D.RIGHT.mag())
        );
    }

    @ParameterizedTest
    @MethodSource("provideNonZeroVectorArgs")
    public void testConstants_ScaleOperation_RemainUnchanged(float x) {
        Vec2D.UP   .scale(x);
        Vec2D.DOWN .scale(x);
        Vec2D.LEFT .scale(x);
        Vec2D.RIGHT.scale(x);
        
        assertAll(
                () -> assertEquals(0,  Vec2D.DOWN.x),
                () -> assertEquals(0,  Vec2D.UP.x),
                () -> assertEquals(0,  Vec2D.LEFT.y),
                () -> assertEquals(0,  Vec2D.RIGHT.y),
                () -> assertEquals(-1, Vec2D.LEFT.x),
                () -> assertEquals(-1, Vec2D.UP.y),
                () -> assertEquals(1,  Vec2D.RIGHT.x),
                () -> assertEquals(1,  Vec2D.DOWN.y),
                () -> assertEquals(1,  Vec2D.UP.mag()),
                () -> assertEquals(1,  Vec2D.DOWN.mag()),
                () -> assertEquals(1,  Vec2D.LEFT.mag()),
                () -> assertEquals(1,  Vec2D.RIGHT.mag())
        );
    }

    // ========================== Vector Zero Tests ========================= //
    @Test
    public void testZeroVectorConstant_ZeroValues() {
        assertAll(
                () -> assertEquals(0, Vec2D.ZERO.x),
                () -> assertEquals(0, Vec2D.ZERO.y)
        );
    }

    @ParameterizedTest
    @MethodSource("provideNonZeroVectorArgs")
    public void testZeroVectorConstant_ScalarFunctions(float x, float y) {
        assertAll(
                () -> assertEquals(0, Vec2D.ZERO.mag()),
                () -> assertEquals(0, abs(Vec2D.ZERO.dot(new Vec2D(x, y)))),
                () -> assertEquals(0, abs(Vec2D.ZERO.cross(new Vec2D(x, y))))
        );
    }

    @Test
    public void testZeroVectorConstant_Immutability() {
        assertAll(
                () -> assertThrows(UnsupportedOperationException.class, () -> Vec2D.ZERO.setX(1)),
                () -> assertThrows(UnsupportedOperationException.class, () -> Vec2D.ZERO.setY(1)),
                () -> assertThrows(UnsupportedOperationException.class, () -> Vec2D.ZERO.setComponents(1, 1)),
                () -> assertThrows(UnsupportedOperationException.class, () -> Vec2D.ZERO.mult(new Vec2D(10, 10))),
                () -> assertThrows(UnsupportedOperationException.class, () -> Vec2D.ZERO.div(new Vec2D(10, 10))),
                () -> assertThrows(UnsupportedOperationException.class, () -> Vec2D.ZERO.scale(10)),
                () -> assertThrows(UnsupportedOperationException.class, () -> Vec2D.ZERO.div(10)),
                () -> assertThrows(UnsupportedOperationException.class, () -> Vec2D.ZERO.rotate(10)),
                // Operations allowed but the return value is a new or different Vector
                () -> assertNotSame(Vec2D.ZERO, Vec2D.ZERO.copy(new Vec2D(10, 10))),
                () -> assertNotSame(Vec2D.ZERO, Vec2D.ZERO.add(new Vec2D(10, 10))),
                () -> assertNotSame(Vec2D.ZERO, Vec2D.ZERO.sub(new Vec2D(10, 10)))
        );
    }

    @Test
    public void testZeroVectorMethod() {
        assertAll(
                () -> assertEquals(0, Vec2D.zero().x),
                () -> assertEquals(0, Vec2D.zero().y)
        );
    }

    // ======================= Vector Normalize Tests ======================= //
    @ParameterizedTest
    @MethodSource("provideNonZeroVectorArgs")
    public void testNormalize_MagEqualsOne(float x, float y) {
        float mag = new Vec2D(x, y).normalize().mag();
        float tolerance = 0.0001f;
        assertTrue(mag <= 1 + tolerance && mag >= 1 - tolerance);
    }

    // ========================== Vector Add Tests ========================== //
    @ParameterizedTest
    @MethodSource("provideNonZeroVectorArgs")
    public void testAdd_ToZero(float x, float y) {
        Vec2D v1 = Vec2D.zero();
        Vec2D v2 = new Vec2D(x, y);

        v1.add(v2);
        v1.add(Vec2D.ZERO);
        v1.add(Vec2D.zero());

        assertAll(
                () -> assertEquals(x, v1.x),
                () -> assertEquals(y, v1.y)
        );
    }

    @ParameterizedTest
    @MethodSource("provideNonZeroVectorArgs")
    public void testAdd_ToReverseParams(float x, float y) {
        Vec2D v1 = new Vec2D(x, y);
        Vec2D v2 = new Vec2D(y, x);

        v1.add(v2);

        assertAll(
                () -> assertEquals(x + y, v1.x),
                () -> assertEquals(y + x, v1.y)
        );
    }

    @ParameterizedTest
    @MethodSource("provideNonZeroVectorArgs")
    public void testAdd_SelfAddition(float x, float y) {
        Vec2D v1 = new Vec2D(x, y);

        v1.add(v1);

        assertAll(
                () -> assertEquals(x + x, v1.x),
                () -> assertEquals(y + y, v1.y)
        );
    }

    @ParameterizedTest
    @MethodSource("provideNonZeroVectorArgs")
    public void testAdd_ChainAddition(float x, float y) {
        Vec2D v1 = Vec2D.zero();
        Vec2D v2 = new Vec2D(x, y);

        v1.add(v2)
                .add(v2);

        assertAll(
                () -> assertEquals(x + x, v1.x),
                () -> assertEquals(y + y, v1.y)
        );
    }

    // ========================== Vector Sub Tests ========================== //
    @ParameterizedTest
    @MethodSource("provideNonZeroVectorArgs")
    public void testSub_FromZero(float x, float y) {
        Vec2D v1 = Vec2D.zero();
        Vec2D v2 = new Vec2D(x, y);

        v1.sub(v2);
        v1.sub(Vec2D.ZERO);
        v1.sub(Vec2D.zero());

        assertAll(
                () -> assertEquals(0 - x, v1.x(), "X coordinate should be the negative of the initial value."),
                () -> assertEquals(0 - y, v1.y(), "Y coordinate should be the negative of the initial value.")
        );
    }

    @ParameterizedTest
    @MethodSource("provideNonZeroVectorArgs")
    public void testSub_FromReverseParams(float x, float y) {
        Vec2D v1 = new Vec2D(x, y);
        Vec2D v2 = new Vec2D(y, x);

        v1.sub(v2);

        assertAll(
                () -> assertEquals(x - y, v1.x(), "X coordinate should be the difference between x and y."),
                () -> assertEquals(y - x, v1.y(), "Y coordinate should be the difference between y and x.")
        );
    }

    @ParameterizedTest
    @MethodSource("provideNonZeroVectorArgs")
    public void testSub_SelfSubtraction(float x, float y) {
        Vec2D v1 = new Vec2D(x, y);

        v1.sub(v1);

        assertAll(
                () -> assertEquals(0, v1.x(), "X coordinate should be 0 after self-subtraction."),
                () -> assertEquals(0, v1.y(), "Y coordinate should be 0 after self-subtraction.")
        );
    }

    @ParameterizedTest
    @MethodSource("provideNonZeroVectorArgs")
    public void testSub_ChainSubtraction(float x, float y) {
        Vec2D v1 = new Vec2D(x + x, y + y);
        Vec2D v2 = new Vec2D(x, y);

        v1.sub(v2)
                .sub(v2);

        assertAll(
                () -> assertEquals(0, v1.x(), "X coordinate should be 0 after chain subtraction."),
                () -> assertEquals(0, v1.y(), "Y coordinate should be 0 after chain subtraction.")
        );
    }

    // ========================= Vector Mult Tests ========================== //
    @ParameterizedTest
    @MethodSource("provideNonZeroVectorArgs")
    public void testMult_WithOne(float x, float y) {
        Vec2D v1 = new Vec2D(x, y);
        Vec2D v2 = new Vec2D(1, 1);

        v1.mult(v2);
        v1.mult(Vec2D.ZERO);
        v1.mult(Vec2D.zero());

        assertAll(
                () -> assertEquals(0, abs(v1.x()), "X coordinate should be 0 after multiplying by zero."),
                () -> assertEquals(0, abs(v1.y()), "Y coordinate should be 0 after multiplying by zero.")
        );
    }

    @ParameterizedTest
    @MethodSource("provideNonZeroVectorArgs")
    public void testMult_WithReverseParams(float x, float y) {
        Vec2D v1 = new Vec2D(x, y);
        Vec2D v2 = new Vec2D(y, x);

        v1.mult(v2);

        assertAll(
                () -> assertEquals(x * y, v1.x(), "X coordinate should be the product of x and y."),
                () -> assertEquals(y * x, v1.y(), "Y coordinate should be the product of y and x.")
        );
    }

    @ParameterizedTest
    @MethodSource("provideNonZeroVectorArgs")
    public void testMult_SelfMultiplication(float x, float y) {
        Vec2D v1 = new Vec2D(x, y);

        v1.mult(v1);

        assertAll(
                () -> assertEquals(x * x, v1.x(), "X coordinate should be x squared after self-multiplication."),
                () -> assertEquals(y * y, v1.y(), "Y coordinate should be y squared after self-multiplication.")
        );
    }

    @ParameterizedTest
    @MethodSource("provideNonZeroVectorArgs")
    public void testMult_ChainMultiplication(float x, float y) {
        Vec2D v1 = new Vec2D(x, y);
        Vec2D v2 = new Vec2D(x, y);

        v1.mult(v2)
                .mult(v2);

        assertAll(
                () -> assertEquals(x * x * x, v1.x(), "X coordinate should be x cubed after chain multiplication."),
                () -> assertEquals(y * y * y, v1.y(), "Y coordinate should be y cubed after chain multiplication.")
        );
    }

    // ========================== Vector Div Tests ========================== //
    @ParameterizedTest
    @MethodSource("provideNonZeroVectorArgs")
    public void testDiv_ByZero_ReturnsZero(float x, float y) {
        Vec2D v1 = new Vec2D(x, y);
        Vec2D v2 = new Vec2D(1, 1);

        v1.div(v2)
                .div(Vec2D.ZERO);

        assertAll(
                () -> assertEquals(0, v1.x(), "X coordinate should be 0 when divided by 0."),
                () -> assertEquals(0, v1.y(), "Y coordinate should be 0 when divided by 0.")
        );
    }

    @ParameterizedTest
    @MethodSource("provideNonZeroVectorArgs")
    public void testDiv_ByOne(float x, float y) {
        Vec2D v1 = new Vec2D(x, y);
        Vec2D v2 = new Vec2D(1, 1);

        v1.div(v2);

        assertAll(
                () -> assertEquals(x, v1.x(), "X coordinate should remain the same when divided by 1."),
                () -> assertEquals(y, v1.y(), "Y coordinate should remain the same when divided by 1.")
        );
    }

    @ParameterizedTest
    @MethodSource("provideNonZeroVectorArgs")
    public void testDiv_ByReverseParams(float x, float y) {
        Vec2D v1 = new Vec2D(x, y);
        Vec2D v2 = new Vec2D(y, x);

        v1.div(v2);

        assertAll(
                () -> assertEquals(x / y, v1.x(), "X coordinate should be x divided by y."),
                () -> assertEquals(y / x, v1.y(), "Y coordinate should be y divided by x.")
        );
    }

    @ParameterizedTest
    @MethodSource("provideNonZeroVectorArgs")
    public void testDiv_SelfDivision(float x, float y) {
        Vec2D v1 = new Vec2D(x, y);

        v1.div(v1);

        assertAll(
                () -> assertEquals(1, v1.x(), "X coordinate should be 1 after self-division."),
                () -> assertEquals(1, v1.y(), "Y coordinate should be 1 after self-division.")
        );
    }

    @ParameterizedTest
    @MethodSource("provideNonZeroVectorArgs")
    public void testDiv_ChainDivision(float x, float y) {
        Vec2D v1 = new Vec2D(x * 100, y * 100);
        Vec2D v2 = new Vec2D(10, 10);

        v1.div(v2)
          .div(v2);

        assertAll(
                () -> assertEquals(x, v1.x, PRECISION_THRESHOLD), 
                () -> assertEquals(y, v1.y, PRECISION_THRESHOLD)
        );
    }

    // ========================= Vector Scale Tests ========================== //
    @ParameterizedTest
    @MethodSource("provideScalarArgs")
    public void testScaleUp_ByPositiveScalar(float x, float y, float scalar) {
        Vec2D v1 = new Vec2D(x, y);

        v1.scale(scalar);

        assertAll(
                () -> assertEquals(x * scalar, v1.x(), "X coordinate should be scaled by the scalar."),
                () -> assertEquals(y * scalar, v1.y(), "Y coordinate should be scaled by the scalar.")
        );
    }

    @ParameterizedTest
    @MethodSource("provideScalarArgs")
    public void testScaleUp_ByNegativeScalar(float x, float y, float scalar) {
        Vec2D v1 = new Vec2D(x, y);

        v1.scale(-scalar);

        assertAll(
                () -> assertEquals(x * -scalar, v1.x(), "X coordinate should be scaled by the negative scalar."),
                () -> assertEquals(y * -scalar, v1.y(), "Y coordinate should be scaled by the negative scalar.")
        );
    }

    @ParameterizedTest
    @MethodSource("provideNonZeroVectorArgs")
    public void testScaleUp_ByZero(float x, float y) {
        Vec2D v1 = new Vec2D(x, y);

        v1.scale(0);

        assertAll(
                () -> assertEquals(0, abs(v1.x()), "X coordinate should be 0 after scaling by zero."),
                () -> assertEquals(0, abs(v1.y()), "Y coordinate should be 0 after scaling by zero.")
        );
    }

    @ParameterizedTest
    @MethodSource("provideScalarArgs")
    public void testScaleUp_ChainScaling(float x, float y, float scalar) {
        Vec2D v1 = new Vec2D(x, y);

        v1.scale(scalar)
                .scale(scalar);

        assertAll(
                () -> assertEquals(x * scalar * scalar, v1.x(), "X coordinate should be scaled by the scalar twice."),
                () -> assertEquals(y * scalar * scalar, v1.y(), "Y coordinate should be scaled by the scalar twice.")
        );
    }

    @ParameterizedTest
    @MethodSource("provideScalarArgs")
    public void testScaleDown_ByPositiveScalar(float x, float y, float scalar) {
        Vec2D v1 = new Vec2D(x, y);

        v1.div(scalar);

        assertAll(
                () -> assertEquals(x / scalar, v1.x(), "X coordinate should be scaled down by the scalar."),
                () -> assertEquals(y / scalar, v1.y(), "Y coordinate should be scaled down by the scalar.")
        );
    }

    @ParameterizedTest
    @MethodSource("provideScalarArgs")
    public void testScaleDown_ByNegativeScalar(float x, float y, float scalar) {
        Vec2D v1 = new Vec2D(x, y);

        v1.div(-scalar);

        assertAll(
                () -> assertEquals(x / -scalar, v1.x(), "X coordinate should be scaled down by the negative scalar."),
                () -> assertEquals(y / -scalar, v1.y(), "Y coordinate should be scaled down by the negative scalar.")
        );
    }

    @ParameterizedTest
    @MethodSource("provideScalarArgs")
    public void testScaleDown_ByZero(float x, float y) {
        Vec2D v1 = new Vec2D(x, y);

        v1.div(0);

        assertAll(
                () -> assertEquals(x, v1.x(), "X coordinate should remain unchanged when scaled down by zero."),
                () -> assertEquals(y, v1.y(), "Y coordinate should remain unchanged when scaled down by zero.")
        );
    }

    @ParameterizedTest
    @MethodSource("provideScalarArgs")
    public void testScaleDown_ChainScaling(float x, float y, float scalar) {
        Vec2D v1 = new Vec2D(x * scalar * scalar, y * scalar * scalar);

        v1.div(scalar)
          .div(scalar);

        assertAll(
                () -> assertEquals(x, v1.x(), PRECISION_THRESHOLD, "X coordinate should return to the original x after chain scaling down."),
                () -> assertEquals(y, v1.y(), PRECISION_THRESHOLD, "Y coordinate should return to the original y after chain scaling down.")
        );
    }

    // ===================== Vector Mag Function Test ======================= //
    @ParameterizedTest
    @MethodSource("provideNonZeroVectorArgs")
    void testMag_NonZeroVectors(float x, float y) {
        Vec2D v1 = new Vec2D(x, y);

        float expectedMagnitude = (float) Math.sqrt(x * x + y * y);

        assertEquals(expectedMagnitude, v1.mag(), 1e-9,
                () -> String.format("Magnitude should be %f for vector (%f, %f)", expectedMagnitude, x, y));
    }

    @Test
    void testMag_ZeroVector() {
        Vec2D v1 = Vec2D.zero();

        assertEquals(0, v1.mag(), "Magnitude of the zero vector should be 0.");
    }

    @Test
    void testMag_UnitVectors() {
        Vec2D v1 = new Vec2D(1, 0);
        Vec2D v2 = new Vec2D(0, 1);

        assertEquals(1, v1.mag(), "Magnitude of vector (1, 0) should be 1.");
        assertEquals(1, v2.mag(), "Magnitude of vector (0, 1) should be 1.");
    }

    @ParameterizedTest
    @MethodSource("provideScalarArgs")
    public void testSetMag(float x, float y, float scalar) {
        Vec2D v = new Vec2D(x, y);

        v.setMag(scalar);
        float mag = v.mag();
        float lowerThreshold = scalar - PRECISION_THRESHOLD;
        float upperThreshold = scalar + PRECISION_THRESHOLD;

        assertTrue(mag >= lowerThreshold && mag <= upperThreshold, mag + " outside of accepted tolerance values: " + lowerThreshold + " - " + upperThreshold);
    }

}