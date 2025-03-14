/**
 * BSD-style license; for more info see http://pmd.sourceforge.net/license.html
 */

package net.sourceforge.pmd.lang.apex.rule.internal;

import java.util.Arrays;
import java.util.List;
import java.util.Locale;

import net.sourceforge.pmd.lang.apex.ast.ASTDmlDeleteStatement;
import net.sourceforge.pmd.lang.apex.ast.ASTDmlInsertStatement;
import net.sourceforge.pmd.lang.apex.ast.ASTDmlMergeStatement;
import net.sourceforge.pmd.lang.apex.ast.ASTDmlUndeleteStatement;
import net.sourceforge.pmd.lang.apex.ast.ASTDmlUpdateStatement;
import net.sourceforge.pmd.lang.apex.ast.ASTDmlUpsertStatement;
import net.sourceforge.pmd.lang.apex.ast.ASTField;
import net.sourceforge.pmd.lang.apex.ast.ASTFieldDeclaration;
import net.sourceforge.pmd.lang.apex.ast.ASTMethodCallExpression;
import net.sourceforge.pmd.lang.apex.ast.ASTModifierNode;
import net.sourceforge.pmd.lang.apex.ast.ASTNewKeyValueObjectExpression;
import net.sourceforge.pmd.lang.apex.ast.ASTNewObjectExpression;
import net.sourceforge.pmd.lang.apex.ast.ASTParameter;
import net.sourceforge.pmd.lang.apex.ast.ASTReferenceExpression;
import net.sourceforge.pmd.lang.apex.ast.ASTSoqlExpression;
import net.sourceforge.pmd.lang.apex.ast.ASTSoslExpression;
import net.sourceforge.pmd.lang.apex.ast.ASTUserClass;
import net.sourceforge.pmd.lang.apex.ast.ASTVariableDeclaration;
import net.sourceforge.pmd.lang.apex.ast.ASTVariableExpression;
import net.sourceforge.pmd.lang.apex.ast.ApexNode;

/**
 * Helper methods
 *
 * @author sergey.gorbaty
 *
 */
public final class Helper {
    public static final String ANY_METHOD = "*";
    private static final String DATABASE_CLASS_NAME = "Database";

    private Helper() {
        throw new AssertionError("Can't instantiate helper classes");
    }

    public static boolean isTestMethodOrClass(final ApexNode<?> node) {
        for (final ASTModifierNode m : node.children(ASTModifierNode.class)) {
            if (m.isTest()) {
                return true;
            }
        }

        final String className = node.getDefiningType();
        return className.endsWith("Test");
    }

    public static boolean foundAnySOQLorSOSL(final ApexNode<?> node) {
        return node.descendants(ASTSoqlExpression.class).nonEmpty()
                || node.descendants(ASTSoslExpression.class).nonEmpty();
    }

    /**
     * Finds DML operations in a given node descendants' path
     *
     * @param node
     *
     * @return true if found DML operations in node descendants
     */
    public static boolean foundAnyDML(final ApexNode<?> node) {

        final List<ASTDmlUpsertStatement> dmlUpsertStatement = node.descendants(ASTDmlUpsertStatement.class).toList();
        final List<ASTDmlUpdateStatement> dmlUpdateStatement = node.descendants(ASTDmlUpdateStatement.class).toList();
        final List<ASTDmlUndeleteStatement> dmlUndeleteStatement = node.descendants(ASTDmlUndeleteStatement.class).toList();
        final List<ASTDmlMergeStatement> dmlMergeStatement = node.descendants(ASTDmlMergeStatement.class).toList();
        final List<ASTDmlInsertStatement> dmlInsertStatement = node.descendants(ASTDmlInsertStatement.class).toList();
        final List<ASTDmlDeleteStatement> dmlDeleteStatement = node.descendants(ASTDmlDeleteStatement.class).toList();

        return !dmlUpsertStatement.isEmpty() || !dmlUpdateStatement.isEmpty() || !dmlUndeleteStatement.isEmpty()
                || !dmlMergeStatement.isEmpty() || !dmlInsertStatement.isEmpty() || !dmlDeleteStatement.isEmpty();
    }

