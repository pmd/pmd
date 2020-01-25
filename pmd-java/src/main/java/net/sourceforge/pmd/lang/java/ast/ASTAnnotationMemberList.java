/**
 * BSD-style license; for more info see http://pmd.sourceforge.net/license.html
 */

package net.sourceforge.pmd.lang.java.ast;

import java.util.Iterator;

import net.sourceforge.pmd.lang.ast.NodeStream;

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
public final class ASTAnnotationMemberList extends AbstractJavaNode implements Iterable<ASTMemberValuePair> {

    ASTAnnotationMemberList(int id) {
        super(id);
    }


    @Override
    public ASTAnnotation getParent() {
        return (ASTAnnotation) super.getParent();
    }

    @Override
    @SuppressWarnings("unchecked")
    public NodeStream<ASTMemberValuePair> children() {
        return (NodeStream<ASTMemberValuePair>) super.children();
    }

    @Override
    public Object jjtAccept(JavaParserVisitor visitor, Object data) {
        return visitor.visit(this, data);
    }


    @Override
    public <T> void jjtAccept(SideEffectingVisitor<T> visitor, T data) {
        visitor.visit(this, data);
    }

    @Override
    public Iterator<ASTMemberValuePair> iterator() {
        return children().iterator();
    }
}
