/**
 * BSD-style license; for more info see http://pmd.sourceforge.net/license.html
 */

package net.sourceforge.pmd.lang.java.symbols.internal;

import net.sourceforge.pmd.lang.ast.Node;


/**
 * Represents declarations having access myModifiers common to {@link JFieldSymbol},
 * {@link JClassSymbol}, {@link JMethodSymbol}, and {@link JConstructorSymbol}.
 *
 * @author Clément Fournier
 * @since 7.0.0
 */
public interface JAccessibleDeclarationSymbol<N extends Node> extends JDeclarationSymbol<N> {

    // TODO #905 is an enormous hole is this architecture, because
    // one cannot build a JClassSymbol from an anonymous class because
    // it's not an ASTAnyTypeDeclaration

    /**
     * Returns the class that directly encloses this declaration.
     * This is equivalent to {@link Class#getEnclosingClass()}.
     * Returns null if this is a top-level type declaration.
     *
     * This is necessarily an already resolved symbol, because
     * 1. if it's obtained from reflection, then the enclosing class is available, in all likelyhood
     * 2. if it's obtained from an AST, then the enclosing class is in the same source file so we can
     * know about ita
     */
    JClassSymbol getEnclosingClass();


    boolean isPublic();


    boolean isPrivate();


    boolean isProtected();


    boolean isPackagePrivate();
}
