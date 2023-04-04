/*
 * BSD-style license; for more info see http://pmd.sourceforge.net/license.html
 */

package net.sourceforge.pmd.lang.java.ast;

import java.util.Iterator;

import net.sourceforge.pmd.lang.ast.NodeStream;
import net.sourceforge.pmd.lang.java.ast.InternalInterfaces.AtLeastOneChildOfType;


/**
 * Represents the type node of a multi-catch statement. This node is used
 * to make the grammar of {@link ASTCatchParameter CatchParameter} more
 * straightforward. Note though, that the Java type system does not feature
 * union types at all. The type of this node is defined as the least upper-bound
 * of all its components.
 *
 * <pre class="grammar">
 *
 * UnionType ::= {@link ASTClassOrInterfaceType ClassType} ("|" {@link ASTClassOrInterfaceType ClassType})+
 *
 * </pre>
 */
public final class ASTUnionType extends AbstractJavaTypeNode
    implements ASTReferenceType,
               AtLeastOneChildOfType<ASTClassOrInterfaceType>,
               Iterable<ASTClassOrInterfaceType> {

    ASTUnionType(int id) {
        super(id);
    }

    @Override
    protected <P, R> R acceptVisitor(JavaVisitor<? super P, ? extends R> visitor, P data) {
        return visitor.visit(this, data);
    }

    /** Returns a stream of component types. */
    public NodeStream<ASTClassOrInterfaceType> getComponents() {
        return children(ASTClassOrInterfaceType.class);
    }

    @Override
    public Iterator<ASTClassOrInterfaceType> iterator() {
        return children(ASTClassOrInterfaceType.class).iterator();
    }

}
