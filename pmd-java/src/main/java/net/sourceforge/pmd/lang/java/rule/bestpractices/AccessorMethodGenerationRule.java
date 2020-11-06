/**
 * BSD-style license; for more info see http://pmd.sourceforge.net/license.html
 */

package net.sourceforge.pmd.lang.java.rule.bestpractices;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import net.sourceforge.pmd.RuleContext;
import net.sourceforge.pmd.lang.java.ast.ASTCompilationUnit;
import net.sourceforge.pmd.lang.java.ast.ASTExpression;
import net.sourceforge.pmd.lang.java.ast.ASTFieldDeclaration;
import net.sourceforge.pmd.lang.java.ast.ASTLiteral;
import net.sourceforge.pmd.lang.java.ast.ASTMethodDeclaration;
import net.sourceforge.pmd.lang.java.ast.ASTName;
import net.sourceforge.pmd.lang.java.ast.ASTPrimaryExpression;
import net.sourceforge.pmd.lang.java.ast.ASTPrimarySuffix;
import net.sourceforge.pmd.lang.java.ast.ASTVariableDeclarator;
import net.sourceforge.pmd.lang.java.ast.ASTVariableInitializer;
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

    private List<String> cache = new ArrayList<>();

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

        if (node.isFinal()) {
            for (final ASTVariableDeclarator varDecl: node.findChildrenOfType(ASTVariableDeclarator.class)) {
                if (varDecl.hasInitializer()) {
                    ASTVariableInitializer varInit = varDecl.getInitializer();
                    List<ASTExpression> initExpression = varInit.findDescendantsOfType(ASTExpression.class);
                    boolean isConstantExpression = true;
                    constantCheck:
                    for (ASTExpression exp: initExpression) {
                        List<ASTPrimaryExpression> primaryExpressions = exp.findDescendantsOfType(ASTPrimaryExpression.class);
                        for (ASTPrimaryExpression expression: primaryExpressions) {
                            if (!isCompileTimeConstant(expression)) {
                                isConstantExpression = false;
                                break constantCheck;
                            }
                        }
                    }
                    if (isConstantExpression) {
                        cache.add(varDecl.getName());
                        return;
                    }
                }
            }
        }

        for (final NameOccurrence no : occurrences) {
            ClassScope usedAtScope = no.getLocation().getScope().getEnclosingScope(ClassScope.class);

            // Are we within the same class that defines the field / method?
            if (!classScope.equals(usedAtScope)) {
                addViolation(data, no.getLocation());
            }
        }
    }

    public boolean isCompileTimeConstant(ASTPrimaryExpression expressions) {
        // function call detected
        List<ASTPrimarySuffix> suffix = expressions.findDescendantsOfType(ASTPrimarySuffix.class);
        if (!suffix.isEmpty()) {
            return false;
        }

        // single node expression
        List<ASTName> nameNodes = expressions.findDescendantsOfType(ASTName.class);
        List<ASTLiteral> literalNodes = expressions.findDescendantsOfType(ASTLiteral.class);
        if (nameNodes.size() + literalNodes.size() < 2) {
            for (ASTName node: nameNodes) {
                // TODO : use the symbol table to get the declaration of the referenced var and check it
                if (!cache.contains(node.getImage())) {
                    return false;
                }
            }
            return true;
        }

        // multiple node expression
        List<ASTPrimaryExpression> subExpressions = expressions.findDescendantsOfType(ASTPrimaryExpression.class);
        for (ASTPrimaryExpression exp: subExpressions) {
            if (!isCompileTimeConstant(exp)) {
                return false;
            }
        }
        return true;
    }

    @Override
    public void end(RuleContext ctx) {
        cache.clear();
    }
}
