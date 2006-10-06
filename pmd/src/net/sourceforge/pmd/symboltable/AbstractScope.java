/**
 * BSD-style license; for more info see http://pmd.sourceforge.net/license.html
 */
package net.sourceforge.pmd.symboltable;

import java.util.Iterator;
import java.util.Map;

public abstract class AbstractScope implements Scope {

    private Scope parent;

    public Map getClassDeclarations() {
        throw new RuntimeException("AbstractScope.getClassDeclarations() was invoked.  That shouldn't happen... bug.");
    }

    public MethodScope getEnclosingMethodScope() {
        return parent.getEnclosingMethodScope();
    }

    public ClassScope getEnclosingClassScope() {
        return parent.getEnclosingClassScope();
    }

    public SourceFileScope getEnclosingSourceFileScope() {
        return parent.getEnclosingSourceFileScope();
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

    public void addDeclaration(ClassNameDeclaration classDecl) {
        parent.addDeclaration(classDecl);
    }

    public boolean contains(NameOccurrence occurrence) {
        return findVariableHere(occurrence) != null;
    }

    protected abstract NameDeclaration findVariableHere(NameOccurrence occurrence);

    protected String glomNames(Iterator i) {
        StringBuffer result = new StringBuffer();
        while (i.hasNext()) {
            result.append(i.next().toString());
            result.append(',');
        }
        return result.length() == 0 ? "" : result.toString().substring(0, result.length() - 1);
    }

}
