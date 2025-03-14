/*
 * BSD-style license; for more info see http://pmd.sourceforge.net/license.html
 */

package net.sourceforge.pmd.lang.apex.rule.bestpractices;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.HashSet;
import java.util.List;
import java.util.Locale;
import java.util.Set;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import org.apache.commons.lang3.StringUtils;
import org.checkerframework.checker.nullness.qual.NonNull;

import net.sourceforge.pmd.lang.apex.ast.ASTBlockStatement;
import net.sourceforge.pmd.lang.apex.ast.ASTLiteralExpression;
import net.sourceforge.pmd.lang.apex.ast.ASTMethodCallExpression;
import net.sourceforge.pmd.lang.apex.ast.ASTReferenceExpression;
import net.sourceforge.pmd.lang.apex.ast.ASTSoslExpression;
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

    private static final Pattern BINDING_VARIABLE = Pattern.compile("(?i):\\s*+([_a-z0-9]+)");

    @Override
    protected @NonNull RuleTargetSelector buildTargetSelector() {
        return RuleTargetSelector.forTypes(ASTVariableDeclaration.class);
    }

    @Override
    public Object visit(ASTVariableDeclaration node, Object data) {
        String variableName = node.getImage();

        ASTBlockStatement variableContext = node.ancestors(ASTBlockStatement.class).first();
        if (variableContext == null) {
            // if there is no parent BlockStatement, e.g. in triggers
            return data;
        }

        List<ApexNode<?>> potentialUsages = new ArrayList<>();

        // Variable expression catch things like the `a` in `a + b` or in `:a` (BindingExpression)
        potentialUsages.addAll(variableContext.descendants(ASTVariableExpression.class).toList());
        // Reference expressions catch things like the `a` in `a.foo()` or in `:a.Id` (Binding Expression)
        potentialUsages.addAll(variableContext.descendants(ASTReferenceExpression.class).toList());

        for (ApexNode<?> usage : potentialUsages) {
            if (usage.getParent() == node) {
                continue;
            }

            if (StringUtils.equalsIgnoreCase(variableName, usage.getImage())) {
                return data;
            }
        }

        List<String> bindingVariables = new ArrayList<>(findBindingsInSOQLStringLiterals(variableContext));
        bindingVariables.addAll(findBindingsInSOSLQueries(variableContext));
        if (bindingVariables.contains(variableName.toLowerCase(Locale.ROOT))) {
            return data;
        }

        asCtx(data).addViolation(node, variableName);
        return data;
    }

    /**
     * Manually parses the sosl query, as not all binding vars are in the AST yet
     * (as {@link net.sourceforge.pmd.lang.apex.ast.ASTBindExpressions}).
     * This is not needed anymore, once summit ast is fixed.
     */
    private Collection<String> findBindingsInSOSLQueries(ASTBlockStatement variableContext) {
        return variableContext.descendants(ASTSoslExpression.class)
                .toStream()
                .map(ASTSoslExpression::getQuery)
                .flatMap(UnusedLocalVariableRule::extractBindindVars)
                .collect(Collectors.toList());
    }

    private List<String> findBindingsInSOQLStringLiterals(ASTBlockStatement variableContext) {
        List<ASTMethodCallExpression> methodCalls = variableContext.descendants(ASTMethodCallExpression.class)
            .filter(m -> DATABASE_QUERY_METHODS.contains(m.getFullMethodName().toLowerCase(Locale.ROOT)))
            .collect(Collectors.toList());

        List<String> stringLiterals = new ArrayList<>();

        for (ASTMethodCallExpression databaseMethodCall : methodCalls) {
            stringLiterals.addAll(databaseMethodCall.descendants(ASTLiteralExpression.class)
                    .filter(ASTLiteralExpression::isString)
                    .toStream()
                    .map(ASTLiteralExpression::getImage)
                    .collect(Collectors.toList()));

            databaseMethodCall.descendants(ASTVariableExpression.class).forEach(variableUsage -> {
                String referencedVariable = variableUsage.getImage();

                // Search other usages of the same variable within this code block
                variableContext.descendants(ASTVariableExpression.class)
                        .filter(usage -> referencedVariable.equalsIgnoreCase(usage.getImage()))
                        .forEach(usage -> {
                            stringLiterals.addAll(usage.getParent()
                                    .descendants(ASTLiteralExpression.class)
                                    .filter(ASTLiteralExpression::isString)
                                    .toStream()
                                    .map(ASTLiteralExpression::getImage)
                                    .collect(Collectors.toList()));
                        });
            });
        }

        return stringLiterals.stream()
                .flatMap(UnusedLocalVariableRule::extractBindindVars)
                .collect(Collectors.toList());
    }

    private static Stream<String> extractBindindVars(String query) {
        List<String> vars = new ArrayList<>();
        Matcher matcher = BINDING_VARIABLE.matcher(query);
        while (matcher.find()) {
            vars.add(matcher.group(1).toLowerCase(Locale.ROOT));
        }
        return vars.stream();
    }
}
