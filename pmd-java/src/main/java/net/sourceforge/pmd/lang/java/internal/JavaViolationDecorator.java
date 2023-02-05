/*
 * BSD-style license; for more info see http://pmd.sourceforge.net/license.html
 */

package net.sourceforge.pmd.lang.java.internal;


import java.util.Map;
import java.util.stream.Collectors;

import org.checkerframework.checker.nullness.qual.NonNull;
import org.checkerframework.checker.nullness.qual.Nullable;

import net.sourceforge.pmd.RuleViolation;
import net.sourceforge.pmd.lang.ast.Node;
import net.sourceforge.pmd.lang.java.ast.ASTAnyTypeDeclaration;
import net.sourceforge.pmd.lang.java.ast.ASTBodyDeclaration;
import net.sourceforge.pmd.lang.java.ast.ASTExpression;
import net.sourceforge.pmd.lang.java.ast.ASTFieldDeclaration;
import net.sourceforge.pmd.lang.java.ast.ASTFormalParameter;
import net.sourceforge.pmd.lang.java.ast.ASTInitializer;
import net.sourceforge.pmd.lang.java.ast.ASTLocalVariableDeclaration;
import net.sourceforge.pmd.lang.java.ast.ASTMethodOrConstructorDeclaration;
import net.sourceforge.pmd.lang.java.ast.ASTVariableDeclarator;
import net.sourceforge.pmd.lang.java.ast.ASTVariableDeclaratorId;
import net.sourceforge.pmd.lang.java.ast.JavaNode;
import net.sourceforge.pmd.reporting.ViolationDecorator;
import net.sourceforge.pmd.util.IteratorUtil;

final class JavaViolationDecorator implements ViolationDecorator {

    static final ViolationDecorator INSTANCE = new JavaViolationDecorator();

    @Override
    public void decorate(Node violationNode, Map<String, String> additionalInfo) {
        JavaNode javaNode = (JavaNode) violationNode;

        setIfNonNull(RuleViolation.VARIABLE_NAME, getVariableNameIfExists(javaNode), additionalInfo);
        setIfNonNull(RuleViolation.METHOD_NAME, getMethodName(javaNode), additionalInfo);
        setIfNonNull(RuleViolation.CLASS_NAME, getClassName(javaNode), additionalInfo);
        setIfNonNull(RuleViolation.PACKAGE_NAME, javaNode.getRoot().getPackageName(), additionalInfo);
    }

    private @Nullable String getClassName(JavaNode javaNode) {
        ASTAnyTypeDeclaration enclosing = null;
        if (javaNode instanceof ASTAnyTypeDeclaration) {
            enclosing = (ASTAnyTypeDeclaration) javaNode;
        }
        if (enclosing == null) {
            enclosing = javaNode.getEnclosingType();
        }
        if (enclosing == null) {
            enclosing = javaNode.getRoot().getTypeDeclarations().first(ASTAnyTypeDeclaration::isPublic);
        }
        if (enclosing == null) {
            enclosing = javaNode.getRoot().getTypeDeclarations().first();
        }
        if (enclosing != null) {
            return enclosing.getSimpleName();
        }
        return null;
    }

    private void setIfNonNull(String key, String value, Map<String, String> additionalInfo) {
        if (value != null) {
            additionalInfo.put(key, value);
        }
    }

    private static @Nullable String getMethodName(@NonNull JavaNode javaNode) {
        @Nullable ASTBodyDeclaration enclosingDecl =
            javaNode.ancestorsOrSelf()
                    .filterIs(ASTBodyDeclaration.class)
                    .first();

        if (enclosingDecl instanceof ASTMethodOrConstructorDeclaration) {
            return ((ASTMethodOrConstructorDeclaration) enclosingDecl).getName();
        } else if (enclosingDecl instanceof ASTInitializer) {
            return ((ASTInitializer) enclosingDecl).isStatic() ? "<clinit>" : "<init>";
        }
        return null;
    }

    private static String getVariableNames(Iterable<ASTVariableDeclaratorId> iterable) {
        return IteratorUtil.toStream(iterable.iterator())
                           .map(ASTVariableDeclaratorId::getName)
                           .collect(Collectors.joining(", "));
    }

    private static @Nullable String getVariableNameIfExists(JavaNode node) {
        if (node instanceof ASTFieldDeclaration) {
            return getVariableNames((ASTFieldDeclaration) node);
        } else if (node instanceof ASTLocalVariableDeclaration) {
            return getVariableNames((ASTLocalVariableDeclaration) node);
        } else if (node instanceof ASTVariableDeclarator) {
            return ((ASTVariableDeclarator) node).getVarId().getName();
        } else if (node instanceof ASTVariableDeclaratorId) {
            return ((ASTVariableDeclaratorId) node).getName();
        } else if (node instanceof ASTFormalParameter) {
            return getVariableNameIfExists(node.firstChild(ASTVariableDeclaratorId.class));
        } else if (node instanceof ASTExpression) {
            return getVariableNameIfExists(node.getParent());
        }
        return null;
    }
}
