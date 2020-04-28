/*
 * BSD-style license; for more info see http://pmd.sourceforge.net/license.html
 */

package net.sourceforge.pmd.lang.scala.ast;

import org.checkerframework.checker.nullness.qual.NonNull;

import net.sourceforge.pmd.lang.ast.RootNode;
import net.sourceforge.pmd.util.document.TextDocument;

import scala.meta.Source;

/**
 * The ASTSource node implementation.
 */
public final class ASTSource extends AbstractScalaNode<Source> implements RootNode {

    private TextDocument textDocument;

    ASTSource(Source scalaNode) {
        super(scalaNode);
    }

    void setTextDocument(TextDocument textDocument) {
        this.textDocument = textDocument;
    }

    @Override
    public @NonNull TextDocument getTextDocument() {
        return textDocument;
    }

    @Override
    protected <P, R> R acceptVisitor(ScalaParserVisitor<? super P, ? extends R> visitor, P data) {
        return visitor.visit(this, data);
    }
}
