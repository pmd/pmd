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
import net.sourceforge.pmd.lang.apex.ast.ASTUserClass;
import net.sourceforge.pmd.lang.apex.rule.AbstractApexRule;
import net.sourceforge.pmd.lang.ast.NodeStream;
import net.sourceforge.pmd.lang.rule.RuleTargetSelector;
import net.sourceforge.pmd.reporting.RuleContext;

public class AvoidNonRestrictiveQueriesRule extends AbstractApexRule {
    private static final Pattern RESTRICTIVE_PATTERN = Pattern.compile("(where )|(limit )", Pattern.CASE_INSENSITIVE);
    private static final Pattern SELECT_PATTERN = Pattern.compile("(select )", Pattern.CASE_INSENSITIVE);
    private static final Pattern SUB_QUERY_PATTERN = Pattern.compile("(?i)\\(\\s*select\\s+[^)]+\\)");

    @Override
    protected @NonNull RuleTargetSelector buildTargetSelector() {
        return RuleTargetSelector.forTypes(ASTSoqlExpression.class);
    }

    @Override
    public Object visit(ASTSoqlExpression node, Object data) {
        String query = node.getQuery();

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
                    .filter(p -> ASTAnnotationParameter.SEE_ALL_DATA.equalsIgnoreCase(p.getName()))
                    .firstOpt()
                    .map(ASTAnnotationParameter::getBooleanValue));
            boolean classSeeAllData = classAnnotation.flatMap(m -> m.children(ASTAnnotationParameter.class)
                    .filter(p -> ASTAnnotationParameter.SEE_ALL_DATA.equalsIgnoreCase(p.getName()))
                    .firstOpt()
                    .map(ASTAnnotationParameter::getBooleanValue))
                    .orElse(false);

            if (methodSeeAllData.isPresent()) {
                if (!methodSeeAllData.get()) {
                    return null;
                }
            } else if (!classSeeAllData) {
                return null;
            }
        }

        Matcher subQueryMatcher = SUB_QUERY_PATTERN.matcher(query);
        StringBuffer queryWithoutSubQueries = new StringBuffer(query.length());
        while (subQueryMatcher.find()) {
            subQueryMatcher.appendReplacement(queryWithoutSubQueries, "(replaced_subquery)");
        }
        subQueryMatcher.appendTail(queryWithoutSubQueries);

        verifyQuery(asCtx(data), node, queryWithoutSubQueries.toString());

        return data;
    }

    private void verifyQuery(RuleContext ctx, ASTSoqlExpression node, String query) {
        int occurrencesSelect = countOccurrences(SELECT_PATTERN, query);
        int occurrencesWhereOrLimit = countOccurrences(RESTRICTIVE_PATTERN, query);

        if (occurrencesSelect > 0 && occurrencesWhereOrLimit == 0) {
            ctx.addViolation(node);
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
