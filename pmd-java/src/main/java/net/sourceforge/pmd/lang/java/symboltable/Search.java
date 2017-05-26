/**
 * BSD-style license; for more info see http://pmd.sourceforge.net/license.html
 */

package net.sourceforge.pmd.lang.java.symboltable;

import java.util.Collections;
import java.util.HashSet;
import java.util.Set;

import net.sourceforge.pmd.lang.symboltable.NameDeclaration;
import net.sourceforge.pmd.lang.symboltable.NameOccurrence;
import net.sourceforge.pmd.lang.symboltable.Scope;

public class Search {
    private static final boolean TRACE = false;

    private NameOccurrence occ;
    private Set<NameDeclaration> declarations = new HashSet<>();

    public Search(JavaNameOccurrence occ) {
        if (TRACE) {
            System.out.println(
                    "new search for " + (occ.isMethodOrConstructorInvocation() ? "method" : "variable") + " " + occ);
        }
        this.occ = occ;
    }

    public void execute() {
        Set<NameDeclaration> found = searchUpward(occ, occ.getLocation().getScope());
        if (TRACE) {
            System.out.println("found " + found);
        }
        declarations.addAll(found);
    }

    public void execute(Scope startingScope) {
        Set<NameDeclaration> found = searchUpward(occ, startingScope);
        if (TRACE) {
            System.out.println("found " + found);
        }
        declarations.addAll(found);
    }

    public Set<NameDeclaration> getResult() {
        return declarations;
    }

    private Set<NameDeclaration> searchUpward(NameOccurrence nameOccurrence, Scope scope) {
        if (TRACE) {
            System.out.println(" checking scope " + scope + " for name occurrence " + nameOccurrence);
        }
        final boolean isInScope = scope.contains(nameOccurrence);
        if (!isInScope && scope.getParent() != null) {
            if (TRACE) {
                System.out.println(" moving up from " + scope + " to " + scope.getParent());
            }
            return searchUpward(nameOccurrence, scope.getParent());
        }
        if (isInScope) {
            if (TRACE) {
                System.out.println(" found it!");
            }
            return scope.addNameOccurrence(nameOccurrence);
        }
        return Collections.emptySet();
    }
}
