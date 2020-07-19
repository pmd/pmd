/**
 * BSD-style license; for more info see http://pmd.sourceforge.net/license.html
 */

package net.sourceforge.pmd.lang.java.ast;

/**
 * Represents an assignment operator in an {@linkplain ASTExpression assignment expression}.
 *
 * <pre>
 *
 *  AssignmentOperator ::= "=" | "*=" | "/=" | "%=" | "+=" | "-=" | "<<=" | ">>=" | ">>>=" | "&=" | "^=" | "|="
 *
 * </pre>
 * @deprecated Superseded by {@link ASTAssignmentExpression}
 */
@Deprecated
public class ASTAssignmentOperator extends AbstractJavaNode {

    private boolean isCompound;

    ASTAssignmentOperator(int id) {
        super(id);
    }


    public boolean isCompound() {
        return this.isCompound;
    }


    @Override
    protected <P, R> R acceptVisitor(JavaVisitor<? super P, ? extends R> visitor, P data) {
        return visitor.visit(this, data);
    }
}
