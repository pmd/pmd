/**
 * BSD-style license; for more info see http://pmd.sourceforge.net/license.html
 */

package net.sourceforge.pmd.lang.java.ast;

import java.util.Arrays;
import java.util.Collections;
import java.util.Locale;
import java.util.Map;
import java.util.Objects;
import java.util.stream.Collectors;

import org.checkerframework.checker.nullness.qual.Nullable;

import net.sourceforge.pmd.annotation.Experimental;
import net.sourceforge.pmd.annotation.InternalApi;
import net.sourceforge.pmd.lang.java.symboltable.ClassScope;


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

    /**
     * @deprecated Made public for one shady usage in {@link ClassScope}
     */
    @Deprecated
    @InternalApi
    public ASTPrimitiveType(PrimitiveType type) {
        super(JavaParserImplTreeConstants.JJTPRIMITIVETYPE);
        setImage(type.getToken());
    }


    ASTPrimitiveType(int id) {
        super(id);
    }


    public boolean isBoolean() {
        return "boolean".equals(getImage());
    }


    @Override
    @Deprecated
    public String getTypeImage() {
        return getImage();
    }


    @Override
    protected <P, R> R acceptVisitor(JavaVisitor<? super P, ? extends R> visitor, P data) {
        return visitor.visit(this, data);
    }


    public PrimitiveType getModelConstant() {
        return Objects.requireNonNull(PrimitiveType.fromToken(getImage()), "Image doesn't denote a primitive type??");
    }


    /**
     * Constants to symbolise a primitive type when the tree context is
     * not important. I expect this may be fleshed out to be used by type
     * resolution or something.
     */
    @Experimental
    public enum PrimitiveType {
        BOOLEAN,
        CHAR,
        INT,
        BYTE,
        SHORT,
        LONG,
        DOUBLE,
        FLOAT;

        private static final Map<String, PrimitiveType> LOOKUP =
            Collections.unmodifiableMap(
                Arrays.stream(values()).collect(Collectors.toMap(
                    PrimitiveType::getToken,
                    t -> t
                ))
            );


        /**
         * Returns true if this denotes a numeric type.
         */
        public boolean isNumeric() {
            return this != BOOLEAN;
        }


        @Override
        public String toString() {
            return getToken();
        }

        /**
         * Returns the token used to represent the type in source,
         * e.g. "int" or "double".
         */
        public String getToken() {
            return name().toLowerCase(Locale.ROOT);
        }


        /**
         * Gets an enum constant from the token used to represent it in source,
         * e.g. "int" or "double".
         *
         * @param token String token
         *
         * @return A constant, or null if the string doesn't correspond
         * to a primitive type
         */
        @Nullable
        public static PrimitiveType fromToken(String token) {
            return LOOKUP.get(token);
        }
    }

}
