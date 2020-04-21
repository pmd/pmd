/*
 * BSD-style license; for more info see http://pmd.sourceforge.net/license.html
 */

package net.sourceforge.pmd.lang.plsql.ast;

import org.checkerframework.checker.nullness.qual.Nullable;

import net.sourceforge.pmd.lang.ast.CharStream;
import net.sourceforge.pmd.lang.ast.ParseException;
import net.sourceforge.pmd.lang.ast.impl.javacc.JavaccTokenDocument;
import net.sourceforge.pmd.lang.ast.impl.javacc.JjtreeParserAdapter;
import net.sourceforge.pmd.util.document.TextDocument;
import net.sourceforge.pmd.util.document.TextDocument;

public class PLSQLParser extends JjtreeParserAdapter<ASTInput> {

    @Override
    protected JavaccTokenDocument newDocumentImpl(TextDocument fullText) {
        return new JavaccTokenDocument(fullText) {
            @Override
            protected @Nullable String describeKindImpl(int kind) {
                return PLSQLTokenKinds.describe(kind);
            }
        };
    }

    @Override
    protected ASTInput parseImpl(CharStream cs, ParserTask task) throws ParseException {
        return new PLSQLParserImpl(cs).Input().addTaskInfo(task);
    }

}
