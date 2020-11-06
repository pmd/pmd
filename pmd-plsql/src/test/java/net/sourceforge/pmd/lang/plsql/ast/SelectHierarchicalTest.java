/**
 * BSD-style license; for more info see http://pmd.sourceforge.net/license.html
 */

package net.sourceforge.pmd.lang.plsql.ast;

import org.junit.Test;

import net.sourceforge.pmd.lang.plsql.AbstractPLSQLParserTst;

public class SelectHierarchicalTest extends AbstractPLSQLParserTst {

    @Test
    public void parseSelectHierarchicalQueries() {
        plsql.parseResource("SelectHierarchical.pls");
    }
}
