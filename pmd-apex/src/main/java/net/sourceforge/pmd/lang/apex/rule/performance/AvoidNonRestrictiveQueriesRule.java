/**
 * BSD-style license; for more info see http://pmd.sourceforge.net/license.html
 */

package net.sourceforge.pmd.lang.apex.rule.performance;

import java.util.regex.Pattern;

import net.sourceforge.pmd.lang.apex.ast.ASTSoqlExpression;
import net.sourceforge.pmd.lang.apex.rule.AbstractApexRule;

public class AvoidNonRestrictiveQueriesRule extends AbstractApexRule {
    private static final Pattern RESTRICTIVE_PATTERN = Pattern.compile("(where |limit )", Pattern.CASE_INSENSITIVE);
    private static final Pattern SELECT_PATTERN = Pattern.compile("(select )", Pattern.CASE_INSENSITIVE);
    
    public AvoidNonRestrictiveQueriesRule() {
        setProperty(CODECLIMATE_CATEGORIES, "Performance");
        setProperty(CODECLIMATE_REMEDIATION_MULTIPLIER, 100);
        setProperty(CODECLIMATE_BLOCK_HIGHLIGHTING, false);
    }

    @Override
    public Object visit(ASTSoqlExpression node, Object data) {
        Integer occurencesSelect = 0;
        Integer occurencesWhereOrLimit = 0;

        Object o = node.getNode().getRawQuery();
        if (o instanceof String) {
            String query = (String) o;
            while (RESTRICTIVE_PATTERN.matcher(query).find()) {
                occurencesWhereOrLimit++;
            }
            while (SELECT_PATTERN.matcher(query).find()) {
                occurencesSelect++;
            }
            if (occurencesSelect > occurencesWhereOrLimit) {
                addViolation(data, node);
            }
        }
        return data;
    }
}
