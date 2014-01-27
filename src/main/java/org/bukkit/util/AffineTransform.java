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
     */
    public AffineTransform() {
        this(Matrix3.identity(), new Vector());
    }

    /**
     * Constructs an affine transformation that rotates the specified number of degrees around the specified axis and then shifts by the specified offset.
     *
     * @param offset
     * @param axis
     * @param radians
     * @return
     */
    public static AffineTransform fromOffsetAxisAndAngle(Vector offset, Vector axis, double radians) {
        return new AffineTransform(Matrix3.fromAxisAndAngle(axis, radians), offset.clone());
    }

    /**
     * Constructs an affine transformation from the specified offset and angles.
     * <p>
     * The following conditions apply:
     * <ul>
     * <li>X axis points to the left
     * <li>Y axis points upward
     * <li>Z axis points forward
     * </ul>
     *
     * @param offset
     * @param yaw
     * @param pitch
     * @param roll
     * @return
     */
    public static AffineTransform fromOffsetAndAngles(Vector offset, double yaw, double pitch, double roll) {
        return new AffineTransform(Matrix3.fromAngles(yaw, pitch, roll), offset.clone());
    }

    /**
     * Constructs an affine transformation from the specified {@link Location}.
     * <p>
     * The following conditions apply:
     * <ul>
     * <li>X axis points to the left
     * <li>Y axis points upward
     * <li>Z axis points forward
     * </ul>
     *
     * @param location
     * @return
     */
    public static AffineTransform fromOffsetAndAngles(Location location) {
        return new AffineTransform(Matrix3.fromAngles(location), location.toVector());
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
     * <p>
     * The following conditions apply:
     * <ul>
     * <li>X axis points to the left
     * <li>Y axis points upward
     * <li>Z axis points forward
     * </ul>
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
     * <p>
     * The following conditions apply:
     * <ul>
     * <li>X axis points to the left
     * <li>Y axis points upward
     * <li>Z axis points forward
     * </ul>
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
     * <p>
     * The following conditions apply:
     * <ul>
     * <li>X axis points to the left
     * <li>Y axis points upward
     * <li>Z axis points forward
     * </ul>
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
}
