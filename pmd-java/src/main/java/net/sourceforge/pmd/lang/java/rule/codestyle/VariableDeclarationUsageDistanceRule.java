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
    private static final PropertyDescriptor<Boolean> IGNORE_VARIABLES_AT_TOP_OF_BLOCK =
        PropertyFactory.booleanProperty("ignoreVariablesAtTopOfBlock")
            .desc("Ignore variables that are declared together with other variables at the very top of a "
                + "block (e.g. method or loop), before any other statement. This supports the coding "
                + "convention of declaring all local variables upfront.")
            .defaultValue(false)
            .build();

    public VariableDeclarationUsageDistanceRule() {
        super(ASTLocalVariableDeclaration.class);
    }

    @Override
    public Object visit(ASTLocalVariableDeclaration node, Object data) {
        if (node.getParent() instanceof ASTForInit
            || node.getParent() instanceof ASTResource
            || node.isFinal()
            || getProperty(IGNORE_VARIABLES_AT_TOP_OF_BLOCK) && isPartOfLeadingDeclarationGroup(node)) {
            // those don't count
            return null;
        }
        int maxDistance = getProperty(MAX_DISTANCE);
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
            if (distance > maxDistance) {
                asCtx(data).addViolation(node, id.getName(), distance, maxDistance);
            }
        }

        return null;
    }

    /** Returns all the statements following the given local var declaration. */
    private static NodeStream<ASTStatement> statementsAfter(ASTLocalVariableDeclaration node) {
        return node.asStream().followingSiblings().filterIs(ASTStatement.class);
    }

    /**
     * Returns true if the given declaration is declared alongside other variable declarations
     * at the very top of its enclosing block, before any other statement. This is the case if
     * all the statements preceding it in the block are themselves local variable declarations,
     * and it is adjacent to at least one other declaration (either before or after it).
     */
    private static boolean isPartOfLeadingDeclarationGroup(ASTLocalVariableDeclaration node) {
        NodeStream<ASTStatement> precedingStatements = node.asStream().precedingSiblings().filterIs(ASTStatement.class);
        if (!precedingStatements.all(stmt -> stmt instanceof ASTLocalVariableDeclaration)) {
            return false;
        }
        if (!precedingStatements.isEmpty()) {
            return true;
        }
        ASTStatement nextStatement = node.asStream().followingSiblings().filterIs(ASTStatement.class).first();
        return nextStatement instanceof ASTLocalVariableDeclaration;
    }
}
