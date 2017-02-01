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
import net.sourceforge.pmd.lang.apex.ast.ASTDottedExpression;
import net.sourceforge.pmd.lang.apex.ast.ASTField;
import net.sourceforge.pmd.lang.apex.ast.ASTFieldDeclaration;
import net.sourceforge.pmd.lang.apex.ast.ASTMethodCallExpression;
import net.sourceforge.pmd.lang.apex.ast.ASTModifierNode;
import net.sourceforge.pmd.lang.apex.ast.ASTNewNameValueObjectExpression;
import net.sourceforge.pmd.lang.apex.ast.ASTReferenceExpression;
import net.sourceforge.pmd.lang.apex.ast.ASTSoqlExpression;
import net.sourceforge.pmd.lang.apex.ast.ASTSoslExpression;
import net.sourceforge.pmd.lang.apex.ast.ASTVariableDeclaration;
import net.sourceforge.pmd.lang.apex.ast.ASTVariableExpression;
import net.sourceforge.pmd.lang.apex.ast.ApexNode;

import apex.jorje.data.ast.Identifier;
import apex.jorje.data.ast.TypeRef.ClassTypeRef;
import apex.jorje.semantic.ast.expression.MethodCallExpression;
import apex.jorje.semantic.ast.expression.NewNameValueObjectExpression;
import apex.jorje.semantic.ast.expression.VariableExpression;
import apex.jorje.semantic.ast.member.Field;
import apex.jorje.semantic.ast.statement.FieldDeclaration;
import apex.jorje.semantic.ast.statement.VariableDeclaration;

/**
 * Helper methods
 * 
 * @author sergey.gorbaty
 *
 */
public final class Helper {
    protected static final String ANY_METHOD = "*";

    private Helper() {
        throw new AssertionError("Can't instantiate helper classes");
    }

    static boolean isTestMethodOrClass(final ApexNode<?> node) {
        final List<ASTModifierNode> modifierNode = node.findChildrenOfType(ASTModifierNode.class);
        for (final ASTModifierNode m : modifierNode) {
            if (m.getNode().getModifiers().isTest()) {
                return true;
            }
        }

        final String className = node.getNode().getDefiningType().getApexName();
        if (className.endsWith("Test")) {
            return true;
        }

        return false;
    }

    static boolean foundAnySOQLorSOSL(final ApexNode<?> node) {
        final List<ASTSoqlExpression> dmlSoqlExpression = node.findDescendantsOfType(ASTSoqlExpression.class);
        final List<ASTSoslExpression> dmlSoslExpression = node.findDescendantsOfType(ASTSoslExpression.class);

        if (dmlSoqlExpression.isEmpty() && dmlSoslExpression.isEmpty()) {
            return false;
        }

        return true;
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

        if (dmlUpsertStatement.isEmpty() && dmlUpdateStatement.isEmpty() && dmlUndeleteStatement.isEmpty()
                && dmlMergeStatement.isEmpty() && dmlInsertStatement.isEmpty() && dmlDeleteStatement.isEmpty()) {
            return false;
        }

        return true;
    }

    static boolean isMethodName(final ASTMethodCallExpression methodNode, final String className,
            final String methodName) {
        final ASTReferenceExpression reference = methodNode.getFirstChildOfType(ASTReferenceExpression.class);
        if (reference.getNode().getJadtIdentifiers().size() == 1) {
            if (reference.getNode().getJadtIdentifiers().get(0).value.equalsIgnoreCase(className)) {
                if (methodName.equals(ANY_METHOD) || isMethodName(methodNode, methodName)) {
                    return true;
                }
            }
        }

        return false;

    }

    static boolean isMethodName(final ASTMethodCallExpression m, final String methodName) {
        return isMethodName(m.getNode(), methodName);
    }

    static boolean isMethodName(final MethodCallExpression m, final String methodName) {
        return m.getMethodName().equalsIgnoreCase(methodName);
    }

    static boolean isMethodCallChain(final ASTMethodCallExpression methodNode, final String... methodNames) {
        String methodName = methodNames[methodNames.length - 1];
        if (Helper.isMethodName(methodNode, methodName)) {
            final ASTReferenceExpression reference = methodNode.getFirstChildOfType(ASTReferenceExpression.class);
            if (reference != null) {
                final ASTDottedExpression dottedExpression = reference.getFirstChildOfType(ASTDottedExpression.class);
                if (dottedExpression != null) {
                    final ASTMethodCallExpression nestedMethod = dottedExpression
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
        }

        return false;
    }

    static String getFQVariableName(final ASTVariableExpression variable) {
        final ASTReferenceExpression ref = variable.getFirstChildOfType(ASTReferenceExpression.class);
        String objectName = "";
        if (ref != null) {
            if (ref.getNode().getJadtIdentifiers().size() == 1) {
                objectName = ref.getNode().getJadtIdentifiers().get(0).value + ".";
            }
        }

        VariableExpression n = variable.getNode();
        StringBuilder sb = new StringBuilder().append(n.getDefiningType().getApexName()).append(":").append(objectName)
                .append(n.getIdentifier().value);
        return sb.toString();
    }

    static String getFQVariableName(final ASTVariableDeclaration variable) {
        VariableDeclaration n = variable.getNode();
        StringBuilder sb = new StringBuilder().append(n.getDefiningType().getApexName()).append(":")
                .append(n.getLocalInfo().getName());
        return sb.toString();
    }

    static String getFQVariableName(final ASTField variable) {
        Field n = variable.getNode();
        StringBuilder sb = new StringBuilder().append(n.getDefiningType().getApexName()).append(":")
                .append(n.getFieldInfo().getName());
        return sb.toString();
    }

    static String getFQVariableName(final ASTFieldDeclaration variable) {
        FieldDeclaration n = variable.getNode();
        String name = "";

        try {
            java.lang.reflect.Field f = n.getClass().getDeclaredField("name");
            f.setAccessible(true);
            Identifier nameField = (Identifier) f.get(n);
            name = nameField.value;

        } catch (NoSuchFieldException | SecurityException | IllegalArgumentException | IllegalAccessException e) {
            e.printStackTrace();
        }

        StringBuilder sb = new StringBuilder().append(n.getDefiningType().getApexName()).append(":").append(name);
        return sb.toString();
    }

    static String getFQVariableName(final ASTNewNameValueObjectExpression variable) {
        NewNameValueObjectExpression n = variable.getNode();
        String objType = "";
        try {
            // no other way to get this field, Apex Jorje does not expose it
            java.lang.reflect.Field f = n.getClass().getDeclaredField("typeRef");
            f.setAccessible(true);
            ClassTypeRef hiddenField = (ClassTypeRef) f.get(n);
            objType = hiddenField.className.get(0).value;
        } catch (NoSuchFieldException | SecurityException | IllegalArgumentException | IllegalAccessException e) {
        }

        StringBuilder sb = new StringBuilder().append(n.getDefiningType().getApexName()).append(":").append(objType);
        return sb.toString();
    }

}
