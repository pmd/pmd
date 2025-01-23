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

class Java24PreviewTreeDumpTest extends BaseJavaTreeDumpTest {
    private final JavaParsingHelper java24p =
            JavaParsingHelper.DEFAULT.withDefaultVersion("24-preview")
                    .withResourceContext(Java24PreviewTreeDumpTest.class, "jdkversiontests/java24p/");
    private final JavaParsingHelper java24 = java24p.withDefaultVersion("24");

    @Override
    public BaseParsingHelper<?, ?> getParser() {
        return java24p;
    }

    @Test
    void jep488PrimitiveTypesInPatternsInstanceofAndSwitch() {
        doTest("Jep488_PrimitiveTypesInPatternsInstanceofAndSwitch");
    }

    @Test
    void jep488PrimitiveTypesInPatternsInstanceofAndSwitchBeforeJava24Preview() {
        ParseException thrown = assertThrows(ParseException.class, () -> java24.parseResource("Jep488_PrimitiveTypesInPatternsInstanceofAndSwitch.java"));
        assertThat(thrown.getMessage(), containsString("Primitive types in patterns instanceof and switch is a preview feature of JDK 24, you should select your language version accordingly"));
    }

    @Test
    void jep492FlexibleConstructorBodies() {
        doTest("Jep492_FlexibleConstructorBodies");
    }

    @Test
    void jep492FlexibleConstructorBodiesBeforeJava24Preview() {
        ParseException thrown = assertThrows(ParseException.class, () -> java24.parseResource("Jep492_FlexibleConstructorBodies.java"));
        assertThat(thrown.getMessage(), containsString("Flexible constructor bodies is a preview feature of JDK 24, you should select your language version accordingly"));
    }

    @Test
    void jep494ModuleImportDeclarations() {
        doTest("Jep494_ModuleImportDeclarations");
    }

    @Test
    void jep494ModuleImportDeclarationsBeforeJava24Preview() {
        ParseException thrown = assertThrows(ParseException.class, () -> java24.parseResource("Jep494_ModuleImportDeclarations.java"));
        assertThat(thrown.getMessage(), containsString("Module import declarations is a preview feature of JDK 24, you should select your language version accordingly"));
    }
}
