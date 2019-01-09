/**
 * BSD-style license; for more info see http://pmd.sourceforge.net/license.html
 */

package net.sourceforge.pmd.lang.java.symbols.internal;

import net.sourceforge.pmd.lang.java.ast.ASTMethodDeclaration;


/**
 * Reference to a method.
 *
 * @author Clément Fournier
 * @since 7.0.0
 */
public interface JMethodSymbol extends JTypeParameterOwnerSymbol,
                                       JFormalParameterOwnerSymbol,
                                       BoundToNode<ASTMethodDeclaration> {
    // FIXME should also represent annotation methods. We need another interface
    // between ASTMethodDeclaration and ASTMethodOrConstructorDeclaration


    /** Returns true if this declaration is declared final. */
    boolean isFinal();


    boolean isStrict();


    boolean isAbstract();


    boolean isVarargs();


    /** Returns true if this declaration is static. */
    boolean isStatic();


    boolean isSynchronized();


    boolean isNative();


    boolean isDefault();

}
