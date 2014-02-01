package org.bukkit.util;

import org.apache.commons.lang.Validate;
import org.bukkit.Location;

import java.util.Arrays;

public class Matrix3 {
    private final double[] data;

    //******************************* constructors ********************************

    private Matrix3() {
        this(new double[9]);
    }

    private Matrix3(double... elements) {
        data = elements;
    }


    /**
     * Constructs a matrix that does nothing to a vector when multiplied with it.
     *
     * @return an identity matrix
     */
    public static Matrix3 identity() {
        return new Matrix3(
                1, 0, 0,
                0, 1, 0,
                0, 0, 1
        );
    }

    /**
     * Builds a matrix from the specified columns.
     *
     * @param a0 left column
     * @param a1 center column
     * @param a2 right column
     * @return the constructed matrix
     */
    public static Matrix3 fromColumns(Vector a0, Vector a1, Vector a2) {
        return new Matrix3(
                a0.x, a1.x, a2.x,
                a0.y, a1.y, a2.y,
                a0.z, a1.z, a2.z
        );
    }

    /**
     * Builds a matrix from the specified rows.
     *
     * @param a0 top row
     * @param a1 middle row
     * @param a2 bottom row
     * @return the constructed matrix
     */
    public static Matrix3 fromRows(Vector a0, Vector a1, Vector a2) {
        return new Matrix3(
                a0.x, a0.y, a0.z,
                a1.x, a1.y, a1.z,
                a2.x, a2.y, a2.z
        );
    }

    /**
     * Constructs a matrix that, when a vector multiplied with it, rotates
     * it the specified amount of degrees around the specified axis.
     *
     * @param axis rotate about the axis described by this vector
     * @param angle rotate this many degrees
     * @return the resulting matrix
     */
    public static Matrix3 fromAxisAndAngle(Vector axis, double angle) {
        if (angle == 0) {
            return Matrix3.identity();
        }

        angle = Math.toRadians(angle);

        final double length = axis.length();

        final double x = axis.getX() / length;
        final double y = axis.getY() / length;
        final double z = axis.getZ() / length;
        final double cosRadians = Math.cos(angle);
        final double factor = (1 - cosRadians);
        final double sinRadians = Math.sin(angle);
        return new Matrix3(
                x * x + (1 - x * x) * cosRadians, x * y * factor - z * sinRadians , x * z * factor + y * sinRadians,
                x * y * factor + z * sinRadians , y * y + (1 - y * y) * cosRadians, y * z * factor - x * sinRadians,
                x * z * factor - y * sinRadians , y * z * factor + x * sinRadians , z * z + (1 - z * z) * cosRadians
        );
    }

    /**
     * Constructs a matrix from the specified angles.
     * <p>
     * The Minecraft axis conventions are used, so that if a player's eye
     * location's components are passed, the following conditions apply:
     * <ul>
     * <li>The X axis points to the left
     * <li>The Y axis points upward
     * <li>The Z axis points forward
     * </ul>
     *
     * @param yaw first, rotate by this many degrees around the Y axis
     * @param pitch second, rotate by this many degrees around the X axis
     * @param roll third, rotate by this many degrees around the Z axis
     * @return the resulting affine transformation
     */
    public static Matrix3 fromAngles(double yaw, double pitch, double roll) {
        if (yaw == 0 && pitch == 0 && roll == 0) {
            return Matrix3.identity();
        }

        yaw = Math.toRadians(yaw);
        pitch = Math.toRadians(pitch);
        roll = Math.toRadians(roll);
        final double cy = Math.cos(yaw);
        final double sy = Math.sin(yaw);
        final double cp = Math.cos(pitch);
        final double sp = Math.sin(pitch);
        final double cr = Math.cos(roll);
        final double sr = Math.sin(roll);

        return new Matrix3(
                cr * cy - sp * sr * sy, -cy * sr - cr * sp * sy, -cp * sy,
                cp * sr               ,  cp * cr               , -sp,
                cy * sp * sr + cr * sy,  cr * cy * sp - sr * sy,  cp * cy
        );
    }

    /**
     * Constructs a matrix from the specified {@link Location}'s angles.
     * <p>
     * The Minecraft axis conventions are used, so that if a player's eye
     * location is passed, the following conditions apply:
     * <ul>
     * <li>The X axis points to the left
     * <li>The Y axis points upward
     * <li>The Z axis points forward
     * </ul>
     *
     * @param location the location to use as a reference for constructing an affine transformation
     * @return the resulting affine transformation
     */
    public static Matrix3 fromAngles(Location location) {
        return fromAngles(location.getYaw(), location.getPitch(), 0);
    }

