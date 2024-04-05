/*
 * BSD-style license; for more info see http://pmd.sourceforge.net/license.html
 */

package net.sourceforge.pmd.lang.plsql.ast;

import org.junit.jupiter.api.Test;

import net.sourceforge.pmd.lang.plsql.PlsqlParsingHelper;
import net.sourceforge.pmd.lang.test.ast.BaseParsingHelper;
import net.sourceforge.pmd.lang.test.ast.BaseTreeDumpTest;
import net.sourceforge.pmd.lang.test.ast.RelevantAttributePrinter;

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
