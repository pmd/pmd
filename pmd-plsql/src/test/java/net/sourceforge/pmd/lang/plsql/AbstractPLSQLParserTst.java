/*
 * BSD-style license; for more info see http://pmd.sourceforge.net/license.html
 */

package net.sourceforge.pmd.lang.plsql;

import org.checkerframework.checker.nullness.qual.NonNull;

import net.sourceforge.pmd.lang.plsql.ast.ASTInput;
import net.sourceforge.pmd.lang.test.ast.BaseParsingHelper;
import net.sourceforge.pmd.lang.test.ast.BaseTreeDumpTest;
import net.sourceforge.pmd.lang.test.ast.RelevantAttributePrinter;

public abstract class AbstractPLSQLParserTst extends BaseTreeDumpTest {

    public AbstractPLSQLParserTst() {
        super(new RelevantAttributePrinter(), ".pls");
    }

    @Override
    public @NonNull BaseParsingHelper<PlsqlParsingHelper, ASTInput> getParser() {
        return plsql;
    }

    protected final PlsqlParsingHelper plsql = PlsqlParsingHelper.DEFAULT.withResourceContext(getClass());

}
