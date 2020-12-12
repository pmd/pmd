/**
 * BSD-style license; for more info see http://pmd.sourceforge.net/license.html
 */

package net.sourceforge.pmd.lang;

/**
 * Unit tests for {@link ParserOptions}.
 * @deprecated for removal in PMD 7.0. Use {@link ParserOptionsTestUtils}.
 */
@Deprecated
public class ParserOptionsTest {

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
