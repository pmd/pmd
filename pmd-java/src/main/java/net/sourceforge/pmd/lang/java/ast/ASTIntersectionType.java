/*
 * BSD-style license; for more info see http://pmd.sourceforge.net/license.html
 */

package net.sourceforge.pmd.lang.java.ast;

import java.util.Iterator;

import net.sourceforge.pmd.lang.ast.NodeStream;


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
public final class ASTIntersectionType extends AbstractJavaTypeNode
    implements ASTReferenceType,
               InternalInterfaces.AtLeastOneChildOfType<ASTType>,
               Iterable<ASTType> {

    ASTIntersectionType(int id) {
        super(id);
    }

    /** Returns a stream of component types. */
    public NodeStream<ASTClassOrInterfaceType> getComponents() {
        return children(ASTClassOrInterfaceType.class);
    }

    @Override
    protected <P, R> R acceptVisitor(JavaVisitor<? super P, ? extends R> visitor, P data) {
        return visitor.visit(this, data);
    }

    @Override
    public Iterator<ASTType> iterator() {
        return children(ASTType.class).iterator();
    }
}
