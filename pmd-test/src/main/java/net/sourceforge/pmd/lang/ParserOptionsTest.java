/**
 * BSD-style license; for more info see http://pmd.sourceforge.net/license.html
 */

package net.sourceforge.pmd.lang;

import org.junit.Assert;
import org.junit.Test;

/**
 * Unit tests for {@link ParserOptions}.
 * @deprecated for removal in PMD 7.0. Use {@link ParserOptionsTestUtils}.
 */
@Deprecated
public class ParserOptionsTest {

    /**
     * Verify that the equals and hashCode methods work as expected.
     */
    @Test
    public void testEqualsHashcode() {
        ParserOptions options1 = new ParserOptions();
        options1.setSuppressMarker("foo");
        ParserOptions options2 = new ParserOptions();
        options2.setSuppressMarker("bar");
        ParserOptions options3 = new ParserOptions();
        options3.setSuppressMarker("foo");
        ParserOptions options4 = new ParserOptions();
        options4.setSuppressMarker("bar");
        verifyOptionsEqualsHashcode(options1, options2, options3, options4);
    }

    /**
     * Verify equals and hashCode for 4 {@link ParserOptions} instances. The
     * given options should be as follows: 1 and 3 are equals, as are 2 and 4.
     *
     * @param options1
     *            first option instance - equals third
     * @param options2
     *            second option instance - equals fourth
     * @param options3
     *            third option instance - equals first
     * @param options4
     *            fourth option instance - equals second
     */
    public static void verifyOptionsEqualsHashcode(ParserOptions options1, ParserOptions options2,
                                                   ParserOptions options3, ParserOptions options4) {
        ParserOptionsTestUtils.verifyOptionsEqualsHashcode(options1, options2, options3, options4);
    }
}
