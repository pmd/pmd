/*
 * BSD-style license; for more info see http://pmd.sourceforge.net/license.html
 */

package net.sourceforge.pmd.lang.java.ast;

import java.util.Iterator;
import java.util.List;


/**
 * Represents the type node of a multi-catch statement. This node is used
 * to make the grammar of {@link ASTCatchStatement CatchStatement} more
 * straightforward. Note though, that the Java type system does not feature
 * union types per se. The type of this node is defined as the least upper-bound
 * of all its components.
 *
 * <pre class="grammar">
 *
 * UnionType ::= {@link ASTClassOrInterfaceType ClassType} ("|" {@link ASTClassOrInterfaceType ClassType})+
 *
 * </pre>
 */
public final class ASTUnionType extends AbstractJavaTypeNode implements ASTReferenceType, JSingleChildNode<ASTClassOrInterfaceType>, Iterable<ASTClassOrInterfaceType> {

    ASTUnionType(int id) {
        super(id);
    }


    ASTUnionType(JavaParser p, int id) {
        super(p, id);
    }


    @Override
    public String getTypeImage() {
        // TODO
        return iterator().next().getTypeImage();
    }

    @Override
    public List<ASTType> asList() {
        return findChildrenOfType(ASTType.class);
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
    public Iterator<ASTClassOrInterfaceType> iterator() {
        return new NodeChildrenIterator<>(this, ASTClassOrInterfaceType.class);
    }

    @Override
    public ASTClassOrInterfaceType jjtGetChild(int index) {
        return (ASTClassOrInterfaceType) super.jjtGetChild(index);
    }
}
