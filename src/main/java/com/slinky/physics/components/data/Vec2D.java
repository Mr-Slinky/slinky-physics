package com.slinky.physics.components.data;

import java.util.Objects;

/**
 * The {@code Vec2D} class represents a 2D vector. While vector data is stored
 * in flat arrays for efficient processing, this class serves as a lightweight,
 * temporary container for vector operations, rather than being used as a
 * persistent storage structure. By offering a fluent API, it provides an
 * intuitive way to perform common vector operations like addition, subtraction,
 * scaling, and normalization in an easy-to-read, chained format.
 *
 * <p>
 * This class operates primarily by manipulating vector components directly,
 * facilitating method chaining to improve code clarity and readability. Each
 * method returns the current {@code Vec2D} instance, enabling developers to
 * perform multiple operations in a single, concise statement.
 * </p>
 * 
 * <p>
 * For example:
 * </p>
 * <pre><code>
 *     // Create a new Vec2D instance
 *     Vec2D v1 = Vec2D.of(10, 10);
 *     Vec2D v2 = Vec2D.of(20, 20);
 *
 *     // Chain multiple operations: mult, add, and normalise the vector
 *     v1.mult(2)       // floats the x and y components of v1 (20, 20)
 *       .add(v2)       // Adds the x and y components of v2 to v1 (40, 40)
 *       .normalize();  // (0.71, 0.71)
 *
 *     // The resulting vector 'velocity' will be scaled, adjusted, and normalised in sequence
 *     System.out.println(velocity);  // Output: Vector{x, y} (based on the operations)
 * </code></pre>
 *
 * <p>
 * In this example, the vector's x and y components are first scaled, then
 * adjusted by adding another vector, and finally normalised to unit length, all
 * in one fluent chain of method calls. This improves code readability and
 * maintains a clear and concise style.
 * </p>
 *
 * <p>
 * For most non-static operation provided by this class, there is a static
 * counterpart that returns a new {@code Vec2D} instance instead of mutating
 * the existing vector. This allows for flexibility in vector operations, where
 * developers can choose between modifying the current vector or generating a
 * new vector based on the operation. The non-static methods are designed for
 * in-place updates, while the static methods facilitate operations that require
 * a fresh vector, making the class versatile for different use cases.
 * </p>
 *
 * <p>
 * <b>Example Usage:</b>
 * <pre><code>
 *     // Non-static add operation (in-place)
 *     Vec2D v1 = Vec2D.of(2, 3);
 *     Vec2D v2 = Vec2D.of(4, 5);
 *     v1.add(v2); // v1 is now (6, 8)
 *
 *     // Static add operation (new vector)
 *     Vec2D v3 = Vec2D.add(v1, v2); // v3 is (10, 13), v1 remains (6, 8)
 * </code></pre>
 * </p>
 *
 * <p>
 * The {@code Vec2D} class provides constructors for initialising vectors and
 * methods for modifying and retrieving the x and y coordinates. This class is
 * designed to be extendable and reusable across different systems that require
 * two-dimensional vector data, making it a core part of the physics engine.
 * </p>
 *
 * <p>
 * It is important to note that the {@code Vec2D} class is <b>not
 * thread-safe</b>. This is a direct consequence of its design as a temporary
 * housing unit for vector operations, rather than the vector data itself. Since
 * an instance of {@code Vec2D} can be reused across different vector
 * operations, multiple threads operating on the same {@code Vec2D} instance may
 * inadvertently modify its internal state unpredictably, leading to incorrect
 * results.
 * </p>
 *
 * <p>
 * Specifically, if one thread modifies the components of the {@code Vec2D}
 * instance while another thread is using the same instance for a different
 * operation, the results may become inconsistent. Because vector data is not
 * encapsulated within the instance but temporarily stored and manipulated,
 * concurrent access without proper synchronization mechanisms will result in
 * data races, where the vector's state may change unexpectedly between
 * operations.
 * </p>
 *
 * <p>
 * For scenarios where multiple threads need to operate on vectors, it is
 * recommended to either use separate {@code Vec2D} instances per thread or
 * implement appropriate synchronization mechanisms to ensure safe access to
 * shared {@code Vec2D} instances.
 * </p>
 *
 * @version 1.1
 * @since   0.1.0
 *
 * @author  Kheagen Haskins
 */
public class Vec2D {

    // ============================== Static ================================ //
    /**
     * A static, immutable instance of {@code Vec2D} representing the zero
     * vector (0, 0). This vector is useful as a default or neutral value in
     * various physics calculations where no movement or displacement is
     * desired. It is also particularly valuable for equality checks and testing
     * scenarios where a consistent and unmodifiable reference to the zero
     * vector is required.
     *
     * <p>
     * Since this vector is immutable, any attempt to modify its components will
     * result in an {@code UnsupportedOperationException}.
     * </p>
     */
    public static final Vec2D ZERO = new ZeroVector(0, 0);
    
    /**
     * A constant representing the leftward direction as a unit vector. The
     * vector points in the negative X direction with coordinates (-1, 0).
     */
    public static final Vec2D LEFT = new UnitVectorConstant(-1, 0);

    /**
     * A constant representing the downward direction as a unit vector. The
     * vector points in the positive Y direction with coordinates (0, 1).
     */
    public static final Vec2D DOWN = new UnitVectorConstant(0, 1);

    /**
     * A constant representing the rightward direction as a unit vector. The
     * vector points in the positive X direction with coordinates (1, 0).
     */
    public static final Vec2D RIGHT = new UnitVectorConstant(1, 0);

    /**
     * A constant representing the upward direction as a unit vector. The vector
     * points in the negative Y direction with coordinates (0, -1).
     */
    public static final Vec2D UP = new UnitVectorConstant(0, -1);


    // ============================== Fields ================================ //
    /**
     * The x coordinate of the position.
     */
    protected float x;

    /**
     * The y coordinate of the position.
     */
    protected float y;

