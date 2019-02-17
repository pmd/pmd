/**
 * BSD-style license; for more info see http://pmd.sourceforge.net/license.html
 */

package net.sourceforge.pmd.lang.java.ast;

import java.util.Iterator;


/**
 * Represents array type dimensions. This node may occur in several contexts:
 * <ul>
 * <li>In an {@linkplain ASTArrayType array type}</li>
 * <li>TODO At the end {@linkplain ASTMethodDeclarator method declarator}</li>
 * <li>TODO After an {@link ASTVariableDeclaratorId variable declarator id}</li>
 * <li>TODO inside an array creation expression (currently {@link ASTAllocationExpression})</li>
 * </ul>
 *
 * <pre>
 *
 * ArrayTypeDims ::= {@link ASTArrayTypeDim ArrayTypeDim}*
 *
 * </pre>
 */
public final class ASTArrayTypeDims extends AbstractJavaTypeNode implements Iterable<ASTArrayTypeDim> {
    ASTArrayTypeDims(int id) {
        super(id);
    }


    ASTArrayTypeDims(JavaParser p, int id) {
        super(p, id);
    }


    @Override
    public Object jjtAccept(JavaParserVisitor visitor, Object data) {
        return visitor.visit(this, data);
    }


    @Override
    public Iterator<ASTArrayTypeDim> iterator() {
        return new NodeChildrenIterator<>(this, ASTArrayTypeDim.class);
    }


    /**
     * Returns the number of array dimensions of this type.
     * E.g. for [][], this will return 2. The returned number
     * is always greater than 0.
     */
    public int getSize() {
        return findChildrenOfType(ASTArrayTypeDim.class).size();
    }
}
