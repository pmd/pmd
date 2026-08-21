/*
 * BSD-style license; for more info see http://pmd.sourceforge.net/license.html
 */

package net.sourceforge.pmd.lang.kotlin.rule.design;

import static net.sourceforge.pmd.properties.NumericConstraints.positive;

import org.checkerframework.checker.nullness.qual.NonNull;

import net.sourceforge.pmd.lang.kotlin.AbstractKotlinRule;
import net.sourceforge.pmd.lang.kotlin.ast.KotlinNode;
import net.sourceforge.pmd.lang.kotlin.ast.KotlinParser.KtClassParameters;
import net.sourceforge.pmd.lang.kotlin.ast.KotlinParser.KtFunctionDeclaration;
import net.sourceforge.pmd.lang.kotlin.ast.KotlinParser.KtFunctionValueParameters;
import net.sourceforge.pmd.lang.kotlin.ast.KotlinParser.KtPrimaryConstructor;
import net.sourceforge.pmd.lang.kotlin.ast.KotlinParser.KtSecondaryConstructor;
import net.sourceforge.pmd.lang.kotlin.ast.KotlinVisitor;
import net.sourceforge.pmd.lang.kotlin.ast.KotlinVisitorBase;
import net.sourceforge.pmd.lang.rule.RuleTargetSelector;
import net.sourceforge.pmd.properties.PropertyDescriptor;
import net.sourceforge.pmd.properties.PropertyFactory;
import net.sourceforge.pmd.reporting.RuleContext;

/**
 * Detects functions and constructors with an excessive number of parameters.
 * Long parameter lists are hard to maintain and suggest the method is doing too much.
 */
public class ExcessiveParameterListRule extends AbstractKotlinRule {

    private static final PropertyDescriptor<Integer> THRESHOLD_DESCRIPTOR =
        PropertyFactory.intProperty("threshold")
                       .desc("The parameter count reporting threshold")
                       .require(positive())
                       .defaultValue(10)
                       .build();

    public ExcessiveParameterListRule() {
        definePropertyDescriptor(THRESHOLD_DESCRIPTOR);
    }

    @Override
    public KotlinVisitor<RuleContext, ?> buildVisitor() {
        return new Visitor();
    }

    @Override
    protected @NonNull RuleTargetSelector buildTargetSelector() {
        return RuleTargetSelector.forTypes(
            KtFunctionDeclaration.class,
            KtPrimaryConstructor.class,
            KtSecondaryConstructor.class
        );
    }

    private final class Visitor extends KotlinVisitorBase<RuleContext, Void> {

        @Override
        public Void visitFunctionDeclaration(KtFunctionDeclaration node, RuleContext data) {
            checkFunctionValueParameters(node.functionValueParameters(), node, data);
            return null;
        }

        @Override
        public Void visitPrimaryConstructor(KtPrimaryConstructor node, RuleContext data) {
            checkClassParameters(node.classParameters(), node, data);
            return null;
        }

        @Override
        public Void visitSecondaryConstructor(KtSecondaryConstructor node, RuleContext data) {
            checkFunctionValueParameters(node.functionValueParameters(), node, data);
            return null;
        }

        private void checkFunctionValueParameters(KtFunctionValueParameters params,
                                                  KotlinNode reportNode,
                                                  RuleContext data) {
            if (params == null) {
                return;
            }
            int count = params.functionValueParameter().size();
            reportIfExceedsThreshold(count, reportNode, data);
        }

        private void checkClassParameters(KtClassParameters params,
                                          KotlinNode reportNode,
                                          RuleContext data) {
            if (params == null) {
                return;
            }
            int count = params.classParameter().size();
            reportIfExceedsThreshold(count, reportNode, data);
        }

        private void reportIfExceedsThreshold(int count, KotlinNode reportNode, RuleContext data) {
            int threshold = getProperty(THRESHOLD_DESCRIPTOR);
            if (count > threshold) {
                data.addViolation(reportNode, count, threshold);
            }
        }
    }
}
