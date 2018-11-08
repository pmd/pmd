/**
 * BSD-style license; for more info see http://pmd.sourceforge.net/license.html
 */

package net.sourceforge.pmd.lang.java.symbols.refs;

import java.lang.reflect.Modifier;

import net.sourceforge.pmd.lang.java.ast.ASTVariableDeclaratorId;
import net.sourceforge.pmd.lang.java.symbols.scopes.JScope;


/**
 * Reference to a local variable, method or lambda parameter.
 *
 * @author Clément Fournier
 * @since 7.0.0
 */
public class JLocalVarReference extends AbstractCodeReference<ASTVariableDeclaratorId> implements JVarReference {

    // cannot be built from reflection, but a node is always available


    /**
     * Constructor using the AST node.
     *
     * @param declaringScope Scope of the declaration
     * @param node           Node representing the id of the field, must be from an ASTLocalVariableDeclaration
     */
    public JLocalVarReference(JScope declaringScope, ASTVariableDeclaratorId node) {
        super(declaringScope, node, node.isFinal() ? Modifier.FINAL : 0, node.getVariableName());
    }


    @Override
    public boolean isFinal() {
        return Modifier.isFinal(modifiers);
    }
}
