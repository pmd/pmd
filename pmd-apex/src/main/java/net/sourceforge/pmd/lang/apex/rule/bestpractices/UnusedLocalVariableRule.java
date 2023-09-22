/*
 * BSD-style license; for more info see http://pmd.sourceforge.net/license.html
 */

package net.sourceforge.pmd.lang.apex.rule.bestpractices;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashSet;
import java.util.List;
import java.util.Locale;
import java.util.Set;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.stream.Collectors;

import org.apache.commons.lang3.StringUtils;
import org.checkerframework.checker.nullness.qual.NonNull;

import net.sourceforge.pmd.lang.apex.ast.ASTBlockStatement;
import net.sourceforge.pmd.lang.apex.ast.ASTLiteralExpression;
import net.sourceforge.pmd.lang.apex.ast.ASTMethodCallExpression;
import net.sourceforge.pmd.lang.apex.ast.ASTReferenceExpression;
import net.sourceforge.pmd.lang.apex.ast.ASTVariableDeclaration;
import net.sourceforge.pmd.lang.apex.ast.ASTVariableExpression;
import net.sourceforge.pmd.lang.apex.ast.ApexNode;
import net.sourceforge.pmd.lang.apex.rule.AbstractApexRule;
import net.sourceforge.pmd.lang.rule.RuleTargetSelector;

public class UnusedLocalVariableRule extends AbstractApexRule {
    private static final Set<String> DATABASE_QUERY_METHODS = new HashSet<>(Arrays.asList(
            "Database.query".toLowerCase(Locale.ROOT),
            "Database.getQueryLocator".toLowerCase(Locale.ROOT),
            "Database.countQuery".toLowerCase(Locale.ROOT)
    ));

    private static final Pattern BINDING_VARIABLE = Pattern.compile("(?i):([a-z0-9]+)");

    @Override
    protected @NonNull RuleTargetSelector buildTargetSelector() {
        return RuleTargetSelector.forTypes(ASTVariableDeclaration.class);
    }

    @Override
    public Object visit(ASTVariableDeclaration node, Object data) {
        String variableName = node.getImage();

        ASTBlockStatement variableContext = node.getFirstParentOfType(ASTBlockStatement.class);
        if (variableContext == null) {
            // if there is no parent BlockStatement, e.g. in triggers
            return data;
        }

        List<ApexNode<?>> potentialUsages = new ArrayList<>();

        // Variable expression catch things like the `a` in `a + b`
        potentialUsages.addAll(variableContext.findDescendantsOfType(ASTVariableExpression.class));
        // Reference expressions catch things like the `a` in `a.foo()`
        potentialUsages.addAll(variableContext.findDescendantsOfType(ASTReferenceExpression.class));

        for (ApexNode<?> usage : potentialUsages) {
            if (usage.getParent() == node) {
                continue;
            }

            if (StringUtils.equalsIgnoreCase(variableName, usage.getImage())) {
                return data;
            }
        }

        List<String> soqlBindingVariables = findBindingsInSOQLStringLiterals(variableContext);
        if (soqlBindingVariables.contains(variableName.toLowerCase(Locale.ROOT))) {
            return data;
        }

        addViolation(data, node, variableName);
        return data;
    }

    private List<String> findBindingsInSOQLStringLiterals(ASTBlockStatement variableContext) {
        List<String> bindingVariables = new ArrayList<>();

        List<ASTMethodCallExpression> methodCalls = variableContext.findDescendantsOfType(ASTMethodCallExpression.class)
            .stream()
            .filter(m -> DATABASE_QUERY_METHODS.contains(m.getFullMethodName().toLowerCase(Locale.ROOT)))
            .collect(Collectors.toList());

        methodCalls.forEach(databaseMethodCall -> {
            List<String> stringLiterals = new ArrayList<>();
            stringLiterals.addAll(databaseMethodCall.findDescendantsOfType(ASTLiteralExpression.class)
                    .stream()
                    .filter(ASTLiteralExpression::isString)
                    .map(ASTLiteralExpression::getImage)
                    .collect(Collectors.toList()));

            databaseMethodCall.findDescendantsOfType(ASTVariableExpression.class).forEach(variableUsage -> {
                String referencedVariable = variableUsage.getImage();

                // Search other usages of the same variable within this code block
                variableContext.findDescendantsOfType(ASTVariableExpression.class)
                        .stream()
                        .filter(usage -> referencedVariable.equalsIgnoreCase(usage.getImage()))
                        .forEach(usage -> {
                            stringLiterals.addAll(usage.getParent()
                                    .findChildrenOfType(ASTLiteralExpression.class)
                                    .stream()
                                    .filter(ASTLiteralExpression::isString)
                                    .map(ASTLiteralExpression::getImage)
                                    .collect(Collectors.toList()));
                        });
            });

            stringLiterals.forEach(s -> {
                Matcher matcher = BINDING_VARIABLE.matcher(s);
                while (matcher.find()) {
                    bindingVariables.add(matcher.group(1).toLowerCase(Locale.ROOT));
                }
            });
        });

        return bindingVariables;
    }
}
