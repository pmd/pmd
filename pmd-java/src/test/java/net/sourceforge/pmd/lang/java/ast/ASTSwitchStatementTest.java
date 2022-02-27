/*
 * BSD-style license; for more info see http://pmd.sourceforge.net/license.html
 */

package net.sourceforge.pmd.lang.java.ast;

import org.junit.Assert;
import org.junit.Test;

public class ASTSwitchStatementTest extends BaseParserTest {

    @Test
    public void exhaustiveEnumSwitchWithDefault() {
        ASTSwitchStatement switchStatement = getNodes(ASTSwitchStatement.class,
                "import java.nio.file.AccessMode; class Foo { void bar(AccessMode m) {"
                + "switch (m) { case READ: break; default: break; } } }")
                .get(0);
        Assert.assertFalse(switchStatement.isExhaustiveEnumSwitch()); // this should not throw a NPE...
        Assert.assertTrue(switchStatement.hasDefaultCase());
        Assert.assertTrue(switchStatement.isFallthroughSwitch());
    }

    @Test
    public void defaultCaseWithArrowBlock() {
        ASTSwitchStatement switchStatement = java.parse(
                "class Foo { void bar(int x) {"
                + "switch (x) { default -> { } } } }")
                .getFirstDescendantOfType(ASTSwitchStatement.class);
        Assert.assertFalse(switchStatement.isExhaustiveEnumSwitch());
        Assert.assertTrue(switchStatement.iterator().hasNext());
        Assert.assertTrue(switchStatement.hasDefaultCase());
        Assert.assertFalse(switchStatement.isFallthroughSwitch());
    }

    @Test
    public void emptySwitch() {
        ASTSwitchStatement switchStatement = java.parse(
                "class Foo { void bar(int x) {"
                + "switch (x) { } } }")
                .getFirstDescendantOfType(ASTSwitchStatement.class);
        Assert.assertFalse(switchStatement.isExhaustiveEnumSwitch());
        Assert.assertFalse(switchStatement.iterator().hasNext());
        Assert.assertFalse(switchStatement.hasDefaultCase());
        Assert.assertFalse(switchStatement.isFallthroughSwitch());
    }

    @Test
    public void defaultCaseWithArrowExprs() {
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
                .getFirstDescendantOfType(ASTSwitchStatement.class);
        Assert.assertFalse(switchStatement.isExhaustiveEnumSwitch());
        Assert.assertTrue(switchStatement.iterator().hasNext());
        Assert.assertFalse(switchStatement.isFallthroughSwitch());
        Assert.assertTrue(switchStatement.hasDefaultCase());
    }
}
