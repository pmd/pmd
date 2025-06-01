/**
 * BSD-style license; for more info see http://pmd.sourceforge.net/license.html
 */
package net.sourceforge.pmd.lang.plsql.ast;

import net.sourceforge.pmd.lang.plsql.AbstractPLSQLParserTst;
import org.junit.jupiter.api.Test;

class ViewTest extends AbstractPLSQLParserTst {

    @Test
    void parseCreateViewIssue981() {
        plsql.parseResource("ViewIssue981.pls");
    }

    @Test
    void parseCreateView() {
        plsql.parseResource("CreateViewWithSubquery.pls");
    }

    @Test
    void parseCreateViewWithoutSemicolon() {
        plsql.parseResource("QueryWithoutSemicolon.sql");
    }
}
