/**
 * BSD-style license; for more info see http://pmd.sourceforge.net/license.html
 */

package net.sourceforge.pmd.lang.apex.rule.design;

import java.util.Map;
import java.util.HashMap;
import java.util.List;

import com.google.common.collect.ImmutableSet;

import net.sourceforge.pmd.lang.apex.ast.ASTLiteralExpression;
import net.sourceforge.pmd.lang.apex.ast.ASTUserClass;
import net.sourceforge.pmd.lang.apex.ast.ApexNode;
import net.sourceforge.pmd.lang.apex.rule.AbstractApexRule;
import net.sourceforge.pmd.lang.apex.ast.ASTMethodCallExpression;
import net.sourceforge.pmd.lang.apex.ast.ASTReferenceExpression;

public class RepeatedPrimitiveValueRule extends AbstractApexRule {

    private static final ImmutableSet<String> OMITTED_VALUES = ImmutableSet.of("0", "1", "-1", "");

    private Map<String, Integer> numberCount = new HashMap<String, Integer>();
    private Map<String, Integer> stringCount = new HashMap<String, Integer>();

    @Override
    public Object visit(ASTUserClass topLevelClass, Object data) {
        countOccurrences(topLevelClass);
        markDuplicates(topLevelClass, data);
        return data;
    }

    private void countOccurrences(ASTUserClass topLevelClass) {
        populateOccurrenceMaps(topLevelClass);
        for (ASTUserClass innerClass : topLevelClass.descendants(ASTUserClass.class)) {
            populateOccurrenceMaps(innerClass);
        }
    }

    private void populateOccurrenceMaps(ASTUserClass theClass) {
        for (ASTLiteralExpression expression : theClass.descendants(ASTLiteralExpression.class)) {
            String value = expression.getImage();
            if (shouldSkip(expression, value)) {
                continue;
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
            if ((expression.isString() && stringCount.get(value) > 1) || numberCount.get(value) > 1) {
                asCtx(data).addViolation(expression);
            }
        }
    }

    private Boolean shouldSkip(ASTLiteralExpression expression, String value) {
        return expression.isNull() || OMITTED_VALUES.contains(value)
                || isNumberUsedInDateOrDateTimeConstruction(expression);
    }

    private boolean isNumberUsedInDateOrDateTimeConstruction(ASTLiteralExpression expression) {
        ApexNode parent = expression.getParent();
        if (!(parent instanceof ASTMethodCallExpression)) {
            return false;
        }

        ASTMethodCallExpression methodCallExpression = (ASTMethodCallExpression) parent;
        String methodName = methodCallExpression.getMethodName();
        if (methodName == null || !methodName.equalsIgnoreCase("newInstance")) {
            return false;
        }

        List<ASTReferenceExpression> references = methodCallExpression.descendants(ASTReferenceExpression.class)
                .toList();
        if (references.size() != 1) {
            return false;
        }

        return ImmutableSet.of("date", "datetime").contains(references.get(0).getImage().toLowerCase());
    }

}
