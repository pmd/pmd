/*
 * BSD-style license; for more info see http://pmd.sourceforge.net/license.html
 */

package net.sourceforge.pmd.lang.apex.ast;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.util.List;

import org.junit.jupiter.api.Test;

class ASTSwitchStatementTest extends ApexParserTestBase {

    @Test
    void testExamples() {
        ApexNode<?> node = parseResource("SwitchStatements.cls");
        List<ASTSwitchStatement> switchStatements = node.findDescendantsOfType(ASTSwitchStatement.class);
        assertEquals(4, switchStatements.size());

        assertTrue(switchStatements.get(0).getChild(0) instanceof ASTVariableExpression);
        assertEquals(5, switchStatements.get(0).findChildrenOfType(ASTValueWhenBlock.class).size());
        assertEquals(3, switchStatements.get(0).findChildrenOfType(ASTValueWhenBlock.class)
            .get(1).findChildrenOfType(ASTLiteralCase.class).size());
        assertEquals(1, switchStatements.get(0).findChildrenOfType(ASTElseWhenBlock.class).size());

        assertTrue(switchStatements.get(1).getChild(0) instanceof ASTMethodCallExpression);
        assertEquals(2, switchStatements.get(1).findChildrenOfType(ASTValueWhenBlock.class).size());
        assertEquals(1, switchStatements.get(1).findChildrenOfType(ASTElseWhenBlock.class).size());

        assertTrue(switchStatements.get(2).getChild(0) instanceof ASTVariableExpression);
        assertEquals(2, switchStatements.get(2).findChildrenOfType(ASTTypeWhenBlock.class).size());
        assertEquals("Account", switchStatements.get(2).findChildrenOfType(ASTTypeWhenBlock.class)
            .get(0).getType());
        assertEquals("a", switchStatements.get(2).findChildrenOfType(ASTTypeWhenBlock.class)
            .get(0).getName());
        assertEquals(1, switchStatements.get(2).findChildrenOfType(ASTValueWhenBlock.class).size());
        assertEquals(1, switchStatements.get(2).findChildrenOfType(ASTElseWhenBlock.class).size());

        assertTrue(switchStatements.get(3).getChild(0) instanceof ASTVariableExpression);
        assertEquals(2, switchStatements.get(3).findChildrenOfType(ASTValueWhenBlock.class).size());
        assertEquals(1, switchStatements.get(3).findChildrenOfType(ASTElseWhenBlock.class).size());
    }

}
