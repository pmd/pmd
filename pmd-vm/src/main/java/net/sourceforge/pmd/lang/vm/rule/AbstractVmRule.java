/**
 * BSD-style license; for more info see http://pmd.sourceforge.net/license.html
 */

package net.sourceforge.pmd.lang.vm.rule;

import java.util.List;

import net.sourceforge.pmd.RuleContext;
import net.sourceforge.pmd.lang.LanguageRegistry;
import net.sourceforge.pmd.lang.ast.Node;
import net.sourceforge.pmd.lang.rule.AbstractRule;
import net.sourceforge.pmd.lang.rule.ImmutableLanguage;
import net.sourceforge.pmd.lang.vm.VmLanguageModule;
import net.sourceforge.pmd.lang.vm.ast.ASTAddNode;
import net.sourceforge.pmd.lang.vm.ast.ASTAndNode;
import net.sourceforge.pmd.lang.vm.ast.ASTAssignment;
import net.sourceforge.pmd.lang.vm.ast.ASTBlock;
import net.sourceforge.pmd.lang.vm.ast.ASTComment;
import net.sourceforge.pmd.lang.vm.ast.ASTDirective;
import net.sourceforge.pmd.lang.vm.ast.ASTDivNode;
import net.sourceforge.pmd.lang.vm.ast.ASTEQNode;
import net.sourceforge.pmd.lang.vm.ast.ASTElseIfStatement;
import net.sourceforge.pmd.lang.vm.ast.ASTElseStatement;
import net.sourceforge.pmd.lang.vm.ast.ASTEscape;
import net.sourceforge.pmd.lang.vm.ast.ASTEscapedDirective;
import net.sourceforge.pmd.lang.vm.ast.ASTExpression;
import net.sourceforge.pmd.lang.vm.ast.ASTFalse;
import net.sourceforge.pmd.lang.vm.ast.ASTFloatingPointLiteral;
import net.sourceforge.pmd.lang.vm.ast.ASTForeachStatement;
import net.sourceforge.pmd.lang.vm.ast.ASTGENode;
import net.sourceforge.pmd.lang.vm.ast.ASTGTNode;
import net.sourceforge.pmd.lang.vm.ast.ASTIdentifier;
import net.sourceforge.pmd.lang.vm.ast.ASTIfStatement;
import net.sourceforge.pmd.lang.vm.ast.ASTIndex;
import net.sourceforge.pmd.lang.vm.ast.ASTIntegerLiteral;
import net.sourceforge.pmd.lang.vm.ast.ASTIntegerRange;
import net.sourceforge.pmd.lang.vm.ast.ASTLENode;
import net.sourceforge.pmd.lang.vm.ast.ASTLTNode;
import net.sourceforge.pmd.lang.vm.ast.ASTMap;
import net.sourceforge.pmd.lang.vm.ast.ASTMethod;
import net.sourceforge.pmd.lang.vm.ast.ASTModNode;
import net.sourceforge.pmd.lang.vm.ast.ASTMulNode;
import net.sourceforge.pmd.lang.vm.ast.ASTNENode;
import net.sourceforge.pmd.lang.vm.ast.ASTNotNode;
import net.sourceforge.pmd.lang.vm.ast.ASTObjectArray;
import net.sourceforge.pmd.lang.vm.ast.ASTOrNode;
import net.sourceforge.pmd.lang.vm.ast.ASTReference;
import net.sourceforge.pmd.lang.vm.ast.ASTSetDirective;
import net.sourceforge.pmd.lang.vm.ast.ASTStringLiteral;
import net.sourceforge.pmd.lang.vm.ast.ASTSubtractNode;
import net.sourceforge.pmd.lang.vm.ast.ASTText;
import net.sourceforge.pmd.lang.vm.ast.ASTTextblock;
import net.sourceforge.pmd.lang.vm.ast.ASTTrue;
import net.sourceforge.pmd.lang.vm.ast.ASTWord;
import net.sourceforge.pmd.lang.vm.ast.ASTprocess;
import net.sourceforge.pmd.lang.vm.ast.VmNode;
import net.sourceforge.pmd.lang.vm.ast.VmParserVisitor;

public abstract class AbstractVmRule extends AbstractRule implements VmParserVisitor, ImmutableLanguage {

    public AbstractVmRule() {
        super.setLanguage(LanguageRegistry.getLanguage(VmLanguageModule.NAME));
    }

    @Override
    public void setUsesTypeResolution() {
        // No Type resolution for Velocity rules?
    }

    @Override
    public void apply(final List<? extends Node> nodes, final RuleContext ctx) {
        visitAll(nodes, ctx);
    }

    protected void visitAll(final List<? extends Node> nodes, final RuleContext ctx) {
        for (final Object element : nodes) {
            final ASTprocess node = (ASTprocess) element;
            visit(node, ctx);
        }
    }

    @Override
    public Object visit(final VmNode node, final Object data) {
        for (VmNode child : node.children()) {
            child.jjtAccept(this, data);
        }
        return null;
    }

