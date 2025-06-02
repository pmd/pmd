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
public final class ASTEmptyDeclaration extends AbstractJavaNode
    implements ASTBodyDeclaration, ASTTopLevelDeclaration {

    ASTEmptyDeclaration(int id) {
        super(id);
    }


    @Override
    public <P, R> R acceptVisitor(JavaVisitor<? super P, ? extends R> visitor, P data) {
        return visitor.visit(this, data);
    }
}
