/*
 * BSD-style license; for more info see http://pmd.sourceforge.net/license.html
 */

package net.sourceforge.pmd.lang.plsql.ast;

import org.checkerframework.checker.nullness.qual.NonNull;

import net.sourceforge.pmd.lang.Parser.ParserTask;
import net.sourceforge.pmd.lang.ast.RootNode;
import net.sourceforge.pmd.util.document.TextDocument;

public final class ASTInput extends AbstractPLSQLNode implements RootNode {

    private TextDocument textDocument;

    ASTInput(int id) {
        super(id);
    }


    @Override
    public @NonNull TextDocument getTextDocument() {
        return textDocument;
    }

    ASTInput addTaskInfo(ParserTask task) {
        textDocument = task.getTextDocument();
        return this;
    }


    @Override
    protected <P, R> R acceptPlsqlVisitor(PlsqlVisitor<? super P, ? extends R> visitor, P data) {
        return visitor.visit(this, data);
    }

    public String getSourcecode() {
        return new StringBuilder(getText()).toString();
    }
}
