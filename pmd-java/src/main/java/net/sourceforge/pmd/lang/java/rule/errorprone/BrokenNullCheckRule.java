/**
 * BSD-style license; for more info see http://pmd.sourceforge.net/license.html
 */

package net.sourceforge.pmd.lang.java.rule.errorprone;

import java.util.ArrayList;
import java.util.List;

import org.apache.commons.lang3.StringUtils;

import net.sourceforge.pmd.lang.ast.Node;
import net.sourceforge.pmd.lang.java.ast.ASTAssignmentOperator;
import net.sourceforge.pmd.lang.java.ast.ASTClassOrInterfaceType;
import net.sourceforge.pmd.lang.java.ast.ASTConditionalAndExpression;
import net.sourceforge.pmd.lang.java.ast.ASTConditionalOrExpression;
import net.sourceforge.pmd.lang.java.ast.ASTEqualityExpression;
import net.sourceforge.pmd.lang.java.ast.ASTExpression;
import net.sourceforge.pmd.lang.java.ast.ASTIfStatement;
import net.sourceforge.pmd.lang.java.ast.ASTLiteral;
import net.sourceforge.pmd.lang.java.ast.ASTName;
import net.sourceforge.pmd.lang.java.ast.ASTNullLiteral;
import net.sourceforge.pmd.lang.java.ast.ASTPrimaryExpression;
import net.sourceforge.pmd.lang.java.ast.ASTPrimaryPrefix;
import net.sourceforge.pmd.lang.java.ast.ASTPrimarySuffix;
import net.sourceforge.pmd.lang.java.rule.AbstractJavaRule;

public class BrokenNullCheckRule extends AbstractJavaRule {

    @Override
    public Object visit(ASTIfStatement node, Object data) {
        ASTExpression expression = (ASTExpression) node.getChild(0);

        ASTConditionalAndExpression conditionalAndExpression = expression
                .getFirstDescendantOfType(ASTConditionalAndExpression.class);
        if (conditionalAndExpression != null) {
            checkForViolations(node, data, conditionalAndExpression);
        }

        ASTConditionalOrExpression conditionalOrExpression = expression
                .getFirstDescendantOfType(ASTConditionalOrExpression.class);
        if (conditionalOrExpression != null) {
            checkForViolations(node, data, conditionalOrExpression);
        }

        return super.visit(node, data);
    }

    private void checkForViolations(ASTIfStatement node, Object data, Node conditionalExpression) {
        ASTEqualityExpression equalityExpression = conditionalExpression
                .getFirstChildOfType(ASTEqualityExpression.class);
        if (equalityExpression == null) {
            return;
        }
        if (conditionalExpression instanceof ASTConditionalAndExpression
                && !"==".equals(equalityExpression.getImage())) {
            return;
        }
        if (conditionalExpression instanceof ASTConditionalOrExpression
                && !"!=".equals(equalityExpression.getImage())) {
            return;
        }
        ASTNullLiteral nullLiteral = equalityExpression.getFirstDescendantOfType(ASTNullLiteral.class);
        if (nullLiteral == null) {
            return; // No null check
        }
        // If there is an assignment in the equalityExpression we give up,
        // because things get too complex
        if (conditionalExpression.hasDescendantOfType(ASTAssignmentOperator.class)) {
            return;
        }

        // Find the expression used in the null compare
        ASTPrimaryExpression nullCompareExpression = findNullCompareExpression(equalityExpression);
        if (nullCompareExpression == null) {
            return; // No good null check
        }

        // Now we find the expression to compare to and do the comparison
        for (int i = 0; i < conditionalExpression.getNumChildren(); i++) {
            Node conditionalSubnode = conditionalExpression.getChild(i);

            // We skip the null compare branch
            ASTEqualityExpression nullEqualityExpression = nullLiteral
                    .getFirstParentOfType(ASTEqualityExpression.class);
            if (conditionalSubnode.equals(nullEqualityExpression)) {
                continue;
            }
            ASTPrimaryExpression conditionalPrimaryExpression;
            if (conditionalSubnode instanceof ASTPrimaryExpression) {
                conditionalPrimaryExpression = (ASTPrimaryExpression) conditionalSubnode;
            } else {
                // The ASTPrimaryExpression is hidden (in a negation, braces or
                // EqualityExpression)
                conditionalPrimaryExpression = conditionalSubnode.getFirstDescendantOfType(ASTPrimaryExpression.class);
            }

            if (primaryExpressionsAreEqual(nullCompareExpression, conditionalPrimaryExpression)) {
                addViolation(data, node); // We have a match
            }

        }
    }

    private boolean primaryExpressionsAreEqual(ASTPrimaryExpression nullCompareVariable,
            ASTPrimaryExpression expressionUsage) {
        List<String> nullCompareNames = new ArrayList<>();
        findExpressionNames(nullCompareVariable, nullCompareNames);

        List<String> expressionUsageNames = new ArrayList<>();
        findExpressionNames(expressionUsage, expressionUsageNames);

        for (int i = 0; i < nullCompareNames.size(); i++) {
            if (expressionUsageNames.size() == i) {
                // The used expression is shorter than the null
                // compare expression (and we don't want to crash
                // below)
                return false;
            }

            String nullCompareExpressionName = nullCompareNames.get(i);
            String expressionUsageName = expressionUsageNames.get(i);

            // Variablenames should match or the expressionUsage should have the
            // variable with a method call (ie. var.equals())
            if (!nullCompareExpressionName.equals(expressionUsageName)
                    && !expressionUsageName.startsWith(nullCompareExpressionName + ".")) {
                // Some other expression is being used after the
                // null compare
                return false;
            }
        }

        return true;
    }

    /**
     * Find the names of variables, methods and array arguments in a
     * PrimaryExpression.
     */
    private void findExpressionNames(Node nullCompareVariable, List<String> results) {
        for (int i = 0; i < nullCompareVariable.getNumChildren(); i++) {
            Node child = nullCompareVariable.getChild(i);

            if (child instanceof ASTName) {
                // Variable names and some method calls
                results.add(((ASTName) child).getImage());
            } else if (child instanceof ASTLiteral) { // Array arguments
                String literalImage = ((ASTLiteral) child).getImage();
                // Skip other null checks
                if (literalImage != null) {
                    results.add(literalImage);
                }
            } else if (child instanceof ASTPrimarySuffix) { // More method calls
                String name = ((ASTPrimarySuffix) child).getImage();
                if (StringUtils.isNotBlank(name)) {
                    results.add(name);
                }
            } else if (child instanceof ASTClassOrInterfaceType) {
                // A class can be an argument too
                String name = ((ASTClassOrInterfaceType) child).getImage();
                results.add(name);
            }

            if (child.getNumChildren() > 0) {
                findExpressionNames(child, results);
            }
        }
    }

    private ASTPrimaryExpression findNullCompareExpression(ASTEqualityExpression equalityExpression) {
        List<ASTPrimaryExpression> primaryExpressions = equalityExpression
                .findDescendantsOfType(ASTPrimaryExpression.class);
        for (ASTPrimaryExpression primaryExpression : primaryExpressions) {
            List<ASTPrimaryPrefix> primaryPrefixes = primaryExpression.findDescendantsOfType(ASTPrimaryPrefix.class);
            for (ASTPrimaryPrefix primaryPrefix : primaryPrefixes) {
                if (primaryPrefix.hasDescendantOfType(ASTName.class)) {
                    // We found the variable that is compared to null
                    return primaryExpression;
                }
            }
        }
        return null; // Nothing found
    }

}
