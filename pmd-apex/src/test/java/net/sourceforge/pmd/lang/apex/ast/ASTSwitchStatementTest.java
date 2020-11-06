/*
 * BSD-style license; for more info see http://pmd.sourceforge.net/license.html
 */

package net.sourceforge.pmd.lang.apex.ast;

import java.util.List;

import org.junit.Assert;
import org.junit.Test;

import apex.jorje.semantic.ast.compilation.Compilation;

public class ASTSwitchStatementTest extends ApexParserTestBase {

    @Test
    public void testExamples() {
        ApexNode<Compilation> node = parseResource("SwitchStatements.cls");
        List<ASTSwitchStatement> switchStatements = node.findDescendantsOfType(ASTSwitchStatement.class);
        Assert.assertEquals(4, switchStatements.size());

        Assert.assertTrue(switchStatements.get(0).getChild(0) instanceof ASTVariableExpression);
        Assert.assertEquals(5, switchStatements.get(0).findChildrenOfType(ASTValueWhenBlock.class).size());
        Assert.assertEquals(3, switchStatements.get(0).findChildrenOfType(ASTValueWhenBlock.class)
                .get(1).findChildrenOfType(ASTLiteralCase.class).size());
        Assert.assertEquals(1, switchStatements.get(0).findChildrenOfType(ASTElseWhenBlock.class).size());

        Assert.assertTrue(switchStatements.get(1).getChild(0) instanceof ASTMethodCallExpression);
        Assert.assertEquals(2, switchStatements.get(1).findChildrenOfType(ASTValueWhenBlock.class).size());
        Assert.assertEquals(1, switchStatements.get(1).findChildrenOfType(ASTElseWhenBlock.class).size());

        Assert.assertTrue(switchStatements.get(2).getChild(0) instanceof ASTVariableExpression);
        Assert.assertEquals(2, switchStatements.get(2).findChildrenOfType(ASTTypeWhenBlock.class).size());
        Assert.assertEquals("Account", switchStatements.get(2).findChildrenOfType(ASTTypeWhenBlock.class)
                .get(0).getType());
        Assert.assertEquals("a", switchStatements.get(2).findChildrenOfType(ASTTypeWhenBlock.class)
                .get(0).getName());
        Assert.assertEquals(1, switchStatements.get(2).findChildrenOfType(ASTValueWhenBlock.class).size());
        Assert.assertEquals(1, switchStatements.get(2).findChildrenOfType(ASTElseWhenBlock.class).size());

        Assert.assertTrue(switchStatements.get(3).getChild(0) instanceof ASTVariableExpression);
        Assert.assertEquals(2, switchStatements.get(3).findChildrenOfType(ASTValueWhenBlock.class).size());
        Assert.assertEquals(1, switchStatements.get(3).findChildrenOfType(ASTElseWhenBlock.class).size());
    }

}
