/**
 * BSD-style license; for more info see http://pmd.sourceforge.net/license.html
 */

package net.sourceforge.pmd.lang.apex.rule.design;

import java.util.Map;
import java.util.HashMap;
import java.util.List;

import com.google.common.collect.ImmutableSet;

import net.sourceforge.pmd.lang.apex.ast.ASTFieldDeclaration;
import net.sourceforge.pmd.lang.apex.ast.ASTFieldDeclarationStatements;
import net.sourceforge.pmd.lang.apex.ast.ASTLiteralExpression;
import net.sourceforge.pmd.lang.apex.ast.ASTUserClass;
import net.sourceforge.pmd.lang.apex.ast.ApexNode;
import net.sourceforge.pmd.lang.apex.rule.AbstractApexRule;
import scala.collection.mutable.HashSet;
import net.sourceforge.pmd.lang.apex.ast.ASTMethodCallExpression;
import net.sourceforge.pmd.lang.apex.ast.ASTModifierNode;
import net.sourceforge.pmd.lang.apex.ast.ASTReferenceExpression;

public class RepeatedPrimitiveValueRule extends AbstractApexRule {

    private static final ImmutableSet<String> OMITTED_VALUES = ImmutableSet.of("0", "1", "-1", "");

    private Map<String, Integer> numberCount = new HashMap<String, Integer>();
    private Map<String, Integer> stringCount = new HashMap<String, Integer>();
    private HashSet<String> constantNumbers = new HashSet<String>();

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

    private boolean isAConstantNumber(ASTLiteralExpression expression) {
        if (expression.isString()) {
            return false;
        }
        ApexNode parent = expression.getParent();
        if (!(parent instanceof ASTFieldDeclaration)) {
            return false;
        }
        ASTFieldDeclaration fieldDeclaration = (ASTFieldDeclaration) parent;
        parent = fieldDeclaration.getParent();
        if (!(parent instanceof ASTFieldDeclarationStatements)) {
            return false;
        }
        ASTFieldDeclarationStatements fieldDeclarationStatements = (ASTFieldDeclarationStatements) parent;
        ASTModifierNode modifiers = fieldDeclarationStatements.getModifiers();
        return modifiers.isFinal() && modifiers.isStatic();
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

    private boolean shouldSkip(ASTLiteralExpression expression, String value) {
        return expression.isNull() || OMITTED_VALUES.contains(value)
                || isNumberUsedInDateOrDateTimeConstruction(expression)
                || isAConstantNumberWhichHasAlreadyBeenDefined(expression, value);
    }

    private boolean hasBeenObservedBefore(ASTLiteralExpression expression, String value) {
        return (expression.isString() && stringCount.get(value) > 1) || numberCount.get(value) > 1;
    }

    private boolean isNumberUsedInDateOrDateTimeConstruction(ASTLiteralExpression expression) {
        ApexNode parent = expression.getParent();
        if (!(parent instanceof ASTMethodCallExpression)) {
            return false;
        }
        ASTMethodCallExpression methodCallExpression = (ASTMethodCallExpression) parent;
        if (!isTheNewInstanceMethod(methodCallExpression)) {
            return false;
        }
        return isReferencedByDateOrDateTimeClass(methodCallExpression);
    }

    private boolean isTheNewInstanceMethod(ASTMethodCallExpression methodCallExpression) {
        String methodName = methodCallExpression.getMethodName();
        return methodName != null && methodName.equalsIgnoreCase("newInstance");
    }

    private boolean isReferencedByDateOrDateTimeClass(ASTMethodCallExpression methodCallExpression) {
        List<ASTReferenceExpression> references = methodCallExpression.descendants(ASTReferenceExpression.class)
                .toList();
        if (references.size() != 1) {
            return false;
        }
        return ImmutableSet.of("date", "datetime").contains(references.get(0).getImage().toLowerCase());
    }

    private boolean isAConstantNumberWhichHasAlreadyBeenDefined(ASTLiteralExpression expression, String value) {
        return isAConstantNumber(expression) && constantNumbers.contains(value);
    }

}
