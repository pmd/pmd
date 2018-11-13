/**
 * BSD-style license; for more info see http://pmd.sourceforge.net/license.html
 */

package net.sourceforge.pmd.lang.java.symboltable;

import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.Set;

import net.sourceforge.pmd.lang.ast.Node;
import net.sourceforge.pmd.lang.java.ast.ASTName;
import net.sourceforge.pmd.lang.symboltable.Applier;
import net.sourceforge.pmd.lang.symboltable.NameDeclaration;
import net.sourceforge.pmd.lang.symboltable.NameOccurrence;

/**
 * A LocalScope can have variable declarations and class declarations within it.
 */
public class LocalScope extends AbstractJavaScope {

    public Map<VariableNameDeclaration, List<NameOccurrence>> getVariableDeclarations() {
        return getDeclarations(VariableNameDeclaration.class);
    }

    @Override
    public Set<NameDeclaration> addNameOccurrence(NameOccurrence occurrence) {
        JavaNameOccurrence javaOccurrence = (JavaNameOccurrence) occurrence;
        Set<NameDeclaration> declarations = findVariableHere(javaOccurrence);
        if (!declarations.isEmpty() && !javaOccurrence.isThisOrSuper()) {
            for (NameDeclaration decl : declarations) {
                List<NameOccurrence> nameOccurrences = getVariableDeclarations().get(decl);
                nameOccurrences.add(javaOccurrence);
                Node n = javaOccurrence.getLocation();
                if (n instanceof ASTName) {
                    ((ASTName) n).setNameDeclaration(decl);
                } // TODO what to do with PrimarySuffix case?
            }
        }
        return declarations;
    }

    @Override
    public void addDeclaration(NameDeclaration nameDecl) {
        if (!(nameDecl instanceof VariableNameDeclaration || nameDecl instanceof ClassNameDeclaration)) {
            throw new IllegalArgumentException(
                    "A LocalScope can contain only VariableNameDeclarations or ClassNameDeclarations. "
                            + "Tried to add " + nameDecl.getClass() + "(" + nameDecl + ")");
        }
        super.addDeclaration(nameDecl);
    }

    @Override
    public Set<NameDeclaration> findVariableHere(JavaNameOccurrence occurrence) {
        if (occurrence.isThisOrSuper() || occurrence.isMethodOrConstructorInvocation()) {
            return Collections.emptySet();
        }
        DeclarationFinderFunction finder = new DeclarationFinderFunction(occurrence);
        Applier.apply(finder, getVariableDeclarations().keySet().iterator());
        if (finder.getDecl() != null) {
            return Collections.singleton(finder.getDecl());
        }
        return Collections.emptySet();
    }

    @Override
    public String toString() {
        return "LocalScope:" + glomNames(getVariableDeclarations().keySet());
    }
}
