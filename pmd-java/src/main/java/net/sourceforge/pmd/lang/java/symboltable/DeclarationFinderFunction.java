/**
 * BSD-style license; for more info see http://pmd.sourceforge.net/license.html
 */

package net.sourceforge.pmd.lang.java.symboltable;

import net.sourceforge.pmd.lang.java.ast.ASTMethodReference;
import net.sourceforge.pmd.lang.symboltable.NameDeclaration;
import net.sourceforge.pmd.lang.symboltable.NameOccurrence;
import net.sourceforge.pmd.util.SearchFunction;

public class DeclarationFinderFunction implements SearchFunction<NameDeclaration> {

    private NameOccurrence occurrence;
    private NameDeclaration decl;

    public DeclarationFinderFunction(NameOccurrence occurrence) {
        this.occurrence = occurrence;
    }

    @Override
    public boolean applyTo(NameDeclaration nameDeclaration) {
        // do not match method references
        if (occurrence.getLocation() instanceof ASTMethodReference) {
            return false;
        }

        if (isDeclaredBefore(nameDeclaration) && isSameName(nameDeclaration)) {
            decl = nameDeclaration;
            return false;
        }
        return true;
    }

    private boolean isDeclaredBefore(NameDeclaration nameDeclaration) {
        if (nameDeclaration.getNode() != null && occurrence.getLocation() != null) {
            return nameDeclaration.getNode().getBeginLine() <= occurrence.getLocation().getBeginLine();
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
