/**
 * BSD-style license; for more info see http://pmd.sourceforge.net/license.html
 */

package net.sourceforge.pmd.lang.java.ast;

import java.util.Iterator;


/**
 * Represents array type dimensions, as occurring in {@linkplain ASTArrayType array types}.
 *
 * <pre>
 *
 * ArrayTypeDims ::= {@linkplain ASTArrayTypeDim ArrayTypeDim} *
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


    public int getSize() {
        return jjtGetNumChildren();
    }
}
