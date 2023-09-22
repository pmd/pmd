/*
 * BSD-style license; for more info see http://pmd.sourceforge.net/license.html
 */


package net.sourceforge.pmd.lang.java.symbols;

import org.checkerframework.checker.nullness.qual.NonNull;
import org.checkerframework.checker.nullness.qual.Nullable;

import net.sourceforge.pmd.annotation.Experimental;
import net.sourceforge.pmd.annotation.InternalApi;
import net.sourceforge.pmd.lang.java.ast.ASTMethodDeclaration;
import net.sourceforge.pmd.lang.java.ast.JavaNode;
import net.sourceforge.pmd.lang.java.types.JTypeMirror;
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
     * Returns true if the simple name of this symbol is the same as the given
     * name.
     *
     * @param name Simple name
     *
     * @throws NullPointerException If the parameter is null
     */
    default boolean nameEquals(@NonNull String name) {
        // implicit null check of the parameter
        return name.equals(getSimpleName());
    }


    /**
     * Returns the type system that created this symbol. The symbol uses
     * this instance to create new types, for example to reflect its
     * superclass.
     */
    TypeSystem getTypeSystem();


    /**
     * Returns true if this symbol is a placeholder, created to fill-in
     * an unresolved reference. Depending on the type of this symbol,
     * this may be:
     * <ul>
     * <li>An unresolved class (more details on {@link JTypeDeclSymbol#isUnresolved()})
     * <li>An unresolved field
     * <li>An unresolved method or constructor. Note that we cheat and
     * represent them only with the constant {@link TypeSystem#UNRESOLVED_METHOD TypeSystem.UNRESOLVED_METHOD.getSymbol()},
     * which may not match its usage site in either name, formal parameters,
     * location, etc.
     * </ul>
     *
     * <p>We try to recover some information about the missing
     * symbol from the references we found, currently this includes
     * only the number of type parameters of an unresolved class.
     *
     * <p>Rules should care about unresolved symbols to avoid false
     * positives or logic errors. The equivalent for {@linkplain JTypeMirror types}
     * is {@link TypeSystem#UNKNOWN}.
     *
     * <p>The following symbols are never unresolved, because they are
     * lexically scoped:
     * <ul>
     * <li>{@linkplain JTypeParameterSymbol type parameters}
     * <li>{@linkplain JLocalVariableSymbol local variables}
     * <li>{@linkplain JFormalParamSymbol formal parameters}
     * <li>local classes, anonymous classes
     * </ul>
     */
    default boolean isUnresolved() {
        return false;
    }


    /**
     * Returns the node that declares this symbol. Eg for {@link JMethodSymbol},
     * it's an {@link ASTMethodDeclaration}. Will only return non-null
     * if the symbol is declared in the file currently being analysed.
     */
    default @Nullable JavaNode tryGetNode() {
        return null;
    }

    /**
     * Two symbols representing the same program element should be equal.
     * So eg two {@link JClassSymbol}, even if their implementation class
     * is different, should compare publicly observable properties (their
     * binary name is enough). {@code #hashCode()} must of course be consistent
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


    /**
     * Dispatch to the appropriate visit method of the visitor and returns its result.
     */
    <R, P> R acceptVisitor(SymbolVisitor<R, P> visitor, P param);

}
