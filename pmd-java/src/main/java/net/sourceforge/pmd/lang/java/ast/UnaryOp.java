/**
 * BSD-style license; for more info see http://pmd.sourceforge.net/license.html
 */

package net.sourceforge.pmd.lang.java.ast;

import static java.util.stream.Collectors.collectingAndThen;
import static java.util.stream.Collectors.toMap;

import java.util.Arrays;
import java.util.Collections;
import java.util.Map;


/**
 * A unary operator for {@link ASTUnaryExpression}.
 *
 * <pre class="grammar">
 *
 * UnaryOp ::= "+" | "-" | "~" | "!"
 *
 * </pre>
 *
 * @see BinaryOp
 * @see AssignmentOp
 */
public enum UnaryOp {
    /** "+" */
    UNARY_PLUS("+"),
    /** "-" */
    UNARY_MINUS("-"),
    /** "~" */
    BITWISE_INVERSE("~"),
    /** "!" */
    BOOLEAN_NOT("!"),
    /** "++" */
    INCREMENT("++"),
    /** "--" */
    DECREMENT("--");

    private static final Map<String, UnaryOp> LOOKUP =
        Arrays.stream(values())
              .collect(
                  collectingAndThen(
                      toMap(Object::toString, op -> op),
                      Collections::unmodifiableMap
                  )
              );

    private final String code;


    UnaryOp(String code) {
        this.code = code;
    }

    @Override
    public String toString() {
        return this.code;
    }


    // parser only for now
    static UnaryOp fromImage(String image) {
        return LOOKUP.get(image);
    }
}
