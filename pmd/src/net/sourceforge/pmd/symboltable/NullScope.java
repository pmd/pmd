/*
 * User: tom
 * Date: Oct 3, 2002
 * Time: 2:41:04 PM
 */
package net.sourceforge.pmd.symboltable;

import java.util.Iterator;
import java.util.Collections;

public class NullScope implements Scope {
    public Iterator getUnusedDeclarations() {
        return Collections.EMPTY_LIST.iterator();
    }

    public void addDeclaration(NameDeclaration decl) {
    }

    public boolean contains(NameOccurrence occ) {
        return false;
    }

    public void addOccurrence(NameOccurrence occ) {
    }

    public void setParent(Scope parent) {
    }

    public Scope getParent() {
        return new NullScope();
    }
}
