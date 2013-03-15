/**
 * BSD-style license; for more info see http://pmd.sourceforge.net/license.html
 */
package net.sourceforge.pmd.lang.java.symboltable;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import net.sourceforge.pmd.lang.ast.Node;
import net.sourceforge.pmd.lang.java.ast.ASTName;

public class ClassScope extends AbstractScope {

    protected Map<ClassNameDeclaration, List<NameOccurrence>> classNames = new HashMap<ClassNameDeclaration, List<NameOccurrence>>();
    protected Map<MethodNameDeclaration, List<NameOccurrence>> methodNames = new HashMap<MethodNameDeclaration, List<NameOccurrence>>();
    protected Map<VariableNameDeclaration, List<NameOccurrence>> variableNames = new HashMap<VariableNameDeclaration, List<NameOccurrence>>();

    // FIXME - this breaks given sufficiently nested code
    private static ThreadLocal<Integer> anonymousInnerClassCounter = new ThreadLocal<Integer>() {
        protected Integer initialValue() { return Integer.valueOf(1); }
    };

    private String className;

    public ClassScope(String className) {
        this.className = className;
        anonymousInnerClassCounter.set(Integer.valueOf(1));
    }

    /**
     * This is only for anonymous inner classes
     * <p/>
     * FIXME - should have name like Foo$1, not Anonymous$1
     * to get this working right, the parent scope needs
     * to be passed in when instantiating a ClassScope
     */
    public ClassScope() {
        //this.className = getParent().getEnclosingClassScope().getClassName() + "$" + String.valueOf(anonymousInnerClassCounter);
        int v = anonymousInnerClassCounter.get().intValue();
        this.className = "Anonymous$" + v;
        anonymousInnerClassCounter.set(v + 1);
    }

    public void addDeclaration(VariableNameDeclaration variableDecl) {
        if (variableNames.containsKey(variableDecl)) {
            throw new RuntimeException(variableDecl + " is already in the symbol table");
        }
        variableNames.put(variableDecl, new ArrayList<NameOccurrence>());
    }

    public NameDeclaration addVariableNameOccurrence(NameOccurrence occurrence) {
        NameDeclaration decl = findVariableHere(occurrence);
        if (decl != null && occurrence.isMethodOrConstructorInvocation()) {
            List<NameOccurrence> nameOccurrences = methodNames.get(decl);
            if (nameOccurrences == null) {
                // TODO may be a class name: Foo.this.super();
            } else {
                nameOccurrences.add(occurrence);
                Node n = occurrence.getLocation();
                if (n instanceof ASTName) {
                    ((ASTName) n).setNameDeclaration(decl);
                } // TODO what to do with PrimarySuffix case?
            }

        } else if (decl != null && !occurrence.isThisOrSuper()) {
            List<NameOccurrence> nameOccurrences = variableNames.get(decl);
            if (nameOccurrences == null) {
                // TODO may be a class name

                // search inner classes
                for (ClassNameDeclaration innerClass : classNames.keySet()) {
                    Scope innerClassScope = innerClass.getScope();
                    if (innerClassScope.contains(occurrence)) {
                        innerClassScope.addVariableNameOccurrence(occurrence);
                    }
                }
            } else {
                nameOccurrences.add(occurrence);
                Node n = occurrence.getLocation();
                if (n instanceof ASTName) {
                    ((ASTName) n).setNameDeclaration(decl);
                } // TODO what to do with PrimarySuffix case?
            }
        }
        return decl;
    }

    public Map<VariableNameDeclaration, List<NameOccurrence>> getVariableDeclarations() {
        VariableUsageFinderFunction f = new VariableUsageFinderFunction(variableNames);
        Applier.apply(f, variableNames.keySet().iterator());
        return f.getUsed();
    }

    public Map<MethodNameDeclaration, List<NameOccurrence>> getMethodDeclarations() {
        return methodNames;
    }

    public Map<ClassNameDeclaration, List<NameOccurrence>> getClassDeclarations() {
        return classNames;
    }

    public ClassScope getEnclosingClassScope() {
        return this;
    }

    public String getClassName() {
        return this.className;
    }

    public void addDeclaration(MethodNameDeclaration decl) {
        methodNames.put(decl, new ArrayList<NameOccurrence>());
    }

    public void addDeclaration(ClassNameDeclaration decl) {
        classNames.put(decl, new ArrayList<NameOccurrence>());
    }

    protected NameDeclaration findVariableHere(NameOccurrence occurrence) {
        if (occurrence.isThisOrSuper() || occurrence.getImage().equals(className)) {
            if (variableNames.isEmpty() && methodNames.isEmpty()) {
                // this could happen if you do this:
                // public class Foo {
                //  private String x = super.toString();
                // }
                return null;
            }
            // return any name declaration, since all we really want is to get the scope
            // for example, if there's a
            // public class Foo {
            //  private static final int X = 2;
            //  private int y = Foo.X;
            // }
            // we'll look up Foo just to get a handle to the class scope
            // and then we'll look up X.
            if (!variableNames.isEmpty()) {
                return variableNames.keySet().iterator().next();
            }
            return methodNames.keySet().iterator().next();
        }

        if (occurrence.isMethodOrConstructorInvocation()) {
            for (MethodNameDeclaration mnd: methodNames.keySet()) {
                if (mnd.getImage().equals(occurrence.getImage())) {
                    int args = occurrence.getArgumentCount();
                    if (args == mnd.getParameterCount() || (mnd.isVarargs() && args >= mnd.getParameterCount() - 1)) {
                        // FIXME if several methods have the same name
                        // and parameter count, only one will get caught here
                        // we need to make some attempt at type lookup and discrimination
                        // or, failing that, mark this as a usage of all those methods
                        return mnd;
                    }
                }
            }
            return null;
        }

        List<String> images = new ArrayList<String>();
        images.add(occurrence.getImage());
        if (occurrence.getImage().startsWith(className)) {
            images.add(clipClassName(occurrence.getImage()));
        }
        ImageFinderFunction finder = new ImageFinderFunction(images);
        Applier.apply(finder, variableNames.keySet().iterator());
        NameDeclaration result = finder.getDecl();

        // search inner classes
        if (result == null && !classNames.isEmpty()) {
            for (ClassNameDeclaration innerClass : classNames.keySet()) {
                Applier.apply(finder, innerClass.getScope().getVariableDeclarations().keySet().iterator());
                result = finder.getDecl();
                if (result != null) {
                    break;
                }
            }
        }
        return result;
    }

    public String toString() {
        StringBuilder res = new StringBuilder("ClassScope (").append(className).append("): ");
        if (!classNames.isEmpty()) {
            res.append("Inner Classes ").append(glomNames(classNames.keySet())).append("; ");
        }
        if (!methodNames.isEmpty()) {
            for (MethodNameDeclaration mnd: methodNames.keySet()) {
                res.append(mnd.toString());
                int usages = methodNames.get(mnd).size();
                res.append("(begins at line ").append(mnd.getNode().getBeginLine()).append(", ").append(usages).append(" usages)");
                res.append(", ");
            }
        }
        if (!variableNames.isEmpty()) {
            res.append("Variables ").append(glomNames(variableNames.keySet()));
        }
        return res.toString();
    }

    private String clipClassName(String s) {
        return s.substring(s.indexOf('.') + 1);
    }
}
