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

    public void addVariableDeclaration(VariableNameDeclaration decl) {}

    public boolean contains(NameOccurrence occ) {
        return false;
    }

    public VariableNameDeclaration addOccurrence(NameOccurrence occ) {
        return null;
    }

    public String toString() {
        String result = "GlobalScope:";
        for (Iterator i = names.keySet().iterator(); i.hasNext();) {
            VariableNameDeclaration nameDeclaration = (VariableNameDeclaration)i.next();
            result += nameDeclaration.getImage() +",";
        }
        return result;
    }

    protected VariableNameDeclaration findHere(NameOccurrence occ) {
        return null;
    }

}
