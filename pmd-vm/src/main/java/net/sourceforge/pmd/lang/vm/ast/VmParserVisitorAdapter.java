
package net.sourceforge.pmd.lang.vm.ast;

public class VmParserVisitorAdapter implements VmParserVisitor {

    @Override
    public Object visit(final VmNode node, final Object data) {
        node.childrenAccept(this, data);
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
