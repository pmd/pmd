/*
 * User: tom
 * Date: Oct 3, 2002
 * Time: 11:13:44 AM
 */
package net.sourceforge.pmd.symboltable;

import java.util.Iterator;
import java.util.Collections;
import java.util.Map;

public class GlobalScope extends AbstractScope implements Scope {

    public Scope getEnclosingClassScope() {
        throw new RuntimeException("getEnclosingClassScope() called on GlobalScope");
    }

    public void addDeclaration(MethodNameDeclaration decl) {
        throw new RuntimeException("addMethodDeclaration() called on GlobalScope");
    }

    public Map getUnusedVariableDeclarations() {
        return Collections.EMPTY_MAP;
    }

    public void addDeclaration(VariableNameDeclaration decl) {}

    public boolean contains(NameOccurrence occ) {
        return false;
    }

    public NameDeclaration addVariableNameOccurrence(NameOccurrence occ) {
        return null;
    }

    public String toString() {
        String result = "GlobalScope:";
        for (Iterator i = variableNames.keySet().iterator(); i.hasNext();) {
            NameDeclaration nameDeclaration = (NameDeclaration)i.next();
            result += nameDeclaration.getImage() +",";
        }
        return result;
    }

    protected NameDeclaration findVariableHere(NameOccurrence occ) {
        return null;
    }

}
