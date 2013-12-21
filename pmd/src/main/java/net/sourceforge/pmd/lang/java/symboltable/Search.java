/**
 * BSD-style license; for more info see http://pmd.sourceforge.net/license.html
 */
package net.sourceforge.pmd.lang.java.symboltable;

import net.sourceforge.pmd.lang.symboltable.NameDeclaration;
import net.sourceforge.pmd.lang.symboltable.NameOccurrence;
import net.sourceforge.pmd.lang.symboltable.Scope;

public class Search {
    private static final boolean TRACE = false;

    private NameOccurrence occ;
    private NameDeclaration decl;

    public Search(JavaNameOccurrence occ) {
        if (TRACE) {
            System.out.println("new search for " + (occ.isMethodOrConstructorInvocation() ? "method" : "variable") + " " + occ);
        }
        this.occ = occ;
    }

    public void execute() {
        decl = searchUpward(occ, occ.getLocation().getScope());
        if (TRACE) {
            System.out.println("found " + decl);
        }
    }

    public void execute(Scope startingScope) {
        decl = searchUpward(occ, startingScope);
        if (TRACE) {
            System.out.println("found " + decl);
        }
    }

    public NameDeclaration getResult() {
        return decl;
    }

    private NameDeclaration searchUpward(NameOccurrence nameOccurrence, Scope scope) {
        if (TRACE) {
            System.out.println(" checking scope " + scope + " for name occurrence " + nameOccurrence);
        }
        if (!scope.contains(nameOccurrence) && scope.getParent() != null) {
            if (TRACE) {
                System.out.println(" moving up from " + scope + " to " + scope.getParent());
            }
            return searchUpward(nameOccurrence, scope.getParent());
        }
        if (scope.contains(nameOccurrence)) {
            if (TRACE) {
                System.out.println(" found it!");
            }
            return scope.addNameOccurrence(nameOccurrence);
        }
        return null;
    }
}
