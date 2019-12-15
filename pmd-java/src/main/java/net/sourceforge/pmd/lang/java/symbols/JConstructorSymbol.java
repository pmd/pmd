/*
 * BSD-style license; for more info see http://pmd.sourceforge.net/license.html
 */


package net.sourceforge.pmd.lang.java.symbols;

import net.sourceforge.pmd.lang.java.ast.ASTConstructorDeclaration;


/**
 * Represents a constructor declaration.
 *
 * @author Clément Fournier
 * @since 7.0.0
 */
public interface JConstructorSymbol extends JExecutableSymbol, BoundToNode<ASTConstructorDeclaration> {

    /** Common dummy name for constructor symbols. */
    String CTOR_NAME = "new";


    /** For constructors, this returns the special name {@value CTOR_NAME}. */
    @Override
    default String getSimpleName() {
        return CTOR_NAME;
    }
}
