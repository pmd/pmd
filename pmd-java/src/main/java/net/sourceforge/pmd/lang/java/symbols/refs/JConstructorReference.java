/**
 * BSD-style license; for more info see http://pmd.sourceforge.net/license.html
 */

package net.sourceforge.pmd.lang.java.symbols.refs;

import java.lang.reflect.Constructor;

import net.sourceforge.pmd.lang.java.ast.ASTConstructorDeclaration;
import net.sourceforge.pmd.lang.java.symbols.scopes.JScope;


/**
 * Reference to a constructor.
 *
 * @author Clément Fournier
 * @since 7.0.0
 */
public class JConstructorReference extends JAccessibleReference<ASTConstructorDeclaration> {

    JConstructorReference(JScope declaringScope, Constructor<?> constructor) {
        super(declaringScope, constructor.getModifiers(), constructor.getDeclaringClass().getSimpleName());
    }


    JConstructorReference(JScope declaringScope, ASTConstructorDeclaration node) {
        super(declaringScope, node, accessNodeToModifiers(node), node.getImage());
    }
}
