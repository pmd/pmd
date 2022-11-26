/**
 * BSD-style license; for more info see http://pmd.sourceforge.net/license.html
 */

package net.sourceforge.pmd.lang.plsql.ast;

import org.junit.jupiter.api.Test;

import net.sourceforge.pmd.lang.plsql.AbstractPLSQLParserTst;

class ExecuteImmediateTest extends AbstractPLSQLParserTst {

    @Test
    void parseExecuteImmediate1047a() {
        plsql.parseResource("ExecuteImmediate1047a.pls");
    }

    @Test
    void parseExecuteImmediate1047b() {
        plsql.parseResource("ExecuteImmediate1047b.pls");
    }

    @Test
    void parseExecuteImmediateString() {
        plsql.parseResource("ExecuteImmediateString.pls");
    }
}
