/*
 * BSD-style license; for more info see http://pmd.sourceforge.net/license.html
 */

package net.sourceforge.pmd.lang.java.ast;

import net.sourceforge.pmd.annotation.Experimental;

/**
 * A pattern (for pattern matching constructs like {@link ASTInstanceOfExpression InstanceOfExpression}).
 * This is a JDK 16 feature.
 *
 * <p>This interface will be implemented by all forms of patterns. For
 * now, only type test patterns are supported. Record deconstruction
 * patterns is planned for a future JDK version.
 *
 * <pre class="grammar">
 *
 * Pattern ::= {@link ASTTypeTestPattern TypeTestPattern}
 *
 * </pre>
 * 
 * @see <a href="https://openjdk.java.net/jeps/394">JEP 394: Pattern Matching for instanceof</a>
 */
@Experimental
public interface ASTPattern extends JavaNode {

}
