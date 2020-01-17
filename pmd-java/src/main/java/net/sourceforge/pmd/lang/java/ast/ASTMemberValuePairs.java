/**
 * BSD-style license; for more info see http://pmd.sourceforge.net/license.html
 */

package net.sourceforge.pmd.lang.java.ast;

import java.util.Iterator;


/**
 * Represents a list of member values in an {@linkplain ASTNormalAnnotation annotation}.
 *
 * <pre class="grammar">
 *
 *  MemberValuePairs ::= {@linkplain ASTMemberValuePair MemberValuePair} ( "," {@linkplain ASTMemberValuePair MemberValuePair} )*
 *
 * </pre>
 *
 * @deprecated Removed from the tree, added no info
 */
@Deprecated
public final class ASTMemberValuePairs extends AbstractJavaNode implements Iterable<ASTMemberValuePair> {

    ASTMemberValuePairs(int id) {
        super(id);
    }


    ASTMemberValuePairs(JavaParser p, int id) {
        super(p, id);
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
    public ASTMemberValuePair getChild(int index) {
        return (ASTMemberValuePair) super.getChild(index);
    }


    @Override
    public ASTNormalAnnotation getParent() {
        return (ASTNormalAnnotation) super.getParent();
    }


    @Override
    public Iterator<ASTMemberValuePair> iterator() {
        return children(ASTMemberValuePair.class).iterator();
    }
}
