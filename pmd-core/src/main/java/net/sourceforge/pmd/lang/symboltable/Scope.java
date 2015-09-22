/**
 * BSD-style license; for more info see http://pmd.sourceforge.net/license.html
 */
package net.sourceforge.pmd.lang.symboltable;

import java.util.List;
import java.util.Map;
import java.util.Set;

/**
 * A scope is a region within variables and other declarations are visible.
 * Scopes can be nested and form a tree. This is expressed through "parent scopes".
 * Each scope manages its own declarations.
 *
 * @see AbstractScope AbstractScope as a base class
 * @see <a href="http://docs.oracle.com/javase/specs/jls/se7/html/jls-6.html#jls-6.3">Java Language Specification, 6.3: Scope of a Declaration</a>
 */
public interface Scope {
    /**
     * Retrieves this scope's parent
     */
    Scope getParent();

    /**
     * Points this scope to its parent
     */
    void setParent(Scope parent);

    /**
     * Helper method that goes up the parent scopes to find a scope
     * of the specified type
     * @param clazz the type of the Scope to search for
     * @return the found scope of the specified type or <code>null</code> if no
     * such scope was found.
     */
    <T extends Scope> T getEnclosingScope(Class<T> clazz);

    /**
     * Gets all the declaration with the occurrences in this scope.
     * @return map of declarations with occurrences.
     */
    Map<NameDeclaration, List<NameOccurrence>> getDeclarations();

    /**
     * Helper method to get only a specific type of name declarations.
     * The return map elemens have already been casted to the correct type.
     * This method usually returns a subset of {@link #getDeclarations()}.
     * @param clazz the type of name declarations to use
     * @return map of declarations with occurrences.
     */
    <T extends NameDeclaration> Map<T, List<NameOccurrence>> getDeclarations(Class<T> clazz);

    /**
     * Tests whether or not a {@link NameOccurrence} is directly contained in the scope.
     * This means, whether the given {@link NameOccurrence} references a declaration, that has been
     * declared within this scope.
     * Note that this search is just for this scope - it doesn't go diving into any
     * parent scopes.
     */
    boolean contains(NameOccurrence occ);

    /**
     * Adds a new declaration to this scope. Only after the declaration has been added,
     * {@link #contains(NameOccurrence)} and {@link #addNameOccurrence(NameOccurrence)} can
     * be used correctly.
     * @param declaration the declaration to add
     */
    void addDeclaration(NameDeclaration declaration);

    /**
     * Adds a {@link NameOccurrence} to this scope - only call this after getting
     * a true back from {@link #contains(NameOccurrence)}.
     * @return the {@link NameDeclaration}s that are referenced by the given {@link NameOccurrence},
     * if the {@link NameOccurrence} could be added. Otherwise an empty set is returned.
     */
    Set<NameDeclaration> addNameOccurrence(NameOccurrence occurrence);
}
