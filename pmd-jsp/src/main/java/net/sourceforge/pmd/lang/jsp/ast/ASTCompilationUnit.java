/*
 * BSD-style license; for more info see http://pmd.sourceforge.net/license.html
 */

package net.sourceforge.pmd.lang.jsp.ast;

import org.checkerframework.checker.nullness.qual.NonNull;

import net.sourceforge.pmd.lang.Parser.ParserTask;
import net.sourceforge.pmd.lang.ast.RootNode;
import net.sourceforge.pmd.util.document.TextDocument;

public final class ASTCompilationUnit extends AbstractJspNode implements RootNode {

    private TextDocument textDocument;

    ASTCompilationUnit(int id) {
        super(id);
    }

    @Override
    public @NonNull TextDocument getTextDocument() {
        return textDocument;
    }

    ASTCompilationUnit addTaskInfo(ParserTask task) {
        this.textDocument = task.getTextDocument();
        return this;
    }

    @Override
    protected <P, R> R acceptVisitor(JspVisitor<? super P, ? extends R> visitor, P data) {
        return visitor.visit(this, data);
    }
}
