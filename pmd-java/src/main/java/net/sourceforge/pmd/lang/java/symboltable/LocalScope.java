/**
 * BSD-style license; for more info see http://pmd.sourceforge.net/license.html
 */
package net.sourceforge.pmd.lang.java.symboltable;

import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import net.sourceforge.pmd.lang.ast.Node;
import net.sourceforge.pmd.lang.java.ast.ASTName;
import net.sourceforge.pmd.lang.symboltable.NameDeclaration;
import net.sourceforge.pmd.lang.symboltable.NameOccurrence;

/**
 * A LocalScope can have variable declarations and class declarations within it.
 */
public class LocalScope extends AbstractJavaScope {

    public Map<VariableNameDeclaration, List<NameOccurrence>> getVariableDeclarations() {
        return getDeclarations(VariableNameDeclaration.class);
    }

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

    public void addDeclaration(NameDeclaration nameDecl) {
        if (!(nameDecl instanceof VariableNameDeclaration || nameDecl instanceof ClassNameDeclaration)) {
            throw new IllegalArgumentException(
                    "A LocalScope can contain only VariableNameDeclarations or ClassNameDeclarations. "
                            + "Tried to add " + nameDecl.getClass() + "(" + nameDecl + ")");
        }
        super.addDeclaration(nameDecl);
    }

    public Set<NameDeclaration> findVariableHere(JavaNameOccurrence occurrence) {
        Set<NameDeclaration> result = new HashSet<>();
        if (occurrence.isThisOrSuper() || occurrence.isMethodOrConstructorInvocation()) {
            return result;
        }
        DeclarationFinderFunction finder = new DeclarationFinderFunction(occurrence);
        Applier.apply(finder, getVariableDeclarations().keySet().iterator());
        if (finder.getDecl() != null) {
            result.add(finder.getDecl());
        }
        return result;
    }

    public String toString() {
        return "LocalScope:" + glomNames(getVariableDeclarations().keySet());
    }
}
