/*
 * BSD-style license; for more info see http://pmd.sourceforge.net/license.html
 */

package net.sourceforge.pmd.lang.apex.rule.performance;

import java.util.Optional;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.checkerframework.checker.nullness.qual.NonNull;

import net.sourceforge.pmd.lang.apex.ast.ASTAnnotation;
import net.sourceforge.pmd.lang.apex.ast.ASTAnnotationParameter;
import net.sourceforge.pmd.lang.apex.ast.ASTMethod;
import net.sourceforge.pmd.lang.apex.ast.ASTModifierNode;
import net.sourceforge.pmd.lang.apex.ast.ASTSoqlExpression;
import net.sourceforge.pmd.lang.apex.ast.ASTSoslExpression;
import net.sourceforge.pmd.lang.apex.ast.ASTUserClass;
import net.sourceforge.pmd.lang.apex.ast.ApexNode;
import net.sourceforge.pmd.lang.apex.rule.AbstractApexRule;
import net.sourceforge.pmd.lang.ast.NodeStream;
import net.sourceforge.pmd.lang.rule.RuleTargetSelector;
import net.sourceforge.pmd.reporting.RuleContext;

public class AvoidNonRestrictiveQueriesRule extends AbstractApexRule {
    private static final Pattern RESTRICTIVE_PATTERN = Pattern.compile("(where\\s+)|(limit\\s+)", Pattern.CASE_INSENSITIVE);
    private static final Pattern SELECT_OR_FIND_PATTERN = Pattern.compile("(select\\s+|find\\s+)", Pattern.CASE_INSENSITIVE);
    private static final Pattern SUB_QUERY_PATTERN = Pattern.compile("(?i)\\(\\s*select\\s+[^)]+\\)");

    @Override
    protected @NonNull RuleTargetSelector buildTargetSelector() {
        return RuleTargetSelector.forTypes(ASTSoqlExpression.class, ASTSoslExpression.class);
    }

    @Override
    public Object visit(ASTSoqlExpression node, Object data) {
        visitSoqlOrSosl(node, "SOQL", node.getQuery(), asCtx(data));
        return data;
    }

    @Override
    public Object visit(ASTSoslExpression node, Object data) {
        visitSoqlOrSosl(node, "SOSL", node.getQuery(), asCtx(data));
        return data;
    }

    private void visitSoqlOrSosl(ApexNode<?> node, String type, String query, RuleContext ruleContext) {
        ASTMethod method = node.ancestors(ASTMethod.class).first();
        if (method != null && method.getModifiers().isTest()) {
            Optional<ASTAnnotation> methodAnnotation = method
                    .children(ASTModifierNode.class)
                    .children(ASTAnnotation.class)
                    .filter(a -> "isTest".equalsIgnoreCase(a.getName()))
                    .firstOpt();

            Optional<ASTAnnotation> classAnnotation = method
                    .ancestors(ASTUserClass.class)
                    .firstOpt()
                    .map(u -> u.children(ASTModifierNode.class))
                    .map(s -> s.children(ASTAnnotation.class))
                    .map(NodeStream::first);

            Optional<Boolean> methodSeeAllData = methodAnnotation.flatMap(m -> m.children(ASTAnnotationParameter.class)
                    .filter(p -> p.hasName(ASTAnnotationParameter.SEE_ALL_DATA))
                    .firstOpt()
                    .map(ASTAnnotationParameter::getBooleanValue));
            boolean classSeeAllData = classAnnotation.flatMap(m -> m.children(ASTAnnotationParameter.class)
                            .filter(p -> p.hasName(ASTAnnotationParameter.SEE_ALL_DATA))
                            .firstOpt()
                            .map(ASTAnnotationParameter::getBooleanValue))
                    .orElse(false);

            if (methodSeeAllData.isPresent()) {
                if (!methodSeeAllData.get()) {
                    return;
                }
            } else if (!classSeeAllData) {
                return;
            }
        }

        Matcher subQueryMatcher = SUB_QUERY_PATTERN.matcher(query);
        StringBuffer queryWithoutSubQueries = new StringBuffer(query.length());
        while (subQueryMatcher.find()) {
            subQueryMatcher.appendReplacement(queryWithoutSubQueries, "(replaced_subquery)");
        }
        subQueryMatcher.appendTail(queryWithoutSubQueries);

        verifyQuery(ruleContext, node, type, queryWithoutSubQueries.toString());
    }

    private void verifyQuery(RuleContext ctx, ApexNode<?> node, String type, String query) {
        int occurrencesSelectOrFind = countOccurrences(SELECT_OR_FIND_PATTERN, query);
        int occurrencesWhereOrLimit = countOccurrences(RESTRICTIVE_PATTERN, query);

        if (occurrencesSelectOrFind > 0 && occurrencesWhereOrLimit == 0) {
            ctx.addViolation(node, type);
        }
    }

    private int countOccurrences(Pattern pattern, String s) {
        int occurrences = 0;
        Matcher matcher = pattern.matcher(s);
        while (matcher.find()) {
            occurrences++;
        }
        return occurrences;
    }
}
