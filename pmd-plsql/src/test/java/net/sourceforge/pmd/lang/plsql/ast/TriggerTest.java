/*
 * BSD-style license; for more info see http://pmd.sourceforge.net/license.html
 */


package net.sourceforge.pmd.lang.plsql.ast;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;

import org.junit.jupiter.api.Test;

import net.sourceforge.pmd.lang.plsql.AbstractPLSQLParserTst;


class TriggerTest extends AbstractPLSQLParserTst {

    /**
     * Parsing a trigger should not result in a NPE.
     *
     * @see <a href="https://github.com/pmd/pmd/issues/2325">#2325 [plsql] NullPointerException while running parsing test for CREATE TRIGGER</a>
     */
    @Test
    void parseCreateTrigger() {
        ASTInput input = plsql.parseResource("TriggerUnit.pls");
        PLSQLNode trigger = input.getChild(0);
        assertEquals(ASTTriggerUnit.class, trigger.getClass());
        assertNotNull(trigger.getScope());
    }
}
