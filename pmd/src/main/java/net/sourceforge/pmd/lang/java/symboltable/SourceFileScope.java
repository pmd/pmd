package net.sourceforge.pmd.lang.java.symboltable;


import java.util.ArrayList;
import java.util.List;
import java.util.HashMap;
import java.util.Map;

public class SourceFileScope extends AbstractScope {

    protected Map<ClassNameDeclaration, List<NameOccurrence>> classNames = new HashMap<ClassNameDeclaration, List<NameOccurrence>>();
    private String packageImage;

    public SourceFileScope() {
        this("");
    }

    public SourceFileScope(String image) {
        this.packageImage = image;
    }

    public ClassScope getEnclosingClassScope() {
        throw new RuntimeException("getEnclosingClassScope() called on SourceFileScope");
    }

    public MethodScope getEnclosingMethodScope() {
        throw new RuntimeException("getEnclosingMethodScope() called on SourceFileScope");
    }

    public String getPackageName() {
        return packageImage;
    }

    public SourceFileScope getEnclosingSourceFileScope() {
        return this;
    }

    public void addDeclaration(ClassNameDeclaration classDecl) {
        classNames.put(classDecl, new ArrayList<NameOccurrence>());
    }

    public void addDeclaration(MethodNameDeclaration decl) {
        throw new RuntimeException("SourceFileScope.addDeclaration(MethodNameDeclaration decl) called");
    }

    public void addDeclaration(VariableNameDeclaration decl) {
        throw new RuntimeException("SourceFileScope.addDeclaration(VariableNameDeclaration decl) called");
    }

    public Map<ClassNameDeclaration, List<NameOccurrence>> getClassDeclarations() {
        return classNames;
    }

    public Map<VariableNameDeclaration, List<NameOccurrence>> getVariableDeclarations() {
        throw new RuntimeException("PackageScope.getVariableDeclarations() called");
    }

    public NameDeclaration addVariableNameOccurrence(NameOccurrence occ) {
        return null;
    }

    public String toString() {
        return "SourceFileScope: " + glomNames(classNames.keySet());
    }

    protected NameDeclaration findVariableHere(NameOccurrence occ) {
        ImageFinderFunction finder = new ImageFinderFunction(occ.getImage());
        Applier.apply(finder, classNames.keySet().iterator());
        return finder.getDecl();
    }

}
