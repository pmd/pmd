/*
 * BSD-style license; for more info see http://pmd.sourceforge.net/license.html
 */

package net.sourceforge.pmd.lang.plsql.ast;

import net.sourceforge.pmd.benchmark.TimeTracker;
import net.sourceforge.pmd.lang.ast.ParseException;
import net.sourceforge.pmd.lang.ast.impl.javacc.CharStream;
import net.sourceforge.pmd.lang.ast.impl.javacc.JavaccTokenDocument.TokenDocumentBehavior;
import net.sourceforge.pmd.lang.ast.impl.javacc.JjtreeParserAdapter;
import net.sourceforge.pmd.lang.plsql.symboltable.SymbolFacade;

public class PLSQLParser extends JjtreeParserAdapter<ASTInput> {

    private static final TokenDocumentBehavior TOKEN_BEHAVIOR = new TokenDocumentBehavior(PLSQLTokenKinds.TOKEN_NAMES);

    @Override
    protected TokenDocumentBehavior tokenBehavior() {
        return TOKEN_BEHAVIOR;
    }

    @Override
    protected ASTInput parseImpl(CharStream cs, ParserTask task) throws ParseException {
        ASTInput root = new PLSQLParserImpl(cs).Input().addTaskInfo(task);
        TimeTracker.bench("PLSQL symbols", () -> SymbolFacade.process(root));
        return root;
    }

}
