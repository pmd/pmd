/**
 * BSD-style license; for more info see http://pmd.sourceforge.net/license.html
 */

package net.sourceforge.pmd.lang.java.symbols.internal;

import net.sourceforge.pmd.lang.java.ast.ASTConstructorDeclaration;


/**
 * Represents a constructor declaration.
 *
 * @author Clément Fournier
 * @since 7.0.0
 */
public interface JConstructorSymbol extends JFormalParameterOwnerSymbol, BoundToNode<ASTConstructorDeclaration> {

}