    // =========================== Constructors ============================= //
    /**
     * Creates a new {@code Vec2D} instance with the specified x and y
     * components.
     *
     * <p>
     * This method provides a cleaner alternative to the constructor for
     * creating {@code Vec2D} objects. It functions identically to the
     * constructor.
     * </p>
     * 
     * @param x the x component of the vector
     * @param y the y component of the vector
     * @return a new {@code Vec2D} instance with the given x and y components
     */
    public static Vec2D of(float x, float y) {
        return new Vec2D(x, y);
    }

    
    /**
     * Constructs a {@code Position2D} with the specified x and y coordinates.
     *
     * @param x the x coordinate of the position
     * @param y the y coordinate of the position
     */
    Vec2D(float x, float y) {
        this.x = x;
        this.y = y;
    }

    // ============================== Getters =============================== //
    /**
     * Returns the x coordinate of the position.
     *
     * @return the x coordinate
     */
    public float x() {
        return x;
    }

    /**
     * Returns the y coordinate of the position.
     *
     * @return the y coordinate
     */
    public float y() {
        return y;
    }

    // ============================== Setters =============================== //
    /**
     * Updates the x-coordinate of this vector.
     *
     * <p>
     * This method modifies the x-coordinate of the current vector instance and
     * returns the modified instance to support method chaining.
     * </p>
     *
     * @param x the new value for the x-coordinate
     * @return this {@code Vec2D} instance with the updated x-coordinate
     */
    public Vec2D setX(float x) {
        this.x = x;
        return this;
    }

    /**
     * Updates the y-coordinate of this vector.
     *
     * <p>
     * This method modifies the y-coordinate of the current vector instance and
     * returns the modified instance to support method chaining.
     * </p>
     *
     * @param y the new value for the y-coordinate
     * @return this {@code Vec2D} instance with the updated y-coordinate
     */
    public Vec2D setY(float y) {
        this.y = y;
        return this;
    }

    /**
     * Updates both the x and y coordinates of this vector.
     *
     * <p>
     * This method modifies both the x and y coordinates of the current vector
     * instance and returns the modified instance to support method
     * chaining.
     * </p>
     *
     * @param x the new value for the x-coordinate
     * @param y the new value for the y-coordinate
     * @return this {@code Vec2D} instance with the updated coordinates
     */
    public Vec2D setComponents(float x, float y) {
        this.x = x;
        this.y = y;
        return this;
    }

    /**
     * Sets a new magnitude for this vector and returns the vector itself for
     * chaining. If the desired new magnitude is zero, the vector components are
     * set to zero. If the current magnitude of the vector is zero, an exception
     * is thrown, as a zero vector cannot be scaled to a non-zero magnitude.
     *
     * This method allows method chaining by returning the modified vector
     * instance.
     *
     * @param scalar The new magnitude to set for this vector.
     * @return this vector after modifying its magnitude, allowing for method
     * chaining.
     * @throws IllegalStateException if the current vector is a zero vector
     * (magnitude is zero) and an attempt is made to set a non-zero magnitude.
     */
    public Vec2D setMag(float scalar) {
        float mag = mag();  // Calculate the current magnitude of the vector

        if (mag == 0) {
            throw new IllegalStateException("Cannot mutate the magnitude of a zero vector");
        }

        if (scalar == 0) {
            x = 0;  // Set vector components to zero if new magnitude is zero
            y = 0;
        } else {
            float scaleFactor = scalar / mag; 
            x *= scaleFactor;  // Scale the x component
            y *= scaleFactor;  // Scale the y component
        }
        
        return this;
    }


    // ============================ API Methods ============================= //
    /**
     * Adds the components of the specified {@code Vec2D} to this vector.
     * 
     * <p>
     * This method modifies the current vector by adding the x and y components
     * of the provided vector to the corresponding components of this vector. 
     * The operation is performed in place, meaning the current vector is
     * updated and returned, allowing for method chaining.
     * </p>
     *
     * @param other the {@code Vec2D} to add to this vector.
     * @return <code>this</code> vector after the addition.
     */
    public Vec2D add(Vec2D other) {
        this.x += other.x;
        this.y += other.y;

        return this;
    }
    
    /**
     * Adds the given x and y values to the respective components of this
     * vector.
     * <p>
     * This method allows for directly modifying the x and y components of the
     * current vector without creating a new {@code Vec2D} object. It is
     * useful for optimising performance by avoiding unnecessary object
     * creation, especially in scenarios where frequent vector manipulation is
     * needed.
     * </p>
     *
     * @param x The value to be added to the x-component of this vector.
     * @param y The value to be added to the y-component of this vector.
     * @return The current {@code Vec2D} instance, with the x and y
     * components updated.
     *
     * <p>
     * Example usage:</p>
     * <pre>
     * Vec2D vec = new Vec2D(3, 4);
     * vec.add(2, 1);  // vec is now (5, 5)
     * </pre>
     *
     * <p>
     * This method modifies the vector in place and returns the modified
     * instance, enabling method chaining:</p>
     * <pre>
     * vec.add(1, 1).sub(0.5, 0.5);  // Chained operations on the same vector
     * </pre>
     */
    public Vec2D add(float x, float y) {
        this.x += x;
        this.y += y;

        return this;
    }

    /**
     * Subtracts the components of the specified {@code Vec2D} from this
     * vector.
     * <p>
     * This method modifies the current vector by subtracting the x and y
     * components of the provided vector from the corresponding components of
     * this vector.
     * </p>
     *
     * <p>
     * The operation is performed in place, meaning the current vector is
     * updated and returned. This allows for method chaining.
     * </p>
     *
     * @param other the {@code Vec2D} to subtract from this vector.
     * @return this vector after the subtraction.
     */
    public Vec2D sub(Vec2D other) {
        this.x -= other.x;
        this.y -= other.y;

        return this;
    }
    
    /**
     * Subtracts the given x and y values from the respective components of this
     * vector.
     * <p>
     * This method directly modifies the x and y components of the current
     * vector, allowing for efficient vector manipulation without the overhead
     * of creating a new {@code Vec2D} object. It is especially useful in
     * performance-sensitive applications, such as physics simulations.
     * </p>
     *
     * @param x The value to be subtracted from the x-component of this vector.
     * @param y The value to be subtracted from the y-component of this vector.
     * @return The current {@code Vec2D} instance, with the x and y
     * components updated.
     *
     * <p>
     * Example usage:</p>
     * <pre>
     * Vec2D vec = new Vec2D(5, 5);
     * vec.sub(2, 1);  // vec is now (3, 4)
     * </pre>
     *
     * <p>
     * This method modifies the vector in place and returns the modified
     * instance, enabling method chaining:</p>
     * <pre>
     * vec.sub(1, 1).add(0.5, 0.5);  // Chained operations on the same vector
     * </pre>
     */
    public Vec2D sub(float x, float y) {
        this.x -= x;
        this.y -= y;

        return this;
    }

