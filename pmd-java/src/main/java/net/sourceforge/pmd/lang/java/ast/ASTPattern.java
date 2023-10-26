/*
 * BSD-style license; for more info see http://pmd.sourceforge.net/license.html
 */

package net.sourceforge.pmd.lang.java.ast;

import net.sourceforge.pmd.annotation.Experimental;

/**
 * A pattern for pattern matching constructs like {@link ASTInfixExpression InstanceOfExpression}
 * or within a {@link ASTSwitchLabel}). This is a JDK 16 feature.
 *
 * The {@link ASTRecordPattern} is a JDK 21 feature.
 *
 * <p>This interface is implemented by all forms of patterns.
 *
 * <pre class="grammar">
 *
 * Pattern ::=   {@linkplain ASTTypePattern TypePattern} | {@linkplain ASTRecordPattern RecordPattern}
 *
 * </pre>
 * 
 * @see <a href="https://openjdk.org/jeps/394">JEP 394: Pattern Matching for instanceof</a> (Java 16)
 * @see <a href="https://openjdk.org/jeps/405">JEP 405: Record Patterns (Preview)</a> (Java 19)
 * @see <a href="https://openjdk.org/jeps/432">JEP 432: Record Patterns (Second Preview)</a> (Java 20)
 * @see <a href="https://openjdk.org/jeps/440">JEP 440: Record Patterns</a> (Java 21)
 */
public interface ASTPattern extends JavaNode {

    /**
     * Returns the number of parenthesis levels around this pattern.
     * If this method returns 0, then no parentheses are present.
     * @deprecated Parenthesized patterns are only possible with Java 20 Preview and are removed with Java 21.
     */
    @Experimental
    @Deprecated
    int getParenthesisDepth();
}
