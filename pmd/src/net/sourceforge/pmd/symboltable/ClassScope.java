/*
 * User: tom
 * Date: Oct 3, 2002
 * Time: 11:08:37 AM
 */
package net.sourceforge.pmd.symboltable;

import java.util.*;

public class ClassScope extends AbstractScope {

    private String className;

    public ClassScope(String className) {
        this.className = className;
    }

    public Scope getEnclosingClassScope() {
        return this;
    }

    public void addDeclaration(MethodNameDeclaration decl) {
        if (methodNames.containsKey(decl)) {
            return;
            //throw new RuntimeException("Method " + decl + " is already in the symbol table");
        }
        methodNames.put(decl, new ArrayList());
    }

    protected VariableNameDeclaration findVariableHere(NameOccurrence occurrence) {
        if (occurrence.isThisOrSuper() || occurrence.getImage().equals(className)) {
            if (variableNames.isEmpty()) {
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
            return (VariableNameDeclaration)variableNames.keySet().iterator().next();
        }

        for (Iterator i = variableNames.keySet().iterator(); i.hasNext();) {
            VariableNameDeclaration decl = (VariableNameDeclaration)i.next();
            if (decl.getImage().equals(occurrence.getImage()) || (className + "." + decl.getImage()).equals(occurrence.getImage())) {
                return decl;
            }
        }
        return null;
    }

    public String toString() {
        return "ClassScope:" + super.glomNames();
    }

}
