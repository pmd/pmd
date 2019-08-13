/*
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

public class Java13Test {
    private static String loadSource(String name) {
        try {
            return IOUtils.toString(Java13Test.class.getResourceAsStream("jdkversiontests/java13/" + name),
                    StandardCharsets.UTF_8);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    @Test
    public void testSwitchExpressions() {
        ASTCompilationUnit compilationUnit = ParserTstUtil.parseAndTypeResolveJava("13",
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

    @Test(expected = ParseException.class)
    public void testSwitchExpressionsBeforeJava13() {
        ParserTstUtil.parseAndTypeResolveJava("12", loadSource("SwitchExpressions.java"));
    }

}
