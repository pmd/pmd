/*
 * BSD-style license; for more info see http://pmd.sourceforge.net/license.html
 */

package net.sourceforge.pmd.lang.java.ast;

import static org.hamcrest.CoreMatchers.containsString;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.junit.jupiter.api.Assertions.assertThrows;

import org.junit.jupiter.api.Test;

import net.sourceforge.pmd.lang.ast.ParseException;
import net.sourceforge.pmd.lang.java.BaseJavaTreeDumpTest;
import net.sourceforge.pmd.lang.java.JavaParsingHelper;
import net.sourceforge.pmd.lang.test.ast.BaseParsingHelper;

class Java26PreviewTreeDumpTest extends BaseJavaTreeDumpTest {
    private final JavaParsingHelper java26p =
            JavaParsingHelper.DEFAULT.withDefaultVersion("26-preview")
                    .withResourceContext(Java26PreviewTreeDumpTest.class, "jdkversiontests/java26p/");
    private final JavaParsingHelper java26 = java26p.withDefaultVersion("26");

    @Override
    public BaseParsingHelper<?, ?> getParser() {
        return java26p;
    }

    @Test
    void jep530PrimitiveTypesInPatternsInstanceofAndSwitch() {
        doTest("Jep530_PrimitiveTypesInPatternsInstanceofAndSwitch");
    }

    @Test
    void jep530PrimitiveTypesInPatternsInstanceofAndSwitchBeforeJava26Preview() {
        ParseException thrown = assertThrows(ParseException.class, () -> java26.parseResource("Jep530_PrimitiveTypesInPatternsInstanceofAndSwitch.java"));
        assertThat(thrown.getMessage(), containsString("Primitive types in patterns instanceof and switch is a preview feature of JDK 26, you should select your language version accordingly"));
    }
}
