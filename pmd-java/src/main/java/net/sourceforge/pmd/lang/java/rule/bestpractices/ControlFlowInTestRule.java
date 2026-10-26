/*
 * BSD-style license; for more info see http://pmd.sourceforge.net/license.html
 */

package net.sourceforge.pmd.lang.java.rule.bestpractices;

import net.sourceforge.pmd.lang.ast.Node;
import net.sourceforge.pmd.lang.java.ast.ASTConditionalExpression;
import net.sourceforge.pmd.lang.java.ast.ASTIfStatement;
import net.sourceforge.pmd.lang.java.ast.ASTLoopStatement;
import net.sourceforge.pmd.lang.java.ast.ASTMethodDeclaration;
import net.sourceforge.pmd.lang.java.ast.ASTSwitchExpression;
import net.sourceforge.pmd.lang.java.ast.ASTSwitchStatement;
import net.sourceforge.pmd.lang.java.ast.ASTThrowStatement;
import net.sourceforge.pmd.lang.java.ast.ASTTryStatement;
import net.sourceforge.pmd.lang.java.ast.ASTTypeDeclaration;
import net.sourceforge.pmd.lang.java.rule.AbstractJavaRulechainRule;
import net.sourceforge.pmd.lang.java.rule.internal.TestFrameworksUtil;
import net.sourceforge.pmd.properties.PropertyDescriptor;
import net.sourceforge.pmd.properties.PropertyFactory;
import net.sourceforge.pmd.reporting.RuleContext;

/**
 * @since 7.29.0
 */
public class ControlFlowInTestRule extends AbstractJavaRulechainRule {

    private static final PropertyDescriptor<Boolean> ALLOW_LOOPS =
            PropertyFactory.booleanProperty("allowLoops")
                    .desc("Allow loops in test methods")
                    .defaultValue(true)
                    .build();

    public ControlFlowInTestRule() {
        super(ASTMethodDeclaration.class);
        definePropertyDescriptor(ALLOW_LOOPS);
    }

    @Override
    public Object visit(ASTMethodDeclaration method, Object data) {
        if (method.getBody() == null
                || !TestFrameworksUtil.isTestMethod(method)) {
            return null;
        }

        RuleContext context = (RuleContext) data;
        boolean allowLoops = getProperty(ALLOW_LOOPS);

        method.getBody().descendants()
                .crossFindBoundaries(true)
                .filter(node -> node.ancestors(ASTMethodDeclaration.class).first() == method)
                .filter(node -> node.ancestors(ASTTypeDeclaration.class).first()
                        == method.getEnclosingType())
                .filter(node -> isDisallowedControlFlow(node, allowLoops))
                .forEach(context::addViolation);

        return null;
    }

    private boolean isDisallowedControlFlow(Node node, boolean allowLoops) {
        if (node instanceof ASTIfStatement
                || node instanceof ASTConditionalExpression
                || node instanceof ASTSwitchStatement
                || node instanceof ASTSwitchExpression
                || node instanceof ASTThrowStatement) {
            return true;
        }

        if (node instanceof ASTTryStatement) {
            return ((ASTTryStatement) node).getCatchClauses().nonEmpty();
        }

        return !allowLoops && node instanceof ASTLoopStatement;
    }
}
