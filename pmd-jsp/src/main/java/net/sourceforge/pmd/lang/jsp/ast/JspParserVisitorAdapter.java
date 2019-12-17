/**
 * BSD-style license; for more info see http://pmd.sourceforge.net/license.html
 */

package net.sourceforge.pmd.lang.jsp.ast;

import net.sourceforge.pmd.lang.ast.Node;
import net.sourceforge.pmd.lang.java.ast.impl.BaseGenericVisitor;

public class JspParserVisitorAdapter extends BaseGenericVisitor implements JspParserVisitor {

    @Override
    protected Object zero(Node parent, Object data) {
        return data;
    }

    @Override
    protected Object visitChildAt(Node node, int idx, Object data) {
        return ((JspNode) node).getChild(idx).jjtAccept(this, data);
    }

    @Override
    @SuppressWarnings("unchecked")
    public Object visit(JspNode node, Object data) {
        return super.visit(node, data);
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
