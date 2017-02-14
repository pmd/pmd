/**
 * BSD-style license; for more info see http://pmd.sourceforge.net/license.html
 */

package net.sourceforge.pmd.lang.vf.rule;

import java.util.List;

import net.sourceforge.pmd.RuleContext;
import net.sourceforge.pmd.lang.LanguageRegistry;
import net.sourceforge.pmd.lang.ast.Node;
import net.sourceforge.pmd.lang.vf.ast.VfParserVisitor;
import net.sourceforge.pmd.lang.rule.AbstractRule;
import net.sourceforge.pmd.lang.rule.ImmutableLanguage;
import net.sourceforge.pmd.lang.vf.VfLanguageModule;
import net.sourceforge.pmd.lang.vf.ast.ASTAttribute;
import net.sourceforge.pmd.lang.vf.ast.ASTAttributeValue;
import net.sourceforge.pmd.lang.vf.ast.ASTCData;
import net.sourceforge.pmd.lang.vf.ast.ASTCommentTag;
import net.sourceforge.pmd.lang.vf.ast.ASTCompilationUnit;
import net.sourceforge.pmd.lang.vf.ast.ASTContent;
import net.sourceforge.pmd.lang.vf.ast.ASTDeclaration;
import net.sourceforge.pmd.lang.vf.ast.ASTDoctypeDeclaration;
import net.sourceforge.pmd.lang.vf.ast.ASTDoctypeExternalId;
import net.sourceforge.pmd.lang.vf.ast.ASTElExpression;
import net.sourceforge.pmd.lang.vf.ast.ASTElement;
import net.sourceforge.pmd.lang.vf.ast.ASTHtmlScript;
import net.sourceforge.pmd.lang.vf.ast.ASTText;
import net.sourceforge.pmd.lang.vf.ast.ASTUnparsedText;
import net.sourceforge.pmd.lang.vf.ast.VfNode;

public abstract class AbstractVFRule extends AbstractRule implements VfParserVisitor, ImmutableLanguage {

    public AbstractVFRule() {
        super.setLanguage(LanguageRegistry.getLanguage(VfLanguageModule.NAME));
    }

    @Override
    public void setUsesTypeResolution() {
        // No Type resolution for JSP rules?
    }

    public void apply(List<? extends Node> nodes, RuleContext ctx) {
        visitAll(nodes, ctx);
    }

    protected void visitAll(List<? extends Node> nodes, RuleContext ctx) {
        for (Object element : nodes) {
            VfNode node = (VfNode) element;
            visit(node, ctx);
        }
    }

    //
    // The following APIs are identical to those in JspParserVisitorAdapter.
    // Due to Java single inheritance, it preferred to extend from the more
    // complex Rule base class instead of from relatively simple Visitor.
    //

    public Object visit(VfNode node, Object data) {
        node.childrenAccept(this, data);
        return null;
    }

    public Object visit(ASTCompilationUnit node, Object data) {
        return visit((VfNode) node, data);
    }

    public Object visit(ASTContent node, Object data) {
        return visit((VfNode) node, data);
    }

    public Object visit(ASTText node, Object data) {
        return visit((VfNode) node, data);
    }

    public Object visit(ASTUnparsedText node, Object data) {
        return visit((VfNode) node, data);
    }

    public Object visit(ASTElExpression node, Object data) {
        return visit((VfNode) node, data);
    }

    public Object visit(ASTCData node, Object data) {
        return visit((VfNode) node, data);
    }

    public Object visit(ASTElement node, Object data) {
        return visit((VfNode) node, data);
    }

    public Object visit(ASTAttribute node, Object data) {
        return visit((VfNode) node, data);
    }

    public Object visit(ASTAttributeValue node, Object data) {
        return visit((VfNode) node, data);
    }

    public Object visit(ASTCommentTag node, Object data) {
        return visit((VfNode) node, data);
    }

    public Object visit(ASTDeclaration node, Object data) {
        return visit((VfNode) node, data);
    }

    public Object visit(ASTDoctypeDeclaration node, Object data) {
        return visit((VfNode) node, data);
    }

    public Object visit(ASTDoctypeExternalId node, Object data) {
        return visit((VfNode) node, data);
    }

    public Object visit(ASTHtmlScript node, Object data) {
        return visit((VfNode) node, data);
    }
}
