package org.bukkit.util;

import org.bukkit.Location;
import org.junit.Test;

import static org.hamcrest.Matchers.closeTo;
import static org.hamcrest.Matchers.is;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertThat;

public class AffineTransformTest {
    @Test
    public void testTransform() throws Exception {
        final Location[] references = {
                new Location(null, 1, 2, 3, 4, 5),
                new Location(null, 5, 4, 3, 2, 1),
                new Location(null, 827, 2834, 65981, 36, 51),
        };

        final Vector[] vectors =  {
                new Vector(2323, 666, 42),
                new Vector(123, 456, 789),
                new Vector(1e-6, 0, 1e-6),
                new Vector(1e6, 0, 1e6),
                new Vector(1234, 567, 89),
        };

        for (Location reference : references) {
            testTransform(reference);
            for (Vector vector : vectors) {
                testTransform(reference, vector);
                for (Location reference2 : references) {
                    testTransform(reference, reference2, vector);
                }
            }
        }

        for (Vector vector : vectors) {
            testTransform(vector);
            for (Vector vector2 : vectors) {
                testTransform(vector, vector2);
            }
        }
    }

    private void testTransform(Vector vectorA, Vector vectorB) {
        vectorA = vectorA.clone().normalize();
        vectorB = vectorB.clone().normalize();

        if (vectorA.equals(vectorB)) {
            return;
        }

        if (vectorA.clone().multiply(-1.0).equals(vectorB)) {
            return;
        }

        final Matrix3 matrix = Matrix3.fromPerpendicularRotation(vectorA, vectorB);
        for (int i = 0 ; i < 3; ++i) {
            for (int j = 0; j < 3; ++j) {
                assertFalse(String.format("Element %d/%d is NaN", i, j), Double.isNaN(matrix.get(i, j)));
            }
        }

        final Vector cross = vectorA.clone().crossProduct(vectorB);

        assertVectorEquals(vectorB, matrix.multiply(vectorA));
        assertVectorEquals(cross, matrix.multiply(cross));
    }

    private void testTransform(Vector vector) {
        // Test conformity with the untransformed coordinate system
        AffineTransform transform = AffineTransform.fromAnglesAndOffset(new Location(null, 0, 0, 0, 0, 0));
        assertVectorEquals(vector, transform.toLocalAxis(vector));
        assertVectorEquals(vector, transform.toWorldAxis(vector));
        assertVectorEquals(vector, transform.toLocal(vector));
        assertVectorEquals(vector, transform.toWorld(vector));
    }

    private void testTransform(Location reference, Vector vector) {
        AffineTransform transform = AffineTransform.fromAnglesAndOffset(reference);
        // Test inverse functions
        assertVectorEquals(vector, transform.toWorldAxis(transform.toLocalAxis(vector)));
        assertVectorEquals(vector, transform.toLocalAxis(transform.toWorldAxis(vector)));
        assertVectorEquals(vector, transform.toWorld(transform.toLocal(vector)));
        assertVectorEquals(vector, transform.toLocal(transform.toWorld(vector)));

        // Test length preservation
        assertThat(transform.toWorldAxis(vector).length(), is(closeTo(vector.length(), 1e-9)));

        // Test inversion
        AffineTransform inverse = transform.inverse();

        assertVectorEquals(vector, transform.toWorldAxis(inverse.toWorldAxis(vector)));
        assertVectorEquals(vector, transform.toLocalAxis(inverse.toLocalAxis(vector)));
        assertVectorEquals(vector, transform.toWorld(inverse.toWorld(vector)));
        assertVectorEquals(vector, transform.toLocal(inverse.toLocal(vector)));

        assertVectorEquals(vector, inverse.toWorldAxis(transform.toWorldAxis(vector)));
        assertVectorEquals(vector, inverse.toLocalAxis(transform.toLocalAxis(vector)));
        assertVectorEquals(vector, inverse.toWorld(transform.toWorld(vector)));
        assertVectorEquals(vector, inverse.toLocal(transform.toLocal(vector)));
    }

    private void testTransform(Location reference) {
        AffineTransform transform = AffineTransform.fromAnglesAndOffset(reference);
        // Test conformity with Location.getDirection()
        assertVectorEquals(reference.getDirection(), transform.toWorldAxis(new Vector(0, 0, 1)));
    }

    private void testTransform(Location referenceA, Location referenceB, Vector vector) {
        //referenceA = referenceA.clone(); referenceA.setX(0); referenceA.setY(0); referenceA.setZ(0);
        //referenceB = referenceB.clone(); referenceB.setX(0); referenceB.setY(0); referenceB.setZ(0);
        AffineTransform transformA = AffineTransform.fromAnglesAndOffset(referenceA);
        AffineTransform transformB = AffineTransform.fromAnglesAndOffset(referenceB);
        AffineTransform transformAB = transformA.clone().multiply(transformB);
        AffineTransform transformBA = transformB.clone().multiply(transformA);

        // Test associativity for toWorld[Axis]: A(Bv) == (AB)v
        assertVectorEquals(transformA.toWorld(transformB.toWorld(vector)), transformAB.toWorld(vector));
        assertVectorEquals(transformA.toWorldAxis(transformB.toWorldAxis(vector)), transformAB.toWorldAxis(vector));

        // Test associativity for toLocal[Axis]: A'(B'v) == (BA)'v where M' = M ^ -1, the inverse of M
        assertVectorEquals(transformA.toLocal(transformB.toLocal(vector)), transformBA.toLocal(vector));
        assertVectorEquals(transformA.toLocalAxis(transformB.toLocalAxis(vector)), transformBA.toLocalAxis(vector));
    }

    /**
     * Tests vector equality more accurately than {@link org.bukkit.util.Vector#equals(Object)}
     *
     * @param expected expected value
     * @param actual the value to check against <code>expected</code>
     */
    private void assertVectorEquals(Vector expected, Vector actual) {
        assertThat("X axis of vector is wrong", actual.getX(), is(closeTo(expected.getX(), 1e-9)));
        assertThat("Y axis of vector is wrong", actual.getY(), is(closeTo(expected.getY(), 1e-9)));
        assertThat("Z axis of vector is wrong", actual.getZ(), is(closeTo(expected.getZ(), 1e-9)));
    }
}
