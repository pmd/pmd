/**
 * BSD-style license; for more info see http://pmd.sourceforge.net/license.html
 */

package net.sourceforge.pmd.lang.java.ast;

import net.sourceforge.pmd.annotation.Experimental;

/**
 * A pattern (for pattern matching constructs like {@link ASTInstanceOfExpression InstanceOfExpression}).
 * This is a JDK 14 preview feature and is subject to change.
 *
 * <p>This interface will be implemented by all forms of patterns. For
 * now, only type test patterns are supported. Record deconstruction
 * patterns are in the works for JDK 15 preview.
 *
 * <p>See https://openjdk.java.net/jeps/305, https://openjdk.java.net/jeps/8235186
 *
 * <pre class="grammar">
 *
 * Pattern ::= {@link ASTTypeTestPattern TypeTestPattern}
 *
 * </pre>
 */
@Experimental
public interface ASTPattern extends JavaNode {


}
