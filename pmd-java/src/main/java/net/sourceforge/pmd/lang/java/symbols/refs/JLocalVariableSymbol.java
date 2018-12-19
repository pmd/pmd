/**
 * BSD-style license; for more info see http://pmd.sourceforge.net/license.html
 */

package net.sourceforge.pmd.lang.java.symbols.refs;

import net.sourceforge.pmd.lang.java.ast.ASTVariableDeclaratorId;


/**
 * Represents a local variable declaration, method or lambda parameter.
 *
 * @author Clément Fournier
 * @since 7.0.0
 */
public class JLocalVariableSymbol extends AbstractDeclarationSymbol<ASTVariableDeclaratorId> implements JValueSymbol {

    private final boolean isFinal;

    /**
     * Constructor using the AST node.
     *
     * @param node           Node representing the id of the field, must be from an ASTLocalVariableDeclaration
     */
    // cannot be built from reflection, but a node is always available
    public JLocalVariableSymbol(ASTVariableDeclaratorId node) {
        super(node, node.getVariableName());
        this.isFinal = node.isFinal();
    }


    @Override
    public boolean isFinal() {
        return isFinal;
    }
}
