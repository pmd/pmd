/*
 * BSD-style license; for more info see http://pmd.sourceforge.net/license.html
 */

package net.sourceforge.pmd.lang.java.ast;

import java.util.Collections;
import java.util.List;

import org.checkerframework.checker.nullness.qual.Nullable;

import net.sourceforge.pmd.lang.java.symbols.JTypeParameterOwnerSymbol;

/**
 * @author Clément Fournier
 */
public interface TypeParamOwnerNode extends SymbolDeclaratorNode {

    @Override
    JTypeParameterOwnerSymbol getSymbol();

    /**
     * Returns the type parameter declaration of this node, or null if
     * there is none.
     */
    @Nullable
    default ASTTypeParameters getTypeParameters() {
        return getFirstChildOfType(ASTTypeParameters.class);
    }


    default List<ASTTypeParameter> getTypeParameterList() {
        ASTTypeParameters parameters = getTypeParameters();
        if (parameters == null) {
            return Collections.emptyList();
        }

        return parameters.asList();
    }


}
