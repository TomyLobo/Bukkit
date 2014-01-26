package org.bukkit.util;

import org.bukkit.Location;
import org.junit.Test;

import static org.hamcrest.Matchers.closeTo;
import static org.hamcrest.Matchers.is;
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
            }
        }

        for (Vector vector : vectors) {
            testTransform(vector);
        }
    }

    private void testTransform(Vector vector) {
        // Test conformity with the untransformed coordinate system
        AffineTransform transform = AffineTransform.fromOffsetAndAngles(new Location(null, 0, 0, 0, 0, 0));
        assertVectorEquals(vector, transform.toLocalAxis(vector));
        assertVectorEquals(vector, transform.toWorldAxis(vector));
        assertVectorEquals(vector, transform.toLocal(vector));
        assertVectorEquals(vector, transform.toWorld(vector));
    }

    private void testTransform(Location reference, Vector vector) {
        AffineTransform transform = AffineTransform.fromOffsetAndAngles(reference);
        // Test inverse functions
        assertVectorEquals(vector, transform.toWorldAxis(transform.toLocalAxis(vector)));
        assertVectorEquals(vector, transform.toLocalAxis(transform.toWorldAxis(vector)));
        assertVectorEquals(vector, transform.toWorld(transform.toLocal(vector)));
        assertVectorEquals(vector, transform.toLocal(transform.toWorld(vector)));
        assertThat(transform.toWorldAxis(vector).length(), is(closeTo(vector.length(), 1e-9)));
    }

    private void testTransform(Location reference) {
        AffineTransform transform = AffineTransform.fromOffsetAndAngles(reference);
        // Test conformity with Location.getDirection()
        assertVectorEquals(reference.getDirection(), transform.toWorldAxis(new Vector(0, 0, 1)));
    }

    /**
     * Tests vector equality more accurately than {@link org.bukkit.util.Vector#equals(Object)}
     *
     * @param expected expected value
     * @param actual the value to check against <code>expected</code>
     */
    private void assertVectorEquals(Vector expected, Vector actual) {
        assertThat(actual.getX(), is(closeTo(expected.getX(), 1e-9)));
        assertThat(actual.getY(), is(closeTo(expected.getY(), 1e-9)));
        assertThat(actual.getZ(), is(closeTo(expected.getZ(), 1e-9)));
    }
}
