/**
 * <copyright>
 *  Copyright 1997-2002 InfoEther, LLC
 *  under sponsorship of the Defense Advanced Research Projects Agency
(DARPA).
 *
 *  This program is free software; you can redistribute it and/or modify
 *  it under the terms of the Cougaar Open Source License as published
by
 *  DARPA on the Cougaar Open Source Website (www.cougaar.org).
 *
 *  THE COUGAAR SOFTWARE AND ANY DERIVATIVE SUPPLIED BY LICENSOR IS
 *  PROVIDED 'AS IS' WITHOUT WARRANTIES OF ANY KIND, WHETHER EXPRESS OR
 *  IMPLIED, INCLUDING (BUT NOT LIMITED TO) ALL IMPLIED WARRANTIES OF
 *  MERCHANTABILITY AND FITNESS FOR A PARTICULAR PURPOSE, AND WITHOUT
 *  ANY WARRANTIES AS TO NON-INFRINGEMENT.  IN NO EVENT SHALL COPYRIGHT
 *  HOLDER BE LIABLE FOR ANY DIRECT, SPECIAL, INDIRECT OR CONSEQUENTIAL
 *  DAMAGES WHATSOEVER RESULTING FROM LOSS OF USE OF DATA OR PROFITS,
 *  TORTIOUS CONDUCT, ARISING OUT OF OR IN CONNECTION WITH THE USE OR
 *  PERFORMANCE OF THE COUGAAR SOFTWARE.
 * </copyright>
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
