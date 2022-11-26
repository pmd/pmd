/**
 * BSD-style license; for more info see http://pmd.sourceforge.net/license.html
 */

package net.sourceforge.pmd.lang.plsql.ast;

import org.junit.jupiter.api.Test;

import net.sourceforge.pmd.lang.plsql.AbstractPLSQLParserTst;

class AnonymousBlockTest extends AbstractPLSQLParserTst {

    @Test
    void parseCursorInsideProcAnonymousBlock() {
        plsql.parseResource("AnonymousBlock1.sql");
    }

    @Test
    void parseCursorInsideAnonymousBlock() {
        plsql.parseResource("AnonymousBlock2.sql");
    }
}
