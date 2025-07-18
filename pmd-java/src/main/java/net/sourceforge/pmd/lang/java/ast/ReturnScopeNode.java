/*
 * BSD-style license; for more info see http://pmd.sourceforge.net/license.html
 */

package net.sourceforge.pmd.lang.java.ast;

import org.checkerframework.checker.nullness.qual.Nullable;

/**
 * Marker interface for those nodes that can be the target of a return statement.
 * These delimit scopes for the control flow. For instance the control flow
 * in a lambda expression is distinct from that of the block it appears in: returns
 * and throws in the lambda and in the parent block do not jump to the same place.
 *
 *
 * <pre class="grammar">
 *
 * ReturnScopeNode ::= {@link ASTExecutableDeclaration ExecutableDeclaration}
 *                   | {@link ASTInitializer Initializer}
 *                   | {@link ASTLambdaExpression LambdaExpression}
 *                   | {@link ASTCompactConstructorDeclaration CompactConstructorDeclaration}
 *
 * </pre>
 *
 * @since 7.14.0
 */
public interface ReturnScopeNode extends JavaNode {

    @Nullable
    ASTBlock getBody();
}