    /**
     * Multiplies the components of this vector by the components of the
     * specified {@code Vec2D}.
     * <p>
     * This method modifies the current vector by multiplying the x and y
     * components of the provided vector with the corresponding components of
     * this vector.
     * </p>
     *
     * <p>
     * The operation is performed in place, meaning the current vector is
     * updated and returned. This allows for method chaining.
     * </p>
     *
     * @param other the {@code Vec2D} to mult this vector by.
     * @return this vector after the scaling operation.
     */
    public Vec2D mult(Vec2D other) {
        this.x *= other.x;
        this.y *= other.y;

        return this;
    }

    /**
     * Multiplies the x and y components of this vector by the specified values,
     * allowing for independent scaling.
     * <p>
     * This method modifies the current vector in place by scaling the x and y
     * components individually by the provided factors. It is particularly
     * useful when you need non-uniform scaling along different axes, unlike the
     * {@code scale} method, which applies a uniform scaling factor.
     * </p>
     *
     * @param x The value by which to multiply the x-component of this vector.
     * @param y The value by which to multiply the y-component of this vector.
     * @return The current {@code Vec2D} instance, with the x and y
     * components scaled.
     *
     * <p>
     * Example usage:</p>
     * <pre>
     * Vec2D vec = new Vec2D(3, 4);
     * vec.mult(2, 3);  // vec is now (6, 12)
     * </pre>
     *
     * <p>
     * This method modifies the vector in place and allows method chaining:</p>
     * <pre>
     * vec.mult(2, 3).add(1, 1);  // Chained operations on the same vector
     * </pre>
     */
    public Vec2D mult(float x, float y) {
        this.x *= x;
        this.y *= y;

        return this;
    }

    /**
     * Divides the components of this vector by the components of the specified
     * {@code Vec2D}.
     * <p>
     * This method modifies the current vector by dividing the x and y
     * components of this vector by the corresponding components of the provided
     * vector.
     * </p>
     *
     * <p>
     * The operation is performed in place, meaning the current vector is
     * updated and returned. This allows for method chaining.
     * </p>
     *
     * <p>
     * Note: If any component of the provided vector is zero, the corresponding
     * component of this vector will be set to zero, avoiding division by zero.
     * This prevents an {@code ArithmeticException} from occurring during the
     * operation.
     * </p>
     *
     * @param other the {@code Vec2D} to divide this vector by.
     * @return this vector after the division operation.
     */
    public Vec2D div(Vec2D other) {
        this.x = other.x == 0 ? 0 : this.x / other.x;
        this.y = other.y == 0 ? 0 : this.y / other.y;

        return this;
    }

    /**
     * Scales down the components of this vector by the specified scalar value.
     * <p>
     * This method divides both the x and y components of the vector by the
     * provided scalar value, effectively reducing the magnitude of the vector
     * by that factor.
     * </p>
     *
     * <p>
     * If the scalar value is zero, the method returns the vector unchanged,
     * avoiding division by zero errors.
     * </p>
     *
     * <p>
     * The operation is performed in place, meaning the current vector is
     * updated and returned. This allows for method chaining.
     * </p>
     *
     * <p>
     * <b>Example Usage:</b>
     * <pre><code>
     * Vec2D velocity = new Vec2D(4, 6);
     * velocity.div(2); // Now velocity is (2, 3)
     * </code></pre>
     * </p>
     *
     * @param scalar the value by which to divide the vector components. If the
     * scalar is zero, the vector remains unchanged.
     * @return this vector after the scaling operation.
     */
    public Vec2D div(float scalar) {
        if (scalar == 0) {
            return this;
        }

        this.x /= scalar;
        this.y /= scalar;

        return this;
    }
    
    /**
     * Divides the x and y components of this vector by the specified values,
     * allowing for independent scaling down.
     * <p>
     * This method modifies the current vector in place by dividing the x and y
     * components individually by the provided divisors. If either divisor is
     * zero, the corresponding component will be set to zero to avoid division
     * by zero errors.
     * </p>

     *
     * <p>
     * Example usage:</p>
     * <pre>
     * Vec2D vec = new Vec2D(6, 12);
     * vec.div(2, 3);  // vec is now (3, 4)
     * </pre>
     *
     * <p>
     * This method modifies the vector in place and allows method chaining:</p>
     * <pre>
     * vec.div(2, 3).add(1, 1);  // Chained operations on the same vector
     * </pre>
     *
     * @param x The value by which to divide the x-component of this vector.
     * @param y The value by which to divide the y-component of this vector.
     * @return The current {@code Vec2D} instance, with the x and y
     * components divided.
     */
    public Vec2D div(float x, float y) {
        this.x = x == 0 ? 0 : this.x / x;
        this.y = y == 0 ? 0 : this.y / y;

        return this;
    }
    
    /**
     * Scales up the components of this vector by the specified scalar value.
     * <p>
     * This method multiplies both the x and y components of the vector by the
     * provided scalar value, effectively increasing or decreasing the magnitude
     * of the vector by that factor.
     * </p>
     *
     * <p>
     * The operation is performed in place, meaning the current vector is
     * updated and returned. This allows for method chaining.
     * </p>
     *
     * <p>
     * <b>Example Usage:</b>
     * <pre><code>
     * Vec2D velocity = new Vec2D(2, 3);
     * velocity.scale(2); // Now velocity is (4, 6)
     * </code></pre>
     * </p>
     *
     * @param scalar the value by which to scale the vector components.
     * @return this vector after the scaling operation.
     */
    public Vec2D scale(float scalar) {
        this.x *= scalar;
        this.y *= scalar;

        return this;
    }

    /**
     * Calculates the magnitude (length) of this vector.
     *
     * <p>
     * The magnitude is computed using the Pythagorean theorem, which is the
     * square root of the sum of the squares of the vector's x and y components.
     * </p>
     *
     * @return the magnitude of this vector.
     */
    public float mag() {
        return (float) Math.sqrt(x * x + y * y);
    }

