/**
 * BSD-style license; for more info see http://pmd.sourceforge.net/license.html
*/
package net.sourceforge.pmd.symboltable;

public class Search {
    private static final boolean TRACE = false;

    private NameOccurrence occ;
    private NameDeclaration decl;

    public Search(NameOccurrence occ) {
        if (TRACE)
            System.out.println("new search for " + occ);
        this.occ = occ;
    }

    public void execute() {
        decl = searchUpward(occ, occ.getScope());
        if (TRACE)
            System.out.println("found " + decl);
    }

    public void execute(Scope startingScope) {
        decl = searchUpward(occ, startingScope);
        if (TRACE)
            System.out.println("found " + decl);
    }

    public NameDeclaration getResult() {
        return decl;
    }

    private NameDeclaration searchUpward(NameOccurrence nameOccurrence, Scope scope) {
        if (!scope.contains(nameOccurrence) && scope.getParent() != null) {
            if (TRACE)
                System.out.println("moving up fm " + scope + " to " + scope.getParent());
            return searchUpward(nameOccurrence, scope.getParent());
        }
        if (scope.contains(nameOccurrence)) {
            return scope.addVariableNameOccurrence(nameOccurrence);
        }
        return null;
    }
}
