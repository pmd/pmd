/*
 * User: tom
 * Date: Oct 2, 2002
 * Time: 9:34:10 AM
 */
package net.sourceforge.pmd.symboltable;

import java.util.Iterator;
import java.util.Map;

/**
 * Provides methods which all scopes must implement
 *
 * See JLS 6.3 for a description of scopes
 */
public interface Scope {

    /**
     * Returns an Iterator over a collection of unused declarations that exist at this scope
     */
    public Iterator getUnusedVariableDeclarations();

    /**
     * Returns a Map (VariableNameDeclaration->List(NameOccurrence,NameOccurrence)) of declarations that
     * exist and are used at the scope
     */
    public Map getUsedVariableDeclarations();

    /**
     * Add a variable declaration to this scope
     */
    public void addDeclaration(VariableNameDeclaration decl);

    /**
     * Add a method declaration to this scope
     */
    public void addDeclaration(MethodNameDeclaration decl);

    /**
     * Tests whether or not a NameOccurrence is directly contained in the scope
     * Note that if this search is just in this scope - it doesn't go diving into any
     * contained scopes.
     */
    public boolean contains(NameOccurrence occ);

    /**
     * Adds a NameOccurrence to this scope - only call this after getting
     * a true back from contains()
     */
    public NameDeclaration addVariableNameOccurrence(NameOccurrence occ);

    /**
     * Points this scope to its parent
     */
    public void setParent(Scope parent);

    /**
     * Retrieves this scope's parent
     */
    public Scope getParent();

    /**
     * Goes searching up the tree for this scope's enclosing ClassScope
     * This is handy if you're buried down in a LocalScope and need to
     * hop up to the ClassScope to find a method name.
     */
    public Scope getEnclosingClassScope();
}