    /**
     * Computes the dot product of this vector and the specified
     * {@code Vec2D}.
     *
     * <p>
     * The dot product is calculated as the sum of the products of the
     * corresponding components of the two vectors. It provides a measure of how
     * much one vector extends in the direction of the other.
     * </p>
     *
     * @param other the {@code Vec2D} to compute the dot product with.
     * @return the dot product of this vector and the specified vector.
     */
    public float dot(Vec2D other) {
        return (this.x * other.x) + (this.y * other.y);
    }

    /**
     * Computes the cross product of this vector and the specified
     * {@code Vec2D}.
     *
     * <p>
     * In 2D space, the cross product is a scalar value representing the
     * difference between the products of the x and y components of the two
     * vectors. It is useful for determining the relative orientation of the
     * vectors (e.g., whether they are clockwise or counterclockwise relative to
     * each other).
     * </p>
     *
     * @param other the {@code Vec2D} to compute the cross product with.
     * @return the cross product of this vector and the specified vector.
     */
    public float cross(Vec2D other) {
        return (this.x * other.y) - (this.y * other.x);
    }

    /**
     * Rotates this vector by the specified angle <b>around the origin</b>.
     *
     * <p>
     * This method modifies the current vector by applying a 2D rotation
     * transformation using the given angle. The rotation is performed
     * counterclockwise.
     * </p>
     *
     * @param angle the angle by which to rotate the vector, in radians
     * @return the current {@code Vec2D} instance, after rotation
     */
    public Vec2D rotate(float angle) {
        float cosTheta = (float) Math.cos(angle);
        float sinTheta = (float) Math.sin(angle);
        
        float newX = x * cosTheta - y * sinTheta;
        float newY = x * sinTheta + y * cosTheta;
        
        x = newX;
        y = newY;
        
        return this;
    }

    /**
     * Normalises this vector to have a magnitude of 1 (unit vector), preserving
     * its direction.
     * <p>
     * The normalisation is performed by dividing each component of the vector
     * by its magnitude. If the vector's magnitude is zero (i.e., it's a zero
     * vector), no changes are made.
     * </p>
     *
     * @return the noramlised product of this vector.
     *
     */
    public Vec2D normalize() {
        float magSquared = x * x + y * y;

        if (magSquared > 1e-12) {  // Use threshold to avoid precision issues
            float inverseMag = (float) (1.0 / Math.sqrt(magSquared));
            x *= inverseMag;
            y *= inverseMag;
        }

        return this;
    }
    
    /**
     * Negates this vector by reversing the sign of each component.
     * 
     * <p>
     * This method modifies the current vector, making both the x and y
     * components their negative counterparts. For example, if the vector's
     * current components are (x, y), after calling this method, the components
     * will be (-x, -y).
     * </p>
     *
     * @return The current vector after negating its components.
     */
    public Vec2D negate() {
        x = -(x);
        y = -(y);
        return this;
    }

    /**
     * Copies the components of the supplier vector into this vector.
     *
     * <p>
     * This method updates the components of the invoking target vector with the
     * corresponding components of the provided {@code other} vector. This
     * updated target vector is then returned.
     * </p>
     *
     * @param other the vector providing the components to copy
     * @return the updated target vector with the copied components
     */
    public Vec2D copy(Vec2D other) {
        this.x = other.x;
        this.y = other.y;

        return this;
    }

    /**
     * Creates and returns a copy of this vector.
     *
     * <p>
     * This method creates a new instance of the {@code Vec2D} class with the
     * same x and y coordinates as the current vector. The returned vector is a
     * separate object, meaning modifications to the copy will not affect the
     * original vector, and vice versa.
     * </p>
     *
     * <p>
     * This method is useful when you need to duplicate a vector and perform
     * operations on the copy without altering the original vector.
     * </p>
     *
     * <p>
     * <b>Example Usage:</b></p>
     * <pre><code>
     *     Vec2D original = new Vec2D(3, 4);
     *     Vec2D copy = original.copy();
     *     // Now, 'copy' is a new vector with the same values as 'original'.
     *     // Modifying 'copy' will not change 'original'.
     * </code></pre>
     *
     * @return a new {@code Vec2D} object with the same x and y coordinates
     * as this vector.
     */
    public Vec2D copy() {
        return new Vec2D(x, y);
    }

    /**
     * Compares this {@code Vec2D} object to the specified {@code Vec2D}
     * object for equality. The vectors are considered equal if both their x and
     * y coordinates are exactly the same.
     *
     * <p>
     * This method performs a direct comparison using the {@code ==} operator,
     * so it does not account for potential floating-point precision issues. It
     * is primarily intended for cases where exact equality is required, such as
     * when comparing vectors with known discrete values.
     * </p>
     *
     * @param otherVector the {@code Vec2D} object to be compared with this
     * vector
     * @return {@code true} if the x and y coordinates of both vectors are
     * equal; {@code false} otherwise
     */
    public boolean matches(Vec2D otherVector) {
        return (Float.compare(x, otherVector.x) == 0) && (Float.compare(y, otherVector.y) == 0);
    }
    
    /**
     * {@inheritDoc}
     */
    @Override
    public boolean equals(Object obj) {
        if (this == obj) {
            return true;
        }
        if (!(obj instanceof Vec2D)) {
            return false;
        }
        Vec2D other = (Vec2D) obj;
        return Float.compare(x, other.x) == 0
                && Float.compare(y, other.y) == 0;
    }
    
    /**
     * {@inheritDoc}
     */
    @Override
    public int hashCode() {
        return Objects.hash(x, y);
    }

    
    /**
     * Returns a string representation of this vector in the format:
     * {@code Vector{x, y}}.
     *
     * <p>
     * This method provides a concise summary of the vector's components,
     * showing the x and y values in a readable format. It is useful for
     * debugging and logging purposes where a quick glance at the vector's
     * values is needed.
     * </p>
     *
     * @return a string representation of this vector, including its x and y
     * components.
     */
    @Override
    public String toString() {
        return "Vector{" + x + ", " + y + "}";
    }

