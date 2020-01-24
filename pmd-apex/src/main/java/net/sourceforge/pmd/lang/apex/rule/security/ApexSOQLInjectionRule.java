/**
 * BSD-style license; for more info see http://pmd.sourceforge.net/license.html
 */

package net.sourceforge.pmd.lang.apex.rule.security;

import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Set;
import java.util.regex.Pattern;

import net.sourceforge.pmd.lang.apex.ast.ASTAssignmentExpression;
import net.sourceforge.pmd.lang.apex.ast.ASTBinaryExpression;
import net.sourceforge.pmd.lang.apex.ast.ASTFieldDeclaration;
import net.sourceforge.pmd.lang.apex.ast.ASTLiteralExpression;
import net.sourceforge.pmd.lang.apex.ast.ASTMethod;
import net.sourceforge.pmd.lang.apex.ast.ASTMethodCallExpression;
import net.sourceforge.pmd.lang.apex.ast.ASTParameter;
import net.sourceforge.pmd.lang.apex.ast.ASTStandardCondition;
import net.sourceforge.pmd.lang.apex.ast.ASTUserClass;
import net.sourceforge.pmd.lang.apex.ast.ASTVariableDeclaration;
import net.sourceforge.pmd.lang.apex.ast.ASTVariableExpression;
import net.sourceforge.pmd.lang.apex.ast.AbstractApexNode;
import net.sourceforge.pmd.lang.apex.rule.AbstractApexRule;
import net.sourceforge.pmd.lang.apex.rule.internal.Helper;

/**
 * Detects if variables in Database.query(variable) is escaped with
 * String.escapeSingleQuotes
 *
 * @author sergey.gorbaty
 *
 */
public class ApexSOQLInjectionRule extends AbstractApexRule {
    private static final String DOUBLE = "double";
    private static final String LONG = "long";
    private static final String DECIMAL = "decimal";
    private static final String BOOLEAN = "boolean";
    private static final String ID = "id";
    private static final String INTEGER = "integer";
    private static final String JOIN = "join";
    private static final String ESCAPE_SINGLE_QUOTES = "escapeSingleQuotes";
    private static final String STRING = "String";
    private static final String DATABASE = "Database";
    private static final String QUERY = "query";
    private static final Pattern SELECT_PATTERN = Pattern.compile("^select[\\s]+?.*?$", Pattern.CASE_INSENSITIVE);
    private final Set<String> safeVariables = new HashSet<>();
    private final Map<String, Boolean> selectContainingVariables = new HashMap<>();

    public ApexSOQLInjectionRule() {
        setProperty(CODECLIMATE_CATEGORIES, "Security");
        setProperty(CODECLIMATE_REMEDIATION_MULTIPLIER, 100);
        setProperty(CODECLIMATE_BLOCK_HIGHLIGHTING, false);
    }

    @Override
    public Object visit(ASTUserClass node, Object data) {

        if (Helper.isTestMethodOrClass(node) || Helper.isSystemLevelClass(node)) {
            return data; // stops all the rules
        }

        final List<ASTMethod> methodExpr = node.findDescendantsOfType(ASTMethod.class);
        for (ASTMethod m : methodExpr) {
            findSafeVariablesInSignature(m);
        }

        final List<ASTFieldDeclaration> fieldExpr = node.findDescendantsOfType(ASTFieldDeclaration.class);
        for (ASTFieldDeclaration a : fieldExpr) {
            findSanitizedVariables(a);
            findSelectContainingVariables(a);
        }

        // String foo = String.escapeSignleQuotes(...);
        final List<ASTVariableDeclaration> variableDecl = node.findDescendantsOfType(ASTVariableDeclaration.class);
        for (ASTVariableDeclaration a : variableDecl) {
            findSanitizedVariables(a);
            findSelectContainingVariables(a);
        }

        // baz = String.escapeSignleQuotes(...);
        final List<ASTAssignmentExpression> assignmentCalls = node.findDescendantsOfType(ASTAssignmentExpression.class);
        for (ASTAssignmentExpression a : assignmentCalls) {
            findSanitizedVariables(a);
            findSelectContainingVariables(a);
        }

        // Database.query(...) check
        final List<ASTMethodCallExpression> potentialDbQueryCalls = node
                .findDescendantsOfType(ASTMethodCallExpression.class);

        for (ASTMethodCallExpression m : potentialDbQueryCalls) {
            if (!Helper.isTestMethodOrClass(m) && Helper.isMethodName(m, DATABASE, QUERY)) {
                reportStrings(m, data);
                reportVariables(m, data);
            }
        }

        safeVariables.clear();
        selectContainingVariables.clear();

        return data;
    }

    private void findSafeVariablesInSignature(ASTMethod m) {
        for (ASTParameter p : m.findChildrenOfType(ASTParameter.class)) {
            switch (p.getType().toLowerCase(Locale.ROOT)) {
            case ID:
            case INTEGER:
            case BOOLEAN:
            case DECIMAL:
            case LONG:
            case DOUBLE:
                safeVariables.add(Helper.getFQVariableName(p));
                break;
            default:
                break;
            }

        }

    }

