/**
 * BSD-style license; for more info see http://pmd.sourceforge.net/license.html
 */

package net.sourceforge.pmd.lang.apex.rule.security;

import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Set;
import java.util.regex.Pattern;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import org.checkerframework.checker.nullness.qual.NonNull;

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
import net.sourceforge.pmd.lang.apex.ast.ApexNode;
import net.sourceforge.pmd.lang.apex.rule.AbstractApexRule;
import net.sourceforge.pmd.lang.apex.rule.internal.Helper;
import net.sourceforge.pmd.lang.rule.RuleTargetSelector;

/**
 * Detects if variables in Database.query(variable) or Database.countQuery is escaped with
 * String.escapeSingleQuotes
 *
 * @author sergey.gorbaty
 *
 */
public class ApexSOQLInjectionRule extends AbstractApexRule {
    private static final Set<String> SAFE_VARIABLE_TYPES = 
        Collections.unmodifiableSet(Stream.of(
            "double", "long", "decimal", "boolean", "id", "integer",
            "sobjecttype", "schema.sobjecttype", "sobjectfield", "schema.sobjectfield"
        ).collect(Collectors.toSet()));
    
    private static final String JOIN = "join";
    private static final String ESCAPE_SINGLE_QUOTES = "escapeSingleQuotes";
    private static final String STRING = "String";
    private static final String DATABASE = "Database";
    private static final String QUERY = "query";
    private static final String COUNT_QUERY = "countQuery";
    private static final Pattern SELECT_PATTERN = Pattern.compile("^select[\\s]+?.*?$", Pattern.CASE_INSENSITIVE);
    private final Set<String> safeVariables = new HashSet<>();
    private final Map<String, Boolean> selectContainingVariables = new HashMap<>();

    @Override
    protected @NonNull RuleTargetSelector buildTargetSelector() {
        return RuleTargetSelector.forTypes(ASTUserClass.class);
    }

    @Override
    public Object visit(ASTUserClass node, Object data) {

        if (Helper.isTestMethodOrClass(node) || Helper.isSystemLevelClass(node)) {
            return data; // stops all the rules
        }

        for (ASTMethod m : node.descendants(ASTMethod.class)) {
            findSafeVariablesInSignature(m);
        }

        for (ASTFieldDeclaration a : node.descendants(ASTFieldDeclaration.class)) {
            findSanitizedVariables(a);
            findSelectContainingVariables(a);
        }

        // String foo = String.escapeSignleQuotes(...);
        for (ASTVariableDeclaration a : node.descendants(ASTVariableDeclaration.class)) {
            findSanitizedVariables(a);
            findSelectContainingVariables(a);
        }

        // baz = String.escapeSignleQuotes(...);
        for (ASTAssignmentExpression a : node.descendants(ASTAssignmentExpression.class)) {
            findSanitizedVariables(a);
            findSelectContainingVariables(a);
        }

        // Database.query(...) check
        for (ASTMethodCallExpression m : node.descendants(ASTMethodCallExpression.class)) {
            if (!Helper.isTestMethodOrClass(m) && isQueryMethodCall(m)) {
                reportStrings(m, data);
                reportVariables(m, data);
            }
        }

        safeVariables.clear();
        selectContainingVariables.clear();

        return data;
    }

    private boolean isQueryMethodCall(ASTMethodCallExpression m) {
        return Helper.isMethodName(m, DATABASE, QUERY) || Helper.isMethodName(m, DATABASE, COUNT_QUERY);
    }

    private boolean isSafeVariableType(String typeName) {
        return SAFE_VARIABLE_TYPES.contains(typeName.toLowerCase(Locale.ROOT));
    }

    private void findSafeVariablesInSignature(ASTMethod m) {
        for (ASTParameter p : m.children(ASTParameter.class)) {
            if (isSafeVariableType(p.getType())) {
                safeVariables.add(Helper.getFQVariableName(p));
            }
        }
    }

    private void findSanitizedVariables(ApexNode<?> node) {
        final ASTVariableExpression left = node.firstChild(ASTVariableExpression.class);
        final ASTLiteralExpression literal = node.firstChild(ASTLiteralExpression.class);
        final ASTMethodCallExpression right = node.firstChild(ASTMethodCallExpression.class);

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
            if (isSafeVariableType(((ASTVariableDeclaration) node).getType())) {
                safeVariables.add(Helper.getFQVariableName(left));
            }
        }
    }

    private void findSelectContainingVariables(ApexNode<?> node) {
        final ASTVariableExpression left = node.firstChild(ASTVariableExpression.class);
        final ASTBinaryExpression right = node.firstChild(ASTBinaryExpression.class);
        if (left != null && right != null) {
            recursivelyCheckForSelect(left, right);
        }
    }

    private void recursivelyCheckForSelect(final ASTVariableExpression var, final ASTBinaryExpression node) {
        final ASTBinaryExpression right = node.firstChild(ASTBinaryExpression.class);
        if (right != null) {
            recursivelyCheckForSelect(var, right);
        }

        final ASTVariableExpression concatenatedVar = node.firstChild(ASTVariableExpression.class);
        boolean isSafeVariable = false;

        if (concatenatedVar != null) {
            if (safeVariables.contains(Helper.getFQVariableName(concatenatedVar))) {
                isSafeVariable = true;
            }
        }

        final ASTMethodCallExpression methodCall = node.firstChild(ASTMethodCallExpression.class);
        if (methodCall != null) {
            if (Helper.isMethodName(methodCall, STRING, ESCAPE_SINGLE_QUOTES)) {
                isSafeVariable = true;
            }
        }

        final ASTLiteralExpression literal = node.firstChild(ASTLiteralExpression.class);
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
        final Set<ASTVariableExpression> setOfSafeVars = new HashSet<>();
        for (ASTStandardCondition c : m.descendants(ASTStandardCondition.class)) {
            List<ASTVariableExpression> vars = c.descendants(ASTVariableExpression.class).toList();
            setOfSafeVars.addAll(vars);
        }

        for (ASTBinaryExpression b : m.children(ASTBinaryExpression.class)) {
            for (ASTVariableExpression v : b.descendants(ASTVariableExpression.class)) {
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

                final ASTMethodCallExpression parentCall = v.ancestors(ASTMethodCallExpression.class).first();
                boolean isSafeMethod = Helper.isMethodName(parentCall, STRING, ESCAPE_SINGLE_QUOTES)
                        || Helper.isMethodName(parentCall, STRING, JOIN);

                if (!isSafeMethod) {
                    asCtx(data).addViolation(v);
                }
            }
        }
    }

    private void reportVariables(final ASTMethodCallExpression m, Object data) {
        final ASTVariableExpression var = m.firstChild(ASTVariableExpression.class);
        if (var != null) {
            String nameFQ = Helper.getFQVariableName(var);
            if (selectContainingVariables.containsKey(nameFQ)) {
                boolean isLiteral = selectContainingVariables.get(nameFQ);
                if (!isLiteral) {
                    asCtx(data).addViolation(var);
                }
            }
        }
    }
}
