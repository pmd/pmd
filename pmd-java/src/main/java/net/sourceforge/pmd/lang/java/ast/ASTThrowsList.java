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
    public Object jjtAccept(JavaParserVisitor visitor, Object data) {
        return visitor.visit(this, data);
    }


    @Override
    public <T> void jjtAccept(SideEffectingVisitor<T> visitor, T data) {
        visitor.visit(this, data);
    }

}
