/*
 * BSD-style license; for more info see http://pmd.sourceforge.net/license.html
 */


package net.sourceforge.pmd.lang.java.symbols;

import java.lang.reflect.Modifier;

import net.sourceforge.pmd.lang.java.ast.ASTMethodDeclaration;


/**
 * Reference to a method.
 *
 * @since 7.0.0
 */
public interface JMethodSymbol extends JExecutableSymbol, BoundToNode<ASTMethodDeclaration> {

    /** Returns true if this method is a default method of an interface. */
    default boolean isDefault() {
        // Default methods are public non-abstract instance methods
        // declared in an interface.
        return (getModifiers() & (Modifier.ABSTRACT | Modifier.PUBLIC | Modifier.STATIC)) == Modifier.PUBLIC
            && getEnclosingClass().isInterface();
    }

}
