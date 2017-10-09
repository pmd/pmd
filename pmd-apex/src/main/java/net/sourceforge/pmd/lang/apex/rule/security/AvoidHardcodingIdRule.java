/**
 * BSD-style license; for more info see http://pmd.sourceforge.net/license.html
 */

package net.sourceforge.pmd.lang.apex.rule.security;

import java.util.regex.Pattern;

import net.sourceforge.pmd.lang.apex.ast.ASTAssignmentExpression;
import net.sourceforge.pmd.lang.apex.ast.ASTLiteralExpression;
import net.sourceforge.pmd.lang.apex.ast.AbstractApexNode;
import net.sourceforge.pmd.lang.apex.rule.AbstractApexRule;

public class AvoidHardcodingIdRule extends AbstractApexRule {
    private static final Pattern PATTERN = Pattern.compile("^[a-zA-Z0-9]{5}[0][a-zA-Z0-9]{9,12}$", Pattern.CASE_INSENSITIVE);
    
    public AvoidHardcodingIdRule() {
        setProperty(CODECLIMATE_CATEGORIES, "Security");
        setProperty(CODECLIMATE_REMEDIATION_MULTIPLIER, 100);
        setProperty(CODECLIMATE_BLOCK_HIGHLIGHTING, false);
    }

    @Override
    public Object visit(ASTAssignmentExpression node, Object data) {
        findHardcodedId(node, data);
        return data;
    }

    private void findHardcodedId(AbstractApexNode<?> node, Object data) {
        ASTLiteralExpression literalNode = node.getFirstChildOfType(ASTLiteralExpression.class);
        
        if (literalNode != null) {
            Object o = literalNode.getNode().getLiteral();
            if (o instanceof String) {
                String literal = (String) o;
                if (PATTERN.matcher(literal).matches()) {
                    addViolation(data, literalNode);
                }
            }
        }
    }
}
