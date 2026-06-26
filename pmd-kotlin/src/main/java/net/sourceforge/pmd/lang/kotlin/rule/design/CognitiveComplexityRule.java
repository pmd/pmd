/*
 * BSD-style license; for more info see http://pmd.sourceforge.net/license.html
 */

package net.sourceforge.pmd.lang.kotlin.rule.design;

import static net.sourceforge.pmd.properties.NumericConstraints.positive;

import java.util.List;

import org.checkerframework.checker.nullness.qual.NonNull;

import net.sourceforge.pmd.lang.kotlin.AbstractKotlinRule;
import net.sourceforge.pmd.lang.kotlin.ast.KotlinNode;
import net.sourceforge.pmd.lang.kotlin.ast.KotlinParser.KtCatchBlock;
import net.sourceforge.pmd.lang.kotlin.ast.KotlinParser.KtConjunction;
import net.sourceforge.pmd.lang.kotlin.ast.KotlinParser.KtControlStructureBody;
import net.sourceforge.pmd.lang.kotlin.ast.KotlinParser.KtDisjunction;
import net.sourceforge.pmd.lang.kotlin.ast.KotlinParser.KtDoWhileStatement;
import net.sourceforge.pmd.lang.kotlin.ast.KotlinParser.KtElvis;
import net.sourceforge.pmd.lang.kotlin.ast.KotlinParser.KtElvisExpression;
import net.sourceforge.pmd.lang.kotlin.ast.KotlinParser.KtForStatement;
import net.sourceforge.pmd.lang.kotlin.ast.KotlinParser.KtFunctionDeclaration;
import net.sourceforge.pmd.lang.kotlin.ast.KotlinParser.KtFunctionLiteral;
import net.sourceforge.pmd.lang.kotlin.ast.KotlinParser.KtIfExpression;
import net.sourceforge.pmd.lang.kotlin.ast.KotlinParser.KtPrimaryConstructor;
import net.sourceforge.pmd.lang.kotlin.ast.KotlinParser.KtSecondaryConstructor;
import net.sourceforge.pmd.lang.kotlin.ast.KotlinParser.KtWhenEntry;
import net.sourceforge.pmd.lang.kotlin.ast.KotlinParser.KtWhenExpression;
import net.sourceforge.pmd.lang.kotlin.ast.KotlinParser.KtWhileStatement;
import net.sourceforge.pmd.lang.kotlin.ast.KotlinVisitor;
import net.sourceforge.pmd.lang.kotlin.ast.KotlinVisitorBase;
import net.sourceforge.pmd.lang.kotlin.util.KotlinAstUtil;
import net.sourceforge.pmd.lang.rule.RuleTargetSelector;
import net.sourceforge.pmd.properties.PropertyDescriptor;
import net.sourceforge.pmd.properties.PropertyFactory;
import net.sourceforge.pmd.reporting.RuleContext;

/**
 * Measures cognitive complexity of Kotlin functions.
 *
 * <p>Cognitive complexity differs from cyclomatic complexity by penalising
 * deeply-nested decision points. Each structural construct (if, for, while,
 * do-while, catch, when, ?:) contributes {@code 1 + nestingLevel} to the score
 * and increases the nesting level for its body. Else/else-if contribute 1
 * without a nesting penalty (hybrid). Boolean operator sequences (AND, OR)
 * each contribute 1 (fundamental). Lambda bodies increase nesting without
 * adding complexity. Nested local functions are analysed separately.
 */
public class CognitiveComplexityRule extends AbstractKotlinRule {

    private static final PropertyDescriptor<Integer> REPORT_LEVEL_DESCRIPTOR =
        PropertyFactory.intProperty("reportLevel")
                       .desc("Cognitive complexity reporting threshold")
                       .require(positive())
                       .defaultValue(15)
                       .build();

