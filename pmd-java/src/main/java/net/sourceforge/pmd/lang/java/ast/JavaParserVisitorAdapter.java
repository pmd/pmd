/**
 * BSD-style license; for more info see http://pmd.sourceforge.net/license.html
 */

package net.sourceforge.pmd.lang.java.ast;

import net.sourceforge.pmd.annotation.DeprecatedUntil700;
import net.sourceforge.pmd.lang.ast.Node;

/**
 * An adapter for {@link JavaParserVisitor}.
 *
 * @deprecated Use {@link JavaVisitorBase}
 */
@Deprecated
@DeprecatedUntil700
public class JavaParserVisitorAdapter extends JavaVisitorBase<Object, Object> implements JavaParserVisitor {

    @Override
    protected Object visitChildren(Node node, Object data) {
        super.visitChildren(node, data);
        return data;
    }

    // REMOVE ME
    // deprecated stuff kept for compatibility with existing visitors, not matched by anything

    // todo on java-grammar: uncomment
    //
    //    @Deprecated
    //    public Object visit(ASTAllocationExpression node, Object data) {
    //        return null;
    //    }
    //
    //    @Deprecated
    //    public Object visit(ASTTypeArgument node, Object data) {
    //        return null;
    //    }
    //
    //    @Deprecated
    //    public Object visit(ASTWildcardBounds node, Object data) {
    //        return null;
    //    }
    //
    //    @Deprecated
    //    public Object visit(ASTConditionalOrExpression node, Object data) {
    //        return visit((ASTExpression) node, data);
    //    }
    //
    //    @Deprecated
    //    public Object visit(ASTConditionalAndExpression node, Object data) {
    //        return visit((ASTExpression) node, data);
    //    }
    //
    //    @Deprecated
    //    public Object visit(ASTInclusiveOrExpression node, Object data) {
    //        return visit((ASTExpression) node, data);
    //    }
    //
    //    @Deprecated
    //    public Object visit(ASTExclusiveOrExpression node, Object data) {
    //        return visit((ASTExpression) node, data);
    //    }
    //
    //    @Deprecated
    //    public Object visit(ASTAndExpression node, Object data) {
    //        return visit((ASTExpression) node, data);
    //    }
    //
    //    @Deprecated
    //    public Object visit(ASTEqualityExpression node, Object data) {
    //        return visit((ASTExpression) node, data);
    //    }
    //
    //    @Deprecated
    //    public Object visit(ASTRelationalExpression node, Object data) {
    //        return visit((ASTExpression) node, data);
    //    }
    //
    //    @Deprecated
    //    public Object visit(ASTShiftExpression node, Object data) {
    //        return visit((ASTExpression) node, data);
    //    }
    //
    //    @Deprecated
    //    public Object visit(ASTAdditiveExpression node, Object data) {
    //        return visit((ASTExpression) node, data);
    //    }
    //
    //    @Deprecated
    //    public Object visit(ASTMultiplicativeExpression node, Object data) {
    //        return visit((ASTExpression) node, data);
    //    }
    //
    //    @Deprecated
    //    public Object visit(ASTInstanceOfExpression node, Object data) {
    //        return visit((ASTExpression) node, data);
    //    }


}
