/*
 * BSD-style license; for more info see http://pmd.sourceforge.net/license.html
 */

package net.sourceforge.pmd.lang.java.ast;

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
 * Pattern ::=   {@linkplain ASTTypePattern TypePattern}
 *           | {@linkplain ASTRecordPattern RecordPattern}
 *           | {@linkplain ASTUnnamedPattern UnnamedPattern}
 *
 * </pre>
 * 
 * @see <a href="https://openjdk.org/jeps/394">JEP 394: Pattern Matching for instanceof</a> (Java 16)
 * @see <a href="https://openjdk.org/jeps/440">JEP 440: Record Patterns</a> (Java 21)
 */
public interface ASTPattern extends TypeNode {
}
