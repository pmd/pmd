/**
 * BSD-style license; for more info see http://pmd.sourceforge.net/license.html
 */

package net.sourceforge.pmd.lang.java.ast;


import java.util.Iterator;


/**
 * Represents an array of member values in an annotation {@linkplain ASTMemberValue member value}.
 *
 * <pre class="grammar">
 *
 * MemberValueArrayInitializer ::= "{" ( {@linkplain ASTMemberValue MemberValue} ( "," {@linkplain ASTMemberValue MemberValue} )*  ","? )? "}"
 *
 * </pre>
 *
 */
public final class ASTMemberValueArrayInitializer extends AbstractJavaNode implements Iterable<ASTMemberValue>, ASTMemberValue {
    ASTMemberValueArrayInitializer(int id) {
        super(id);
    }

    ASTMemberValueArrayInitializer(JavaParser p, int id) {
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
    public Iterator<ASTMemberValue> iterator() {
        return children(ASTMemberValue.class).iterator();
    }
}
