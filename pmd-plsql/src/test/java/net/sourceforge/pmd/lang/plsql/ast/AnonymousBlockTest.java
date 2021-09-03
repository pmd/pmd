/**
 * BSD-style license; for more info see http://pmd.sourceforge.net/license.html
 */

package net.sourceforge.pmd.lang.plsql.ast;

import org.junit.Test;

import net.sourceforge.pmd.lang.plsql.AbstractPLSQLParserTst;

public class AnonymousBlockTest extends AbstractPLSQLParserTst {

    @Test
    public void parseCursorInsideProcAnonymousBlock() {
        plsql.parseResource("AnonymousBlock1.sql");
    }

    @Test
    public void parseCursorInsideAnonymousBlock() {
        plsql.parseResource("AnonymousBlock2.sql");
    }
}
