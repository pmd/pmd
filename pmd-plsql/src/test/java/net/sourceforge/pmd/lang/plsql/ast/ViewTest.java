/**
 * BSD-style license; for more info see http://pmd.sourceforge.net/license.html
 */

package net.sourceforge.pmd.lang.plsql.ast;

import org.junit.Test;

import net.sourceforge.pmd.lang.plsql.AbstractPLSQLParserTst;

public class ViewTest extends AbstractPLSQLParserTst {

    @Test
    public void parseCreateViewIssue981() {
        plsql.parseResource("ViewIssue981.pls");
    }

    @Test
    public void parseCreateView() {
        plsql.parseResource("CreateViewWithSubquery.pls");
    }

    @Test
    public void parseCreateViewWithoutSemicolon() {
        plsql.parseResource("QueryWithoutSemicolon.sql");
    }
}
