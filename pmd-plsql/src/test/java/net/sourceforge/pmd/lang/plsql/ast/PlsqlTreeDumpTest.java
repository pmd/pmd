/*
 * BSD-style license; for more info see http://pmd.sourceforge.net/license.html
 */

package net.sourceforge.pmd.lang.plsql.ast;

import net.sourceforge.pmd.lang.plsql.AbstractPLSQLParserTst;
import org.junit.jupiter.api.Test;

class PlsqlTreeDumpTest extends AbstractPLSQLParserTst {

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

    @Test
    void parseMergeStatement() {
        doTest("MergeStatementIssue1934");
    }

    @Test
    void errorLoggingClause() {
        doTest("ErrorLoggingClause2779");
    }

    @Test
    void compoundTriggerWithAdditionalDeclarations() {
        doTest("CompoundTriggerWithAdditionalDeclarations4270");
    }

    @Test
    void exceptionHandlerTomKytesDespair() {
        doTest("ExceptionHandlerTomKytesDespair");
    }

    @Test
    void sqlMacroClause() {
        doTest("SqlMacroClause");
    }

    @Test
    void parseSelectExpression() {
        doTest("SelectExpressions");
    }

    @Test
    void issue5133SubTypeDefinition() {
        doTest("Issue5133SubTypeDefinition");
    }

    @Test
    void trimFunction() {
        doTest("TrimFunction");
    }

    @Test
    void trimWithRecordType() {
        doTest("TrimWithRecordType");
    }

    @Test
    void trimCollectionFunction() {
        doTest("TrimCollectionFunction");
    }
}
