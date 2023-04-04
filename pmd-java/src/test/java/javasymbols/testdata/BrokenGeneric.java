/*
 * BSD-style license; for more info see http://pmd.sourceforge.net/license.html
 */

package javasymbols.testdata;

import net.sourceforge.pmd.lang.java.symbols.internal.asm.Classpath;

/**
 * For this test we exclude SuperItf.class from the {@link Classpath}
 * to mimic an incomplete classpath.
 *
 * @see net.sourceforge.pmd.lang.java.symbols.internal.asm.BrokenClasspathTest
 */
public class BrokenGeneric<T0, T1>
    extends SuperKlass<T0, T0>
    implements SuperItf<T1, T1> {
}

class SuperKlass<A, B> { }

interface SuperItf<A, B> { }
