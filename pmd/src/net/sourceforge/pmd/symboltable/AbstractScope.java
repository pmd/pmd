/**
 * BSD-style license; for more info see http://pmd.sourceforge.net/license.html
*/
package net.sourceforge.pmd.symboltable;

import java.util.Iterator;
import java.util.Map;

public abstract class AbstractScope implements Scope {

    private Scope parent;

    public Map getClassDeclarations() {
        throw new RuntimeException("Hm, AbstractScope.getClassDeclarations() was invoked.  That shouldn't happen... bug.");
    }

    public ClassScope getEnclosingClassScope() {
        return parent.getEnclosingClassScope();
    }

    public void setParent(Scope parent) {
        this.parent = parent;
    }

    public Scope getParent() {
        return parent;
    }

    public void addDeclaration(MethodNameDeclaration methodDecl) {
        parent.addDeclaration(methodDecl);
    }

    public void addDeclaration(ClassNameDeclaration methodDecl) {
        parent.addDeclaration(methodDecl);
    }

    public boolean contains(NameOccurrence occurrence) {
        return findVariableHere(occurrence) != null;
    }

    protected abstract NameDeclaration findVariableHere(NameOccurrence occurrence);

    protected String glomNames(Iterator i) {
        StringBuffer result = new StringBuffer();
        while (i.hasNext()) {
            result.append(i.next().toString());
            result.append(",");
        }
        return result.toString();
    }

}
