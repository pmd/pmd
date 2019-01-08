/**
 * BSD-style license; for more info see http://pmd.sourceforge.net/license.html
 */

package net.sourceforge.pmd.lang.java.symbols.internal;

import java.lang.reflect.Modifier;
import java.util.Objects;

import net.sourceforge.pmd.lang.java.ast.ASTVariableDeclaratorId;


/**
 * Represents a local variable declaration, method or lambda parameter.
 * TODO do we need to split those into their own type of reference? This is e.g. done in INRIA/Spoon,
 * but for now doesn't appear to be an interesting tradeoff
 *
 * @author Clément Fournier
 * @since 7.0.0
 */
public final class JLocalVariableSymbol extends AbstractDeclarationSymbol<ASTVariableDeclaratorId> implements JValueSymbol {

    private final boolean isFinal;

    // TODO how to decide whether two of those are equal without
    // storing somehow the context where they were found? maybe it
    // should be a documented limitation

    /**
     * Constructor using the AST node.
     *
     * @param node Node representing the id of the field, must be from an ASTLocalVariableDeclaration
     */
    // cannot be built from reflection, but a node is always available
    public JLocalVariableSymbol(ASTVariableDeclaratorId node) {
        super(node, node.getVariableName());

        if (node.isField()) {
            throw new IllegalArgumentException("Fields are represented by JFieldSymbol");
        }

        this.isFinal = node.isFinal();
    }


    /** Constructor for a reflected method or constructor parameter. */
    public JLocalVariableSymbol(java.lang.reflect.Parameter reflected) {
        super(reflected.getName());
        this.isFinal = Modifier.isFinal(reflected.getModifiers());
    }

    @Override
    public boolean isFinal() {
        return isFinal;
    }


    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || getClass() != o.getClass()) {
            return false;
        }
        if (!super.equals(o)) {
            return false;
        }
        JLocalVariableSymbol that = (JLocalVariableSymbol) o;
        return isFinal == that.isFinal;
    }


    @Override
    public int hashCode() {
        return Objects.hash(super.hashCode(), isFinal);
    }
}
