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

    public void addDeclaration(MethodNameDeclaration decl) {
        throw new RuntimeException("addMethodDeclaration() called on GlobalScope");
    }

    public Iterator getUnusedVariableDeclarations() {
        return Collections.EMPTY_LIST.iterator();
    }

    public void addDeclaration(VariableNameDeclaration decl) {}

    public boolean contains(NameOccurrence occ) {
        return false;
    }

    public VariableNameDeclaration addVariableNameOccurrence(NameOccurrence occ) {
        return null;
    }

    public String toString() {
        String result = "GlobalScope:";
        for (Iterator i = variableNames.keySet().iterator(); i.hasNext();) {
            VariableNameDeclaration nameDeclaration = (VariableNameDeclaration)i.next();
            result += nameDeclaration.getImage() +",";
        }
        return result;
    }

    protected VariableNameDeclaration findVariableHere(NameOccurrence occ) {
        return null;
    }

}
