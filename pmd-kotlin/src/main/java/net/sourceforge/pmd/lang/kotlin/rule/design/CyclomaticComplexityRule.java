/*
 * BSD-style license; for more info see http://pmd.sourceforge.net/license.html
 */

package net.sourceforge.pmd.lang.kotlin.rule.design;

import static net.sourceforge.pmd.properties.NumericConstraints.positive;

import org.checkerframework.checker.nullness.qual.NonNull;

import net.sourceforge.pmd.lang.kotlin.AbstractKotlinRule;
import net.sourceforge.pmd.lang.kotlin.ast.KotlinParser.KtCatchBlock;
import net.sourceforge.pmd.lang.kotlin.ast.KotlinParser.KtConjunction;
import net.sourceforge.pmd.lang.kotlin.ast.KotlinParser.KtDisjunction;
import net.sourceforge.pmd.lang.kotlin.ast.KotlinParser.KtDoWhileStatement;
import net.sourceforge.pmd.lang.kotlin.ast.KotlinParser.KtElvisExpression;
import net.sourceforge.pmd.lang.kotlin.ast.KotlinParser.KtForStatement;
import net.sourceforge.pmd.lang.kotlin.ast.KotlinParser.KtFunctionDeclaration;
import net.sourceforge.pmd.lang.kotlin.ast.KotlinParser.KtIfExpression;
import net.sourceforge.pmd.lang.kotlin.ast.KotlinParser.KtWhenEntry;
import net.sourceforge.pmd.lang.kotlin.ast.KotlinParser.KtWhileStatement;
import net.sourceforge.pmd.lang.kotlin.ast.KotlinVisitor;
import net.sourceforge.pmd.lang.kotlin.ast.KotlinVisitorBase;
import net.sourceforge.pmd.lang.kotlin.util.KotlinAstUtil;
import net.sourceforge.pmd.lang.rule.RuleTargetSelector;
import net.sourceforge.pmd.properties.PropertyDescriptor;
import net.sourceforge.pmd.properties.PropertyFactory;
import net.sourceforge.pmd.reporting.RuleContext;

/**
 * Measures cyclomatic complexity of Kotlin functions.
 * Complexity is 1 (base) plus 1 for each: {@code if}, conditional {@code when} branch,
 * {@code for}, {@code while}, {@code do-while}, {@code catch}, {@code &&} operator,
 * {@code ||} operator, and {@code ?:} (Elvis) operator.
 * Nested function declarations are counted separately and do not contribute to the outer function.
 */
public class CyclomaticComplexityRule extends AbstractKotlinRule {

    private static final PropertyDescriptor<Integer> THRESHOLD_DESCRIPTOR =
        PropertyFactory.intProperty("methodReportLevel")
                       .desc("Cyclomatic complexity reporting threshold")
                       .require(positive())
                       .defaultValue(10)
                       .build();

    public CyclomaticComplexityRule() {
        definePropertyDescriptor(THRESHOLD_DESCRIPTOR);
    }

    @Override
    public KotlinVisitor<RuleContext, ?> buildVisitor() {
        return new Visitor();
    }

    @Override
    protected @NonNull RuleTargetSelector buildTargetSelector() {
        return RuleTargetSelector.forTypes(KtFunctionDeclaration.class);
    }

    private final class Visitor extends KotlinVisitorBase<RuleContext, Void> {

        @Override
        public Void visitFunctionDeclaration(KtFunctionDeclaration node, RuleContext data) {
            int complexity = computeComplexity(node);
            int threshold = getProperty(THRESHOLD_DESCRIPTOR);
            if (complexity > threshold) {
                String funcName = KotlinAstUtil.getIdentifierText(node.simpleIdentifier());
                data.addViolation(node, funcName, complexity, threshold);
            }
            return visitChildren(node, data);
        }

        private int computeComplexity(KtFunctionDeclaration node) {
            int complexity = 1;

            complexity += node.descendants(KtIfExpression.class)
                              .filter(n -> KotlinAstUtil.isDirectDescendantOf(n, node))
                              .count();

            complexity += node.descendants(KtWhenEntry.class)
                              .filter(n -> KotlinAstUtil.isDirectDescendantOf(n, node))
                              .filter(n -> n.ELSE() == null)
                              .count();

            complexity += node.descendants(KtForStatement.class)
                              .filter(n -> KotlinAstUtil.isDirectDescendantOf(n, node))
                              .count();

            complexity += node.descendants(KtWhileStatement.class)
                              .filter(n -> KotlinAstUtil.isDirectDescendantOf(n, node))
                              .count();

            complexity += node.descendants(KtDoWhileStatement.class)
                              .filter(n -> KotlinAstUtil.isDirectDescendantOf(n, node))
                              .count();

            complexity += node.descendants(KtCatchBlock.class)
                              .filter(n -> KotlinAstUtil.isDirectDescendantOf(n, node))
                              .count();

            complexity += node.descendants(KtDisjunction.class)
                              .filter(n -> KotlinAstUtil.isDirectDescendantOf(n, node))
                              .toList().stream()
                              .mapToInt(n -> n.DISJ().size())
                              .sum();

            complexity += node.descendants(KtConjunction.class)
                              .filter(n -> KotlinAstUtil.isDirectDescendantOf(n, node))
                              .toList().stream()
                              .mapToInt(n -> n.CONJ().size())
                              .sum();

            complexity += node.descendants(KtElvisExpression.class)
                              .filter(n -> KotlinAstUtil.isDirectDescendantOf(n, node))
                              .toList().stream()
                              .mapToInt(n -> n.elvis().size())
                              .sum();

            return complexity;
        }
    }
}
