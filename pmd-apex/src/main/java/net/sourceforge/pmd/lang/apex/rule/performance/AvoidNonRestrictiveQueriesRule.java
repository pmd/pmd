/*
 * BSD-style license; for more info see http://pmd.sourceforge.net/license.html
 */

package net.sourceforge.pmd.lang.apex.rule.performance;

import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.checkerframework.checker.nullness.qual.NonNull;

import net.sourceforge.pmd.lang.apex.ast.ASTSoqlExpression;
import net.sourceforge.pmd.lang.apex.rule.AbstractApexRule;
import net.sourceforge.pmd.lang.rule.RuleTargetSelector;
import net.sourceforge.pmd.reporting.RuleContext;

public class AvoidNonRestrictiveQueriesRule extends AbstractApexRule {
    private static final Pattern RESTRICTIVE_PATTERN = Pattern.compile("(where )|(limit )", Pattern.CASE_INSENSITIVE);
    private static final Pattern SELECT_PATTERN = Pattern.compile("(select )", Pattern.CASE_INSENSITIVE);

    @Override
    protected @NonNull RuleTargetSelector buildTargetSelector() {
        return RuleTargetSelector.forTypes(ASTSoqlExpression.class);
    }

    @Override
    public Object visit(ASTSoqlExpression node, Object data) {
        String query = node.getQuery();

        Pattern subQueryPattern = Pattern.compile("(?i)\\(\\s*select\\s+[^)]+\\)");
        Matcher subQueryMatcher = subQueryPattern.matcher(query);
        List<String> subQueries = new ArrayList<>();
        StringBuffer queryWithoutSubQueries = new StringBuffer(query.length());
        while (subQueryMatcher.find()) {
            subQueries.add(subQueryMatcher.group());
            subQueryMatcher.appendReplacement(queryWithoutSubQueries, "replaced_subquery");
        }
        subQueryMatcher.appendTail(queryWithoutSubQueries);

        verifyQuery(asCtx(data), node, queryWithoutSubQueries.toString(), "");

        for (String subQuery : subQueries) {
            verifyQuery(asCtx(data), node, subQuery, "sub-");
        }

        return data;
    }

    private void verifyQuery(RuleContext ctx, ASTSoqlExpression node, String query, String messageArgument) {
        int occurrencesSelect = countOccurrences(SELECT_PATTERN, query);
        int occurrencesWhereOrLimit = countOccurrences(RESTRICTIVE_PATTERN, query);

        if (occurrencesSelect > 0 && occurrencesWhereOrLimit == 0) {
            ctx.addViolation(node, messageArgument);
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
