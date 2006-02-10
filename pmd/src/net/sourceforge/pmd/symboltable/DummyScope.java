package net.sourceforge.pmd.symboltable;

import java.util.HashMap;
import java.util.Map;

/**
 * Implementation of Scope for source types that are simpler than java sources.
 * It implements the methods only when necessary not to break at runtime
 * when Violations are handled.
 *
 * @author pieter_van_raemdonck - Application Engineers NV/SA - www.ae.be
 */
public class DummyScope implements Scope {
    private Map emptyMap = new HashMap();

    private Scope parent;

    public Map getVariableDeclarations() {
        return emptyMap;
    }

    public Map getClassDeclarations() {
        return emptyMap;
    }

    public void addDeclaration(ClassNameDeclaration decl) {
    }

    public void addDeclaration(VariableNameDeclaration decl) {
    }

    public void addDeclaration(MethodNameDeclaration decl) {
    }

    public boolean contains(NameOccurrence occ) {
        return false;
    }

    public NameDeclaration addVariableNameOccurrence(NameOccurrence occ) {
        return null;
    }

    public void setParent(Scope parent) {
        this.parent = parent;
    }

    public Scope getParent() {
        return parent;
    }

    public ClassScope getEnclosingClassScope() {
        return new ClassScope();
    }

    public SourceFileScope getEnclosingSourceFileScope() {
        return new SourceFileScope();
    }

    public MethodScope getEnclosingMethodScope() {
        return null;
    }

}
