/**
 * BSD-style license; for more info see http://pmd.sourceforge.net/license.html
 */
package net.sourceforge.pmd.lang.java.symboltable;

import java.util.List;
import java.util.Map;

import net.sourceforge.pmd.lang.ast.Node;
import net.sourceforge.pmd.lang.java.ast.ASTConstructorDeclaration;
import net.sourceforge.pmd.lang.java.ast.ASTName;
import net.sourceforge.pmd.lang.symboltable.NameDeclaration;
import net.sourceforge.pmd.lang.symboltable.NameOccurrence;

/**
 * A Method Scope can have variable declarations and class declarations within it.
 */
public class MethodScope extends AbstractJavaScope {

    private Node node;

    public MethodScope(Node node) {
        this.node = node;
    }

    public Map<VariableNameDeclaration, List<NameOccurrence>> getVariableDeclarations() {
        return getDeclarations(VariableNameDeclaration.class);
    }

    public NameDeclaration addNameOccurrence(NameOccurrence occurrence) {
        JavaNameOccurrence javaOccurrence = (JavaNameOccurrence)occurrence;
        NameDeclaration decl = findVariableHere(javaOccurrence);
        if (decl != null && !javaOccurrence.isThisOrSuper()) {
            getVariableDeclarations().get(decl).add(javaOccurrence);
            Node n = javaOccurrence.getLocation();
            if (n instanceof ASTName) {
                ((ASTName) n).setNameDeclaration(decl);
            } // TODO what to do with PrimarySuffix case?
        }
        return decl;
    }

    public void addDeclaration(NameDeclaration variableDecl) {
        if (!(variableDecl instanceof VariableNameDeclaration || variableDecl instanceof ClassNameDeclaration)) {
            throw new IllegalArgumentException("A MethodScope can contain only VariableNameDeclarations or ClassNameDeclarations");
        }
        super.addDeclaration(variableDecl);
    }

    public NameDeclaration findVariableHere(JavaNameOccurrence occurrence) {
        if (occurrence.isThisOrSuper() || occurrence.isMethodOrConstructorInvocation()) {
            return null;
        }
        ImageFinderFunction finder = new ImageFinderFunction(occurrence.getImage());
        Applier.apply(finder, getVariableDeclarations().keySet().iterator());
        return finder.getDecl();
    }

    public String getName() {
        if (node instanceof ASTConstructorDeclaration) {
            return this.getEnclosingScope(ClassScope.class).getClassName();
        }
        return node.jjtGetChild(1).getImage();
    }

    public String toString() {
        return "MethodScope:" + glomNames(getVariableDeclarations().keySet());
    }
}
