package net.sourceforge.pmd.symboltable;

import net.sourceforge.pmd.util.Applier;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

public class SourceFileScope extends AbstractScope implements Scope  {

    protected Map classes = new HashMap();
    private String packageImage;

    public SourceFileScope(String image) {
        this.packageImage = image;
    }

    public SourceFileScope() {
        packageImage = "";
    }

    public ClassScope getEnclosingClassScope() {
        throw new RuntimeException("getEnclosingClassScope() called on SourceFileScope");
    }

    public String getPackageName() {
        return packageImage;
    }

    public SourceFileScope getEnclosingSourceFileScope() {
        return this;
    }

    public void addDeclaration(ClassNameDeclaration classDecl) {
        classes.put(classDecl, new ArrayList());
    }

    public void addDeclaration(MethodNameDeclaration decl) {
        throw new RuntimeException("SourceFileScope.addDeclaration(MethodNameDeclaration decl) called");
    }

    public void addDeclaration(VariableNameDeclaration decl) {
        throw new RuntimeException("SourceFileScope.addDeclaration(VariableNameDeclaration decl) called");
    }

    public Map getClassDeclarations() {
        return classes;
    }

    public Map getVariableDeclarations() {
        throw new RuntimeException("PackageScope.getVariableDeclarations() called");
    }

    public NameDeclaration addVariableNameOccurrence(NameOccurrence occ) {
        return null;
    }

    public String toString() {
        return "SourceFileScope class names:" + super.glomNames(classes.keySet().iterator());
    }

    protected NameDeclaration findVariableHere(NameOccurrence occ) {
        ImageFinderFunction finder = new ImageFinderFunction(occ.getImage());
        Applier.apply(finder, classes.keySet().iterator());
        return finder.getDecl();
    }

}
