/*
 * BSD-style license; for more info see http://pmd.sourceforge.net/license.html
 */

package net.sourceforge.pmd.lang.java.ast;

import net.sourceforge.pmd.lang.java.ast.ASTList.ASTNonEmptyList;

/**
 * Throws clause of an {@link ASTConstructorDeclaration} or {@link ASTMethodDeclaration}.
 *
 * <pre class="grammar">
 *
 * ThrowsList ::= "throws" {@link ASTClassOrInterfaceType ClassType} ("," {@link ASTClassOrInterfaceType ClassType})*
 *
 * </pre>
 */
public final class ASTThrowsList extends ASTNonEmptyList<ASTClassOrInterfaceType> {

    ASTThrowsList(int id) {
        super(id, ASTClassOrInterfaceType.class);
    }

    @Override
    protected <P, R> R acceptVisitor(JavaVisitor<? super P, ? extends R> visitor, P data) {
        return visitor.visit(this, data);
    }
}
