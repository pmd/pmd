/**
 * BSD-style license; for more info see http://pmd.sourceforge.net/license.html
 */
package net.sourceforge.pmd.lang.java.symboltable;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import net.sourceforge.pmd.lang.ast.Node;
import net.sourceforge.pmd.lang.java.ast.ASTName;
import net.sourceforge.pmd.lang.symboltable.NameDeclaration;
import net.sourceforge.pmd.lang.symboltable.NameOccurrence;
import net.sourceforge.pmd.lang.symboltable.Scope;

/**
 * This scope represents one Java class.
 * It can have variable declarations, method declarations and inner class declarations.
 */
public class ClassScope extends AbstractJavaScope {

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

    public Map<ClassNameDeclaration, List<NameOccurrence>> getClassDeclarations() {
        return getDeclarations(ClassNameDeclaration.class);
    }

    public Map<MethodNameDeclaration, List<NameOccurrence>> getMethodDeclarations() {
        return getDeclarations(MethodNameDeclaration.class);
    }

    public Map<VariableNameDeclaration, List<NameOccurrence>> getVariableDeclarations() {
        return getDeclarations(VariableNameDeclaration.class);
    }

    public NameDeclaration addNameOccurrence(NameOccurrence occurrence) {
        JavaNameOccurrence javaOccurrence = (JavaNameOccurrence)occurrence;
        NameDeclaration decl = findVariableHere(javaOccurrence);
        if (decl != null && javaOccurrence.isMethodOrConstructorInvocation()) {
            List<NameOccurrence> nameOccurrences = getMethodDeclarations().get(decl);
            if (nameOccurrences == null) {
                // TODO may be a class name: Foo.this.super();
            } else {
                nameOccurrences.add(javaOccurrence);
                Node n = javaOccurrence.getLocation();
                if (n instanceof ASTName) {
                    ((ASTName) n).setNameDeclaration(decl);
                } // TODO what to do with PrimarySuffix case?
            }

        } else if (decl != null && !javaOccurrence.isThisOrSuper()) {
            List<NameOccurrence> nameOccurrences = getVariableDeclarations().get(decl);
            if (nameOccurrences == null) {
                // TODO may be a class name

                // search inner classes
                for (ClassNameDeclaration innerClass : getClassDeclarations().keySet()) {
                    Scope innerClassScope = innerClass.getScope();
                    if (innerClassScope.contains(javaOccurrence)) {
                        innerClassScope.addNameOccurrence(javaOccurrence);
                    }
                }
            } else {
                nameOccurrences.add(javaOccurrence);
                Node n = javaOccurrence.getLocation();
                if (n instanceof ASTName) {
                    ((ASTName) n).setNameDeclaration(decl);
                } // TODO what to do with PrimarySuffix case?
            }
        }
        return decl;
    }

    public String getClassName() {
        return this.className;
    }

    protected NameDeclaration findVariableHere(JavaNameOccurrence occurrence) {
        Map<MethodNameDeclaration, List<NameOccurrence>> methodDeclarations = getMethodDeclarations();
        Map<VariableNameDeclaration, List<NameOccurrence>> variableDeclarations = getVariableDeclarations();
        if (occurrence.isThisOrSuper() || occurrence.getImage().equals(className)) {
            if (variableDeclarations.isEmpty() && methodDeclarations.isEmpty()) {
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
            if (!variableDeclarations.isEmpty()) {
                return variableDeclarations.keySet().iterator().next();
            }
            return methodDeclarations.keySet().iterator().next();
        }

        if (occurrence.isMethodOrConstructorInvocation()) {
            for (MethodNameDeclaration mnd: methodDeclarations.keySet()) {
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
        Applier.apply(finder, variableDeclarations.keySet().iterator());
        NameDeclaration result = finder.getDecl();

        // search inner classes
        Map<ClassNameDeclaration, List<NameOccurrence>> classDeclarations = getClassDeclarations();
        if (result == null && !classDeclarations.isEmpty()) {
            for (ClassNameDeclaration innerClass : getClassDeclarations().keySet()) {
                Applier.apply(finder, innerClass.getScope().getDeclarations().keySet().iterator());
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
        Map<ClassNameDeclaration, List<NameOccurrence>> classDeclarations = getClassDeclarations();
        if (classDeclarations.isEmpty()) {
            res.append("Inner Classes ").append(glomNames(classDeclarations.keySet())).append("; ");
        }
        Map<MethodNameDeclaration, List<NameOccurrence>> methodDeclarations = getMethodDeclarations();
        if (!methodDeclarations.isEmpty()) {
            for (MethodNameDeclaration mnd: methodDeclarations.keySet()) {
                res.append(mnd.toString());
                int usages = methodDeclarations.get(mnd).size();
                res.append("(begins at line ").append(mnd.getNode().getBeginLine()).append(", ").append(usages).append(" usages)");
                res.append(", ");
            }
        }
        Map<VariableNameDeclaration, List<NameOccurrence>> variableDeclarations = getVariableDeclarations();
        if (!variableDeclarations.isEmpty()) {
            res.append("Variables ").append(glomNames(variableDeclarations.keySet()));
        }
        return res.toString();
    }

    private String clipClassName(String s) {
        return s.substring(s.indexOf('.') + 1);
    }
}
