package net.sourceforge.pmd.lang.java.ast;

import java.util.Arrays;
import java.util.Collections;
import java.util.Map;
import java.util.stream.Collectors;


/**
 * A unary operator for {@link ASTUnaryExpression}.
 */
public enum UnaryOp {
    UNARY_PLUS("+"),
    UNARY_MINUS("-"),
    BITWISE_INVERSE("~"),
    BOOLEAN_NOT("!");

    private static final Map<String, UnaryOp> LOOKUP = Collections.unmodifiableMap(
        Arrays.stream(values()).collect(Collectors.toMap(Object::toString, op -> op))
    );

    private final String code;


    UnaryOp(String code) {
        this.code = code;
    }


    public String toString() {
        return this.code;
    }


    // parser only for now
    static UnaryOp fromImage(String image) {
        return LOOKUP.get(image);
    }
}
