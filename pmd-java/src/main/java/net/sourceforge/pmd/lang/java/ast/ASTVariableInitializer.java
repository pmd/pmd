/**
 * BSD-style license; for more info see http://pmd.sourceforge.net/license.html
 */

package net.sourceforge.pmd.lang.java.ast;

/**
 * Marker interface for nodes that can occur as the initializer of a variable or field declaration.
 *
 * <pre class="grammar">
 *
 * VariableInitializer ::= {@link ASTExpression Expression} | {@link ASTArrayInitializer ArrayInitializer}
 *
 * </pre>
 *
 * @deprecated Array initializers behave exactly as expressions, except they're restricted to one specific
 * syntactic context. Such a syntactic distinction is not useful for analysis. This also simplifies the type
 * hierarchy. This interface can be completely substituted by {@link ASTExpression}
 */
@Deprecated
public interface ASTVariableInitializer extends JavaNode {
}
