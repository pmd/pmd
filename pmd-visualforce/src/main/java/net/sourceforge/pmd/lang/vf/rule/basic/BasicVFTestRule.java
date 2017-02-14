/**
 * BSD-style license; for more info see http://pmd.sourceforge.net/license.html
 */

package net.sourceforge.pmd.lang.vf.rule.basic;

import java.util.HashSet;
import java.util.List;
import java.util.Set;

import net.sourceforge.pmd.RuleContext;
import net.sourceforge.pmd.lang.ast.Node;
import net.sourceforge.pmd.lang.rule.ImportWrapper;
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
import net.sourceforge.pmd.lang.vf.rule.AbstractVFRule;

public class BasicVFTestRule extends AbstractVFRule {

    private Set<ImportWrapper> imports = new HashSet<>();

    @Override
    public void apply(List<? extends Node> nodes, RuleContext ctx) {
        /*
         * TODO: This method is a hack! It's overriding the parent's method
         * because the JSP parsing doesn't seem to hit ASTCompilationUnit
         * properly
         */
        imports.clear();
        super.apply(nodes, ctx);
    }

    @Override
    protected void visitAll(List<? extends Node> nodes, RuleContext ctx) {
        // TODO Auto-generated method stub
        super.visitAll(nodes, ctx);
    }

    @Override
    public Object visit(ASTCompilationUnit node, Object data) {
        // TODO Auto-generated method stub
        return super.visit(node, data);
    }

    @Override
    public Object visit(ASTContent node, Object data) {
        // TODO Auto-generated method stub
        return super.visit(node, data);
    }

    @Override
    public Object visit(ASTText node, Object data) {
        // TODO Auto-generated method stub
        return super.visit(node, data);
    }

    @Override
    public Object visit(ASTUnparsedText node, Object data) {
        // TODO Auto-generated method stub
        return super.visit(node, data);
    }

    @Override
    public Object visit(ASTElExpression node, Object data) {
        // TODO Auto-generated method stub
        return super.visit(node, data);
    }

    @Override
    public Object visit(ASTCData node, Object data) {
        // TODO Auto-generated method stub
        return super.visit(node, data);
    }

    @Override
    public Object visit(ASTElement node, Object data) {
        // TODO Auto-generated method stub
        return super.visit(node, data);
    }

    @Override
    public Object visit(ASTAttribute node, Object data) {
        // TODO Auto-generated method stub
        return super.visit(node, data);
    }

    @Override
    public Object visit(ASTAttributeValue node, Object data) {
        // TODO Auto-generated method stub
        return super.visit(node, data);
    }

    @Override
    public Object visit(ASTCommentTag node, Object data) {
        // TODO Auto-generated method stub
        return super.visit(node, data);
    }

    @Override
    public Object visit(ASTDeclaration node, Object data) {
        // TODO Auto-generated method stub
        return super.visit(node, data);
    }

    @Override
    public Object visit(ASTDoctypeDeclaration node, Object data) {
        // TODO Auto-generated method stub
        return super.visit(node, data);
    }

    @Override
    public Object visit(ASTDoctypeExternalId node, Object data) {
        // TODO Auto-generated method stub
        return super.visit(node, data);
    }

    @Override
    public Object visit(ASTHtmlScript node, Object data) {
        // TODO Auto-generated method stub
        return super.visit(node, data);
    }

}
