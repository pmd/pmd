/*
 * BSD-style license; for more info see http://pmd.sourceforge.net/license.html
 */

package net.sourceforge.pmd.lang.rule.internal;

import static java.lang.Integer.signum;
import static org.junit.Assert.assertEquals;

import org.junit.Test;

/**
 * @author Clément Fournier
 */
abstract class ComparatorTest<T> {


    abstract Iterable<? extends T> generator();


    abstract int compare(T a, T b);

    String toString(T t) {
        return t.toString();
    }

    private String format(T a, T b) {
        return format(a, b, compare(a, b));
    }

    private String format(T a, T b, int res) {
        String op = res < 0 ? "<" : res > 0 ? ">" : "==";

        return toString(a) + " " + op + " " + toString(b);
    }

    @Test
    public void transitivityTest() {

        /*
         * The implementor must also ensure that the relation is transitive:
         * {@code ((compare(x, y)>0) && (compare(y, z)>0))} implies
         * {@code compare(x, z)>0}.<p>
         */

        for (T x : generator()) {
            for (T y : generator()) {
                int xY = compare(x, y);

                if (xY != 0) {
                    for (T z : generator()) {
                        int yZ = compare(y, z);
                        if (yZ != 0) {


                            if (signum(xY) == signum(yZ)) {
                                // x < y < z || z < y < x
                                assertEquals(
                                    "Expected " + format(x, y)
                                        + " and " + format(y, z)
                                        + " to imply " + format(x, z, compare(x, y)),
                                    signum(xY),
                                    signum(compare(x, z))
                                );
                            }
                        }
                    }
                }
            }
        }
    }

    @Test
    public void assymetryTest() {
        /*
         * The implementor must ensure that {@code sgn(compare(x, y)) ==
         * -sgn(compare(y, x))} for all {@code x} and {@code y}.
         */

        for (T x : generator()) {
            for (T y : generator()) {

                assertEquals(
                    "Expected " + format(x, y)
                        + " to imply " + format(y, x, -compare(x, y)),
                    signum(compare(x, y)),
                    -signum(compare(y, x))
                );
            }
        }
    }

    @Test
    public void substitutabilityTest() {
        /*
         * Finally, the implementor must ensure that {@code compare(x, y)==0}
         * implies that {@code sgn(compare(x, z))==sgn(compare(y, z))} for all
         * {@code z}.<p>
         */

        for (T x : generator()) {
            for (T y : generator()) {

                int xY = compare(x, y);

                if (xY == 0) {
                    for (T z : generator()) {

                        assertEquals(
                            "Expected " + format(x, y)
                                + " and " + format(x, z)
                                + " to imply " + format(y, z, compare(x, z)),
                            signum(compare(x, z)),
                            signum(compare(y, z)));
                    }
                }
            }
        }
    }

}
