/*
 * User: tom
 * Date: Oct 3, 2002
 * Time: 11:13:44 AM
 */
package net.sourceforge.pmd.symboltable;

import java.util.Iterator;
import java.util.Collections;

public class GlobalScope extends AbstractScope implements Scope {

    public Scope getEnclosingClassScope() {
        throw new RuntimeException("getEnclosingClassScope() called on GlobalScope");
    }

    public void addMethodDeclaration(MethodNameDeclaration decl) {
        throw new RuntimeException("addMethodDeclaration() called on GlobalScope");
    }

    public Iterator getUnusedDeclarations() {
        return Collections.EMPTY_LIST.iterator();
    }

    public void addVariableDeclaration(NameDeclaration decl) {}

    public boolean contains(NameOccurrence occ) {
        return false;
    }

    public NameDeclaration addOccurrence(NameOccurrence occ) {
        return null;
    }

    public String toString() {
        String result = "GlobalScope:";
        for (Iterator i = names.keySet().iterator(); i.hasNext();) {
            NameDeclaration nameDeclaration = (NameDeclaration)i.next();
            result += nameDeclaration.getImage() +",";
        }
        return result;
    }

    protected NameDeclaration findHere(NameOccurrence occ) {
        return null;
    }

}
