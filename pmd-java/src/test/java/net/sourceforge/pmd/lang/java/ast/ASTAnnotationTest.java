/**
 * BSD-style license; for more info see http://pmd.sourceforge.net/license.html
 */

package net.sourceforge.pmd.lang.java.ast;

import org.junit.Test;

public class ASTAnnotationTest extends BaseParserTest {

    @Test
    public void testAnnotationSucceedsWithDefaultMode() {
        java.parse(TEST1);
    }

    @Test(expected = ParseException.class)
    public void testAnnotationFailsWithJDK14() {
        java.parse(TEST1, "1.4");
    }

    @Test
    public void testAnnotationSucceedsWithJDK15() {
        java.parse(TEST1, "1.5");
    }

    private static final String TEST1 =
        "public class Foo extends Buz {\n @Override\n void bar() {\n  // overrides a superclass method\n }\n}";
}
