/*
 * User: tom
 * Date: Oct 2, 2002
 * Time: 9:34:10 AM
 */
package net.sourceforge.pmd.symboltable;

import java.util.Iterator;
import java.util.Map;

/**
 * See JLS 6.3 for a description of scopes
 */
public interface Scope {
    public Iterator getUnusedDeclarations();
    public Map getUsedDeclarations();
    public void addDeclaration(NameDeclaration decl);
    public boolean contains(NameOccurrence occ);
    public NameDeclaration addOccurrence(NameOccurrence occ);
    public void setParent(Scope parent);
    public Scope getParent();
}
