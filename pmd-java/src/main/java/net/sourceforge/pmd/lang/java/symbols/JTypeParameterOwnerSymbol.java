/*
 * BSD-style license; for more info see http://pmd.sourceforge.net/license.html
 */


package net.sourceforge.pmd.lang.java.symbols;

import static java.util.stream.Collectors.toMap;

import java.util.List;

import org.pcollections.HashTreePMap;
import org.pcollections.PMap;


/**
 * Represents a declaration that can declare type parameters,
 * {@literal i.e.} {@link JClassSymbol} or {@link JMethodSymbol}.
 *
 * @author Clément Fournier
 * @since 7.0.0
 */
public interface JTypeParameterOwnerSymbol extends JAccessibleElementSymbol {

    List<JTypeParameterSymbol> getTypeParameters();


    default int getTypeParameterCount() {
        return getTypeParameters().size();
    }


    /**
     * Returns the type parameters that are in scope in this declaration.
     * This is used to share type param instances, to ease mapping reflected
     * types to type params.
     */
    default PMap<String, JTypeParameterSymbol> getLexicalScope() {
        JTypeParameterOwnerSymbol encl = getEnclosingTypeParameterOwner();
        PMap<String, JTypeParameterSymbol> enclScope = encl != null ? encl.getLexicalScope() : HashTreePMap.empty();

        if (getTypeParameterCount() == 0) {
            return enclScope;
        } else {
            return
                enclScope.plusAll(
                    getTypeParameters()
                        .stream()
                        .collect(toMap(JTypeDeclSymbol::getSimpleName, p -> p))
                );
        }
    }


    /**
     * Returns the {@link JClassSymbol#getEnclosingMethod() enclosing method} or
     * the {@link #getEnclosingClass() enclosing class}, in that order
     * of priority.
     */
    default JTypeParameterOwnerSymbol getEnclosingTypeParameterOwner() {
        return getEnclosingClass();
    }
}
