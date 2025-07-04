/*
 * BSD-style license; for more info see http://pmd.sourceforge.net/license.html
 */

package net.sourceforge.pmd.lang.java.ast;

import org.checkerframework.checker.nullness.qual.NonNull;

/**
 * A class declaration added by the compiler implicitly in a compact compilation unit (see JEP 512, Java 25).
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
 * @see <a href="https://openjdk.org/jeps/495">JEP 495: Simple Source Files and Instance Main Methods (Fourth Preview)</a> (Java 24)
 * @see <a href="https://openjdk.org/jeps/512">JEP 512: Compact Source Files and Instance Main Methods</a> (Java 25)
 * @since 7.16.0
 */
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
