/**
 * BSD-style license; for more info see http://pmd.sourceforge.net/license.html
 */

package net.sourceforge.pmd.lang.java.symbols.internal;

import java.lang.reflect.Constructor;

import net.sourceforge.pmd.lang.java.ast.ASTConstructorDeclaration;


/**
 * Represents a constructor declaration.
 *
 * @author Clément Fournier
 * @since 7.0.0
 */
public class JConstructorSymbol extends JAccessibleDeclarationSymbol<ASTConstructorDeclaration> {

    JConstructorSymbol(Constructor<?> constructor) {
        super(constructor.getModifiers(), constructor.getDeclaringClass().getSimpleName());
    }


    JConstructorSymbol(ASTConstructorDeclaration node) {
        super(node, accessNodeToModifiers(node), node.getImage());
    }
}
