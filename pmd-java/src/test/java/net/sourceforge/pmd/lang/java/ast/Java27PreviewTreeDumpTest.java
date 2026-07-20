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

class Java27PreviewTreeDumpTest extends BaseJavaTreeDumpTest {
    private final JavaParsingHelper java27p =
            JavaParsingHelper.DEFAULT.withDefaultVersion("27-preview")
                    .withResourceContext(Java27PreviewTreeDumpTest.class, "jdkversiontests/java27p/");
    private final JavaParsingHelper java27 = java27p.withDefaultVersion("27");

    @Override
    public BaseParsingHelper<?, ?> getParser() {
        return java27p;
    }

    @Test
    void jep530PrimitiveTypesInPatternsInstanceofAndSwitch() {
        doTest("Jep530_PrimitiveTypesInPatternsInstanceofAndSwitch");
    }

    @Test
    void jep530PrimitiveTypesInPatternsInstanceofAndSwitchBeforeJava26Preview() {
        ParseException thrown = assertThrows(ParseException.class, () -> java27.parseResource("Jep530_PrimitiveTypesInPatternsInstanceofAndSwitch.java"));
        assertThat(thrown.getMessage(), containsString("Primitive types in patterns instanceof and switch is a preview feature of JDK 27, you should select your language version accordingly"));
    }
}
