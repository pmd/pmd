/**
 * BSD-style license; for more info see http://pmd.sourceforge.net/license.html
 */
package net.sourceforge.pmd.lang.java.symboltable;

import net.sourceforge.pmd.lang.symboltable.NameDeclaration;
import net.sourceforge.pmd.lang.symboltable.NameOccurrence;
import net.sourceforge.pmd.util.UnaryFunction;

public class DeclarationFinderFunction implements UnaryFunction<NameDeclaration> {

    private NameOccurrence occurrence;
    private NameDeclaration decl;

    public DeclarationFinderFunction(NameOccurrence occurrence) {
        this.occurrence = occurrence;
    }

    public void applyTo(NameDeclaration nameDeclaration) {
        if (isDeclaredBefore(nameDeclaration) && isSameName(nameDeclaration)) {
            decl = nameDeclaration;
        }
    }

    private boolean isDeclaredBefore(NameDeclaration nameDeclaration) {
        if (nameDeclaration.getNode() != null && occurrence.getLocation() != null) {
            return nameDeclaration.getNode().getBeginLine() <=
                    occurrence.getLocation().getBeginLine();
        }

        return true;
    }

    private boolean isSameName(NameDeclaration nameDeclaration) {
        return occurrence.getImage().equals(nameDeclaration.getName());
    }

    public NameDeclaration getDecl() {
        return this.decl;
    }
}
