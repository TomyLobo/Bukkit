package org.bukkit.util;

import org.apache.commons.lang.Validate;
import org.bukkit.Location;

public class AffineTransform {
    final Matrix3 orthogonalMatrix;
    final Vector offset;

    private AffineTransform(Matrix3 orthogonalMatrix, Vector offset) {
        this.orthogonalMatrix = orthogonalMatrix;
        this.offset = offset;
    }

    /**
     * Constructs and affine transformation that does nothing.
     *
     * @return an identity transformation
     */
    public static AffineTransform identity() {
        return new AffineTransform(Matrix3.identity(), new Vector());
    }

    /**
     * Constructs an affine transformation that rotates the specified number of degrees around the specified axis and then shifts by the specified offset.
     *
     * @param offset offset result by this vector after rotation
     * @param axis rotate about the axis described by this vector
     * @param angle rotate this many degrees
     * @return the requested transformation
     */
    public static AffineTransform fromOffsetAxisAndAngle(Vector offset, Vector axis, double angle) {
        return new AffineTransform(Matrix3.fromAxisAndAngle(axis, angle), offset.clone());
    }

    /**
     * Constructs an affine transformation from the specified angles and offset.
     * <p>
     * If used on a player's eye location, the following conditions apply:
     * <ul>
     * <li>The X axis points to the left
     * <li>The Y axis points upward
     * <li>The Z axis points forward
     * </ul>
     *
     * @param yaw first, rotate by this many degrees around the Y axis
     * @param pitch second, rotate by this many degrees around the X axis
     * @param roll third, rotate by this many degrees around the Z axis
     * @param offset finally, offset the result by this vector
     * @return the resulting affine transformation
     */
    public static AffineTransform fromOffsetAndAngles(double yaw, double pitch, double roll, Vector offset) {
        return new AffineTransform(Matrix3.fromAngles(yaw, pitch, roll), offset.clone());
    }

    /**
     * Constructs an affine transformation from the specified {@link Location}.
     * <p>
     * If used on a player's eye location, the following conditions apply:
     * <ul>
     * <li>The X axis points to the left
     * <li>The Y axis points upward
     * <li>The Z axis points forward
     * </ul>
     *
     * @param location the location to use as a reference for constructing an affine transformation
     * @return the resulting affine transformation
     */
    public static AffineTransform fromOffsetAndAngles(Location location) {
        return new AffineTransform(Matrix3.fromAngles(location), location.toVector());
    }

    public AffineTransform inverse() {
        final Matrix3 inverse = orthogonalMatrix.inverse();
        return new AffineTransform(inverse, inverse.multiply(offset).multiply(-1.0));
    }

    public AffineTransform multiply(AffineTransform rhs) {
        /*
        derivation:
        combine Av+x (i.e. this.toWorld) and Bv+y (i.e. rhs.toWorld)
        Cv+z = A(Bv+y)+x  | A(B+C) = AB+AC
        Cv+z = (ABv+Ay)+x | (A+B)+C = A+(B+C)
        Cv+z = ABv+(Ay+x)
         ||
         \/
        C=AB
        z=Ay+x, in other words (Av+x)(y), i.e. toWorld(rhs.offset)
        */
        return new AffineTransform(orthogonalMatrix.clone().multiply(rhs.orthogonalMatrix), toWorld(rhs.offset));
    }

    /**
     * Transforms an axis from the local reference frame described by this
     * affine transformation into the world reference frame.
     *
     * @param axis a local-coordinate axis
     * @return a world-coordinate axis
     */
    public Vector toWorldAxis(Vector axis) {
        return orthogonalMatrix.multiply(axis);
    }

    /**
     * Transforms an axis from the world reference frame into the local
     * reference frame described by this affine transformation.
     *
     * @param axis a world-coordinate axis
     * @return a local-coordinate axis
     */
    public Vector toLocalAxis(Vector axis) {
        return orthogonalMatrix.multiplyTranspose(axis);
    }

    /**
     * Transforms a position from the local reference frame described by this
     * affine transformation into the world reference frame.
     *
     * @param position a local-coordinate position
     * @return a world-coordinate position
     */
    public Vector toWorld(Vector position) {
        Validate.notNull(position);

        return toWorldAxis(position).add(offset);
    }

    /**
     * Transforms a position from the world reference frame into the local
     * reference frame described by this affine transformation.
     *
     * @param position a world-coordinate position
     * @return a local-coordinate position
     */
    public Vector toLocal(Vector position) {
        Validate.notNull(position);

        return toLocalAxis(position.clone().subtract(offset));
    }


    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof AffineTransform)) return false;

        AffineTransform that = (AffineTransform) o;

        if (!offset.equals(that.offset)) return false;
        if (!orthogonalMatrix.equals(that.orthogonalMatrix)) return false;

        return true;
    }

    @Override
    public int hashCode() {
        int result = orthogonalMatrix.hashCode();
        result = 31 * result + offset.hashCode();
        return result;
    }

    @Override
    public String toString() {
        return "AffineTransform{" +
                "orthogonalMatrix=" + orthogonalMatrix +
                ", offset=" + offset +
                '}';
    }

    @Override
    public AffineTransform clone() {
        return new AffineTransform(orthogonalMatrix.clone(), offset.clone());
    }
}
