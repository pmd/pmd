/**
 * BSD-style license; for more info see http://pmd.sourceforge.net/license.html
 */

package net.sourceforge.pmd.lang.apex.rule.design;

import java.util.Map;
import java.util.Set;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;

import com.google.common.collect.ImmutableSet;

import net.sourceforge.pmd.lang.apex.ast.ASTFieldDeclarationStatements;
import net.sourceforge.pmd.lang.apex.ast.ASTLiteralExpression;
import net.sourceforge.pmd.lang.apex.ast.ASTUserClass;
import net.sourceforge.pmd.lang.apex.rule.AbstractApexRule;
import net.sourceforge.pmd.lang.apex.ast.ASTMethodCallExpression;
import net.sourceforge.pmd.lang.apex.ast.ASTModifierNode;
import net.sourceforge.pmd.lang.apex.ast.ASTReferenceExpression;

public class RepeatedPrimitiveValueRule extends AbstractApexRule {

    private static final String NEW_INSTANCE = "newinstance";
    private static final ImmutableSet<String> OMITTED_VALUES = ImmutableSet.of("0", "1", "-1", "");
    private static final ImmutableSet<String> DATE_AND_DATETIME_CLASSES = ImmutableSet.of("date", "datetime");

    private Map<String, Integer> numberCount = new HashMap<String, Integer>();
    private Map<String, Integer> stringCount = new HashMap<String, Integer>();
    private Set<String> constantNumbers = new HashSet<String>();

    @Override
    public Object visit(ASTUserClass topLevelClass, Object data) {
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
        return expression.isNull() || OMITTED_VALUES.contains(value)
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
        if (!isTheNewInstanceMethod(methodCallExpression)) {
            return false;
        }
        return isReferencedByDateOrDateTimeClass(methodCallExpression);
    }

    private boolean isTheNewInstanceMethod(ASTMethodCallExpression methodCallExpression) {
        String methodName = methodCallExpression.getMethodName();
        return methodName != null && NEW_INSTANCE.equalsIgnoreCase(methodName);
    }

    private boolean isReferencedByDateOrDateTimeClass(ASTMethodCallExpression methodCallExpression) {
        List<ASTReferenceExpression> references = methodCallExpression.descendants(ASTReferenceExpression.class)
                .toList();
        if (references.size() != 1) {
            return false;
        }
        return DATE_AND_DATETIME_CLASSES.contains(references.get(0).getImage().toLowerCase());
    }

    private boolean isAConstantNumberWhichHasAlreadyBeenDefined(ASTLiteralExpression expression, String value) {
        return isAConstantNumber(expression) && constantNumbers.contains(value);
    }

}
