/**
 * BSD-style license; for more info see http://pmd.sourceforge.net/license.html
 */
package net.sourceforge.pmd.lang.java.symboltable;

import java.util.List;
import java.util.Map;

import net.sourceforge.pmd.lang.ast.Node;
import net.sourceforge.pmd.lang.java.ast.ASTLambdaExpression;
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

    public NameDeclaration addNameOccurrence(NameOccurrence occurrence) {
        JavaNameOccurrence javaOccurrence = (JavaNameOccurrence)occurrence;
        NameDeclaration decl = findVariableHere(javaOccurrence);
        if (decl != null && !javaOccurrence.isThisOrSuper()) {
            List<NameOccurrence> nameOccurrences = getVariableDeclarations().get(decl);
            nameOccurrences.add(javaOccurrence);
            Node n = javaOccurrence.getLocation();
            if (n instanceof ASTName) {
                ((ASTName) n).setNameDeclaration(decl);
            } // TODO what to do with PrimarySuffix case?
        }
        return decl;
    }

    public void addDeclaration(NameDeclaration nameDecl) {
        if (!(nameDecl instanceof VariableNameDeclaration || nameDecl instanceof ClassNameDeclaration)) {
            throw new IllegalArgumentException("A LocalScope can contain only VariableNameDeclarations or ClassNameDeclarations. "
                    + "Tried to add " + nameDecl.getClass() + "(" + nameDecl + ")");
        }
        if (nameDecl.getNode().jjtGetParent() instanceof ASTLambdaExpression
            || nameDecl.getNode().jjtGetParent().jjtGetParent().jjtGetParent() instanceof ASTLambdaExpression) {
            // don't add the variable declaration, but verify, that there is no other local variable declaration
            // with the same name - this would be a compiler error
            checkForDuplicatedNameDeclaration(nameDecl);
        } else {
            super.addDeclaration(nameDecl);
        }
    }

    public NameDeclaration findVariableHere(JavaNameOccurrence occurrence) {
        if (occurrence.isThisOrSuper() || occurrence.isMethodOrConstructorInvocation()) {
            return null;
        }
        ImageFinderFunction finder = new ImageFinderFunction(occurrence.getImage());
        Applier.apply(finder, getVariableDeclarations().keySet().iterator());
        return finder.getDecl();
    }

    public String toString() {
        return "LocalScope:" + glomNames(getVariableDeclarations().keySet());
    }
}
