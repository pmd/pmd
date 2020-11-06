/**
 * BSD-style license; for more info see http://pmd.sourceforge.net/license.html
 */

package net.sourceforge.pmd.lang.plsql.ast;

import java.util.List;

import org.junit.Assert;
import org.junit.Test;

import net.sourceforge.pmd.lang.plsql.AbstractPLSQLParserTst;


public class ASTCompoundConditionTest extends AbstractPLSQLParserTst {

    @Test
    public void testParseType() {
        ASTInput input = plsql.parse("BEGIN SELECT COUNT(1) INTO MY_TABLE FROM USERS_TABLE WHERE user_id = 1 AnD user_id = 2; END;");
        List<ASTCompoundCondition> compoundConditions = input.findDescendantsOfType(ASTCompoundCondition.class);
        Assert.assertFalse(compoundConditions.isEmpty());
        Assert.assertEquals("AND", compoundConditions.get(0).getType());
    }
}
