/*
 * BSD-style license; for more info see http://pmd.sourceforge.net/license.html
 */

package net.sourceforge.pmd.lang.java.rule.codestyle;

import static java.util.Collections.emptySet;

import net.sourceforge.pmd.lang.ast.NodeStream;
import net.sourceforge.pmd.lang.java.ast.ASTExpression;
import net.sourceforge.pmd.lang.java.ast.ASTForInit;
import net.sourceforge.pmd.lang.java.ast.ASTLocalVariableDeclaration;
import net.sourceforge.pmd.lang.java.ast.ASTResource;
import net.sourceforge.pmd.lang.java.ast.ASTStatement;
import net.sourceforge.pmd.lang.java.ast.ASTVariableId;
import net.sourceforge.pmd.lang.java.ast.internal.JavaAstUtils;
import net.sourceforge.pmd.lang.java.rule.AbstractJavaRulechainRule;
import net.sourceforge.pmd.lang.java.rule.internal.JavaRuleUtil;
import net.sourceforge.pmd.properties.PropertyDescriptor;
import net.sourceforge.pmd.properties.PropertyFactory;

/**
 * Checks for variables in methods that are defined long before they are used.
 * @since 7.25.0
 */
public class VariableDeclarationUsageDistanceRule extends AbstractJavaRulechainRule {

    private static final int DEFAULT_DISTANCE = 7;
    private static final PropertyDescriptor<Integer> MAX_DISTANCE =
        PropertyFactory.intProperty("maxDistance")
            .desc("Maximum distance between declaration and usage")
            .defaultValue(DEFAULT_DISTANCE)
            .build();

    public VariableDeclarationUsageDistanceRule() {
        super(ASTLocalVariableDeclaration.class);
    }

    @Override
    public Object visit(ASTLocalVariableDeclaration node, Object data) {
        if (node.getParent() instanceof ASTForInit
            || node.getParent() instanceof ASTResource
            || node.isFinal()) {
            // those don't count
            return null;
        }

        for (ASTVariableId id : node) {
            ASTExpression initializer = id.getInitializer();

            if (JavaAstUtils.isNeverUsed(id) // avoid the duplicate with unused variables
                || JavaRuleUtil.cannotBeMoved(initializer)
                || JavaRuleUtil.hasSideEffect(initializer, emptySet())) {
                continue;
            }
            int distance = 1;
            for (ASTStatement stmt : statementsAfter(node)) {
                if (JavaRuleUtil.hasReferencesIn(stmt, id)) {
                    break;
                }
                distance++;
            }
            if (distance > getProperty(MAX_DISTANCE)) {
                asCtx(data).addViolation(node, id.getName());
            }
        }

        return null;
    }

    /** Returns all the statements following the given local var declaration. */
    private static NodeStream<ASTStatement> statementsAfter(ASTLocalVariableDeclaration node) {
        return node.asStream().followingSiblings().filterIs(ASTStatement.class);
    }
}
