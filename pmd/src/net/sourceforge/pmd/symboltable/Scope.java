/*
 * User: tom
 * Date: Oct 2, 2002
 * Time: 9:34:10 AM
 */
package net.sourceforge.pmd.symboltable;

import java.util.Iterator;

/**
 * See JLS 6.3 for a description of scopes
 */
public interface Scope {
    public Iterator getUnusedDeclarations();
    public void addDeclaration(NameDeclaration decl);
    public boolean contains(NameOccurrence occ);
    public void addOccurrence(NameOccurrence occ);
    public void setParent(Scope parent);
    public Scope getParent();
}
