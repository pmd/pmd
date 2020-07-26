/**
 * BSD-style license; for more info see http://pmd.sourceforge.net/license.html
 */

package net.sourceforge.pmd.lang.java.ast;

import org.checkerframework.checker.nullness.qual.NonNull;

import net.sourceforge.pmd.annotation.InternalApi;
import net.sourceforge.pmd.lang.java.symboltable.ClassScope;
import net.sourceforge.pmd.lang.java.types.JPrimitiveType;
import net.sourceforge.pmd.lang.java.types.JPrimitiveType.PrimitiveTypeKind;


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

    /**
     * @deprecated Made public for one shady usage in {@link ClassScope}
     */
    @Deprecated
    @InternalApi
    public ASTPrimitiveType(PrimitiveTypeKind type) {
        super(JavaParserImplTreeConstants.JJTPRIMITIVETYPE);
        setKind(type);
    }


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
    @Deprecated
    public String getImage() {
        return null;
    }

    @Override
    public String getTypeImage() {
        return getKind().getSimpleName();
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
