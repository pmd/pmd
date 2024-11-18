/*
 * BSD-style license; for more info see http://pmd.sourceforge.net/license.html
 */

package net.sourceforge.pmd.lang.java.ast;

import org.checkerframework.checker.nullness.qual.NonNull;

import net.sourceforge.pmd.annotation.Experimental;

/**
 *
 * <pre class="grammar">
 * ImplicitClassDeclaration ::= {@linkplain ASTClassBody ClassBody}
 *
 * ClassBody ::= {@linkplain ASTFieldDeclaration FieldDeclaration}*
 *               {@linkplain ASTMethodDeclaration MethodDeclaration}
 *               {@linkplain ASTBodyDeclaration BodyDeclaration}*
 * </pre>
 *
 * @see <a href="https://openjdk.org/jeps/477">JEP 477: Implicitly Declared Classes and Instance Main Methods (Third Preview)</a> (Java 23)
 */
@Experimental("Implicitly Declared Classes and Instance Main Methods is a Java 22 / Java 23 Preview feature")
public final class ASTImplicitClassDeclaration extends AbstractTypeDeclaration {
    ASTImplicitClassDeclaration(int id) {
        super(id);
    }

    @Override
    protected <P, R> R acceptVisitor(JavaVisitor<? super P, ? extends R> visitor, P data) {
        return visitor.visit(this, data);
    }

    @Override
    public @NonNull String getSimpleName() {
        return "";
    }
}
