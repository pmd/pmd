/**
 * BSD-style license; for more info see http://pmd.sourceforge.net/license.html
 */

package net.sourceforge.pmd.lang.plsql.ast;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;

import java.util.List;

import org.junit.jupiter.api.Test;

import net.sourceforge.pmd.lang.plsql.AbstractPLSQLParserTst;

class ASTCompoundConditionTest extends AbstractPLSQLParserTst {

    @Test
    void testParseType() {
        ASTInput input = plsql.parse("BEGIN SELECT COUNT(1) INTO MY_TABLE FROM USERS_TABLE WHERE user_id = 1 AnD user_id = 2; END;");
        List<ASTCompoundCondition> compoundConditions = input.findDescendantsOfType(ASTCompoundCondition.class);
        assertFalse(compoundConditions.isEmpty());
        assertEquals("AND", compoundConditions.get(0).getType());
    }
}
