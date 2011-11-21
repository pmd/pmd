/**
 * BSD-style license; for more info see http://pmd.sourceforge.net/license.html
 */
package net.sourceforge.pmd.lang.java.symboltable;

import java.util.List;
import java.util.Map;
import java.util.Set;

public abstract class AbstractScope implements Scope {

    private Scope parent;

    public Map<ClassNameDeclaration, List<NameOccurrence>> getClassDeclarations() {
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

    protected <T> String glomNames(Set<T> s) {
    	StringBuilder result = new StringBuilder();
        for (T t: s) {
            result.append(t.toString());
            result.append(',');
        }
        return result.length() == 0 ? "" : result.toString().substring(0, result.length() - 1);
    }

}
