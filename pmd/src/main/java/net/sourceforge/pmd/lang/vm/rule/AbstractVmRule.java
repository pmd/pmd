/**
 * BSD-style license; for more info see http://pmd.sourceforge.net/license.html
 */
package net.sourceforge.pmd.lang.vm.rule;

import java.util.List;

import net.sourceforge.pmd.RuleContext;
import net.sourceforge.pmd.lang.Language;
import net.sourceforge.pmd.lang.ast.Node;
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

public abstract class AbstractVmRule extends AbstractRule implements JspParserVisitor, ImmutableLanguage {

    public AbstractVmRule() {
        super.setLanguage(Language.VM);
    }

    @Override
    public void setUsesTypeResolution() {
        // No Type resolution for JSP rules?
    }

    public void apply(final List<? extends Node> nodes, final RuleContext ctx) {
        visitAll(nodes, ctx);
    }

    protected void visitAll(final List<? extends Node> nodes, final RuleContext ctx) {
        for (final Object element : nodes) {
            final JspNode node = (JspNode) element;
            visit(node, ctx);
        }
    }

    //
    // The following APIs are identical to those in JspParserVisitorAdapter.
    // Due to Java single inheritance, it preferred to extend from the more
    // complex Rule base class instead of from relatively simple Visitor.
    //

    public Object visit(final JspNode node, final Object data) {
        node.childrenAccept(this, data);
        return null;
    }

    public Object visit(final ASTCompilationUnit node, final Object data) {
        return visit((JspNode) node, data);
    }

    public Object visit(final ASTContent node, final Object data) {
        return visit((JspNode) node, data);
    }

    public Object visit(final ASTJspDirective node, final Object data) {
        return visit((JspNode) node, data);
    }

    public Object visit(final ASTJspDirectiveAttribute node, final Object data) {
        return visit((JspNode) node, data);
    }

    public Object visit(final ASTJspScriptlet node, final Object data) {
        return visit((JspNode) node, data);
    }

    public Object visit(final ASTJspExpression node, final Object data) {
        return visit((JspNode) node, data);
    }

    public Object visit(final ASTJspDeclaration node, final Object data) {
        return visit((JspNode) node, data);
    }

    public Object visit(final ASTJspComment node, final Object data) {
        return visit((JspNode) node, data);
    }

    public Object visit(final ASTText node, final Object data) {
        return visit((JspNode) node, data);
    }

    public Object visit(final ASTUnparsedText node, final Object data) {
        return visit((JspNode) node, data);
    }

    public Object visit(final ASTElExpression node, final Object data) {
        return visit((JspNode) node, data);
    }

    public Object visit(final ASTValueBinding node, final Object data) {
        return visit((JspNode) node, data);
    }

    public Object visit(final ASTCData node, final Object data) {
        return visit((JspNode) node, data);
    }

    public Object visit(final ASTElement node, final Object data) {
        return visit((JspNode) node, data);
    }

    public Object visit(final ASTAttribute node, final Object data) {
        return visit((JspNode) node, data);
    }

    public Object visit(final ASTAttributeValue node, final Object data) {
        return visit((JspNode) node, data);
    }

    public Object visit(final ASTJspExpressionInAttribute node, final Object data) {
        return visit((JspNode) node, data);
    }

    public Object visit(final ASTCommentTag node, final Object data) {
        return visit((JspNode) node, data);
    }

    public Object visit(final ASTDeclaration node, final Object data) {
        return visit((JspNode) node, data);
    }

    public Object visit(final ASTDoctypeDeclaration node, final Object data) {
        return visit((JspNode) node, data);
    }

    public Object visit(final ASTDoctypeExternalId node, final Object data) {
        return visit((JspNode) node, data);
    }

    public Object visit(final ASTHtmlScript node, final Object data) {
        return visit((JspNode) node, data);
    }
}
