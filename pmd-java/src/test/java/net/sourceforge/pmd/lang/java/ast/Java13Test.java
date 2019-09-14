/*
 * BSD-style license; for more info see http://pmd.sourceforge.net/license.html
 */

package net.sourceforge.pmd.lang.java.ast;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.util.List;

import org.apache.commons.io.IOUtils;
import org.junit.Assert;
import org.junit.Test;

import net.sourceforge.pmd.lang.ast.ParseException;
import net.sourceforge.pmd.lang.java.ParserTstUtil;

public class Java13Test {
    private static String loadSource(String name) {
        try {
            return IOUtils.toString(Java13Test.class.getResourceAsStream("jdkversiontests/java13/" + name),
                                    StandardCharsets.UTF_8)
                .replaceAll("\\R", "\n"); // normalize line separators to \n
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    @Test
    public void testSwitchExpressions() {
        ASTCompilationUnit compilationUnit = ParserTstUtil.parseAndTypeResolveJava("13-preview",
                loadSource("SwitchExpressions.java"));
        Assert.assertNotNull(compilationUnit);

        ASTSwitchExpression switchExpression = compilationUnit.getFirstDescendantOfType(ASTSwitchExpression.class);
        Assert.assertEquals(4, switchExpression.jjtGetNumChildren());
        Assert.assertTrue(switchExpression.jjtGetChild(0) instanceof ASTExpression);
        Assert.assertEquals(3, switchExpression.findChildrenOfType(ASTSwitchLabeledRule.class).size());
        Assert.assertEquals(1, switchExpression.findChildrenOfType(ASTSwitchLabeledBlock.class).size());
        Assert.assertEquals(1, switchExpression.findDescendantsOfType(ASTYieldStatement.class).size());
        ASTYieldStatement yieldStatement = switchExpression.getFirstDescendantOfType(ASTYieldStatement.class);
        Assert.assertEquals(Integer.TYPE, yieldStatement.getType());
    }

    @Test
    public void testSwitchExpressionsYield() {
        ASTCompilationUnit compilationUnit = ParserTstUtil.parseAndTypeResolveJava("13-preview",
                loadSource("SwitchExpressionsYield.java"));
        Assert.assertNotNull(compilationUnit);

        ASTSwitchExpression switchExpression = compilationUnit.getFirstDescendantOfType(ASTSwitchExpression.class);
        Assert.assertEquals(11, switchExpression.jjtGetNumChildren());
        Assert.assertTrue(switchExpression.jjtGetChild(0) instanceof ASTExpression);
        Assert.assertEquals(5, switchExpression.findChildrenOfType(ASTSwitchLabel.class).size());

        ASTYieldStatement yieldStatement = switchExpression.getFirstDescendantOfType(ASTYieldStatement.class);
        Assert.assertEquals("SwitchExpressionsBreak.SIX", yieldStatement.getImage());
        Assert.assertTrue(yieldStatement.jjtGetChild(0) instanceof ASTExpression);

        ASTLocalVariableDeclaration localVar = compilationUnit.findDescendantsOfType(ASTLocalVariableDeclaration.class)
                .get(1);
        ASTVariableDeclarator localVarDecl = localVar.getFirstChildOfType(ASTVariableDeclarator.class);
        Assert.assertEquals(Integer.TYPE, localVarDecl.getType());
        Assert.assertEquals(Integer.TYPE, switchExpression.getType());
    }


    @Test(expected = ParseException.class)
    public void testSwitchExpressionsBeforeJava13() {
        ParserTstUtil.parseAndTypeResolveJava("12", loadSource("SwitchExpressions.java"));
    }

    @Test
    public void testTextBlocks() {
        ASTCompilationUnit compilationUnit = ParserTstUtil.parseAndTypeResolveJava("13-preview", loadSource("TextBlocks.java"));
        Assert.assertNotNull(compilationUnit);
        List<ASTLiteral> literals = compilationUnit.findDescendantsOfType(ASTLiteral.class);
        Assert.assertEquals(10, literals.size());
        for (int i = 0; i < 8; i++) {
            ASTLiteral literal = literals.get(i);
            Assert.assertTrue(literal.isTextBlock());
        }
        Assert.assertEquals("\"\"\"\n"
                                + "                <html>\n"
                                + "                    <body>\n"
                                + "                        <p>Hello, world</p>\n"
                                + "                    </body>\n"
                                + "                </html>\n"
                                + "                \"\"\"",
                            literals.get(0).getImage());
        Assert.assertFalse(literals.get(8).isTextBlock());
        Assert.assertTrue(literals.get(9).isTextBlock());
    }

    @Test(expected = ParseException.class)
    public void testTextBlocksBeforeJava13() {
        ParserTstUtil.parseAndTypeResolveJava("12", loadSource("TextBlocks.java"));
    }

}
