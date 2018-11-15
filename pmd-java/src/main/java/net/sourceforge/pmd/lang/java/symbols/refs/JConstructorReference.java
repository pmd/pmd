/**
 * BSD-style license; for more info see http://pmd.sourceforge.net/license.html
 */

package net.sourceforge.pmd.lang.java.symbols.refs;

import java.lang.reflect.Constructor;

import net.sourceforge.pmd.lang.java.ast.ASTConstructorDeclaration;


/**
 * Reference to a constructor.
 *
 * @author Clément Fournier
 * @since 7.0.0
 */
public class JConstructorReference extends JAccessibleReference<ASTConstructorDeclaration> {

    JConstructorReference(Constructor<?> constructor) {
        super(constructor.getModifiers(), constructor.getDeclaringClass().getSimpleName());
    }


    JConstructorReference(ASTConstructorDeclaration node) {
        super(node, accessNodeToModifiers(node), node.getImage());
    }
}
