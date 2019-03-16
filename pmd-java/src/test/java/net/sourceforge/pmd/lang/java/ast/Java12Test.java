/**
 * BSD-style license; for more info see http://pmd.sourceforge.net/license.html
 */

package net.sourceforge.pmd.lang.java.ast;

import java.io.IOException;
import java.nio.charset.StandardCharsets;

import org.apache.commons.io.IOUtils;
import org.junit.Assert;
import org.junit.Test;

import net.sourceforge.pmd.lang.ast.ParseException;
import net.sourceforge.pmd.lang.java.ParserTstUtil;

public class Java12Test {
    private static String loadSource(String name) {
        try {
            return IOUtils.toString(Java10Test.class.getResourceAsStream("jdkversiontests/java12/" + name),
                    StandardCharsets.UTF_8);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    @Test(expected = ParseException.class)
    public void testMultipleCaseLabelsJava11() {
        ParserTstUtil.parseAndTypeResolveJava("11", loadSource("MultipleCaseLabels.java"));
    }

    @Test
    public void testMultipleCaseLabels() {
        ASTCompilationUnit compilationUnit = ParserTstUtil.parseAndTypeResolveJava("12",
                loadSource("MultipleCaseLabels.java"));
        Assert.assertNotNull(compilationUnit);
        ASTSwitchStatement switchStatement = compilationUnit.getFirstDescendantOfType(ASTSwitchStatement.class);
        Assert.assertTrue(switchStatement.jjtGetChild(0) instanceof ASTExpression);
        Assert.assertTrue(switchStatement.jjtGetChild(1) instanceof ASTSwitchLabel);
        ASTSwitchLabel switchLabel = switchStatement.getFirstChildOfType(ASTSwitchLabel.class);
        Assert.assertEquals(3, switchLabel.findChildrenOfType(ASTExpression.class).size());
    }

    @Test(expected = ParseException.class)
    public void testSwitchRulesJava11() {
        ParserTstUtil.parseAndTypeResolveJava("11", loadSource("SwitchRules.java"));
    }

    @Test
    public void testSwitchRules() {
        ASTCompilationUnit compilationUnit = ParserTstUtil.parseAndTypeResolveJava("12",
                loadSource("SwitchRules.java"));
        Assert.assertNotNull(compilationUnit);
        ASTSwitchStatement switchStatement = compilationUnit.getFirstDescendantOfType(ASTSwitchStatement.class);
        Assert.assertTrue(switchStatement.jjtGetChild(0) instanceof ASTExpression);
        Assert.assertTrue(switchStatement.jjtGetChild(1) instanceof ASTSwitchLabeledRule);
        ASTSwitchLabeledRule switchLabeledRule = (ASTSwitchLabeledRule) switchStatement.jjtGetChild(1);
        Assert.assertEquals(2, switchLabeledRule.jjtGetNumChildren());
        Assert.assertTrue(switchLabeledRule.jjtGetChild(0) instanceof ASTSwitchLabel);
        Assert.assertTrue(switchLabeledRule.jjtGetChild(1) instanceof ASTExpression);

        switchLabeledRule = (ASTSwitchLabeledRule) switchStatement.jjtGetChild(4);
        Assert.assertEquals(2, switchLabeledRule.jjtGetNumChildren());
        Assert.assertTrue(switchLabeledRule.jjtGetChild(0) instanceof ASTSwitchLabel);
        Assert.assertTrue(switchLabeledRule.jjtGetChild(1) instanceof ASTBlock);

        switchLabeledRule = (ASTSwitchLabeledRule) switchStatement.jjtGetChild(5);
        Assert.assertEquals(2, switchLabeledRule.jjtGetNumChildren());
        Assert.assertTrue(switchLabeledRule.jjtGetChild(0) instanceof ASTSwitchLabel);
        Assert.assertTrue(switchLabeledRule.jjtGetChild(1) instanceof ASTThrowStatement);
    }

    @Test(expected = ParseException.class)
    public void testSwitchExpressionsJava11() {
        ParserTstUtil.parseAndTypeResolveJava("11", loadSource("SwitchExpressions.java"));
    }

    @Test
    public void testSwitchExpressions() {
        ASTCompilationUnit compilationUnit = ParserTstUtil.parseAndTypeResolveJava("12",
                loadSource("SwitchExpressions.java"));
        Assert.assertNotNull(compilationUnit);

        ASTSwitchExpression switchExpression = compilationUnit.getFirstDescendantOfType(ASTSwitchExpression.class);
        Assert.assertEquals(6, switchExpression.jjtGetNumChildren());
        Assert.assertTrue(switchExpression.jjtGetChild(0) instanceof ASTExpression);
        Assert.assertEquals(5, switchExpression.findChildrenOfType(ASTSwitchLabeledRule.class).size());

        Assert.assertEquals(Integer.TYPE, switchExpression.getType());
    }

}
