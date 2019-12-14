/*
 * BSD-style license; for more info see http://pmd.sourceforge.net/license.html
 */

package net.sourceforge.pmd.lang.java.ast;

/**
 * An empty declaration (useless). This is kept separate from {@link ASTStatement}
 * because they don't occur in the same syntactic contexts.
 *
 * <pre class="grammar">
 *
 * EmptyDeclaration ::= ";"
 *
 * </pre>
 */
public final class ASTEmptyDeclaration extends AbstractJavaNode {

    ASTEmptyDeclaration(int id) {
        super(id);
    }

    ASTEmptyDeclaration(JavaParser p, int id) {
        super(p, id);
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
