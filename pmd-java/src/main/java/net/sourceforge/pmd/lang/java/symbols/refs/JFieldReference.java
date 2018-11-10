/**
 * BSD-style license; for more info see http://pmd.sourceforge.net/license.html
 */

package net.sourceforge.pmd.lang.java.symbols.refs;

import java.lang.reflect.Field;
import java.lang.reflect.Modifier;

import net.sourceforge.pmd.lang.ast.Node;
import net.sourceforge.pmd.lang.java.ast.ASTFieldDeclaration;
import net.sourceforge.pmd.lang.java.ast.ASTVariableDeclaratorId;
import net.sourceforge.pmd.lang.java.ast.AccessNode;
import net.sourceforge.pmd.lang.java.symbols.scopes.JSymbolTable;


/**
 * Reference to a field.
 *
 * @author Clément Fournier
 * @since 7.0.0
 */
public class JFieldReference extends JAccessibleReference<ASTVariableDeclaratorId> implements JVarReference {


    /**
     * Constructor for fields found through reflection.
     *
     * @param declaringScope Scope of the declaration
     * @param field          Field for which to create a reference
     */
    public JFieldReference(JSymbolTable declaringScope, Field field) {
        super(declaringScope, field.getModifiers(), field.getName());
    }


    /**
     * Constructor using the AST node.
     *
     * @param declaringScope Scope of the declaration
     * @param node           Node representing the id of the field, must be from an ASTFieldDeclaration
     */
    JFieldReference(JSymbolTable declaringScope, ASTVariableDeclaratorId node) {
        super(declaringScope, node, getModifiers(node), node.getVariableName());

    }


    public final boolean isVolatile() {
        return Modifier.isVolatile(modifiers);
    }


    public final boolean isTransient() {
        return Modifier.isTransient(modifiers);
    }


    public final boolean isStatic() {
        return Modifier.isStatic(modifiers);
    }


    public final boolean isFinal() {
        return Modifier.isFinal(modifiers);
    }


    private static int getModifiers(ASTVariableDeclaratorId node) {
        Node fieldDecl = node.jjtGetParent().jjtGetParent();
        if (fieldDecl instanceof ASTFieldDeclaration) {
            return accessNodeToModifiers((AccessNode) fieldDecl);
        } else {
            throw new IllegalArgumentException("Not a field id");
        }
    }

}
