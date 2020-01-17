/**
 * BSD-style license; for more info see http://pmd.sourceforge.net/license.html
 */

package net.sourceforge.pmd.lang.jsp.rule;

import java.util.List;

import net.sourceforge.pmd.RuleContext;
import net.sourceforge.pmd.lang.LanguageRegistry;
import net.sourceforge.pmd.lang.ast.Node;
import net.sourceforge.pmd.lang.jsp.JspLanguageModule;
import net.sourceforge.pmd.lang.jsp.ast.ASTAttribute;
import net.sourceforge.pmd.lang.jsp.ast.ASTAttributeValue;
import net.sourceforge.pmd.lang.jsp.ast.ASTCData;
import net.sourceforge.pmd.lang.jsp.ast.ASTCommentTag;
import net.sourceforge.pmd.lang.jsp.ast.ASTCompilationUnit;
import net.sourceforge.pmd.lang.jsp.ast.ASTContent;
import net.sourceforge.pmd.lang.jsp.ast.ASTDeclaration;
import net.sourceforge.pmd.lang.jsp.ast.ASTDoctypeDeclaration;
import net.sourceforge.pmd.lang.jsp.ast.ASTDoctypeExternalId;
import net.sourceforge.pmd.lang.jsp.ast.ASTElExpression;
import net.sourceforge.pmd.lang.jsp.ast.ASTElement;
import net.sourceforge.pmd.lang.jsp.ast.ASTHtmlScript;
import net.sourceforge.pmd.lang.jsp.ast.ASTJspComment;
import net.sourceforge.pmd.lang.jsp.ast.ASTJspDeclaration;
import net.sourceforge.pmd.lang.jsp.ast.ASTJspDirective;
import net.sourceforge.pmd.lang.jsp.ast.ASTJspDirectiveAttribute;
import net.sourceforge.pmd.lang.jsp.ast.ASTJspExpression;
import net.sourceforge.pmd.lang.jsp.ast.ASTJspExpressionInAttribute;
import net.sourceforge.pmd.lang.jsp.ast.ASTJspScriptlet;
import net.sourceforge.pmd.lang.jsp.ast.ASTText;
import net.sourceforge.pmd.lang.jsp.ast.ASTUnparsedText;
import net.sourceforge.pmd.lang.jsp.ast.ASTValueBinding;
import net.sourceforge.pmd.lang.jsp.ast.JspNode;
import net.sourceforge.pmd.lang.jsp.ast.JspParserVisitor;
import net.sourceforge.pmd.lang.rule.AbstractRule;
import net.sourceforge.pmd.lang.rule.ImmutableLanguage;

public abstract class AbstractJspRule extends AbstractRule implements JspParserVisitor, ImmutableLanguage {

    public AbstractJspRule() {
        super.setLanguage(LanguageRegistry.getLanguage(JspLanguageModule.NAME));
    }

    @Override
    public void setUsesTypeResolution() {
        // No Type resolution for JSP rules?
    }

    @Override
    public void apply(List<? extends Node> nodes, RuleContext ctx) {
        visitAll(nodes, ctx);
    }

    protected void visitAll(List<? extends Node> nodes, RuleContext ctx) {
        for (Object element : nodes) {
            JspNode node = (JspNode) element;
            visit(node, ctx);
        }
    }

    //
    // The following APIs are identical to those in JspParserVisitorAdapter.
    // Due to Java single inheritance, it preferred to extend from the more
    // complex Rule base class instead of from relatively simple Visitor.
    //

    @Override
    public Object visit(JspNode node, Object data) {
        for (JspNode child : node.children()) {
            child.jjtAccept(this, data);
        }
        return null;
    }

    @Override
    public Object visit(ASTCompilationUnit node, Object data) {
        return visit((JspNode) node, data);
    }

    @Override
    public Object visit(ASTContent node, Object data) {
        return visit((JspNode) node, data);
    }

    @Override
    public Object visit(ASTJspDirective node, Object data) {
        return visit((JspNode) node, data);
    }

    @Override
    public Object visit(ASTJspDirectiveAttribute node, Object data) {
        return visit((JspNode) node, data);
    }

    @Override
    public Object visit(ASTJspScriptlet node, Object data) {
        return visit((JspNode) node, data);
    }

    @Override
    public Object visit(ASTJspExpression node, Object data) {
        return visit((JspNode) node, data);
    }

    @Override
    public Object visit(ASTJspDeclaration node, Object data) {
        return visit((JspNode) node, data);
    }

    @Override
    public Object visit(ASTJspComment node, Object data) {
        return visit((JspNode) node, data);
    }

    @Override
    public Object visit(ASTText node, Object data) {
        return visit((JspNode) node, data);
    }

    @Override
    public Object visit(ASTUnparsedText node, Object data) {
        return visit((JspNode) node, data);
    }

    @Override
    public Object visit(ASTElExpression node, Object data) {
        return visit((JspNode) node, data);
    }

    @Override
    public Object visit(ASTValueBinding node, Object data) {
        return visit((JspNode) node, data);
    }

    @Override
    public Object visit(ASTCData node, Object data) {
        return visit((JspNode) node, data);
    }

    @Override
    public Object visit(ASTElement node, Object data) {
        return visit((JspNode) node, data);
    }

    @Override
    public Object visit(ASTAttribute node, Object data) {
        return visit((JspNode) node, data);
    }

    @Override
    public Object visit(ASTAttributeValue node, Object data) {
        return visit((JspNode) node, data);
    }

    @Override
    public Object visit(ASTJspExpressionInAttribute node, Object data) {
        return visit((JspNode) node, data);
    }

    @Override
    public Object visit(ASTCommentTag node, Object data) {
        return visit((JspNode) node, data);
    }

    @Override
    public Object visit(ASTDeclaration node, Object data) {
        return visit((JspNode) node, data);
    }

    @Override
    public Object visit(ASTDoctypeDeclaration node, Object data) {
        return visit((JspNode) node, data);
    }

    @Override
    public Object visit(ASTDoctypeExternalId node, Object data) {
        return visit((JspNode) node, data);
    }

    @Override
    public Object visit(ASTHtmlScript node, Object data) {
        return visit((JspNode) node, data);
    }
}