    // ============================= Utility Methods =============================== //
    /**
     * Creates a new {@code Vec2D} representing the sum of the components of
     * two {@code Vec2D} instances.
     *
     * <p>
     * This operation does not modify the original vectors but returns a new
     * {@code Vec2D} instance with components equal to the sum of the
     * corresponding components of the input vectors. If the provided vectors
     * are not compatible (i.e., not instances of {@code Vec2D}), an
     * {@code IllegalArgumentException} is thrown.
     * </p>
     *
     * @param v1 the first vector to add.
     * @param v2 the second vector to add.
     * @return a new {@code Vec2D} that is the result of adding the
     * components of {@code v1} and {@code v2}.
     * @throws IllegalArgumentException if the vectors are not of type
     * {@code Vec2D}.
     */
    public static Vec2D add(Vec2D v1, Vec2D v2) {
        return new Vec2D(v1.x + v2.x, v1.y + v2.y);
    }
    
    /**
     * Creates a new {@code Vec2D} representing the difference between the
     * components of two {@code Vec2D} instances.
     *
     * <p>
     * This operation does not modify the original vectors but returns a new
     * {@code Vec2D} instance with components equal to the difference between
     * the corresponding components of the input vectors. If the provided
     * vectors are not compatible (i.e., not instances of {@code Vec2D}), an
     * {@code IllegalArgumentException} is thrown.
     * </p>
     *
     * @param v1 the vector from which the other vector's components will be
     * subtracted.
     * @param v2 the vector whose components will be subtracted from the first
     * vector.
     * @return a new {@code Vec2D} that is the result of subtracting the
     * components of {@code v2} from {@code v1}.
     * @throws IllegalArgumentException if the vectors are not of type
     * {@code Vec2D}.
     */
    public static Vec2D sub(Vec2D v1, Vec2D v2) {
        return new Vec2D(v1.x - v2.x, v1.y - v2.y);
    }

    /**
     * Creates a new {@code Vec2D} that is the result of scaling the given
     * {@code Vec2D} by a given scalar value.
     *
     * <p>
     * This operation multiplies both the x and y components of the vector by
     * the specified scalar, returning a new {@code Vec2D} with the scaled
     * components. The original vector remains unchanged.
     * </p>
     *
     * @param v the vector to be scaled.
     * @param scalar the value by which to mult the vector.
     * @return a new {@code Vec2D} with the scaled components.
     */
    public static Vec2D scale(Vec2D v, float scalar) {
        return new Vec2D(v.x * scalar, v.y * scalar);
    }

    /**
     * Divides the components of the given {@code Vec2D} by a scalar value
     * and returns a new {@code Vec2D} with the resulting components.
     *
     * <p>
     * This method does not modify the original vector but returns a new
     * {@code Vec2D} instance with components equal to the original
     * components divided by the scalar value. This operation can be used when
     * you need a new vector that represents the result of a division operation,
     * while keeping the original vector unchanged.
     * </p>
     *
     * @param v the vector whose components will be divided.
     * @param scalar the float value by which to divide the vector's
     * components.
     * @return a new {@code Vec2D} with the components of {@code v} divided
     * by the scalar.
     * @throws ArithmeticException if the scalar is zero, as division by zero is
     * undefined.
     */
    public static Vec2D div(Vec2D v, float scalar) {
        return new Vec2D(
                v.x / scalar,
                v.y / scalar
        );
    }

    /**
     * Computes the dot product of two vectors.
     * 
     * <p>
     * The dot product is calculated as {@code (v1.x * v2.x) + (v1.y * v2.y)}.
     * This operation is useful for determining the angle between two vectors or
     * for projecting one vector onto another.
     * </p>
     *
     * @param v1 the first vector.
     * @param v2 the second vector.
     * @return the dot product of the two vectors.
     * @throws IllegalArgumentException if the vectors are not of the same type.
     */
    public static float dot(Vec2D v1, Vec2D v2) {
        return (v1.x * v2.x) + (v1.y * v2.y);
    }

    /**
     * Computes the 2D cross product of two vectors.
     * 
     * <p>
     * The 2D cross product is computed as
     * {@code (v1.x * v2.y) - (v1.y * v2.x)}. It represents the signed area of
     * the parallelogram formed by the two vectors. This value is useful for
     * determining the relative orientation of the vectors.
     * </p>
     *
     * @param v1 the first vector.
     * @param v2 the second vector.
     * @return the cross product of the two vectors.
     * @throws IllegalArgumentException if the vectors are not of the same type.
     */
    public static float cross(Vec2D v1, Vec2D v2) {
        return (v1.x * v2.y) - (v1.y * v2.x);
    } // UNTESTED

    /**
     * Linearly interpolates between two vectors.
     * 
     * <p>
     * The interpolation is computed as {@code v1 + t * (v2 - v1)} where
     * {@code t} is a value between 0 and 1 that determines the blend factor
     * between the two vectors.
     * </p>
     *
     * @param v1 the starting vector.
     * @param v2 the ending vector.
     * @param t the interpolation factor, which must be between 0 inclusive and
     * 1 exclusive.
     * @return a new vector representing the interpolated result.
     * @throws IllegalArgumentException if the vectors are not of the same type.
     */
    public static Vec2D lerp(Vec2D v1, Vec2D v2, float t) {
        return new Vec2D(
                v1.x + t * (v2.x - v1.x),
                v1.y + t * (v2.y - v1.y)
        );
    } // UNTESTED

    /**
     * Calculates the distance between two vectors.
     * 
     * <p>
     * The distanceBetween is calculated as the Euclidean distance between the
     * two points represented by the vectors.
     * </p>
     *
     * @param v1 the first vector.
     * @param v2 the second vector.
     * @return the distanceBetween between the two vectors.
     * @throws IllegalArgumentException if the vectors are not of the same type.
     */
    public static float distanceBetween(Vec2D v1, Vec2D v2) {
        return (float) Math.sqrt(Math.pow(v2.x - v1.x, 2) + Math.pow(v2.y - v1.y, 2));
    }