    public static boolean isMethodName(final ASTMethodCallExpression methodNode, final String className,
            final String methodName) {
        final ASTReferenceExpression reference = methodNode.firstChild(ASTReferenceExpression.class);

        return reference != null && reference.getNames().size() == 1
                && reference.getNames().get(0).equalsIgnoreCase(className)
                && (ANY_METHOD.equals(methodName) || isMethodName(methodNode, methodName));
    }

    public static boolean isMethodName(final ASTMethodCallExpression m, final String methodName) {
        return m.getMethodName().equalsIgnoreCase(methodName);
    }

    public static boolean isMethodCallChain(final ASTMethodCallExpression methodNode, final String... methodNames) {
        String methodName = methodNames[methodNames.length - 1];
        if (Helper.isMethodName(methodNode, methodName)) {
            final ASTReferenceExpression reference = methodNode.firstChild(ASTReferenceExpression.class);
            if (reference != null) {
                final ASTMethodCallExpression nestedMethod = reference
                        .firstChild(ASTMethodCallExpression.class);
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

    public static String getFQVariableName(final ASTVariableExpression variable) {
        final ASTReferenceExpression ref = variable.firstChild(ASTReferenceExpression.class);
        String objectName = "";
        if (ref != null && ref.getNames().size() == 1) {
            objectName = ref.getNames().get(0) + ".";
        }

        StringBuilder sb = new StringBuilder().append(variable.getDefiningType()).append(":").append(objectName)
                .append(variable.getImage());
        return sb.toString();
    }

    public static String getFQVariableName(final ASTVariableDeclaration variable) {
        StringBuilder sb = new StringBuilder().append(variable.getDefiningType()).append(":")
                .append(variable.getImage());
        return sb.toString();
    }

    public static String getFQVariableName(final ASTField variable) {
        StringBuilder sb = new StringBuilder()
                .append(variable.getDefiningType()).append(":")
                .append(variable.getName());
        return sb.toString();
    }

    static String getVariableType(final ASTField variable) {
        StringBuilder sb = new StringBuilder().append(variable.getDefiningType()).append(":")
                .append(variable.getName());
        return sb.toString();
    }

    public static String getFQVariableName(final ASTFieldDeclaration variable) {
        StringBuilder sb = new StringBuilder()
                .append(variable.getDefiningType()).append(":")
                .append(variable.getImage());
        return sb.toString();
    }

    public static String getFQVariableName(final ASTNewKeyValueObjectExpression variable) {
        StringBuilder sb = new StringBuilder()
                .append(variable.getDefiningType()).append(":")
                .append(variable.getType());
        return sb.toString();
    }

    public static String getFQVariableName(final ASTNewObjectExpression variable) {
        StringBuilder sb = new StringBuilder()
                .append(variable.getDefiningType()).append(":")
                .append(variable.getType());
        return sb.toString();
    }

    public static boolean isSystemLevelClass(ASTUserClass node) {
        List<String> interfaces = node.getInterfaceNames();
        return interfaces.stream().anyMatch(Helper::isAllowed);
    }

    private static boolean isAllowed(String identifier) {
        switch (identifier.toLowerCase(Locale.ROOT)) {
        case "queueable":
        case "database.batchable":
        case "installhandler":
            return true;
        default:
            break;
        }
        return false;
    }

    public static String getFQVariableName(ASTParameter p) {
        StringBuilder sb = new StringBuilder();
        sb.append(p.getDefiningType()).append(":").append(p.getImage());
        return sb.toString();
    }

    /**
     * @return true if {@code node} is an invocation of a {@code Database} method.
     */
    public static boolean isAnyDatabaseMethodCall(ASTMethodCallExpression node) {
        return isMethodName(node, DATABASE_CLASS_NAME, ANY_METHOD);
    }
}
