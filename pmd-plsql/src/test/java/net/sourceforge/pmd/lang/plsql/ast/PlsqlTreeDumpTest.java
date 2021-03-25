/*
 * BSD-style license; for more info see http://pmd.sourceforge.net/license.html
 */

package net.sourceforge.pmd.lang.plsql.ast;

import org.junit.Test;

import net.sourceforge.pmd.lang.ast.test.BaseParsingHelper;
import net.sourceforge.pmd.lang.ast.test.BaseTreeDumpTest;
import net.sourceforge.pmd.lang.ast.test.RelevantAttributePrinter;
import net.sourceforge.pmd.lang.plsql.PlsqlParsingHelper;

public class PlsqlTreeDumpTest extends BaseTreeDumpTest {

    public PlsqlTreeDumpTest() {
        super(new RelevantAttributePrinter(), ".pls");
    }

    @Override
    public BaseParsingHelper<?, ?> getParser() {
        return PlsqlParsingHelper.WITH_PROCESSING.withResourceContext(getClass());
    }

    @Test
    public void sqlPlusLexicalVariables() {
        doTest("SqlPlusLexicalVariablesIssue195");
    }
}
