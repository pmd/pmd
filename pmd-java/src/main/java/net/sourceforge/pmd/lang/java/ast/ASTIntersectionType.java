/*
 * BSD-style license; for more info see http://pmd.sourceforge.net/license.html
 */

package net.sourceforge.pmd.lang.java.ast;

import java.util.Iterator;


/**
 * Represents an <a href="https://docs.oracle.com/javase/specs/jls/se9/html/jls-4.html#jls-4.9">intersection type</a>.
 * Can only occur in the following contexts:
 * <ul>
 *     <li>As the bound of a {@linkplain ASTTypeParameter TypeParameter}</li>
 *     <li>As the target type of a {@linkplain ASTCastExpression CastExpression}, on Java 8 and above</li>
 * </ul>
 *
 * The first type can be a class or interface type, while the additional bounds
 * are necessarily interface types.
 *
 * <pre class="grammar">
 *
 * IntersectionType ::= {@link ASTClassOrInterfaceType ClassOrInterfaceType} ("&amp;" {@link ASTClassOrInterfaceType InterfaceType})+
 *
 * </pre>
 */
public final class ASTIntersectionType extends AbstractJavaTypeNode implements ASTReferenceType, Iterable<ASTType> {

    ASTIntersectionType(int id) {
        super(id);
    }


    ASTIntersectionType(JavaParser p, int id) {
        super(p, id);
    }


    @Override
    public String getTypeImage() {
        return iterator().next().getTypeImage(); //TODO

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
    public Iterator<ASTType> iterator() {
        return children(ASTType.class).iterator();
    }
}
