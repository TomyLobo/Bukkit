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

    public AffineTransform() {
        this(Matrix3.identity(), new Vector());
    }

    public static AffineTransform fromOffsetAxisAndAngle(Vector offset, Vector axis, double radians) {
        return new AffineTransform(Matrix3.fromAxisAndAngle(axis, radians), offset.clone());
    }

    public static AffineTransform fromOffsetAndAngles(Vector offset, double yaw, double pitch, double roll) {
        return new AffineTransform(Matrix3.fromAngles(yaw, pitch, roll), offset.clone());
    }

    public static AffineTransform fromOffsetAndAngles(Location location) {
        return new AffineTransform(Matrix3.fromAngles(location), location.toVector());
    }

    public Vector toWorld(Vector position) {
        Validate.notNull(position);

        return toWorldAxis(position).add(offset);
    }

    public Vector toWorldAxis(Vector axis) {
        return orthogonalMatrix.multiply(axis);
    }

    public Vector toLocal(Vector position) {
        Validate.notNull(position);

        return toLocalAxis(position.clone().subtract(offset));
    }

    public Vector toLocalAxis(Vector axis) {
        return orthogonalMatrix.multiplyTranspose(axis);
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
