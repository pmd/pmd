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

class Java25PreviewTreeDumpTest extends BaseJavaTreeDumpTest {
    private final JavaParsingHelper java25p =
            JavaParsingHelper.DEFAULT.withDefaultVersion("25-preview")
                    .withResourceContext(Java25PreviewTreeDumpTest.class, "jdkversiontests/java25p/");
    private final JavaParsingHelper java25 = java25p.withDefaultVersion("25");

    @Override
    public BaseParsingHelper<?, ?> getParser() {
        return java25p;
    }

    @Test
    void jep507PrimitiveTypesInPatternsInstanceofAndSwitch() {
        doTest("Jep507_PrimitiveTypesInPatternsInstanceofAndSwitch");
    }

    @Test
    void jep507PrimitiveTypesInPatternsInstanceofAndSwitchBeforeJava25Preview() {
        ParseException thrown = assertThrows(ParseException.class, () -> java25.parseResource("Jep507_PrimitiveTypesInPatternsInstanceofAndSwitch.java"));
        assertThat(thrown.getMessage(), containsString("Primitive types in patterns instanceof and switch is a preview feature of JDK 25, you should select your language version accordingly"));
    }

}
