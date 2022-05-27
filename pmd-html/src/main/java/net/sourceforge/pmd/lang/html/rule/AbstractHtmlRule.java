/*
 * BSD-style license; for more info see http://pmd.sourceforge.net/license.html
 */

package net.sourceforge.pmd.lang.html.rule;

import java.util.List;

import net.sourceforge.pmd.RuleContext;
import net.sourceforge.pmd.lang.LanguageRegistry;
import net.sourceforge.pmd.lang.ast.Node;
import net.sourceforge.pmd.lang.html.HtmlLanguageModule;
import net.sourceforge.pmd.lang.html.ast.ASTHtmlCDataNode;
import net.sourceforge.pmd.lang.html.ast.ASTHtmlComment;
import net.sourceforge.pmd.lang.html.ast.ASTHtmlDocument;
import net.sourceforge.pmd.lang.html.ast.ASTHtmlDocumentType;
import net.sourceforge.pmd.lang.html.ast.ASTHtmlElement;
import net.sourceforge.pmd.lang.html.ast.ASTHtmlTextNode;
import net.sourceforge.pmd.lang.html.ast.ASTHtmlXmlDeclaration;
import net.sourceforge.pmd.lang.html.ast.HtmlNode;
import net.sourceforge.pmd.lang.html.ast.HtmlVisitor;
import net.sourceforge.pmd.lang.rule.AbstractRule;

public abstract class AbstractHtmlRule extends AbstractRule implements HtmlVisitor {

    public AbstractHtmlRule() {
        super.setLanguage(LanguageRegistry.getLanguage(HtmlLanguageModule.NAME));
    }

    @Override
    public void apply(List<? extends Node> nodes, RuleContext ctx) {
        for (Node node : nodes) {
            if (node instanceof HtmlNode) {
                ((HtmlNode) node).acceptVisitor(this, ctx);
            }
        }
    }

    //
    // The following APIs are identical to those in HtmlVisitorAdapter.
    // Due to Java single inheritance, it is preferred to extend from the more
    // complex Rule base class instead of from relatively simple Visitor.
    //
    // CPD-OFF

    @Override
    public Object visit(HtmlNode node, Object data) {
        for (HtmlNode child : node.children()) {
            child.acceptVisitor(this, data);
        }
        return null;
    }

    @Override
    public Object visit(ASTHtmlCDataNode node, Object data) {
        return visit((HtmlNode) node, data);
    }

    @Override
    public Object visit(ASTHtmlComment node, Object data) {
        return visit((HtmlNode) node, data);
    }

    @Override
    public Object visit(ASTHtmlDocument node, Object data) {
        return visit((HtmlNode) node, data);
    }

    @Override
    public Object visit(ASTHtmlDocumentType node, Object data) {
        return visit((HtmlNode) node, data);
    }

    @Override
    public Object visit(ASTHtmlElement node, Object data) {
        return visit((HtmlNode) node, data);
    }

    @Override
    public Object visit(ASTHtmlTextNode node, Object data) {
        return visit((HtmlNode) node, data);
    }

    @Override
    public Object visit(ASTHtmlXmlDeclaration node, Object data) {
        return visit((HtmlNode) node, data);
    }

    // CPD-ON
}
