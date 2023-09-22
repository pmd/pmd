/*
 * BSD-style license; for more info see http://pmd.sourceforge.net/license.html
 */

package net.sourceforge.pmd.lang.plsql.ast;

import org.junit.jupiter.api.Test;

import net.sourceforge.pmd.lang.ast.test.BaseParsingHelper;
import net.sourceforge.pmd.lang.ast.test.BaseTreeDumpTest;
import net.sourceforge.pmd.lang.ast.test.RelevantAttributePrinter;
import net.sourceforge.pmd.lang.plsql.PlsqlParsingHelper;

class PlsqlTreeDumpTest extends BaseTreeDumpTest {

    PlsqlTreeDumpTest() {
        super(new RelevantAttributePrinter(), ".pls");
    }

    @Override
    public BaseParsingHelper<?, ?> getParser() {
        return PlsqlParsingHelper.DEFAULT.withResourceContext(getClass());
    }

    @Test
    void sqlPlusLexicalVariables() {
        doTest("SqlPlusLexicalVariablesIssue195");
    }

    @Test
    void parseParsingExclusion() {
        doTest("ParsingExclusion");
    }

    @Test
    void parseOpenForStatement() {
        doTest("OpenForStatement");
    }

    @Test
    void parseSelectIntoAssociativeArrayType() {
        doTest("SelectIntoArray");
    }
}
