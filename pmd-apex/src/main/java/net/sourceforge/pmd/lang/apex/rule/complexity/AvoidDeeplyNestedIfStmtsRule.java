/**
 * BSD-style license; for more info see http://pmd.sourceforge.net/license.html
 */

package net.sourceforge.pmd.lang.apex.rule.complexity;

import net.sourceforge.pmd.lang.apex.ast.ASTIfBlockStatement;
import net.sourceforge.pmd.lang.apex.ast.ASTUserClass;
import net.sourceforge.pmd.lang.apex.rule.AbstractApexRule;
import net.sourceforge.pmd.properties.IntegerProperty;

public class AvoidDeeplyNestedIfStmtsRule extends AbstractApexRule {

    private int depth;
    private int depthLimit;

    private static final IntegerProperty PROBLEM_DEPTH_DESCRIPTOR 
            = IntegerProperty.builder("problemDepth")
                             .desc("The if statement depth reporting threshold")
                             .min(1).max(25).defalt(3).uiOrder(1.0f).build();

    public AvoidDeeplyNestedIfStmtsRule() {
        definePropertyDescriptor(PROBLEM_DEPTH_DESCRIPTOR);

        setProperty(CODECLIMATE_CATEGORIES, "Complexity");
        // Note: Remedy needs better OO design and therefore high effort
        setProperty(CODECLIMATE_REMEDIATION_MULTIPLIER, 200);
        setProperty(CODECLIMATE_BLOCK_HIGHLIGHTING, false);
    }

    public Object visit(ASTUserClass node, Object data) {
        depth = 0;
        depthLimit = getProperty(PROBLEM_DEPTH_DESCRIPTOR);

        return super.visit(node, data);
    }

    public Object visit(ASTIfBlockStatement node, Object data) {
        depth++;

        super.visit(node, data);
        if (depth == depthLimit) {
            addViolation(data, node);
        }
        depth--;

        return data;
    }
}
