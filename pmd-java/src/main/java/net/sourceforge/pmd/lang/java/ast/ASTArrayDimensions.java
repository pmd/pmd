/**
 * BSD-style license; for more info see http://pmd.sourceforge.net/license.html
 */

package net.sourceforge.pmd.lang.java.ast;

import java.util.Iterator;

import net.sourceforge.pmd.lang.java.ast.InternalInterfaces.AtLeastOneChild;


/**
 * Represents array type dimensions. This node may occur in several contexts:
 * <ul>
 * <li>In an {@linkplain ASTArrayType array type}</li>
 * <li>TODO At the end {@linkplain ASTMethodDeclarator method declarator}</li>
 * <li>TODO After a {@link ASTVariableDeclaratorId variable declarator id}</li>
 * </ul>
 *
 * <p>Some dimensions may be initialized with an expression, but only in
 * the array type of an {@link ASTArrayAllocation array allocation expression}.
 *
 * <pre class="grammar">
 *
 * ArrayDimensions ::= {@link ASTArrayTypeDim ArrayTypeDim}+ {@link ASTArrayDimExpr ArrayDimExpr}*
 *
 * </pre>
 */
public final class ASTArrayDimensions extends AbstractJavaTypeNode implements Iterable<ASTArrayTypeDim>, AtLeastOneChild {

    ASTArrayDimensions(int id) {
        super(id);
    }


    ASTArrayDimensions(JavaParser p, int id) {
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
    public Iterator<ASTArrayTypeDim> iterator() {
        return children(ASTArrayTypeDim.class).iterator();
    }


    /**
     * Returns the number of array dimensions of this type.
     * E.g. for [][], this will return 2. The returned number
     * is always greater than 0.
     */
    public int getSize() {
        return jjtGetNumChildren();
    }
}
