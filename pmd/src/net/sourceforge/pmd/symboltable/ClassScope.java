/**
 * BSD-style license; for more info see http://pmd.sourceforge.net/license.html
 */
package net.sourceforge.pmd.symboltable;

import net.sourceforge.pmd.ast.ASTName;
import net.sourceforge.pmd.ast.SimpleNode;
import net.sourceforge.pmd.util.Applier;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

public class ClassScope extends AbstractScope {

    protected Map classNames = new HashMap();
    protected Map methodNames = new HashMap();
    protected Map variableNames = new HashMap();

    // FIXME - this breaks given sufficiently nested code
    private static int anonymousInnerClassCounter = 1;
    private String className;

    public ClassScope(String className) {
        this.className = className;
        anonymousInnerClassCounter = 1;
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
        this.className = "Anonymous$" + anonymousInnerClassCounter;
        anonymousInnerClassCounter++;
    }

    public void addDeclaration(VariableNameDeclaration variableDecl) {
        if (variableNames.containsKey(variableDecl)) {
            throw new RuntimeException(variableDecl + " is already in the symbol table");
        }
        variableNames.put(variableDecl, new ArrayList());
    }

    public NameDeclaration addVariableNameOccurrence(NameOccurrence occurrence) {
        NameDeclaration decl = findVariableHere(occurrence);
        if (decl != null && occurrence.isMethodOrConstructorInvocation()) {
            List nameOccurrences = (List) methodNames.get(decl);
            if (nameOccurrences == null) {
                // TODO may be a class name: Foo.this.super();
            } else {
                nameOccurrences.add(occurrence);
                SimpleNode n = occurrence.getLocation();
                if (n instanceof ASTName) {
                    ((ASTName) n).setNameDeclaration(decl);
                } // TODO what to do with PrimarySuffix case?
            }

        } else if (decl != null && !occurrence.isThisOrSuper()) {
            List nameOccurrences = (List) variableNames.get(decl);
            if (nameOccurrences == null) {
                // TODO may be a class name
            } else {
                nameOccurrences.add(occurrence);
                SimpleNode n = occurrence.getLocation();
                if (n instanceof ASTName) {
                    ((ASTName) n).setNameDeclaration(decl);
                } // TODO what to do with PrimarySuffix case?
            }
        }
        return decl;
    }

    public Map getVariableDeclarations() {
        VariableUsageFinderFunction f = new VariableUsageFinderFunction(variableNames);
        Applier.apply(f, variableNames.keySet().iterator());
        return f.getUsed();
    }

    public Map getMethodDeclarations() {
        return methodNames;
    }

    public Map getClassDeclarations() {
        return classNames;
    }

    public ClassScope getEnclosingClassScope() {
        return this;
    }

    public String getClassName() {
        return this.className;
    }

    public void addDeclaration(MethodNameDeclaration decl) {
        methodNames.put(decl, new ArrayList());
    }

    public void addDeclaration(ClassNameDeclaration decl) {
        classNames.put(decl, new ArrayList());
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
                return (NameDeclaration) variableNames.keySet().iterator().next();
            }
            return (NameDeclaration) methodNames.keySet().iterator().next();
        }

        if (occurrence.isMethodOrConstructorInvocation()) {
            for (Iterator i = methodNames.keySet().iterator(); i.hasNext();) {
                MethodNameDeclaration mnd = (MethodNameDeclaration) i.next();
                if (mnd.getImage().equals(occurrence.getImage())) {
                    int args = occurrence.getArgumentCount();
                    if (args == mnd.getParameterCount()) {
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

        List images = new ArrayList();
        images.add(occurrence.getImage());
        if (occurrence.getImage().startsWith(className)) {
            images.add(clipClassName(occurrence.getImage()));
        }
        ImageFinderFunction finder = new ImageFinderFunction(images);
        Applier.apply(finder, variableNames.keySet().iterator());
        return finder.getDecl();
    }

    public String toString() {
        String res = "ClassScope (" + className + "): ";
        if (!classNames.isEmpty()) res += "(" + glomNames(classNames.keySet().iterator()) + ")";
        if (!methodNames.isEmpty()) {
            Iterator i = methodNames.keySet().iterator();
            while (i.hasNext()) {
                MethodNameDeclaration mnd = (MethodNameDeclaration) i.next();
                res += mnd.toString();
                int usages = ((List) methodNames.get(mnd)).size();
                res += "(begins at line " + mnd.getNode().getBeginLine() + ", " + usages + " usages)";
                res += ",";
            }
        }
        if (!variableNames.isEmpty()) res += "(" + glomNames(variableNames.keySet().iterator()) + ")";
        return res;
    }

    private String clipClassName(String in) {
        return in.substring(in.indexOf('.') + 1);
    }
}
