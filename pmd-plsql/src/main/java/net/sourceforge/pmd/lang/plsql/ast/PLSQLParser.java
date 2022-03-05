/*
 * BSD-style license; for more info see http://pmd.sourceforge.net/license.html
 */

package net.sourceforge.pmd.lang.plsql.ast;

import org.checkerframework.checker.nullness.qual.Nullable;

import net.sourceforge.pmd.benchmark.TimeTracker;
import net.sourceforge.pmd.lang.ast.CharStream;
import net.sourceforge.pmd.lang.ast.ParseException;
import net.sourceforge.pmd.lang.ast.impl.javacc.JavaccTokenDocument;
import net.sourceforge.pmd.lang.ast.impl.javacc.JjtreeParserAdapter;
import net.sourceforge.pmd.lang.plsql.symboltable.SymbolFacade;

public class PLSQLParser extends JjtreeParserAdapter<ASTInput> {

    @Override
    protected JavaccTokenDocument newDocument(String fullText) {
        return new JavaccTokenDocument(fullText) {
            @Override
            protected @Nullable String describeKindImpl(int kind) {
                return PLSQLTokenKinds.describe(kind);
            }
        };
    }

    @Override
    protected ASTInput parseImpl(CharStream cs, ParserTask task) throws ParseException {
        ASTInput root = new PLSQLParserImpl(cs).Input().addTaskInfo(task);
        TimeTracker.bench("PLSQL symbols", () -> SymbolFacade.process(root));
        return root;
    }

}
