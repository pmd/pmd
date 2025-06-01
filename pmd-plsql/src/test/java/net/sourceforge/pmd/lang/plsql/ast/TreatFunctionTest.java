/*
 * BSD-style license; for more info see http://pmd.sourceforge.net/license.html
 */

package net.sourceforge.pmd.lang.plsql.ast;

import net.sourceforge.pmd.lang.plsql.AbstractPLSQLParserTst;
import org.junit.jupiter.api.Test;

class TreatFunctionTest extends AbstractPLSQLParserTst {
    @Test
    void treatFunctionBasic() {
        doTest("TreatFunctionBasic");
    }

    @Test
    void treatFunctionNested() {
        doTest("TreatFunctionNested");
    }
}
