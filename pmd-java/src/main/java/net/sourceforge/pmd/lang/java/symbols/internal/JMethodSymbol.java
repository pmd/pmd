/**
 * BSD-style license; for more info see http://pmd.sourceforge.net/license.html
 */

package net.sourceforge.pmd.lang.java.symbols.internal;

import java.util.List;

import net.sourceforge.pmd.lang.java.ast.ASTMethodDeclaration;


/**
 * Reference to a method.
 *
 * @author Clément Fournier
 * @since 7.0.0
 */
public interface JMethodSymbol extends JAccessibleDeclarationSymbol<ASTMethodDeclaration>, JTypeParameterOwnerSymbol {


    List<JLocalVariableSymbol> getFormalParameters();


    /**
     * Returns true if this declaration is declared final.
     */
    boolean isFinal();


    boolean isStrict();


    boolean isAbstract();


    boolean isVarargs();


    /**
     * Returns true if this declaration is static.
     */
    boolean isStatic();


    boolean isSynchronized();


    boolean isNative();


    boolean isDefault();

}
