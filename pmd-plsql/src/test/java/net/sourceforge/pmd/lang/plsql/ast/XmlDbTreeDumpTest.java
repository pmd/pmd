/*
 * BSD-style license; for more info see http://pmd.sourceforge.net/license.html
 */

package net.sourceforge.pmd.lang.plsql.ast;

import org.junit.jupiter.api.Test;

import net.sourceforge.pmd.lang.plsql.PlsqlParsingHelper;
import net.sourceforge.pmd.lang.test.ast.BaseParsingHelper;
import net.sourceforge.pmd.lang.test.ast.BaseTreeDumpTest;
import net.sourceforge.pmd.lang.test.ast.RelevantAttributePrinter;

class XmlDbTreeDumpTest extends BaseTreeDumpTest {
    XmlDbTreeDumpTest() {
        super(new RelevantAttributePrinter(), ".pls");
    }

    @Override
    public BaseParsingHelper<?, ?> getParser() {
        return PlsqlParsingHelper.DEFAULT.withResourceContext(getClass());
    }

    @Test
    void xmlElement() {
        doTest("XMLElement");
    }

    @Test
    void xmlTable() {
        doTest("XMLTable");
    }

    @Test
    void xmlFunctions() {
        doTest("XMLFunctions");
    }

    /**
     * @see <a href="https://github.com/pmd/pmd/issues/4441">[plsql] Parsing exception with XMLTYPE and XMLQUERY function in SELECT</a>
     */
    @Test
    void xmlQuery() {
        doTest("XMLQuery");
    }

    @Test
    void xmlType() {
        doTest("XMLType");
    }
}
