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
        ApexNode<?> node = apex.parseResource("SwitchStatements.cls").getMainNode();
        List<ASTSwitchStatement> switchStatements = node.descendants(ASTSwitchStatement.class).toList();
        assertEquals(4, switchStatements.size());

        assertTrue(switchStatements.get(0).getChild(0) instanceof ASTVariableExpression);
        assertEquals(5, switchStatements.get(0).children(ASTValueWhenBlock.class).count());
        assertEquals(3, switchStatements.get(0).children(ASTValueWhenBlock.class)
            .get(1).children(ASTLiteralCase.class).count());
        assertEquals(1, switchStatements.get(0).children(ASTElseWhenBlock.class).count());

        assertTrue(switchStatements.get(1).getChild(0) instanceof ASTMethodCallExpression);
        assertEquals(2, switchStatements.get(1).children(ASTValueWhenBlock.class).count());
        assertEquals(1, switchStatements.get(1).children(ASTElseWhenBlock.class).count());

        assertTrue(switchStatements.get(2).getChild(0) instanceof ASTVariableExpression);
        assertEquals(2, switchStatements.get(2).children(ASTTypeWhenBlock.class).count());
        assertEquals("Account", switchStatements.get(2).children(ASTTypeWhenBlock.class)
            .get(0).getType());
        assertEquals("a", switchStatements.get(2).children(ASTTypeWhenBlock.class)
            .get(0).getName());
        assertEquals(1, switchStatements.get(2).children(ASTValueWhenBlock.class).count());
        assertEquals(1, switchStatements.get(2).children(ASTElseWhenBlock.class).count());

        assertTrue(switchStatements.get(3).getChild(0) instanceof ASTVariableExpression);
        assertEquals(2, switchStatements.get(3).children(ASTValueWhenBlock.class).count());
        assertEquals(1, switchStatements.get(3).children(ASTElseWhenBlock.class).count());
    }

}
