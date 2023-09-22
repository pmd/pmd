/*
 * BSD-style license; for more info see http://pmd.sourceforge.net/license.html
 */


package net.sourceforge.pmd.lang.html.ast;

import java.util.Map;

import org.jsoup.nodes.Document;

import net.sourceforge.pmd.lang.ast.AstInfo;
import net.sourceforge.pmd.lang.ast.Parser;
import net.sourceforge.pmd.lang.ast.RootNode;

public final class ASTHtmlDocument extends ASTHtmlElement implements RootNode {

    private final AstInfo<ASTHtmlDocument> astInfo;

    ASTHtmlDocument(Document document,
                    Parser.ParserTask task,
                    Map<Integer, String> suppressMap) {
        super(document);
        this.astInfo = new AstInfo<>(task, this).withSuppressMap(suppressMap);
    }

    @Override
    protected <P, R> R acceptHtmlVisitor(HtmlVisitor<? super P, ? extends R> visitor, P data) {
        return visitor.visit(this, data);
    }

    @Override
    public AstInfo<ASTHtmlDocument> getAstInfo() {
        return astInfo;
    }
}
