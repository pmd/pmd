/*
 * BSD-style license; for more info see http://pmd.sourceforge.net/license.html
 */


package net.sourceforge.pmd.lang.java.symbols;

import net.sourceforge.pmd.annotation.Experimental;
import net.sourceforge.pmd.annotation.InternalApi;
import net.sourceforge.pmd.lang.java.types.TypeSystem;


/**
 * Represents a named program element that can be referred to by simple name. Abstracts over
 * whether the declaration is in the analysed file or not, using reflection when it's not.
 *
 * <p>This type hierarchy is probably not directly relevant to users writing
 * rules. It's mostly intended to unify the representation of type resolution
 * and symbol analysis.
 *
 * @since 7.0.0
 */
@Experimental
@InternalApi
public interface JElementSymbol {

    /**
     * Gets the name with which this declaration may be referred to,
     * eg the name of the method, or the simple name of the class.
     *
     * @return the name
     */
    String getSimpleName();


    /**
     * Returns the type system that created this symbol. The symbol uses
     * this instance to create new types, for example to reflect its
     * superclass.
     */
    TypeSystem getTypeSystem();

    /**
     * Two symbols representing the same program element should be equal.
     * So eg two {@link JClassSymbol}, even if their implementation class
     * is different, should compare publicly observable properties (their
     * binary name is enough). {@code hashCode} must of course be consistent
     * with this contract.
     *
     * <p>Symbols should only be compared using this method, never with {@code ==},
     * because their unicity is not guaranteed.
     *
     * @param o Comparand
     *
     * @return True if the other is a symbol for the same program element
     */
    @Override
    boolean equals(Object o);


    // TODO access to annotations could be added to the API if we publish it

    /**
     * Dispatch to the appropriate visit method of the visitor and returns its result.
     */
    <R, P> R acceptVisitor(SymbolVisitor<R, P> visitor, P param);

}