    //********************************** accessors *********************************

    private double rawGet(int row, int column) {
        return data[row * 3 + column];
    }

    private void rawSet(int row, int column, double value) {
        data[row * 3 + column] = value;
    }

    /**
     * Returns the specified element of the matrix.
     *
     * @param row the row identifying the element
     * @param column the column identifying the element
     * @return the specified element
     */
    public double get(int row, int column) {
        Validate.isTrue(row >= 0, "Row must be non-negative: ", row);
        Validate.isTrue(column >= 0, "Column must be non-negative: ", column);
        Validate.isTrue(row < 3, "Row must be less than 3: ", row);
        Validate.isTrue(column < 3, "Column must be less than 3: ", column);

        return rawGet(row, column);
    }

    /**
     * Sets the specified element of the matrix.
     *
     * @param row the row identifying the element
     * @param column the column identifying the element
     * @param value the value to set the element to
     */
    public void set(int row, int column, double value) {
        Validate.isTrue(row >= 0, "Row must be non-negative: ", row);
        Validate.isTrue(column >= 0, "Column must be non-negative: ", column);
        Validate.isTrue(row < 3, "Row must be less than 3: ", row);
        Validate.isTrue(column < 3, "Column must be less than 3: ", column);

        rawSet(row, column, value);
    }

    //****************************** utility methods *****************************

    /**
     * Returns the determinant of the matrix.
     *
     * @return the determinant
     */
    public double det() {
        return rawGet(0, 0) * rawGet(1, 1) * rawGet(2, 2) + rawGet(0, 1) * rawGet(1, 2) * rawGet(2, 0) + rawGet(0, 2) * rawGet(1, 0) * rawGet(2, 1) - rawGet(2, 0) * rawGet(1, 1) * rawGet(0, 2) - rawGet(2, 1) * rawGet(1, 2) * rawGet(0, 0) - rawGet(2, 2) * rawGet(1, 0) * rawGet(0, 1);
    }

    /**
     * Returns a new matrix that, when multiplied with this matrix, yields the
     * identity matrix.
     *
     * @return the inverted transformation
     */
    public Matrix3 inverse() {
        final double determinant = det();

        Validate.isTrue(determinant != 0, "Matrix must be regular, i.e. determinant must be non-zero: ", determinant);

        final double inverseDeterminant = 1 / determinant;
        return new Matrix3(
                +(rawGet(1, 1) * rawGet(2, 2) - rawGet(2, 1) * rawGet(1, 2)) * inverseDeterminant,
                -(rawGet(0, 1) * rawGet(2, 2) - rawGet(0, 2) * rawGet(2, 1)) * inverseDeterminant,
                +(rawGet(0, 1) * rawGet(1, 2) - rawGet(0, 2) * rawGet(1, 1)) * inverseDeterminant,
                -(rawGet(1, 0) * rawGet(2, 2) - rawGet(1, 2) * rawGet(2, 0)) * inverseDeterminant,
                +(rawGet(0, 0) * rawGet(2, 2) - rawGet(0, 2) * rawGet(2, 0)) * inverseDeterminant,
                -(rawGet(0, 0) * rawGet(1, 2) - rawGet(1, 0) * rawGet(0, 2)) * inverseDeterminant,
                +(rawGet(1, 0) * rawGet(2, 1) - rawGet(2, 0) * rawGet(1, 1)) * inverseDeterminant,
                -(rawGet(0, 0) * rawGet(2, 1) - rawGet(2, 0) * rawGet(0, 1)) * inverseDeterminant,
                +(rawGet(0, 0) * rawGet(1, 1) - rawGet(1, 0) * rawGet(0, 1)) * inverseDeterminant
        );
    }

    /**
     * Returns a matrix, which has columns and rows swapped.
     * <p>
     * That means rows become columns and vice versa.
     *
     * @return the transposed matrix
     */
    public Matrix3 transpose() {
        final Matrix3 ret = new Matrix3();
        for (int i = 0; i < 3; ++i) {
            for (int j = 0; j < 3; ++j) {
                ret.rawSet(i, j, rawGet(j, i));
            }
        }
        return ret;
    }

    //*********************** operations between two matrices **********************

