/**
 * BSD-style license; for more info see http://pmd.sourceforge.net/license.html
*/
package net.sourceforge.pmd.symboltable;

import java.util.Map;

/**
 * Provides methods which all scopes must implement
 *
 * See JLS 6.3 for a description of scopes
 */
public interface Scope {

    /**
     * Returns a Map (VariableNameDeclaration->List(NameOccurrence,NameOccurrence)) of declarations that
     * exist and are either used or not used at this scope
     */
    Map getVariableDeclarations(boolean lookingForUsed);

    /**
     * Add a variable declaration to this scope
     */
    void addDeclaration(VariableNameDeclaration decl);

    /**
     * Add a method declaration to this scope
     */
    void addDeclaration(MethodNameDeclaration decl);

    /**
     * Tests whether or not a NameOccurrence is directly contained in the scope
     * Note that if this search is just in this scope - it doesn't go diving into any
     * contained scopes.
     */
    boolean contains(NameOccurrence occ);

    /**
     * Adds a NameOccurrence to this scope - only call this after getting
     * a true back from contains()
     */
    NameDeclaration addVariableNameOccurrence(NameOccurrence occ);

    /**
     * Points this scope to its parent
     */
    void setParent(Scope parent);

    /**
     * Retrieves this scope's parent
     */
    Scope getParent();

    /**
     * Goes searching up the tree for this scope's enclosing ClassScope
     * This is handy if you're buried down in a LocalScope and need to
     * hop up to the ClassScope to find a method name.
     */
    ClassScope getEnclosingClassScope();
}