    /**
     * Projects one vector onto another vector.
     *
     * <p>
     * The projection is calculated using the formula:
     * {@code proj_v1_on_v2 = (v1 . v2) / |v2|^2 * v2}.
     * </p>
     *
     * <p>
     * If the vector {@code v2} is a zero vector, the method returns a zero
     * vector, as projecting onto a zero vector is undefined and results in a
     * zero projection.
     * </p>
     *
     * @param v1 the vector to be projected.
     * @param v2 the vector onto which {@code v1} is projected.
     * @return a new vector representing the projection of {@code v1} onto
     * {@code v2}, or a zero vector if {@code v2} is a zero vector.
     */
    public static Vec2D project(Vec2D v1, Vec2D v2) {
        float dot = v1.dot(v2);
        float magSq = v2.x * v2.x + v2.y * v2.y;

        if (magSq == 0.0f) {
            // Projection onto a zero vector is a zero vector
            return Vec2D.ZERO.copy();
        }

        float scalar = dot / magSq;
        return new Vec2D(
                scalar * v2.x,
                scalar * v2.y
        );
    }

    /**
     * Calculates the angle between two vectors in radians.
     * 
     * <p>
     * The angle is calculated using the dot product and magnitudes of the
     * vectors. The result is in radians, ranging from 0 to π.
     * </p>
     *
     * @param v1 the first vector.
     * @param v2 the second vector.
     * @return the angle between the two vectors in radians.
     * @throws IllegalArgumentException if the vectors are not of the same type.
     */
    public static float angleBetween(Vec2D v1, Vec2D v2) {
        float dot = v1.dot(v2);
        float mags = v1.mag() * v2.mag();
        return (float) Math.acos(dot / mags);
    }

    /**
     * Reflects a vector across another vector.
     * 
     * <p>
     * The reflection of a vector is calculated using the formula:
     * {@code R = V - 2 * (V . N) * N}, where {@code V} is the original vector
     * and {@code N} is the normal vector.
     * </p>
     *
     * @param v the vector to be reflected.
     * @param normal the vector to reflect across.
     * @return a new vector representing the reflection.
     * @throws IllegalArgumentException if the vectors are not of the same type.
     */
    public static Vec2D reflect(Vec2D v, Vec2D normal) {
        float dot = v.dot(normal);
        return new Vec2D(
                v.x - 2 * dot * normal.x,
                v.y - 2 * dot * normal.y
        );
    }

    /**
     * Creates and returns a new {@code Vec2D} instance with both x and y
     * components set to zero.
     * 
     * <p>
     * This method is useful for representing a neutral vector, where no
     * movement or force is applied in either direction. It can be used in
     * various scenarios, such as initialising vectors or resetting vector
     * values to a default state.
     * </p>
     *
     * <p>
     * <b>Example Usage:</b>
     * <pre><code>
     * Vec2D origin = Vec2D.zero(); // Creates a vector (0, 0)
     * </code></pre>
     * </p>
     *
     * @return a new {@code Vec2D} instance with both components set to zero.
     */
    public static Vec2D zero() {
        return new Vec2D(0, 0);
    }

    /**
     * Creates a new vector that is a copy of the supplier vector.
     * 
     * <p>
     * This method creates and returns a new {@code Vec2D} instance,
     * initialised with the components of the provided {@code supplierVector}.
     * </p>
     *
     * @param v the vector to copy
     * @return a new vector with the same components as the supplier vector
     */
    public static Vec2D copyOf(Vec2D v) {
        return new Vec2D(v.x, v.y);
    }

    // ============================== Inner Classes ================================ //
    /**
     * A private inner class representing an immutable vector. The
     * {@code ZeroVector} class extends {@code Vec2D} but overrides all
     * setter methods and operations that could alter its state, ensuring that
     * the vector remains constant throughout its lifecycle.
     *
     * <p>
     * The {@code ZeroVector} is specifically designed to represent immutable
     * instances of {@code Vec2D}, such as {@link Vec2D#ZERO}. Once an
     * instance of {@code ZeroVector} is created, its components cannot be
     * changed. Any attempt to modify the vector, either by changing its x or y
     * coordinates or by performing operations that would alter its magnitude or
     * direction, will result in an {@code UnsupportedOperationException}.
     * </p>
     *
     * <p>
     * This design ensures that constant vectors, like {@code Vec2D#ZERO},
     * remain unchanged and can be safely used across different parts of an
     * application without the risk of accidental modification. For example, the
     * {@code Vec2D#ZERO} vector represents the origin in a 2D coordinate
     * system (0,0) and should always maintain this state.
     * </p>
     *
     * <h3>Example Usage</h3>
     * <pre>
     * {@code
     * Vec2D zeroVector = Vec2D.ZERO;
     *
     * // Attempting to modify zeroVector will result in an exception:
     * zeroVector.setX(1); // Throws UnsupportedOperationException
     * zeroVector.setY(1); // Throws UnsupportedOperationException
     * }
     * </pre>
     *
     * <p>
     * Additionally, mathematical operations that would normally return a new
     * vector with altered components, such as {@code add(Vec2D other)}, are
     * overridden to either delegate the operation to static utility methods or
     * throw an {@code UnsupportedOperationException} where mutation would be
     * implied. The immutability of {@code ZeroVector} ensures its reliability
     * in contexts where constancy is critical.
     * </p>
     *
     * @see Vec2D
     * @see Vec2D#ZERO
     */
    public static class ZeroVector extends Vec2D {

        /**
         * Constructs an immutable vector with the specified x and y
         * coordinates. This constructor is typically used internally to create
         * instances of immutable vectors like {@code Vec2D#ZERO}.
         *
         * @param x the x coordinate of the vector
         * @param y the y coordinate of the vector
         */
        public ZeroVector(float x, float y) {
            super(x, y);
        }

        /**
         * {@inheritDoc}
         *
         * <p>
         * This method is overridden to prevent modification of the vector's x
         * coordinate. Any attempt to set a new value for x will result in an
         * {@code UnsupportedOperationException}.
         * </p>
         *
         * @throws UnsupportedOperationException if this method is called
         */
        @Override
        public Vec2D setX(float x) {
            throw new UnsupportedOperationException("Vec2D.ZERO is final and cannot be mutated");
        }

        /**
         * {@inheritDoc}
         *
         * <p>
         * This method is overridden to prevent modification of the vector's y
         * coordinate. Any attempt to set a new value for y will result in an
         * {@code UnsupportedOperationException}.
         * </p>
         *
         * @throws UnsupportedOperationException if this method is called
         */
        @Override
        public Vec2D setY(float y) {
            throw new UnsupportedOperationException("Vec2D.ZERO is final and cannot be mutated");
        }

