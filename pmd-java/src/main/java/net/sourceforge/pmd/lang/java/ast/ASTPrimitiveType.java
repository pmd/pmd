/**
 * BSD-style license; for more info see http://pmd.sourceforge.net/license.html
 */
package net.sourceforge.pmd.lang.java.ast;

import net.sourceforge.pmd.lang.java.types.JPrimitiveType;
import net.sourceforge.pmd.lang.java.types.JPrimitiveType.PrimitiveTypeKind;
import org.checkerframework.checker.nullness.qual.NonNull;

/**
 * Represents a primitive type.
 *
 * <pre class="grammar">
 *
 * PrimitiveType ::= {@link ASTAnnotation Annotation}* ("boolean" | "char" | "byte" | "short" | "int" | "long" | "float" | "double")
 *
 * </pre>
 */
public final class ASTPrimitiveType extends AbstractJavaTypeNode implements ASTType {

    private PrimitiveTypeKind kind;

    ASTPrimitiveType(int id) {
        super(id);
    }

    void setKind(PrimitiveTypeKind kind) {
        assert this.kind == null : "Cannot set kind multiple times";
        this.kind = kind;
    }

    public PrimitiveTypeKind getKind() {
        assert kind != null : "Primitive kind not set for " + this;
        return kind;
    }

    @Override
    protected <P, R> R acceptVisitor(JavaVisitor<? super P, ? extends R> visitor, P data) {
        return visitor.visit(this, data);
    }

    @Override
    public @NonNull JPrimitiveType getTypeMirror() {
        return (JPrimitiveType) super.getTypeMirror();
    }
}
