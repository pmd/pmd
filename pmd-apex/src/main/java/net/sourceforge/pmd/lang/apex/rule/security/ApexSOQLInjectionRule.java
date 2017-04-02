/**
 * BSD-style license; for more info see http://pmd.sourceforge.net/license.html
 */

package net.sourceforge.pmd.lang.apex.rule.security;

import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.regex.Pattern;

import net.sourceforge.pmd.lang.apex.ast.ASTAssignmentExpression;
import net.sourceforge.pmd.lang.apex.ast.ASTBinaryExpression;
import net.sourceforge.pmd.lang.apex.ast.ASTFieldDeclaration;
import net.sourceforge.pmd.lang.apex.ast.ASTLiteralExpression;
import net.sourceforge.pmd.lang.apex.ast.ASTMethodCallExpression;
import net.sourceforge.pmd.lang.apex.ast.ASTUserClass;
import net.sourceforge.pmd.lang.apex.ast.ASTVariableDeclaration;
import net.sourceforge.pmd.lang.apex.ast.ASTVariableExpression;
import net.sourceforge.pmd.lang.apex.ast.AbstractApexNode;
import net.sourceforge.pmd.lang.apex.rule.AbstractApexRule;

/**
 * Detects if variables in Database.query(variable) is escaped with
 * String.escapeSingleQuotes
 * 
 * @author sergey.gorbaty
 *
 */
public class ApexSOQLInjectionRule extends AbstractApexRule {
    private static final String JOIN = "join";
    private static final String ESCAPE_SINGLE_QUOTES = "escapeSingleQuotes";
    private static final String STRING = "String";
    private static final String DATABASE = "Database";
    private static final String QUERY = "query";
    private static final Pattern SELECT_PATTERN = Pattern.compile("^select[\\s]+?.*?$", Pattern.CASE_INSENSITIVE);
    private final HashSet<String> safeVariables = new HashSet<>();
    private final HashMap<String, Boolean> selectContainingVariables = new HashMap<>();

    public ApexSOQLInjectionRule() {
        setProperty(CODECLIMATE_CATEGORIES, new String[] { "Security" });
        setProperty(CODECLIMATE_REMEDIATION_MULTIPLIER, 100);
        setProperty(CODECLIMATE_BLOCK_HIGHLIGHTING, false);
    }

    @Override
    public Object visit(ASTUserClass node, Object data) {

        if (Helper.isTestMethodOrClass(node)) {
            return data;
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

    private void findSanitizedVariables(AbstractApexNode<?> node) {
        final ASTVariableExpression left = node.getFirstChildOfType(ASTVariableExpression.class);
        final ASTLiteralExpression literal = node.getFirstChildOfType(ASTLiteralExpression.class);
        final ASTMethodCallExpression right = node.getFirstChildOfType(ASTMethodCallExpression.class);

        // look for String a = 'b';
        if (literal != null) {
            if (left != null) {
                Object o = literal.getNode().getLiteral();
                if (o instanceof Integer || o instanceof Boolean || o instanceof Double) {
                    safeVariables.add(Helper.getFQVariableName(left));
                }

                if (o instanceof String) {
                    if (SELECT_PATTERN.matcher((String) o).matches()) {
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

        final ASTLiteralExpression literal = node.getFirstChildOfType(ASTLiteralExpression.class);
        if (literal != null) {

            Object o = literal.getNode().getLiteral();
            if (o instanceof String) {
                if (SELECT_PATTERN.matcher((String) o).matches()) {
                    if (!isSafeVariable) {
                        // select literal + other unsafe vars
                        selectContainingVariables.put(Helper.getFQVariableName(var), Boolean.FALSE);
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

                if (safeVariables.contains(fqName)) {
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
