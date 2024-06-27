/*
 * BSD-style license; for more info see http://pmd.sourceforge.net/license.html
 */

package net.sourceforge.pmd.lang.apex.rule.performance;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

import net.sourceforge.pmd.lang.apex.ast.ASTSoqlExpression;
import net.sourceforge.pmd.lang.apex.rule.AbstractApexRule;

public class AvoidNonRestrictiveQueriesRule extends AbstractApexRule {
    private static final Pattern RESTRICTIVE_PATTERN = Pattern.compile("(where )|(limit )", Pattern.CASE_INSENSITIVE);
    private static final Pattern SELECT_PATTERN = Pattern.compile("(select )", Pattern.CASE_INSENSITIVE);

    @Override
    public Object visit(ASTSoqlExpression node, Object data) {
        Integer occurencesSelect = 0;
        Integer occurencesWhereOrLimit = 0;

        String query = node.getQuery();
        Matcher matcherRestrictive = RESTRICTIVE_PATTERN.matcher(query);
        Matcher matcherSelect = SELECT_PATTERN.matcher(query);
        while (matcherRestrictive.find()) {
            occurencesWhereOrLimit++;
        }
        while (matcherSelect.find()) {
            occurencesSelect++;
        }
        if (occurencesSelect > occurencesWhereOrLimit) {
            asCtx(data).addViolation(node);
        }
        return data;
    }
}
