/**
 * BSD-style license; for more info see http://pmd.sourceforge.net/license.html
 */

package net.sourceforge.pmd.lang.apex.rule.security;

import java.util.Arrays;
import java.util.List;

import net.sourceforge.pmd.lang.apex.ast.ASTDmlDeleteStatement;
import net.sourceforge.pmd.lang.apex.ast.ASTDmlInsertStatement;
import net.sourceforge.pmd.lang.apex.ast.ASTDmlMergeStatement;
import net.sourceforge.pmd.lang.apex.ast.ASTDmlUndeleteStatement;
import net.sourceforge.pmd.lang.apex.ast.ASTDmlUpdateStatement;
import net.sourceforge.pmd.lang.apex.ast.ASTDmlUpsertStatement;
import net.sourceforge.pmd.lang.apex.ast.ASTField;
import net.sourceforge.pmd.lang.apex.ast.ASTFieldDeclaration;
import net.sourceforge.pmd.lang.apex.ast.ASTMethodCallExpression;
import net.sourceforge.pmd.lang.apex.ast.ASTNewKeyValueObjectExpression;
import net.sourceforge.pmd.lang.apex.ast.ASTParameter;
import net.sourceforge.pmd.lang.apex.ast.ASTReferenceExpression;
import net.sourceforge.pmd.lang.apex.ast.ASTSoqlExpression;
import net.sourceforge.pmd.lang.apex.ast.ASTSoslExpression;
import net.sourceforge.pmd.lang.apex.ast.ASTUserClass;
import net.sourceforge.pmd.lang.apex.ast.ASTVariableDeclaration;
import net.sourceforge.pmd.lang.apex.ast.ASTVariableExpression;
import net.sourceforge.pmd.lang.apex.ast.ApexNode;

import apex.jorje.semantic.ast.member.Parameter;

/**
 * Helper methods
 *
 * @author sergey.gorbaty
 *
 * @deprecated Use {@link net.sourceforge.pmd.lang.apex.rule.internal.Helper} instead.
 */
@Deprecated
public final class Helper {
    static final String ANY_METHOD = "*";

    private Helper() {
        throw new AssertionError("Can't instantiate helper classes");
    }

    static boolean isTestMethodOrClass(final ApexNode<?> node) {
        return net.sourceforge.pmd.lang.apex.rule.internal.Helper.isTestMethodOrClass(node);
    }

    static boolean foundAnySOQLorSOSL(final ApexNode<?> node) {
        final List<ASTSoqlExpression> dmlSoqlExpression = node.findDescendantsOfType(ASTSoqlExpression.class);
        final List<ASTSoslExpression> dmlSoslExpression = node.findDescendantsOfType(ASTSoslExpression.class);

        return !dmlSoqlExpression.isEmpty() || !dmlSoslExpression.isEmpty();
    }

    /**
     * Finds DML operations in a given node descendants' path
     *
     * @param node
     *
     * @return true if found DML operations in node descendants
     */
    static boolean foundAnyDML(final ApexNode<?> node) {

        final List<ASTDmlUpsertStatement> dmlUpsertStatement = node.findDescendantsOfType(ASTDmlUpsertStatement.class);
        final List<ASTDmlUpdateStatement> dmlUpdateStatement = node.findDescendantsOfType(ASTDmlUpdateStatement.class);
        final List<ASTDmlUndeleteStatement> dmlUndeleteStatement = node
                .findDescendantsOfType(ASTDmlUndeleteStatement.class);
        final List<ASTDmlMergeStatement> dmlMergeStatement = node.findDescendantsOfType(ASTDmlMergeStatement.class);
        final List<ASTDmlInsertStatement> dmlInsertStatement = node.findDescendantsOfType(ASTDmlInsertStatement.class);
        final List<ASTDmlDeleteStatement> dmlDeleteStatement = node.findDescendantsOfType(ASTDmlDeleteStatement.class);

        return !dmlUpsertStatement.isEmpty() || !dmlUpdateStatement.isEmpty() || !dmlUndeleteStatement.isEmpty()
                || !dmlMergeStatement.isEmpty() || !dmlInsertStatement.isEmpty() || !dmlDeleteStatement.isEmpty();
    }

    static boolean isMethodName(final ASTMethodCallExpression methodNode, final String className,
            final String methodName) {
        final ASTReferenceExpression reference = methodNode.getFirstChildOfType(ASTReferenceExpression.class);

        return reference != null && reference.getNames().size() == 1
                && reference.getNames().get(0).equalsIgnoreCase(className)
                && (methodName.equals(ANY_METHOD) || isMethodName(methodNode, methodName));
    }

    static boolean isMethodName(final ASTMethodCallExpression m, final String methodName) {
        return m.getMethodName().equalsIgnoreCase(methodName);
    }

    static boolean isMethodCallChain(final ASTMethodCallExpression methodNode, final String... methodNames) {
        String methodName = methodNames[methodNames.length - 1];
        if (Helper.isMethodName(methodNode, methodName)) {
            final ASTReferenceExpression reference = methodNode.getFirstChildOfType(ASTReferenceExpression.class);
            if (reference != null) {
                final ASTMethodCallExpression nestedMethod = reference
                        .getFirstChildOfType(ASTMethodCallExpression.class);
                if (nestedMethod != null) {
                    String[] newMethodNames = Arrays.copyOf(methodNames, methodNames.length - 1);
                    return isMethodCallChain(nestedMethod, newMethodNames);
                } else {
                    String[] newClassName = Arrays.copyOf(methodNames, methodNames.length - 1);
                    if (newClassName.length == 1) {
                        return Helper.isMethodName(methodNode, newClassName[0], methodName);
                    }
                }
            }
        }

        return false;
    }

    static String getFQVariableName(final ASTVariableExpression variable) {
        return net.sourceforge.pmd.lang.apex.rule.internal.Helper.getFQVariableName(variable);
    }

    static String getFQVariableName(final ASTVariableDeclaration variable) {
        return net.sourceforge.pmd.lang.apex.rule.internal.Helper.getFQVariableName(variable);
    }

    static String getFQVariableName(final ASTField variable) {
        return net.sourceforge.pmd.lang.apex.rule.internal.Helper.getFQVariableName(variable);
    }

    static String getVariableType(final ASTField variable) {
        StringBuilder sb = new StringBuilder().append(variable.getDefiningType()).append(":")
                .append(variable.getName());
        return sb.toString();
    }

    static String getFQVariableName(final ASTFieldDeclaration variable) {
        return net.sourceforge.pmd.lang.apex.rule.internal.Helper.getFQVariableName(variable);
    }

    static String getFQVariableName(final ASTNewKeyValueObjectExpression variable) {
        return net.sourceforge.pmd.lang.apex.rule.internal.Helper.getFQVariableName(variable);
    }

    static boolean isSystemLevelClass(ASTUserClass node) {
        return net.sourceforge.pmd.lang.apex.rule.internal.Helper.isSystemLevelClass(node);
    }

    @Deprecated
    public static String getFQVariableName(Parameter p) {
        StringBuilder sb = new StringBuilder();
        sb.append(p.getDefiningType()).append(":").append(p.getName().getValue());
        return sb.toString();
    }

    static String getFQVariableName(ASTParameter p) {
        return net.sourceforge.pmd.lang.apex.rule.internal.Helper.getFQVariableName(p);
    }

}
