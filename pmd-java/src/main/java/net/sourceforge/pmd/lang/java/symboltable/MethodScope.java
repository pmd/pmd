/**
 * BSD-style license; for more info see http://pmd.sourceforge.net/license.html
 */

package net.sourceforge.pmd.lang.java.symboltable;

import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.Set;

import net.sourceforge.pmd.lang.ast.Node;
import net.sourceforge.pmd.lang.java.ast.ASTConstructorDeclaration;
import net.sourceforge.pmd.lang.java.ast.ASTName;
import net.sourceforge.pmd.lang.symboltable.Applier;
import net.sourceforge.pmd.lang.symboltable.NameDeclaration;
import net.sourceforge.pmd.lang.symboltable.NameOccurrence;

/**
 * A Method Scope can have variable declarations and class declarations within
 * it.
 */
public class MethodScope extends AbstractJavaScope {

    private Node node;

    public MethodScope(Node node) {
        this.node = node;
    }

    public Map<VariableNameDeclaration, List<NameOccurrence>> getVariableDeclarations() {
        return getDeclarations(VariableNameDeclaration.class);
    }

    @Override
    public Set<NameDeclaration> addNameOccurrence(NameOccurrence occurrence) {
        JavaNameOccurrence javaOccurrence = (JavaNameOccurrence) occurrence;
        Set<NameDeclaration> declarations = findVariableHere(javaOccurrence);
        if (!declarations.isEmpty() && !javaOccurrence.isThisOrSuper()) {
            for (NameDeclaration decl : declarations) {
                getVariableDeclarations().get(decl).add(javaOccurrence);
                Node n = javaOccurrence.getLocation();
                if (n instanceof ASTName) {
                    ((ASTName) n).setNameDeclaration(decl);
                } // TODO what to do with PrimarySuffix case?
            }
        }
        return declarations;
    }

    @Override
    public void addDeclaration(NameDeclaration variableDecl) {
        if (!(variableDecl instanceof VariableNameDeclaration || variableDecl instanceof ClassNameDeclaration)) {
            throw new IllegalArgumentException(
                    "A MethodScope can contain only VariableNameDeclarations or ClassNameDeclarations");
        }
        super.addDeclaration(variableDecl);
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

    public String getName() {
        if (node instanceof ASTConstructorDeclaration) {
            return this.getEnclosingScope(ClassScope.class).getClassName();
        }
        return node.getChild(1).getImage();
    }

    @Override
    public String toString() {
        return "MethodScope:" + glomNames(getVariableDeclarations().keySet());
    }
}
