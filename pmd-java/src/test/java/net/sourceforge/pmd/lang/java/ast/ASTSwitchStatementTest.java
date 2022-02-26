/*
 * BSD-style license; for more info see http://pmd.sourceforge.net/license.html
 */

package net.sourceforge.pmd.lang.java.ast;

import org.junit.Assert;
import org.junit.Test;

import net.sourceforge.pmd.lang.java.symboltable.BaseParserTest;

public class ASTSwitchStatementTest extends BaseParserTest {

    @Test
    public void exhaustiveEnumSwitchWithDefault() {
        ASTSwitchStatement switchStatement = java.parse(
                "import java.nio.file.AccessMode; class Foo { void bar(AccessMode m) {"
                + "switch (m) { case READ: break; default: break; } } }")
                .descendants(ASTSwitchStatement.class).firstOrThrow();
        Assert.assertFalse(switchStatement.isExhaustiveEnumSwitch()); // this should not throw a NPE...
        Assert.assertTrue(switchStatement.hasDefaultCase());
        Assert.assertTrue(switchStatement.isFallthroughSwitch());
    }

    @Test
    public void defaultCaseWithArrowBlock() {
        ASTSwitchStatement switchStatement = getNodes(ASTSwitchStatement.class,
                "class Foo { void bar(int x) {"
                + "switch (x) { default -> { } } } }")
                .get(0);
        Assert.assertFalse(switchStatement.isExhaustiveEnumSwitch());
        Assert.assertTrue(switchStatement.iterator().hasNext());
        Assert.assertTrue(switchStatement.hasDefaultCase());
        Assert.assertFalse(switchStatement.isFallthroughSwitch());
    }

    @Test
    public void emptySwitch() {
        ASTSwitchStatement switchStatement = getNodes(ASTSwitchStatement.class,
                "class Foo { void bar(int x) {"
                + "switch (x) { } } }")
                .get(0);
        Assert.assertFalse(switchStatement.isExhaustiveEnumSwitch());
        Assert.assertFalse(switchStatement.iterator().hasNext());
        Assert.assertFalse(switchStatement.hasDefaultCase());
        Assert.assertFalse(switchStatement.isFallthroughSwitch());
    }

    @Test
    public void defaultCaseWithArrowExprs() {
        ASTSwitchStatement switchStatement =
            getNodes(ASTSwitchStatement.class,
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
                .get(0);
        Assert.assertFalse(switchStatement.isExhaustiveEnumSwitch());
        Assert.assertTrue(switchStatement.iterator().hasNext());
        Assert.assertFalse(switchStatement.isFallthroughSwitch());
        Assert.assertTrue(switchStatement.hasDefaultCase());
    }
}
