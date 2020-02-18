/*
 * BSD-style license; for more info see http://pmd.sourceforge.net/license.html
 */

package net.sourceforge.pmd.lang.java.ast;

import java.util.List;

import org.junit.Assert;
import org.junit.Test;

import net.sourceforge.pmd.lang.ast.ParseException;
import net.sourceforge.pmd.lang.java.JavaParsingHelper;

public class Java13Test {


    private final JavaParsingHelper java12 =
        JavaParsingHelper.WITH_PROCESSING.withDefaultVersion("12")
                                         .withResourceContext(Java13Test.class, "jdkversiontests/java13/");

    private final JavaParsingHelper java13p = java12.withDefaultVersion("13-preview");


    @Test
    public void testSwitchExpressions() {
        ASTCompilationUnit compilationUnit = java13p.parseResource("SwitchExpressions.java");

        ASTSwitchExpression switchExpression = compilationUnit.getFirstDescendantOfType(ASTSwitchExpression.class);
        Assert.assertEquals(4, switchExpression.getNumChildren());
        Assert.assertTrue(switchExpression.getChild(0) instanceof ASTExpression);
        Assert.assertEquals(3, switchExpression.findChildrenOfType(ASTSwitchLabeledRule.class).size());
        Assert.assertEquals(1, switchExpression.findChildrenOfType(ASTSwitchLabeledBlock.class).size());
        Assert.assertEquals(1, switchExpression.findDescendantsOfType(ASTYieldStatement.class).size());
        ASTYieldStatement yieldStatement = switchExpression.getFirstDescendantOfType(ASTYieldStatement.class);
        Assert.assertEquals(Integer.TYPE, yieldStatement.getType());
    }

    @Test
    public void testSwitchExpressionsYield() {
        ASTCompilationUnit compilationUnit = java13p.parseResource("SwitchExpressionsYield.java");

        ASTSwitchExpression switchExpression = compilationUnit.getFirstDescendantOfType(ASTSwitchExpression.class);
        Assert.assertEquals(11, switchExpression.getNumChildren());
        Assert.assertTrue(switchExpression.getChild(0) instanceof ASTExpression);
        Assert.assertEquals(5, switchExpression.findChildrenOfType(ASTSwitchLabel.class).size());

        ASTYieldStatement yieldStatement = switchExpression.getFirstDescendantOfType(ASTYieldStatement.class);
        Assert.assertEquals("SwitchExpressionsBreak.SIX", yieldStatement.getImage());
        Assert.assertTrue(yieldStatement.getChild(0) instanceof ASTExpression);

        ASTLocalVariableDeclaration localVar = compilationUnit.findDescendantsOfType(ASTLocalVariableDeclaration.class)
                .get(1);
        ASTVariableDeclarator localVarDecl = localVar.getFirstChildOfType(ASTVariableDeclarator.class);
        Assert.assertEquals(Integer.TYPE, localVarDecl.getType());
        Assert.assertEquals(Integer.TYPE, switchExpression.getType());
    }


    @Test(expected = ParseException.class)
    public void testSwitchExpressionsBeforeJava13() {
        java12.parseResource("SwitchExpressions.java");
    }

    @Test
    public void testTextBlocks() {
        ASTCompilationUnit compilationUnit = java13p.parseResource("TextBlocks.java");
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
        java12.parseResource("TextBlocks.java");
    }

}
