/**
 * BSD-style license; for more info see http://pmd.sourceforge.net/license.html
 */

package net.sourceforge.pmd.lang.java.symbols.refs;

import net.sourceforge.pmd.lang.java.ast.ASTVariableDeclaratorId;
import net.sourceforge.pmd.lang.java.symbols.scopes.JSymbolTable;


/**
 * Reference to a local variable, method or lambda parameter.
 *
 * @author Clément Fournier
 * @since 7.0.0
 */
public class JLocalVarReference extends AbstractCodeReference<ASTVariableDeclaratorId> implements JVarReference {

    // cannot be built from reflection, but a node is always available
    private final boolean isFinal;

    /**
     * Constructor using the AST node.
     *
     * @param declaringScope Scope of the declaration
     * @param node           Node representing the id of the field, must be from an ASTLocalVariableDeclaration
     */
    public JLocalVarReference(JSymbolTable declaringScope, ASTVariableDeclaratorId node) {
        super(declaringScope, node, node.getVariableName());
        this.isFinal = node.isFinal();
    }


    @Override
    public boolean isFinal() {
        return isFinal;
    }
}
