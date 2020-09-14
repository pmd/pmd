/**
 * BSD-style license; for more info see http://pmd.sourceforge.net/license.html
 */

package net.sourceforge.pmd.lang.java.ast;

import net.sourceforge.pmd.lang.java.ast.ASTList.ASTMaybeEmptyListOf;

/**
 * Represents the list of {@link ASTMemberValuePair member-value pairs}
 * in an {@link ASTAnnotation annotation}.
 *
 * <pre class="grammar">
 *
 * AnnotationMemberList ::= "(" {@link ASTMemberValuePair MemberValuePair} ( "," {@link ASTMemberValuePair MemberValuePair} )* ")"
 *                        | "(" {@link ASTMemberValuePair ValueShorthand} ")"
 *                        | "(" ")"
 *
 * </pre>
 */
public final class ASTAnnotationMemberList extends ASTMaybeEmptyListOf<ASTMemberValuePair> {

    ASTAnnotationMemberList(int id) {
        super(id, ASTMemberValuePair.class);
    }

    @Override
    public ASTAnnotation getParent() {
        return (ASTAnnotation) super.getParent();
    }

    @Override
    protected <P, R> R acceptVisitor(JavaVisitor<? super P, ? extends R> visitor, P data) {
        return visitor.visit(this, data);
    }
}