        /**
         * {@inheritDoc}
         *
         * <p>
         * This method is overridden to prevent simultaneous modification of the
         * vector's x and y coordinates. Any attempt to set new values for both
         * components will result in an {@code UnsupportedOperationException}.
         * </p>
         *
         * @throws UnsupportedOperationException if this method is called
         */
        @Override
        public Vec2D setComponents(float x, float y) {
            throw new UnsupportedOperationException("Vec2D.ZERO is final and cannot be mutated");
        }

        /**
         * {@inheritDoc}
         *
         * <p>
         * This method is overridden to prevent scaling of the vector's
         * magnitude. Since {@code ZeroVector} represents an immutable vector,
         * any attempt to scale its magnitude will result in an
         * {@code UnsupportedOperationException}.
         * </p>
         *
         * @throws UnsupportedOperationException if this method is called
         */
        @Override
        public Vec2D setMag(float scalar) {
            throw new UnsupportedOperationException("Vec2D.ZERO is final and cannot be mutated");
        }

        /**
         * Adds another vector to this vector. Since {@code ZeroVector} is
         * immutable, this operation returns a new vector representing the sum
         * of this vector and the other vector.
         *
         * @param other the vector to be added
         * @return a new {@code Vec2D} representing the result of the
         * addition
         */
        @Override
        public Vec2D add(Vec2D other) {
            return Vec2D.add(this, other);
        }

        /**
         * Subtracts another vector from this vector. Since {@code ZeroVector}
         * is immutable, this operation returns a new vector representing the
         * result of the subtraction.
         *
         * @param other the vector to be subtracted
         * @return a new {@code Vec2D} representing the result of the
         * subtraction
         */
        @Override
        public Vec2D sub(Vec2D other) {
            return Vec2D.sub(this, other);
        }

        /**
         * {@inheritDoc}
         *
         * <p>
         * This method is overridden to prevent component-wise multiplication of
         * the vector. Any attempt to multiply this vector with another vector
         * will result in an {@code UnsupportedOperationException}.
         * </p>
         *
         * @throws UnsupportedOperationException if this method is called
         */
        @Override
        public Vec2D mult(Vec2D other) {
            throw new UnsupportedOperationException("Vec2D.ZERO is final and cannot be mutated");
        }

        /**
         * {@inheritDoc}
         *
         * <p>
         * This method is overridden to prevent component-wise division of the
         * vector. Any attempt to divide this vector by another vector will
         * result in an {@code UnsupportedOperationException}.
         * </p>
         *
         * @throws UnsupportedOperationException if this method is called
         */
        @Override
        public Vec2D div(Vec2D other) {
            throw new UnsupportedOperationException("Vec2D.ZERO is final and cannot be mutated");
        }

        /**
         * {@inheritDoc}
         *
         * <p>
         * This method is overridden to prevent scalar division of the vector.
         * Any attempt to divide this vector by a scalar will result in an
         * {@code UnsupportedOperationException}.
         * </p>
         *
         * @throws UnsupportedOperationException if this method is called
         */
        @Override
        public Vec2D div(float scalar) {
            throw new UnsupportedOperationException("Vec2D.ZERO is final and cannot be mutated");
        }

        /**
         * {@inheritDoc}
         *
         * <p>
         * This method is overridden to prevent scaling of the vector's
         * magnitude by a scalar. Any attempt to scale this vector will result
         * in an {@code UnsupportedOperationException}.
         * </p>
         *
         * @throws UnsupportedOperationException if this method is called
         */
        @Override
        public Vec2D scale(float scalar) {
            throw new UnsupportedOperationException("Vec2D.ZERO is final and cannot be mutated");
        }

        /**
         * {@inheritDoc}
         *
         * <p>
         * This method is overridden to prevent rotation of the vector. Any
         * attempt to rotate this vector will result in an
         * {@code UnsupportedOperationException}.
         * </p>
         *
         * @throws UnsupportedOperationException if this method is called
         */
        @Override
        public Vec2D rotate(float angle) {
            throw new UnsupportedOperationException("Vec2D.ZERO is final and cannot be mutated");
        }

        /**
         * {@inheritDoc}
         *
         * <p>
         * This method is overridden to allow copying of this vector into
         * another {@code Vec2D} object. The state of the current vector
         * remains unchanged.
         * </p>
         *
         * @param other the vector to be copied
         * @return a new {@code Vec2D} representing the copied vector
         */
        @Override
        public Vec2D copy(Vec2D other) {
            return other.copy();
        }

        /**
         * {@inheritDoc}
         *
         * <p>
         * This method is overridden to prevent normalisation of the vector. Any
         * attempt to normalise this vector will result in an
         * {@code UnsupportedOperationException}.
         * </p>
         *
         * @throws UnsupportedOperationException if this method is called
         */
        @Override
        public Vec2D normalize() {
            throw new UnsupportedOperationException("Vec2D.ZERO is final and cannot be mutated");
        }

    }

    /**
     * An inner class representing an immutable unit vector. The
     * {@code UnitVectorConstant} class extends {@code Vec2D} but overrides
     * all setter methods and certain operations that would alter its state,
     * ensuring that the vector remains constant and immutable throughout its
     * lifecycle.
     *
     * <p>
     * The {@code UnitVectorConstant} is specifically designed to represent
     * immutable direction vectors, such as {@link Vec2D#LEFT}, {@link Vec2D#RIGHT},
     * {@link Vec2D#UP}, and {@link Vec2D#DOWN}. These vectors are
     * typically used to represent fixed directions in 2D space and should not
     * be modified after creation.
     * </p>
     *
     * <p>
     * Similar to the {@link ZeroVector} class, the {@code UnitVectorConstant}
     * ensures that any attempt to alter the x or y coordinates, magnitude, or
     * orientation of the vector will result in an
     * {@code UnsupportedOperationException}. However, unlike
     * {@code ZeroVector}, the immutable nature of {@code UnitVectorConstant}
     * still allows for non-mutating operations that return new vectors based on
     * the original.
     * </p>
     *
     * <h3>Example Usage</h3>
     * <pre>
     * {@code
     * Vec2D leftVector = Vec2D.LEFT;
     *
     * // Attempting to modify leftVector will result in an exception:
     * leftVector.setX(0); // Throws UnsupportedOperationException
     * leftVector.setY(1); // Throws UnsupportedOperationException
     *
     * // Safe operations that return a new vector:
     * Vec2D resultVector = leftVector.add(new Vec2D(1, 0)); // Returns a new vector
     * }
     * </pre>
     *
     * <p>
     * This design ensures that direction vectors, once created, remain
     * unchanged and can be relied upon for consistent behavior throughout an
     * application. Mutable operations like addition, subtraction, and scaling
     * are allowed, but they return new {@code Vec2D} instances rather than
     * altering the original vector.
     * </p>
     *
     * @see Vec2D
     * @see ZeroVector
     */
    private static class UnitVectorConstant extends Vec2D {

