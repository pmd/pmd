/*
 * BSD-style license; for more info see http://pmd.sourceforge.net/license.html
 */


package net.sourceforge.pmd.lang.plsql.ast;

import org.junit.Assert;
import org.junit.Test;

import net.sourceforge.pmd.lang.plsql.AbstractPLSQLParserTst;


public class TriggerTest extends AbstractPLSQLParserTst {

    /**
     * Parsing a trigger should not result in a NPE.
     *
     * @see <a href="https://github.com/pmd/pmd/issues/2325">#2325 [plsql] NullPointerException while running parsing test for CREATE TRIGGER</a>
     */
    @Test
    public void parseCreateTrigger() {
        ASTInput input = plsql.parseResource("TriggerUnit.pls");
        PLSQLNode trigger = input.getChild(0);
        Assert.assertEquals(ASTTriggerUnit.class, trigger.getClass());
        Assert.assertNotNull(trigger.getScope());
    }
}