    public CognitiveComplexityRule() {
        definePropertyDescriptor(REPORT_LEVEL_DESCRIPTOR);
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
            State state = new State();
            visitForComplexity(node, state, node);
            int reportLevel = getProperty(REPORT_LEVEL_DESCRIPTOR);
            if (state.getComplexity() >= reportLevel) {
                String funcName = KotlinAstUtil.getIdentifierText(node.simpleIdentifier());
                data.addViolation(node, funcName, state.getComplexity(), reportLevel);
            }
            return visitChildren(node, data);
        }
    }

    private void visitForComplexity(KotlinNode node, State state, KotlinNode scope) {
        if (isNestedScope(node, scope)) {
            return;
        }
        if (node instanceof KtFunctionLiteral) {
            state.increaseNesting();
            visitChildrenForComplexity(node, state, scope);
            state.decreaseNesting();
            return;
        }
        if (node instanceof KtIfExpression) {
            visitIf((KtIfExpression) node, state, scope);
            return;
        }
        if (node instanceof KtWhenExpression) {
            visitWhen((KtWhenExpression) node, state, scope);
            return;
        }
        if (node instanceof KtForStatement
                || node instanceof KtWhileStatement
                || node instanceof KtDoWhileStatement
                || node instanceof KtCatchBlock) {
            visitStructural(node, state, scope);
            return;
        }
        if (node instanceof KtDisjunction) {
            visitDisjunction((KtDisjunction) node, state, scope);
            return;
        }
        if (node instanceof KtConjunction) {
            visitConjunction((KtConjunction) node, state, scope);
            return;
        }
        if (node instanceof KtElvisExpression) {
            visitElvis((KtElvisExpression) node, state, scope);
            return;
        }
        visitChildrenForComplexity(node, state, scope);
    }

    private boolean isNestedScope(KotlinNode node, KotlinNode scope) {
        return node != scope
            && (node instanceof KtFunctionDeclaration
                || node instanceof KtPrimaryConstructor
                || node instanceof KtSecondaryConstructor);
    }

    private void visitStructural(KotlinNode node, State state, KotlinNode scope) {
        state.structural();
        visitChildrenForComplexity(node, state, scope);
        state.decreaseNesting();
    }

    private void visitIf(KtIfExpression node, State state, KotlinNode scope) {
        boolean isElseIf = isElseIf(node);

        if (node.expression() != null) {
            visitForComplexity(node.expression(), state, scope);
        }

        List<KtControlStructureBody> bodies = node.controlStructureBody();

        if (!isElseIf) {
            state.structural();
        }
        if (!bodies.isEmpty()) {
            visitForComplexity(bodies.get(0), state, scope);
        }
        if (!isElseIf) {
            state.decreaseNesting();
        }

        if (node.ELSE() != null && bodies.size() > 1) {
            state.hybrid();
            visitForComplexity(bodies.get(1), state, scope);
            state.decreaseNesting();
        }
    }

    private boolean isElseIf(KtIfExpression node) {
        KotlinNode parent = node.getParent();
        if (!(parent instanceof KtControlStructureBody)) {
            return false;
        }
        KotlinNode grandParent = parent.getParent();
        if (!(grandParent instanceof KtIfExpression)) {
            return false;
        }
        KtIfExpression parentIf = (KtIfExpression) grandParent;
        List<KtControlStructureBody> bodies = parentIf.controlStructureBody();
        return parentIf.ELSE() != null && bodies.size() > 1 && bodies.get(1) == parent;
    }

    private void visitWhen(KtWhenExpression node, State state, KotlinNode scope) {
        if (node.whenSubject() != null) {
            visitForComplexity(node.whenSubject(), state, scope);
        }
        state.structural();
        for (KtWhenEntry entry : node.whenEntry()) {
            visitForComplexity(entry, state, scope);
        }
        state.decreaseNesting();
    }

    private void visitDisjunction(KtDisjunction node, State state, KotlinNode scope) {
        if (!node.DISJ().isEmpty()) {
            state.fundamental();
        }
        visitChildrenForComplexity(node, state, scope);
    }

    private void visitConjunction(KtConjunction node, State state, KotlinNode scope) {
        if (!node.CONJ().isEmpty()) {
            state.fundamental();
        }
        visitChildrenForComplexity(node, state, scope);
    }

    private void visitElvis(KtElvisExpression node, State state, KotlinNode scope) {
        for (KtElvis ignored : node.elvis()) {
            state.fundamental();
        }
        visitChildrenForComplexity(node, state, scope);
    }

    private void visitChildrenForComplexity(KotlinNode node, State state, KotlinNode scope) {
        for (int i = 0; i < node.getNumChildren(); i++) {
            visitForComplexity((KotlinNode) node.getChild(i), state, scope);
        }
    }

    private static final class State {

        private int complexity = 0;
        private int nestingLevel = 0;

        void structural() {
            complexity += 1 + nestingLevel;
            nestingLevel++;
        }

        void hybrid() {
            complexity++;
            nestingLevel++;
        }

        void fundamental() {
            complexity++;
        }

        void increaseNesting() {
            nestingLevel++;
        }

        void decreaseNesting() {
            nestingLevel--;
        }

        int getComplexity() {
            return complexity;
        }
    }
}
