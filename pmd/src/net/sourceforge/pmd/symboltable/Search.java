/*
 * User: tom
 * Date: Oct 21, 2002
 * Time: 10:51:34 AM
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
                System.out.println("moving up fm " + getClsName(scope.getClass()) + " to " + getClsName(scope.getParent().getClass()));
            return searchUpward(nameOccurrence, scope.getParent());
        }
        if (scope.contains(nameOccurrence)) {
            return scope.addVariableNameOccurrence(nameOccurrence);
        }
        return null;
    }

    private String getClsName(Class cls) {
        String fullName = cls.getName();
        int lastDot = fullName.lastIndexOf('.');
        return fullName.substring(lastDot + 1);
    }
}