    private void findSanitizedVariables(AbstractApexNode<?> node) {
        final ASTVariableExpression left = node.getFirstChildOfType(ASTVariableExpression.class);
        final ASTLiteralExpression literal = node.getFirstChildOfType(ASTLiteralExpression.class);
        final ASTMethodCallExpression right = node.getFirstChildOfType(ASTMethodCallExpression.class);

        // look for String a = 'b';
        if (literal != null) {
            if (left != null) {
                if (literal.isInteger() || literal.isBoolean() || literal.isDouble()) {
                    safeVariables.add(Helper.getFQVariableName(left));
                }

                if (literal.isString()) {
                    if (SELECT_PATTERN.matcher(literal.getImage()).matches()) {
                        selectContainingVariables.put(Helper.getFQVariableName(left), Boolean.TRUE);
                    } else {
                        safeVariables.add(Helper.getFQVariableName(left));
                    }
                }
            }
        }

        // look for String a = String.escapeSingleQuotes(foo);
        if (right != null) {
            if (Helper.isMethodName(right, STRING, ESCAPE_SINGLE_QUOTES)) {
                if (left != null) {
                    safeVariables.add(Helper.getFQVariableName(left));
                }
            }
        }

        if (node instanceof ASTVariableDeclaration) {
            switch (((ASTVariableDeclaration) node).getType().toLowerCase(Locale.ROOT)) {
            case INTEGER:
            case ID:
            case BOOLEAN:
            case DECIMAL:
            case LONG:
            case DOUBLE:
                safeVariables.add(Helper.getFQVariableName(left));
                break;
            default:
                break;
            }
        }
    }

    private void findSelectContainingVariables(AbstractApexNode<?> node) {
        final ASTVariableExpression left = node.getFirstChildOfType(ASTVariableExpression.class);
        final ASTBinaryExpression right = node.getFirstChildOfType(ASTBinaryExpression.class);
        if (left != null && right != null) {
            recursivelyCheckForSelect(left, right);
        }
    }

    private void recursivelyCheckForSelect(final ASTVariableExpression var, final ASTBinaryExpression node) {
        final ASTBinaryExpression right = node.getFirstChildOfType(ASTBinaryExpression.class);
        if (right != null) {
            recursivelyCheckForSelect(var, right);
        }

        final ASTVariableExpression concatenatedVar = node.getFirstChildOfType(ASTVariableExpression.class);
        boolean isSafeVariable = false;

        if (concatenatedVar != null) {
            if (safeVariables.contains(Helper.getFQVariableName(concatenatedVar))) {
                isSafeVariable = true;
            }
        }

        final ASTMethodCallExpression methodCall = node.getFirstChildOfType(ASTMethodCallExpression.class);
        if (methodCall != null) {
            if (Helper.isMethodName(methodCall, STRING, ESCAPE_SINGLE_QUOTES)) {
                isSafeVariable = true;
            }
        }

        final ASTLiteralExpression literal = node.getFirstChildOfType(ASTLiteralExpression.class);
        if (literal != null) {
            if (literal.isString()) {
                if (SELECT_PATTERN.matcher(literal.getImage()).matches()) {
                    if (!isSafeVariable) {
                        // select literal + other unsafe vars
                        selectContainingVariables.put(Helper.getFQVariableName(var), Boolean.FALSE);
                    } else {
                        safeVariables.add(Helper.getFQVariableName(var));
                    }
                }
            }
        } else {
            if (!isSafeVariable) {
                selectContainingVariables.put(Helper.getFQVariableName(var), Boolean.FALSE);
            }
        }

    }

    private void reportStrings(ASTMethodCallExpression m, Object data) {
        final HashSet<ASTVariableExpression> setOfSafeVars = new HashSet<>();
        final List<ASTStandardCondition> conditions = m.findDescendantsOfType(ASTStandardCondition.class);
        for (ASTStandardCondition c : conditions) {
            List<ASTVariableExpression> vars = c.findDescendantsOfType(ASTVariableExpression.class);
            setOfSafeVars.addAll(vars);
        }

        final List<ASTBinaryExpression> binaryExpr = m.findChildrenOfType(ASTBinaryExpression.class);
        for (ASTBinaryExpression b : binaryExpr) {
            List<ASTVariableExpression> vars = b.findDescendantsOfType(ASTVariableExpression.class);
            for (ASTVariableExpression v : vars) {
                String fqName = Helper.getFQVariableName(v);

                if (selectContainingVariables.containsKey(fqName)) {
                    boolean isLiteral = selectContainingVariables.get(fqName);
                    if (isLiteral) {
                        continue;
                    }
                }

                if (setOfSafeVars.contains(v) || safeVariables.contains(fqName)) {
                    continue;
                }

                final ASTMethodCallExpression parentCall = v.getFirstParentOfType(ASTMethodCallExpression.class);
                boolean isSafeMethod = Helper.isMethodName(parentCall, STRING, ESCAPE_SINGLE_QUOTES)
                        || Helper.isMethodName(parentCall, STRING, JOIN);

                if (!isSafeMethod) {
                    addViolation(data, v);
                }
            }
        }
    }

    private void reportVariables(final ASTMethodCallExpression m, Object data) {
        final ASTVariableExpression var = m.getFirstChildOfType(ASTVariableExpression.class);
        if (var != null) {
            String nameFQ = Helper.getFQVariableName(var);
            if (selectContainingVariables.containsKey(nameFQ)) {
                boolean isLiteral = selectContainingVariables.get(nameFQ);
                if (!isLiteral) {
                    addViolation(data, var);
                }
            }
        }
    }
}
