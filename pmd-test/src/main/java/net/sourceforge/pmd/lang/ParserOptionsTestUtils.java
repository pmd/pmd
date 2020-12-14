/**
 * BSD-style license; for more info see http://pmd.sourceforge.net/license.html
 */

package net.sourceforge.pmd.lang;

import org.junit.Assert;

public final class ParserOptionsTestUtils {
    private ParserOptionsTestUtils() {
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
        // Objects should be different
        Assert.assertNotSame(options1, options2);
        Assert.assertNotSame(options1, options2);
        Assert.assertNotSame(options1, options3);
        Assert.assertNotSame(options2, options3);
        Assert.assertNotSame(options2, options4);
        Assert.assertNotSame(options3, options4);

        // Check all 16 equality combinations
        Assert.assertEquals(options1, options1);
        Assert.assertNotEquals(options1, options2);
        Assert.assertEquals(options1, options3);
        Assert.assertNotEquals(options1, options4);

        Assert.assertNotEquals(options2, options1);
        Assert.assertEquals(options2, options2);
        Assert.assertNotEquals(options2, options3);
        Assert.assertEquals(options2, options4);

        Assert.assertEquals(options3, options1);
        Assert.assertNotEquals(options3, options2);
        Assert.assertEquals(options3, options3);
        Assert.assertNotEquals(options3, options4);

        Assert.assertNotEquals(options4, options1);
        Assert.assertEquals(options4, options2);
        Assert.assertNotEquals(options4, options3);
        Assert.assertEquals(options4, options4);

        // Hashcodes should match up
        Assert.assertNotEquals(options1.hashCode(), options2.hashCode());
        Assert.assertEquals(options1.hashCode(), options3.hashCode());
        Assert.assertNotEquals(options1.hashCode(), options4.hashCode());
        Assert.assertNotEquals(options2.hashCode(), options3.hashCode());
        Assert.assertEquals(options2.hashCode(), options4.hashCode());
        Assert.assertNotEquals(options3.hashCode(), options4.hashCode());
    }
}
