/**
 * BSD-style license; for more info see http://pmd.sourceforge.net/license.html
 */

package net.sourceforge.pmd.lang.plsql.ast;

import org.junit.Test;

import net.sourceforge.pmd.lang.plsql.AbstractPLSQLParserTst;

public class ExecuteImmediateTest extends AbstractPLSQLParserTst {

    @Test
    public void parseExecuteImmediate1047a() {
        plsql.parseResource("ExecuteImmediate1047a.pls");
    }

    @Test
    public void parseExecuteImmediate1047b() {
        plsql.parseResource("ExecuteImmediate1047b.pls");
    }

    @Test
    public void parseExecuteImmediateString() {
        plsql.parseResource("ExecuteImmediateString.pls");
    }
}
