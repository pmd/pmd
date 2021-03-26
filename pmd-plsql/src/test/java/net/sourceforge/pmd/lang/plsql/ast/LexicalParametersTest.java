/**
 * BSD-style license; for more info see http://pmd.sourceforge.net/license.html
 */

package net.sourceforge.pmd.lang.plsql.ast;

import org.junit.Test;

import net.sourceforge.pmd.lang.plsql.AbstractPLSQLParserTst;

public class LexicalParametersTest extends AbstractPLSQLParserTst {

    @Test
    public void parseLexicalParameters() {
        plsql.parseResource("LexicalParameters.sql");
    }
}
