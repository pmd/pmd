/**
 * BSD-style license; for more info see http://pmd.sourceforge.net/license.html
 */

package net.sourceforge.pmd.lang.java.rule.bestpractices;

import java.util.List;
import java.util.Map;

import net.sourceforge.pmd.lang.java.ast.ASTCompilationUnit;
import net.sourceforge.pmd.lang.java.ast.ASTFieldDeclaration;
import net.sourceforge.pmd.lang.java.ast.ASTMethodDeclaration;
import net.sourceforge.pmd.lang.java.ast.AbstractJavaAccessNode;
import net.sourceforge.pmd.lang.java.rule.AbstractJavaRule;
import net.sourceforge.pmd.lang.java.symboltable.AbstractJavaScope;
import net.sourceforge.pmd.lang.java.symboltable.ClassNameDeclaration;
import net.sourceforge.pmd.lang.java.symboltable.ClassScope;
import net.sourceforge.pmd.lang.java.symboltable.MethodNameDeclaration;
import net.sourceforge.pmd.lang.java.symboltable.SourceFileScope;
import net.sourceforge.pmd.lang.java.symboltable.VariableNameDeclaration;
import net.sourceforge.pmd.lang.symboltable.NameOccurrence;

public class AccessorMethodGenerationRule extends AbstractJavaRule {

    @Override
    public Object visit(final ASTCompilationUnit node, final Object data) {
        final SourceFileScope file = node.getScope().getEnclosingScope(SourceFileScope.class);
        analyzeScope(file, data);

        return data; // Stop tree navigation
    }

    private void analyzeScope(final AbstractJavaScope file, final Object data) {
        for (final ClassNameDeclaration classDecl : file.getDeclarations(ClassNameDeclaration.class).keySet()) {
            final ClassScope classScope = (ClassScope) classDecl.getScope();

            // Check fields
            for (final Map.Entry<VariableNameDeclaration, List<NameOccurrence>> varDecl : classScope.getVariableDeclarations().entrySet()) {
                final ASTFieldDeclaration field = varDecl.getKey().getNode().getFirstParentOfType(ASTFieldDeclaration.class);
                analyzeMember(field, varDecl.getValue(), classScope, data);
            }

            // Check methods
            for (final Map.Entry<MethodNameDeclaration, List<NameOccurrence>> methodDecl : classScope.getMethodDeclarations().entrySet()) {
                final ASTMethodDeclaration method = methodDecl.getKey().getNode().getFirstParentOfType(ASTMethodDeclaration.class);
                analyzeMember(method, methodDecl.getValue(), classScope, data);
            }

            // Check inner classes
            analyzeScope(classScope, data);
        }
    }

    public void analyzeMember(final AbstractJavaAccessNode node, final List<NameOccurrence> occurrences,
            final ClassScope classScope, final Object data) {
        if (!node.isPrivate()) {
            return;
        }

        for (final NameOccurrence no : occurrences) {
            ClassScope usedAtScope = no.getLocation().getScope().getEnclosingScope(ClassScope.class);

            // Are we within the same class that defines the field / method?
            if (!classScope.equals(usedAtScope)) {
                addViolation(data, no.getLocation());
            }
        }
    }
}
