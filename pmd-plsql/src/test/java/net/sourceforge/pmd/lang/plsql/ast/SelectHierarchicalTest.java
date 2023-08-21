/**
 * BSD-style license; for more info see http://pmd.sourceforge.net/license.html
 */

package net.sourceforge.pmd.lang.plsql.ast;

import org.junit.jupiter.api.Test;

import net.sourceforge.pmd.lang.plsql.AbstractPLSQLParserTst;

class SelectHierarchicalTest extends AbstractPLSQLParserTst {

    @Test
    void parseSelectHierarchicalQueries() {
        plsql.parseResource("SelectHierarchical.pls");
    }
}
