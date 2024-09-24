package com.slinky.physics.components.data;

import static java.lang.Math.abs;
import java.util.stream.Stream;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.DisplayName;

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
    
    // ================== Vector Cross Product Function Test ==================== //
    /**
     * Provides a stream of arguments for cross product tests. Each argument
     * consists of two vectors and the expected cross product result.
     */
    static Stream<Arguments> crossProductProvider() {
        return Stream.of(
                // Basic orthogonal vectors
                Arguments.of(new Vec2D(1, 0),   new Vec2D(0, 1), 1.0f),
                Arguments.of(new Vec2D(0, 1),   new Vec2D(1, 0), -1.0f),
                // Parallel vectors
                Arguments.of(new Vec2D(2, 2),   new Vec2D(4, 4), 0.0f),
                Arguments.of(new Vec2D(-3, -3), new Vec2D(6, 6), 0.0f),
                // Opposite vectors
                Arguments.of(new Vec2D(1, 1),   new Vec2D(-1, -1), 0.0f),
                // Zero vectors
                Arguments.of(new Vec2D(0, 0),   new Vec2D(0, 0), 0.0f),
                Arguments.of(new Vec2D(1, 0),   new Vec2D(0, 0), 0.0f),
                Arguments.of(new Vec2D(0, 1),   new Vec2D(0, 0), 0.0f),
                // Vectors with negative components
                Arguments.of(new Vec2D(3, 4),   new Vec2D(-5, 2), (3 * 2) - (4 * -5)), // 6 + 20 = 26
                Arguments.of(new Vec2D(-2, 7),  new Vec2D(3, -4), (-2 * -4) - (7 * 3)), // 8 - 21 = -13
                Arguments.of(new Vec2D(5, -3),  new Vec2D(-2, 6), (5 * 6) - (-3 * -2)) // 30 - 6 = 24
        );
    }

    /**
     * Tests the cross product method with various vector pairs.
     *
     * @param v1 The first vector.
     * @param v2 The second vector.
     * @param expected The expected cross product result.
     */
    @ParameterizedTest(name = "cross({0}, {1}) = {2}")
    @MethodSource("crossProductProvider")
    @DisplayName("Compute cross product of two vectors")
    void testCrossProduct(Vec2D v1, Vec2D v2, float expected) {
        float result = v1.cross(v2);
        assertAll(
                () -> assertEquals(expected, result, 1e-5f,
                        () -> String.format("Expected cross(%s, %s) to be %f but was %f", v1, v2, expected, result)),
                // Additionally, verify anti-commutativity: cross(v1, v2) == -cross(v2, v1)
                () -> {
                    float reverseResult = v2.cross(v1);
                    assertEquals(-expected, reverseResult, 1e-5f,
                            () -> String.format("Expected cross(%s, %s) to be %f but was %f", v2, v1, -expected, reverseResult));
                }
        );
    }

    /**
     * Tests the cross product method for non-Vec2D inputs. Ensures that passing
     * null vectors throws NullPointerException.
     */
    @Test
    @DisplayName("Cross product with null vector should throw NullPointerException")
    void testCrossProductWithNull() {
        Vec2D v1 = new Vec2D(1, 2);
        Vec2D v2 = null;
        assertThrows(NullPointerException.class, () -> v1.cross(v2),
                "Expected cross product with null to throw NullPointerException");
    }

    // ==================== Vector Projection Function Test ====================== //
    /**
     * Provides a stream of arguments for project tests. Each argument consists
     * of two vectors and the expected projection result.
     */
    static Stream<Arguments> projectProvider() {
        return Stream.of(
                // Basic projections
                Arguments.of(new Vec2D(1, 0), new Vec2D(1, 0), new Vec2D(1, 0)),
                Arguments.of(new Vec2D(1, 1), new Vec2D(1, 0), new Vec2D(1, 0)),
                Arguments.of(new Vec2D(1, 1), new Vec2D(0, 1), new Vec2D(0, 1)),
                Arguments.of(new Vec2D(2, 3), new Vec2D(4, 0), new Vec2D(2, 0)),
                Arguments.of(new Vec2D(2, 3), new Vec2D(0, 4), new Vec2D(0, 3)),
                // Projection onto non-unit vectors
                Arguments.of(new Vec2D(3, 4), new Vec2D(1, 2), new Vec2D(2.2f, 4.4f)),
                Arguments.of(new Vec2D(-1, 2), new Vec2D(3, -4), new Vec2D(-1.32f, 1.76f)),
                // Projection onto zero vector (edge case)
                Arguments.of(new Vec2D(1, 1), new Vec2D(0, 0), new Vec2D(0, 0)),
                Arguments.of(new Vec2D(5, -3), new Vec2D(0, 0), new Vec2D(0, 0)),
                // Projection of zero vector onto another vector
                Arguments.of(new Vec2D(0, 0), new Vec2D(1, 2), new Vec2D(0, 0)),
                // Projection where v1 is parallel to v2
                Arguments.of(new Vec2D(2, 2), new Vec2D(1, 1), new Vec2D(2, 2)),
                Arguments.of(new Vec2D(-3, -3), new Vec2D(1, 1), new Vec2D(-3, -3)),
                // Projection where v1 is orthogonal to v2
                Arguments.of(new Vec2D(1, 0), new Vec2D(0, 1), new Vec2D(0, 0)),
                Arguments.of(new Vec2D(0, 1), new Vec2D(1, 0), new Vec2D(0, 0))
        );
    }

    /**
     * Tests the project method with various vector pairs.
     *
     * @param v1 The vector to be projected.
     * @param v2 The vector onto which v1 is projected.
     * @param expected The expected projection result.
     */
    @ParameterizedTest(name = "project({0}, {1}) = {2}")
    @MethodSource("projectProvider")
    @DisplayName("Compute projection of one vector onto another")
    void testProject(Vec2D v1, Vec2D v2, Vec2D expected) {
        Vec2D result = Vec2D.project(v1, v2);
        final float delta = 1e-4f;

        assertAll("Projection Assertions",
                () -> assertEquals(expected.x(), result.x(), delta,
                        () -> String.format("Expected x=%.5f, but was x=%.5f", expected.x(), result.x())),
                () -> assertEquals(expected.y(), result.y(), delta,
                        () -> String.format("Expected y=%.5f, but was y=%.5f", expected.y(), result.y()))
        );
    }

    /**
     * Tests that projecting onto a zero vector returns a zero vector.
     */
    @Test
    @DisplayName("Project onto zero vector returns zero vector")
    void testProjectOntoZeroVector() {
        Vec2D v1 = new Vec2D(5, -3);
        Vec2D v2 = Vec2D.ZERO.copy();
        Vec2D expected = Vec2D.ZERO.copy();

        Vec2D result = Vec2D.project(v1, v2);

        assertAll("Projection onto Zero Vector",
                () -> assertEquals(expected.x(), result.x(), 1e-5f, "Expected x=0.0"),
                () -> assertEquals(expected.y(), result.y(), 1e-5f, "Expected y=0.0")
        );
    }

    /**
     * Tests that projecting a zero vector onto another vector returns a zero
     * vector.
     */
    @Test
    @DisplayName("Project zero vector onto another vector returns zero vector")
    void testProjectZeroVectorOntoAnother() {
        Vec2D v1 = Vec2D.ZERO.copy();
        Vec2D v2 = new Vec2D(1, 2);
        Vec2D expected = Vec2D.ZERO.copy();

        Vec2D result = Vec2D.project(v1, v2);

        assertAll("Projection of Zero Vector",
                () -> assertEquals(expected.x(), result.x(), 1e-5f, "Expected x=0.0"),
                () -> assertEquals(expected.y(), result.y(), 1e-5f, "Expected y=0.0")
        );
    }

    /**
     * Tests that passing a null vector as v2 throws NullPointerException.
     */
    @Test
    @DisplayName("Project method with null v2 throws NullPointerException")
    void testProjectWithNullV2() {
        Vec2D v1 = new Vec2D(1, 2);
        Vec2D v2 = null;

        assertThrows(NullPointerException.class, () -> Vec2D.project(v1, v2),
                "Expected project method to throw NullPointerException when v2 is null");
    }

    /**
     * Tests that passing a null vector as v1 throws NullPointerException.
     */
    @Test
    @DisplayName("Project method with null v1 throws NullPointerException")
    void testProjectWithNullV1() {
        Vec2D v1 = null;
        Vec2D v2 = new Vec2D(1, 2);

        assertThrows(NullPointerException.class, () -> Vec2D.project(v1, v2),
                "Expected project method to throw NullPointerException when v1 is null");
    }
    
    // ==================== Vector Rotation Function Test ====================== //
    /**
     * Provides a stream of arguments for rotation tests. Each argument consists
     * of a vector, an angle in radians, and the expected rotated vector.
     */
    static Stream<Arguments> rotationProvider() {
        return Stream.of(
                // Rotate by 0 radians (no rotation)
                Arguments.of(new Vec2D(1, 0), 0.0f, new Vec2D(1, 0)),
                Arguments.of(new Vec2D(0, 1), 0.0f, new Vec2D(0, 1)),
                // Rotate by 90 degrees (pi/2 radians)
                Arguments.of(new Vec2D(1, 0), (float) Math.PI / 2, new Vec2D(0, 1)),
                Arguments.of(new Vec2D(0, 1), (float) Math.PI / 2, new Vec2D(-1, 0)),
                // Rotate by 180 degrees (pi radians)
                Arguments.of(new Vec2D(1, 0), (float) Math.PI, new Vec2D(-1, 0)),
                Arguments.of(new Vec2D(0, 1), (float) Math.PI, new Vec2D(0, -1)),
                // Rotate by 270 degrees (3*pi/2 radians)
                Arguments.of(new Vec2D(1, 0), 3 * (float) Math.PI / 2, new Vec2D(0, -1)),
                Arguments.of(new Vec2D(0, 1), 3 * (float) Math.PI / 2, new Vec2D(1, 0)),
                // Rotate by 360 degrees (2*pi radians)
                Arguments.of(new Vec2D(1, 0), 2 * (float) Math.PI, new Vec2D(1, 0)),
                Arguments.of(new Vec2D(0, 1), 2 * (float) Math.PI, new Vec2D(0, 1)),
                // Rotate negative angles
                Arguments.of(new Vec2D(1, 0), -(float) Math.PI / 2, new Vec2D(0, -1)),
                Arguments.of(new Vec2D(0, 1), -(float) Math.PI / 2, new Vec2D(1, 0)),
                // Rotate arbitrary angles
                Arguments.of(new Vec2D(1, 1), (float) Math.PI / 4, new Vec2D(0, (float) Math.sqrt(2))),
                Arguments.of(new Vec2D(1, 1), -(float) Math.PI / 4, new Vec2D((float) Math.sqrt(2), 0)),
                // Rotate zero vector
                Arguments.of(new Vec2D(0, 0), (float) Math.PI / 3, new Vec2D(0, 0)),
                // Rotate vectors with negative components
                Arguments.of(new Vec2D(-1, -1), (float) Math.PI / 2, new Vec2D(1, -1)),
                Arguments.of(new Vec2D(-2, 3), (float) Math.PI / 3,
                        new Vec2D(
                                (-2) * (float) Math.cos(Math.PI / 3) - 3 * (float) Math.sin(Math.PI / 3),
                                (-2) * (float) Math.sin(Math.PI / 3) + 3 * (float) Math.cos(Math.PI / 3)
                        ))
        );
    }

    /**
     * Tests the rotate method with various vectors and angles.
     *
     * @param original The original vector before rotation.
     * @param angle The angle in radians to rotate the vector.
     * @param expectedVec The expected vector after rotation.
     */
    @ParameterizedTest(name = "rotate({0}, angle={1}) = {2}")
    @MethodSource("rotationProvider")
    @DisplayName("Rotate vector by specified angle")
    void testRotate(Vec2D original, float angle, Vec2D expectedVec) {
        Vec2D vectorToRotate = original.copy();
        Vec2D rotatedVector = vectorToRotate.rotate(angle);

        // Define a small delta for floating point comparison
        final float delta = 1e-4f;

        assertAll("Rotated Vector Assertions",
                () -> assertEquals(expectedVec.x(), rotatedVector.x(), delta,
                        () -> String.format("Expected x=%.5f, but was x=%.5f", expectedVec.x(), rotatedVector.x())),
                () -> assertEquals(expectedVec.y(), rotatedVector.y(), delta,
                        () -> String.format("Expected y=%.5f, but was y=%.5f", expectedVec.y(), rotatedVector.y()))
        );
    }

    /**
     * Tests rotating a vector multiple times and ensures consistency.
     */
    @Test
    @DisplayName("Rotate vector multiple times for consistency")
    void testRotateMultipleTimes() {
        Vec2D original = new Vec2D(1, 0);
        Vec2D vector = original.copy();
        float[] angles = {(float) Math.PI / 2, (float) Math.PI / 2, (float) Math.PI / 2, (float) Math.PI / 2}; // Total 2*pi

        for (float angle : angles) {
            vector.rotate(angle);
        }

        assertAll("Multiple Rotations",
                () -> assertEquals(original.x(), vector.x(), 1e-5f, "After full rotation, x should be original"),
                () -> assertEquals(original.y(), vector.y(), 1e-5f, "After full rotation, y should be original")
        );
    }

    /**
     * Tests rotating a vector with zero magnitude.
     */
    @Test
    @DisplayName("Rotate zero vector")
    void testRotateZeroVector() {
        Vec2D zeroVector = new Vec2D(0, 0);
        Vec2D rotated = zeroVector.rotate((float) Math.PI / 3);
        assertAll("Zero Vector Rotation",
                () -> assertEquals(0.0f, rotated.x(), 1e-5f, "Rotating zero vector x should remain 0"),
                () -> assertEquals(0.0f, rotated.y(), 1e-5f, "Rotating zero vector y should remain 0")
        );
    }

}