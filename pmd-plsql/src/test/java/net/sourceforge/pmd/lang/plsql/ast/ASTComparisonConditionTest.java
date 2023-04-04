/**
 * BSD-style license; for more info see http://pmd.sourceforge.net/license.html
 */

package net.sourceforge.pmd.lang.plsql.ast;

import static org.junit.jupiter.api.Assertions.assertEquals;

import java.util.List;

import org.junit.jupiter.api.Test;

import net.sourceforge.pmd.lang.plsql.AbstractPLSQLParserTst;

class ASTComparisonConditionTest extends AbstractPLSQLParserTst {

    @Test
    void testOperator() {
        ASTInput input = plsql.parse("BEGIN SELECT COUNT(1) INTO MY_TABLE FROM USERS_TABLE WHERE user_id = 1; END;");
        List<ASTComparisonCondition> conditions = input.findDescendantsOfType(ASTComparisonCondition.class);
        assertEquals(1, conditions.size());
        assertEquals("=", conditions.get(0).getOperator());
    }
}
