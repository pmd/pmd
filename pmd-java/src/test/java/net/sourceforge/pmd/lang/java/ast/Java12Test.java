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
            return IOUtils.toString(Java12Test.class.getResourceAsStream("jdkversiontests/java12/" + name),
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
        ASTCompilationUnit compilationUnit = ParserTstUtil.parseAndTypeResolveJava("12-preview",
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
        ASTCompilationUnit compilationUnit = ParserTstUtil.parseAndTypeResolveJava("12-preview",
                loadSource("SwitchRules.java"));
        Assert.assertNotNull(compilationUnit);
        ASTSwitchStatement switchStatement = compilationUnit.getFirstDescendantOfType(ASTSwitchStatement.class);
        Assert.assertTrue(switchStatement.jjtGetChild(0) instanceof ASTExpression);
        Assert.assertTrue(switchStatement.jjtGetChild(1) instanceof ASTSwitchLabeledExpression);
        ASTSwitchLabeledExpression switchLabeledExpression = (ASTSwitchLabeledExpression) switchStatement.jjtGetChild(1);
        Assert.assertEquals(2, switchLabeledExpression.jjtGetNumChildren());
        Assert.assertTrue(switchLabeledExpression.jjtGetChild(0) instanceof ASTSwitchLabel);
        Assert.assertTrue(switchLabeledExpression.jjtGetChild(1) instanceof ASTExpression);

        ASTSwitchLabeledBlock switchLabeledBlock = (ASTSwitchLabeledBlock) switchStatement.jjtGetChild(4);
        Assert.assertEquals(2, switchLabeledBlock.jjtGetNumChildren());
        Assert.assertTrue(switchLabeledBlock.jjtGetChild(0) instanceof ASTSwitchLabel);
        Assert.assertTrue(switchLabeledBlock.jjtGetChild(1) instanceof ASTBlock);

        ASTSwitchLabeledThrowStatement switchLabeledThrowStatement = (ASTSwitchLabeledThrowStatement) switchStatement.jjtGetChild(5);
        Assert.assertEquals(2, switchLabeledThrowStatement.jjtGetNumChildren());
        Assert.assertTrue(switchLabeledThrowStatement.jjtGetChild(0) instanceof ASTSwitchLabel);
        Assert.assertTrue(switchLabeledThrowStatement.jjtGetChild(1) instanceof ASTThrowStatement);
    }

    @Test(expected = ParseException.class)
    public void testSwitchExpressionsJava11() {
        ParserTstUtil.parseAndTypeResolveJava("11", loadSource("SwitchExpressions.java"));
    }

    @Test
    public void testSwitchExpressions() {
        ASTCompilationUnit compilationUnit = ParserTstUtil.parseAndTypeResolveJava("12-preview",
                loadSource("SwitchExpressions.java"));
        Assert.assertNotNull(compilationUnit);

        ASTSwitchExpression switchExpression = compilationUnit.getFirstDescendantOfType(ASTSwitchExpression.class);
        Assert.assertEquals(6, switchExpression.jjtGetNumChildren());
        Assert.assertTrue(switchExpression.jjtGetChild(0) instanceof ASTExpression);
        Assert.assertEquals(5, switchExpression.findChildrenOfType(ASTSwitchLabeledRule.class).size());

        ASTLocalVariableDeclaration localVar = compilationUnit.findDescendantsOfType(ASTLocalVariableDeclaration.class).get(1);
        ASTVariableDeclarator localVarDecl = localVar.getFirstChildOfType(ASTVariableDeclarator.class);
        Assert.assertEquals(Integer.TYPE, localVarDecl.getType());
        Assert.assertEquals(Integer.TYPE, switchExpression.getType());
    }

    @Test
    public void testSwitchExpressionsBreak() {
        ASTCompilationUnit compilationUnit = ParserTstUtil.parseAndTypeResolveJava("12-preview",
                loadSource("SwitchExpressionsBreak.java"));
        Assert.assertNotNull(compilationUnit);

        ASTSwitchExpression switchExpression = compilationUnit.getFirstDescendantOfType(ASTSwitchExpression.class);
        Assert.assertEquals(11, switchExpression.jjtGetNumChildren());
        Assert.assertTrue(switchExpression.jjtGetChild(0) instanceof ASTExpression);
        Assert.assertEquals(5, switchExpression.findChildrenOfType(ASTSwitchLabel.class).size());

        ASTBreakStatement breakStatement = switchExpression.getFirstDescendantOfType(ASTBreakStatement.class);
        Assert.assertEquals("SwitchExpressionsBreak.SIX", breakStatement.getImage());
        Assert.assertTrue(breakStatement.jjtGetChild(0) instanceof ASTExpression);

        ASTLocalVariableDeclaration localVar = compilationUnit.findDescendantsOfType(ASTLocalVariableDeclaration.class).get(1);
        ASTVariableDeclarator localVarDecl = localVar.getFirstChildOfType(ASTVariableDeclarator.class);
        Assert.assertEquals(Integer.TYPE, localVarDecl.getType());
        Assert.assertEquals(Integer.TYPE, switchExpression.getType());
    }

}