    /**
     * Replaces this matrix by a new matrix that encompasses both this and the
     * specified matrix.
     * <p>
     * Like in all matrix multiplications, the result applies the
     * transformations right-to-left.
     *
     * @param rhs the matrix to multiply with
     * @return this instance, containing the combined matrix
     */
    public Matrix3 multiply(Matrix3 rhs) {
        // TODO: consider making this method return a new instance.
        // upside: eliminates the forced copy, consistent with other multiply methods
        // downside: inconsistent with the other matrix/matrix operations
        double[] duplicate = Arrays.copyOf(data, 9);
        for (int i = 0; i < 3; ++i) {
            for (int j = 0; j < 3; ++j) {
                double accumulator = 0;
                for (int r = 0; r < 3; ++r) {
                    accumulator += duplicate[i * 3 + r] * rhs.rawGet(r, j);
                }
                rawSet(i, j, accumulator);
            }
        }
        return this;
    }

    /**
     * Adds another matrix to this one, element-wise.
     *
     * @param rhs the matrix to add
     * @return this instance, containing the added elements
     */
    public Matrix3 add(Matrix3 rhs) {
        for (int n = 0; n < 9; ++n) data[n] += rhs.data[n];
        return this;
    }

    /**
     * Subtracts another matrix from this one, element-wise.
     *
     * @param rhs the matrix to subtract
     * @return this instance, containing the subtracted elements
     */
    public Matrix3 subtract(Matrix3 rhs) {
        for (int n = 0; n < 9; ++n) data[n] -= rhs.data[n];
        return this;
    }

    //*************************** operations with vectors **************************

    /**
     * M&sdot;v
     * <p>
     * where M=this, v=rhs
     *
     * @param rhs
     * @return
     */
    public Vector multiply(Vector rhs) {
        final double[] rhsArray = vectorToArray(rhs);
        final double[] ret = new double[3];
        for (int i = 0; i < 3; ++i) {
            double accumulator = 0;
            for (int r = 0; r < 3; ++r) {
                accumulator += rawGet(i, r) * rhsArray[r];
            }
            ret[i] = accumulator;
        }
        return vectorFromArray(ret);
    }

    /**
     * (v<sup>T</sup>&sdot;M)<sup>T</sup> = M<sup>T</sup>&sdot;v
     * <p>
     * where M=this, v=lhs
     *
     * @param lhs
     * @return
     */
    public Vector multiplyTranspose(Vector lhs) {
        final double[] lhsArray = vectorToArray(lhs);
        final double[] ret = new double[3];
        for (int j = 0; j < 3; ++j) {
            double accumulator = 0;
            for (int r = 0; r < 3; ++r) {
                accumulator += lhsArray[r] * rawGet(r, j);
            }
            ret[j] = accumulator;
        }
        return vectorFromArray(ret);
    }

    private double[] vectorToArray(Vector vector) {
        return new double[]{
                vector.getX(),
                vector.getY(),
                vector.getZ(),
        };
    }

    private Vector vectorFromArray(double[] array) {
        return new Vector(
                array[0],
                array[1],
                array[2]
        );
    }

    //*************************** operations with scalars **************************

    /**
     * Multiplies each element with the specified scalar.
     *
     * @param scalar the scalar to multiply with
     * @return this instance, containing the multiplied elements
     */
    public Matrix3 multiply(double scalar) {
        for (int n = 0; n < 9; ++n) data[n] *= scalar;
        return this;
    }

    /**
     * Divides each element by the specified scalar.
     *
     * @param scalar the scalar to divide by
     * @return this instance, containing the divided elements
     */
    public Matrix3 divide(double scalar) {
        for (int n = 0; n < 9; ++n) data[n] /= scalar;
        return this;
    }

    /**
     * Adds the specified scalar to each element.
     *
     * @param scalar the scalar to add
     * @return this instance, containing the added elements
     */
    public Matrix3 add(double scalar) {
        for (int n = 0; n < 9; ++n) data[n] += scalar;
        return this;
    }

    /**
     * Subtracts the specified scalar from each element.
     *
     * @param scalar the scalar to subtract
     * @return this instance, containing the subtracted elements
     */
    public Matrix3 subtract(double scalar) {
        for (int n = 0; n < 9; ++n) data[n] -= scalar;
        return this;
    }

    @Override
    public String toString() {
        StringBuilder b = new StringBuilder();
        for (int i = 0; i < data.length; i++) {
            if (i % 3 == 0)
                b.append("\n    ");
            else
                b.append(", ");
            b.append(data[i]);
        }

        return "Matrix3{" + b.toString() + "\n}";
    }

    @Override
    public Matrix3 clone() {
        return new Matrix3(Arrays.copyOf(data, 9));
    }
}