    @Override
    public Object visit(final ASTprocess node, final Object data) {
        return visit((VmNode) node, data);
    }

    @Override
    public Object visit(final ASTEscapedDirective node, final Object data) {
        return visit((VmNode) node, data);
    }

    @Override
    public Object visit(final ASTEscape node, final Object data) {
        return visit((VmNode) node, data);
    }

    @Override
    public Object visit(final ASTComment node, final Object data) {
        return visit((VmNode) node, data);
    }

    @Override
    public Object visit(final ASTTextblock node, final Object data) {
        return visit((VmNode) node, data);
    }

    @Override
    public Object visit(final ASTFloatingPointLiteral node, final Object data) {
        return visit((VmNode) node, data);
    }

    @Override
    public Object visit(final ASTIntegerLiteral node, final Object data) {
        return visit((VmNode) node, data);
    }

    @Override
    public Object visit(final ASTStringLiteral node, final Object data) {
        return visit((VmNode) node, data);
    }

    @Override
    public Object visit(final ASTIdentifier node, final Object data) {

        return visit((VmNode) node, data);
    }

    @Override
    public Object visit(final ASTWord node, final Object data) {

        return visit((VmNode) node, data);
    }

    @Override
    public Object visit(final ASTDirective node, final Object data) {

        return visit((VmNode) node, data);
    }

    @Override
    public Object visit(final ASTBlock node, final Object data) {

        return visit((VmNode) node, data);
    }

    @Override
    public Object visit(final ASTMap node, final Object data) {

        return visit((VmNode) node, data);
    }

    @Override
    public Object visit(final ASTObjectArray node, final Object data) {

        return visit((VmNode) node, data);
    }

    @Override
    public Object visit(final ASTIntegerRange node, final Object data) {

        return visit((VmNode) node, data);
    }

    @Override
    public Object visit(final ASTMethod node, final Object data) {

        return visit((VmNode) node, data);
    }

    @Override
    public Object visit(final ASTIndex node, final Object data) {

        return visit((VmNode) node, data);
    }

    @Override
    public Object visit(final ASTReference node, final Object data) {

        return visit((VmNode) node, data);
    }

    @Override
    public Object visit(final ASTTrue node, final Object data) {

        return visit((VmNode) node, data);
    }

    @Override
    public Object visit(final ASTFalse node, final Object data) {

        return visit((VmNode) node, data);
    }

    @Override
    public Object visit(final ASTText node, final Object data) {

        return visit((VmNode) node, data);
    }

    @Override
    public Object visit(final ASTForeachStatement node, final Object data) {

        return visit((VmNode) node, data);
    }

    @Override
    public Object visit(final ASTIfStatement node, final Object data) {

        return visit((VmNode) node, data);
    }

    @Override
    public Object visit(final ASTElseStatement node, final Object data) {

        return visit((VmNode) node, data);
    }

    @Override
    public Object visit(final ASTElseIfStatement node, final Object data) {

        return visit((VmNode) node, data);
    }

    @Override
    public Object visit(final ASTSetDirective node, final Object data) {

        return visit((VmNode) node, data);
    }

    @Override
    public Object visit(final ASTExpression node, final Object data) {

        return visit((VmNode) node, data);
    }

    @Override
    public Object visit(final ASTAssignment node, final Object data) {

        return visit((VmNode) node, data);
    }

    @Override
    public Object visit(final ASTOrNode node, final Object data) {

        return visit((VmNode) node, data);
    }

    @Override
    public Object visit(final ASTAndNode node, final Object data) {

        return visit((VmNode) node, data);
    }

    @Override
    public Object visit(final ASTEQNode node, final Object data) {

        return visit((VmNode) node, data);
    }

    @Override
    public Object visit(final ASTNENode node, final Object data) {

        return visit((VmNode) node, data);
    }

    @Override
    public Object visit(final ASTLTNode node, final Object data) {

        return visit((VmNode) node, data);
    }

    @Override
    public Object visit(final ASTGTNode node, final Object data) {

        return visit((VmNode) node, data);
    }

    @Override
    public Object visit(final ASTLENode node, final Object data) {

        return visit((VmNode) node, data);
    }

    @Override
    public Object visit(final ASTGENode node, final Object data) {

        return visit((VmNode) node, data);
    }

    @Override
    public Object visit(final ASTAddNode node, final Object data) {

        return visit((VmNode) node, data);
    }

    @Override
    public Object visit(final ASTSubtractNode node, final Object data) {

        return visit((VmNode) node, data);
    }

    @Override
    public Object visit(final ASTMulNode node, final Object data) {

        return visit((VmNode) node, data);
    }

    @Override
    public Object visit(final ASTDivNode node, final Object data) {

        return visit((VmNode) node, data);
    }

    @Override
    public Object visit(final ASTModNode node, final Object data) {

        return visit((VmNode) node, data);
    }

    @Override
    public Object visit(final ASTNotNode node, final Object data) {

        return visit((VmNode) node, data);
    }

}
