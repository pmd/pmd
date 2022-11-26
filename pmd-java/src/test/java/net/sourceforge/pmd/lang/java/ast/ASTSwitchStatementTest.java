/*
 * BSD-style license; for more info see http://pmd.sourceforge.net/license.html
 */

package net.sourceforge.pmd.lang.java.ast;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

import org.junit.jupiter.api.Test;

import net.sourceforge.pmd.lang.java.BaseParserTest;

class ASTSwitchStatementTest extends BaseParserTest {

    @Test
    void exhaustiveEnumSwitchWithDefault() {
        ASTSwitchStatement switchStatement = java.parse(
                "import java.nio.file.AccessMode; class Foo { void bar(AccessMode m) {"
                + "switch (m) { case READ: break; default: break; } } }")
                .descendants(ASTSwitchStatement.class).firstOrThrow();
        assertFalse(switchStatement.isExhaustiveEnumSwitch()); // this should not throw a NPE...
        assertTrue(switchStatement.hasDefaultCase());
        assertTrue(switchStatement.isFallthroughSwitch());
    }

    @Test
    void defaultCaseWithArrowBlock() {
        ASTSwitchStatement switchStatement =
            java.parse("class Foo { void bar(int x) {switch (x) { default -> { } } } }")
                .descendants(ASTSwitchStatement.class).firstOrThrow();
        assertFalse(switchStatement.isExhaustiveEnumSwitch());
        assertTrue(switchStatement.iterator().hasNext());
        assertTrue(switchStatement.hasDefaultCase());
        assertFalse(switchStatement.isFallthroughSwitch());
    }

    @Test
    void emptySwitch() {
        ASTSwitchStatement switchStatement =
            java.parse("class Foo { void bar(int x) {switch (x) { } } }")
                .descendants(ASTSwitchStatement.class).firstOrThrow();
        assertFalse(switchStatement.isExhaustiveEnumSwitch());
        assertFalse(switchStatement.iterator().hasNext());
        assertFalse(switchStatement.hasDefaultCase());
        assertFalse(switchStatement.isFallthroughSwitch());
    }

    @Test
    void defaultCaseWithArrowExprs() {
        ASTSwitchStatement switchStatement =
            java.parse(
                    "import net.sourceforge.pmd.lang.java.rule.bestpractices.switchstmtsshouldhavedefault.SimpleEnum;\n"
                        + "\n"
                        + "            public class Foo {\n"
                        + "                void bar(SimpleEnum x) {\n"
                        + "                    switch (x) {\n"
                        + "                    case FOO -> System.out.println(\"it is on\");\n"
                        + "                    case BAR -> System.out.println(\"it is off\");\n"
                        + "                    default -> System.out.println(\"it is neither on nor off - should not happen? maybe null?\");\n"
                        + "                    }\n"
                        + "                }\n"
                        + "            }")
                .descendants(ASTSwitchStatement.class).firstOrThrow();
        assertFalse(switchStatement.isExhaustiveEnumSwitch());
        assertTrue(switchStatement.iterator().hasNext());
        assertFalse(switchStatement.isFallthroughSwitch());
        assertTrue(switchStatement.hasDefaultCase());
    }
}
