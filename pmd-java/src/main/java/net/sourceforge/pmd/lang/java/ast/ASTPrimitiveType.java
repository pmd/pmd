/**
 * BSD-style license; for more info see http://pmd.sourceforge.net/license.html
 */

package net.sourceforge.pmd.lang.java.ast;

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

    ASTPrimitiveType(int id) {
        super(id);
    }

    public boolean isBoolean() {
        return "boolean".equals(getImage());
    }

    @Override
    public Object jjtAccept(JavaParserVisitor visitor, Object data) {
        return visitor.visit(this, data);
    }


    @Override
    public <T> void jjtAccept(SideEffectingVisitor<T> visitor, T data) {
        visitor.visit(this, data);
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
