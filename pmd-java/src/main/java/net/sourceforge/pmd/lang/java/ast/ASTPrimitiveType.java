/**
 * BSD-style license; for more info see http://pmd.sourceforge.net/license.html
 */

package net.sourceforge.pmd.lang.java.ast;

import net.sourceforge.pmd.annotation.InternalApi;

/**
 * Represents a primitive type.
 *
 * <pre>
 *
 * PrimitiveType ::= "boolean" | "char" | "byte" | "short" | "int" | "long" | "float" | "double"
 *
 * </pre>
 */
public class ASTPrimitiveType extends AbstractJavaTypeNode implements Dimensionable {

    private int arrayDepth;

    @InternalApi
    @Deprecated
    public ASTPrimitiveType(int id) {
        super(id);
    }

    @InternalApi
    @Deprecated
    public ASTPrimitiveType(JavaParser p, int id) {
        super(p, id);
    }

    public boolean isBoolean() {
        return "boolean".equals(getImage());
    }

    @Override
    public Object jjtAccept(JavaParserVisitor visitor, Object data) {
        return visitor.visit(this, data);
    }

    @Deprecated
    public void bumpArrayDepth() {
        arrayDepth++;
    }

    @Override
    @Deprecated
    public int getArrayDepth() {
        return arrayDepth;
    }

    @Override
    @Deprecated
    public boolean isArray() {
        return arrayDepth > 0;
    }

}
