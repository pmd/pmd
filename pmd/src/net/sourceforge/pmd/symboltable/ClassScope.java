/*
 * User: tom
 * Date: Oct 3, 2002
 * Time: 11:08:37 AM
 */
package net.sourceforge.pmd.symboltable;

import java.util.Iterator;
import java.util.Collections;

public class ClassScope extends AbstractScope implements Scope {

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

}
