/**
 * BSD-style license; for more info see http://pmd.sourceforge.net/license.html
 */
package net.sourceforge.pmd.symboltable;

import net.sourceforge.pmd.util.Applier;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

public class GlobalScope extends AbstractScope implements Scope {

    protected Map classes = new HashMap();

    public ClassScope getEnclosingClassScope() {
        throw new RuntimeException("getEnclosingClassScope() called on GlobalScope");
    }

    public void addDeclaration(ClassNameDeclaration classDecl) {
        classes.put(classDecl, new ArrayList());
    }

    public void addDeclaration(MethodNameDeclaration decl) {
        throw new RuntimeException("GlobalScope.addDeclaration(MethodNameDeclaration decl) called");
    }

    public void addDeclaration(VariableNameDeclaration decl) {
        throw new RuntimeException("GlobalScope.addDeclaration(VariableNameDeclaration decl) called");
    }

    public Map getClassDeclarations() {
        return classes;
    }

    public Map getVariableDeclarations() {
        throw new RuntimeException("GlobalScope.getVariableDeclarations() called");
    }

    public NameDeclaration addVariableNameOccurrence(NameOccurrence occ) {
        return null;
    }

    public String toString() {
        return "GlobalScope class names:" + super.glomNames(classes.keySet().iterator());
    }

    protected NameDeclaration findVariableHere(NameOccurrence occ) {
        ImageFinderFunction finder = new ImageFinderFunction(occ.getImage());
        Applier.apply(finder, classes.keySet().iterator());
        return finder.getDecl();
    }

}
