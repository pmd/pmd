/*
 * BSD-style license; for more info see http://pmd.sourceforge.net/license.html
 */

package net.sourceforge.pmd.lang.java.ast;

import static java.util.stream.Collectors.collectingAndThen;
import static java.util.stream.Collectors.toMap;

import java.util.Arrays;
import java.util.Collections;
import java.util.Map;


/**
 * An increment operator for {@link ASTIncrementExpression IncrementExpression}.
 *
 * <pre class="grammar">
 *
 * UnaryOp ::= "++" | "--"
 *
 * </pre>
 *
 * @see BinaryOp
 * @see AssignmentOp
 * @see UnaryOp
 */
public enum IncrementOp {
    /** "++" */
    INCREMENT("++"),
    /** "--" */
    DECREMENT("--");

    private static final Map<String, IncrementOp> LOOKUP =
        Arrays.stream(values())
              .collect(
                  collectingAndThen(
                      toMap(Object::toString, op -> op),
                      Collections::unmodifiableMap
                  )
              );

    private final String code;


    IncrementOp(String code) {
        this.code = code;
    }

    @Override
    public String toString() {
        return this.code;
    }


    // parser only for now
    static IncrementOp fromImage(String image) {
        return LOOKUP.get(image);
    }
}
