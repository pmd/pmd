/*
 * BSD-style license; for more info see http://pmd.sourceforge.net/license.html
 */

package javasymbols.testdata.impls;


import net.sourceforge.pmd.lang.java.symbols.testdata.AnnotWithDefaults;

public class AnnotationTests {

    @Deprecated
    public final String foo = "";

    @Deprecated
    private AnnotationTests() {

    }

    @AnnotWithDefaults(valueNoDefault = "", stringArrayDefault = {})
    public void someMethod(@AnnotWithDefaults(valueNoDefault = "", stringArrayDefault = {"oio"}) int formal) {

    }
}
