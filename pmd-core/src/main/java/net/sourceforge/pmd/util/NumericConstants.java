/**
 * BSD-style license; for more info see http://pmd.sourceforge.net/license.html
 */

package net.sourceforge.pmd.util;

/**
 * @deprecated These constants were only useful before autoboxing was
 *     introduced, just use a literal or define your own constants
 */
@Deprecated
public final class NumericConstants {

    public static final Integer ZERO = 0;

    public static final Integer ONE = 1;

    public static final Float FLOAT_ZERO = 0.0f;

    private NumericConstants() { }
}
