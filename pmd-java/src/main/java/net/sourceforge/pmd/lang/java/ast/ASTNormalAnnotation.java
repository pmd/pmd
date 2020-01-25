/**
 * BSD-style license; for more info see http://pmd.sourceforge.net/license.html
 */

package net.sourceforge.pmd.lang.java.ast;

import java.util.Iterator;

/**
 * Represents an annotation that with a parenthesized list
 * of key-value pairs (possibly empty).
 *
 * <pre class="grammar">
 *
 * NormalAnnotation ::=  "@" Name "(" ( {@linkplain ASTMemberValuePair MemberValuePair} ( "," {@linkplain ASTMemberValuePair MemberValuePair} )* )? ")"
 *
 * </pre>
 *
 * @see ASTSingleMemberAnnotation
 * @see ASTMarkerAnnotation
 * @deprecated Replaced with {@link ASTAnnotation}
 */
@Deprecated
public final class ASTNormalAnnotation extends AbstractJavaTypeNode implements Iterable<ASTMemberValuePair> {

    ASTNormalAnnotation(int id) {
        super(id);
    }


    @Override
    public Iterator<ASTMemberValuePair> iterator() {
        return children(ASTMemberValuePair.class).iterator();
    }

    @Override
    public Object jjtAccept(JavaParserVisitor visitor, Object data) {
        return visitor.visit(this, data);
    }


    @Override
    public <T> void jjtAccept(SideEffectingVisitor<T> visitor, T data) {
        visitor.visit(this, data);
    }

}
