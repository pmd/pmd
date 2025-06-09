/**
 * BSD-style license; for more info see http://pmd.sourceforge.net/license.html
 */

package net.sourceforge.pmd.lang.apex.rule.design;

import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Set;

import net.sourceforge.pmd.lang.apex.ast.ASTFieldDeclarationStatements;
import net.sourceforge.pmd.lang.apex.ast.ASTLiteralExpression;
import net.sourceforge.pmd.lang.apex.ast.ASTMethodCallExpression;
import net.sourceforge.pmd.lang.apex.ast.ASTModifierNode;
import net.sourceforge.pmd.lang.apex.ast.ASTReferenceExpression;
import net.sourceforge.pmd.lang.apex.ast.ASTUserClass;
import net.sourceforge.pmd.lang.apex.rule.AbstractApexRule;

import com.google.common.collect.ImmutableSet;

/**
 * Detects repeated primitive values (strings and numbers) within an Apex class
 * that could be extracted into named constants to improve code maintainability
 * and reduce magic numbers/strings.
 * 
 * <p>
 * This rule identifies primitive literals that appear multiple times in the
 * same class and flags them as potential candidates for constant extraction.
 * The rule excludes certain common values (0, 1, -1, empty string) and boolean
 * literals as these are typically acceptable to repeat.
 * </p>
 * 
 * <p>
 * Examples of violations:
 * </p>
 * 
 * <pre>
 * public class Example {
 *     public void method1() {
 *         Integer timeout = 30; // violation: 30 appears multiple times
 *     }
 * 
 *     public void method2() {
 *         Integer delay = 30; // violation: same value repeated
 *     }
 * }
 * </pre>
 * 
 * <p>
 * The rule also provides special handling for:
 * </p>
 * <ul>
 * <li>Numbers used in Date/DateTime construction (newInstance calls)</li>
 * <li>Constants already defined as static final fields</li>
 * <li>Boolean literals (always ignored)</li>
 * <li>Null values and common numeric constants</li>
 * </ul>
 * 
 * @see <a href="https://github.com/pmd/pmd/issues/5323">GitHub Issue #5323</a>
 */
public class RepeatedPrimitiveValueRule extends AbstractApexRule {

    private static final String NEW_INSTANCE = "newinstance";
    private static final Set<String> OMITTED_VALUES = ImmutableSet.of("0", "1", "-1", "");
    private static final Set<String> DATE_AND_DATETIME_CLASSES = ImmutableSet.of("date", "datetime");

    private Map<String, Integer> numberCount = new HashMap<>();
    private Map<String, Integer> stringCount = new HashMap<>();
    private Set<String> constantNumbers = new HashSet<>();

    /**
     * Main entry point for analyzing an Apex class for repeated primitive
     * values.
     * 
     * <p>
     * This method performs a two-pass analysis:
     * </p>
     * <ol>
     * <li>First pass: Count all occurrences of primitive literals in the
     * class</li>
     * <li>Second pass: Mark violations for literals that appear more than
     * once</li>
     * </ol>
     * 
     * @param topLevelClass
     *            the Apex class to analyze
     * @param data
     *            the rule context data
     * @return the rule context data with any violations added
     */
    @Override
    public Object visit(ASTUserClass topLevelClass, Object data) {
        super.visit(topLevelClass, data);
        countOccurrences(topLevelClass);
        markDuplicates(topLevelClass, data);
        return data;
    }

    private void countOccurrences(ASTUserClass topLevelClass) {
        populateOccurrences(topLevelClass);
        for (ASTUserClass innerClass : topLevelClass.descendants(ASTUserClass.class)) {
            populateOccurrences(innerClass);
        }
    }

    private void populateOccurrences(ASTUserClass theClass) {
        for (ASTLiteralExpression expression : theClass.descendants(ASTLiteralExpression.class)) {
            String value = expression.getImage();
            if (shouldSkip(expression, value)) {
                continue;
            }
            if (isAConstantNumber(expression)) {
                constantNumbers.add(value);
            }
            if (expression.isString()) {
                stringCount.put(value, stringCount.getOrDefault(value, 0) + 1);
            } else {
                numberCount.put(value, numberCount.getOrDefault(value, 0) + 1);
            }
        }
    }

    private void markDuplicates(ASTUserClass topLevelClass, Object data) {
        addViolationToRepeatedValues(topLevelClass, data);
        for (ASTUserClass innerClass : topLevelClass.descendants(ASTUserClass.class)) {
            addViolationToRepeatedValues(innerClass, data);
        }
    }

    private void addViolationToRepeatedValues(ASTUserClass theClass, Object data) {
        for (ASTLiteralExpression expression : theClass.descendants(ASTLiteralExpression.class)) {
            String value = expression.getImage();
            if (shouldSkip(expression, value)) {
                continue;
            }
            if (hasBeenObservedBefore(expression, value)) {
                asCtx(data).addViolation(expression);
            }
        }
    }

    private boolean isAConstantNumber(ASTLiteralExpression expression) {
        if (expression.isString()) {
            return false;
        }
        List<ASTFieldDeclarationStatements> ancestors = expression.ancestors(ASTFieldDeclarationStatements.class)
                .toList();
        if (ancestors.isEmpty()) {
            return false;
        }
        ASTModifierNode modifiers = ancestors.get(0).getModifiers();
        return modifiers.isFinal() && modifiers.isStatic();
    }

    private boolean shouldSkip(ASTLiteralExpression expression, String value) {
        return expression.isNull() || expression.isBoolean() || OMITTED_VALUES.contains(value)
                || isNumberUsedInDateOrDateTimeConstruction(expression)
                || isAConstantNumberWhichHasAlreadyBeenDefined(expression, value);
    }

    private boolean hasBeenObservedBefore(ASTLiteralExpression expression, String value) {
        return isObservedString(expression, value) || isObservedNumber(expression, value);
    }

    private boolean isObservedString(ASTLiteralExpression expression, String value) {
        return expression.isString() && stringCount.containsKey(value) && stringCount.get(value) > 1;
    }

    private boolean isObservedNumber(ASTLiteralExpression expression, String value) {
        return !expression.isString() && numberCount.containsKey(value) && numberCount.get(value) > 1;
    }

    private boolean isNumberUsedInDateOrDateTimeConstruction(ASTLiteralExpression expression) {
        if (expression.isString()) {
            return false;
        }
        List<ASTMethodCallExpression> methodCallExpressions = expression.ancestors(ASTMethodCallExpression.class)
                .toList();
        if (methodCallExpressions.isEmpty()) {
            return false;
        }
        ASTMethodCallExpression methodCallExpression = methodCallExpressions.get(0);
        return isTheNewInstanceMethod(methodCallExpression) && isReferencedByDateOrDateTimeClass(methodCallExpression);
    }

    private boolean isTheNewInstanceMethod(ASTMethodCallExpression methodCallExpression) {
        String methodName = methodCallExpression.getMethodName();
        return methodName != null && NEW_INSTANCE.equalsIgnoreCase(methodName);
    }

    private boolean isReferencedByDateOrDateTimeClass(ASTMethodCallExpression methodCallExpression) {
        List<ASTReferenceExpression> references = methodCallExpression.descendants(ASTReferenceExpression.class)
                .toList();
        return references.size() == 1
                && DATE_AND_DATETIME_CLASSES.contains(references.get(0).getImage().toLowerCase(Locale.ROOT));
    }

    private boolean isAConstantNumberWhichHasAlreadyBeenDefined(ASTLiteralExpression expression, String value) {
        return isAConstantNumber(expression) && constantNumbers.contains(value);
    }

}
