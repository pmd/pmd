/*
 * User: tom
 * Date: Oct 2, 2002
 * Time: 9:34:10 AM
 */
package net.sourceforge.pmd.symboltable;

import java.util.Iterator;

public interface Scope {
    public Iterator getUnusedDeclarations();
    public void addDeclaration(NameDeclaration decl);
    public boolean contains(NameOccurrence occ);
    public void addOccurrence(NameOccurrence occ);
}