        /**
         * Constructs an immutable unit vector with the specified x and y
         * coordinates. This constructor is typically used internally to create
         * instances of immutable direction vectors, like {@code Vec2D.LEFT}.
         *
         * @param x the x coordinate of the vector
         * @param y the y coordinate of the vector
         */
        public UnitVectorConstant(float x, float y) {
            super(x, y);
        }

        /**
         * {@inheritDoc}
         *
         * <p>
         * This method is overridden to prevent modification of the vector's x
         * coordinate. Any attempt to set a new value for x will result in an
         * {@code UnsupportedOperationException}.
         * </p>
         *
         * @throws UnsupportedOperationException if this method is called
         */
        @Override
        public Vec2D setX(float x) {
            throw new UnsupportedOperationException("Direction Vec2D instance is final and cannot be mutated");
        }

        /**
         * {@inheritDoc}
         *
         * <p>
         * This method is overridden to prevent modification of the vector's y
         * coordinate. Any attempt to set a new value for y will result in an
         * {@code UnsupportedOperationException}.
         * </p>
         *
         * @throws UnsupportedOperationException if this method is called
         */
        @Override
        public Vec2D setY(float y) {
            throw new UnsupportedOperationException("Direction Vec2D instance is final and cannot be mutated");
        }

        /**
         * {@inheritDoc}
         *
         * <p>
         * This method is overridden to prevent simultaneous modification of the
         * vector's x and y coordinates. Any attempt to set new values for both
         * components will result in an {@code UnsupportedOperationException}.
         * </p>
         *
         * @throws UnsupportedOperationException if this method is called
         */
        @Override
        public Vec2D setComponents(float x, float y) {
            throw new UnsupportedOperationException("Direction Vec2D instance is final and cannot be mutated");
        }

        /**
         * {@inheritDoc}
         *
         * <p>
         * This method is overridden to prevent scaling of the vector's
         * magnitude. Since {@code UnitVectorConstant} represents a fixed
         * direction vector, any attempt to scale its magnitude will result in
         * an {@code UnsupportedOperationException}.
         * </p>
         *
         * @throws UnsupportedOperationException if this method is called
         */
        @Override
        public Vec2D setMag(float scalar) {
            throw new UnsupportedOperationException("Direction Vec2D instance is final and cannot be mutated");
        }

        /**
         * Adds another vector to this vector. Since {@code UnitVectorConstant}
         * is immutable, this operation returns a new vector representing the
         * sum of this vector and the other vector.
         *
         * @param other the vector to be added
         * @return a new {@code Vec2D} representing the result of the
         * addition
         */
        @Override
        public Vec2D add(Vec2D other) {
            return this.copy().add(other);
        }

        /**
         * Subtracts another vector from this vector. Since
         * {@code UnitVectorConstant} is immutable, this operation returns a new
         * vector representing the result of the subtraction.
         *
         * @param other the vector to be subtracted
         * @return a new {@code Vec2D} representing the result of the
         * subtraction
         */
        @Override
        public Vec2D sub(Vec2D other) {
            return this.copy().sub(other);
        }

        /**
         * Multiplies this vector by another vector. Since
         * {@code UnitVectorConstant} is immutable, this operation returns a new
         * vector representing the result of the multiplication.
         *
         * @param other the vector to be multiplied
         * @return a new {@code Vec2D} representing the result of the
         * multiplication
         */
        @Override
        public Vec2D mult(Vec2D other) {
            return this.copy().mult(other);
        }

        /**
         * Divides this vector by another vector. Since
         * {@code UnitVectorConstant} is immutable, this operation returns a new
         * vector representing the result of the division.
         *
         * @param other the vector to divide by
         * @return a new {@code Vec2D} representing the result of the
         * division
         */
        @Override
        public Vec2D div(Vec2D other) {
            return this.copy().div(other);
        }

        /**
         * Divides this vector by a scalar. Since {@code UnitVectorConstant} is
         * immutable, this operation returns a new vector representing the
         * result of the division.
         *
         * @param scalar the scalar value to divide by
         * @return a new {@code Vec2D} representing the result of the
         * division
         */
        @Override
        public Vec2D div(float scalar) {
            return this.copy().div(scalar);
        }

        /**
         * Scales this vector by a scalar. Since {@code UnitVectorConstant} is
         * immutable, this operation returns a new vector representing the
         * result of the scaling.
         *
         * @param scalar the scalar value to scale by
         * @return a new {@code Vec2D} representing the result of the scaling
         */
        @Override
        public Vec2D scale(float scalar) {
            return this.copy().scale(scalar);
        }

        /**
         * Rotates this vector by a specified angle. Since
         * {@code UnitVectorConstant} is immutable, this operation returns a new
         * vector representing the rotated vector.
         *
         * @param angle the angle in radians to rotate by
         * @return a new {@code Vec2D} representing the rotated vector
         */
        @Override
        public Vec2D rotate(float angle) {
            return this.copy().rotate(angle);
        }

        /**
         * {@inheritDoc}
         *
         * <p>
         * This method is overridden to allow copying of this vector into
         * another {@code Vec2D} object. The state of the current vector
         * remains unchanged.
         * </p>
         *
         * @param other the vector to be copied
         * @return a new {@code Vec2D} representing the copied vector
         */
        @Override
        public Vec2D copy(Vec2D other) {
            return other.copy();
        }

        /**
         * Normalises this vector to have a magnitude of 1. Since
         * {@code UnitVectorConstant} is immutable, this operation returns a new
         * vector representing the normalised vector.
         *
         * @return a new {@code Vec2D} representing the normalised vector
         */
        @Override
        public Vec2D normalize() {
            return this.copy();
        }

    }

}