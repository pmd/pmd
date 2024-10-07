/**
 * BSD-style license; for more info see http://pmd.sourceforge.net/license.html
 */

package net.sourceforge.pmd.lang.apex.rule.design;

import static net.sourceforge.pmd.properties.NumericConstraints.positive;

import java.util.ArrayDeque;
import java.util.Deque;

import net.sourceforge.pmd.lang.apex.ast.ASTMethod;
import net.sourceforge.pmd.lang.apex.ast.ASTUserClass;
import net.sourceforge.pmd.lang.apex.ast.ASTUserTrigger;
import net.sourceforge.pmd.lang.apex.metrics.ApexMetrics;
import net.sourceforge.pmd.lang.apex.rule.AbstractApexRule;
import net.sourceforge.pmd.lang.metrics.MetricsUtil;
import net.sourceforge.pmd.properties.PropertyDescriptor;
import net.sourceforge.pmd.properties.PropertyFactory;

public class CognitiveComplexityRule extends AbstractApexRule {

    private static final PropertyDescriptor<Integer> CLASS_LEVEL_DESCRIPTOR
            = PropertyFactory.intProperty("classReportLevel")
            .desc("Total class cognitive complexity reporting threshold")
            .require(positive())
            .defaultValue(50)
            .build();

    private static final PropertyDescriptor<Integer> METHOD_LEVEL_DESCRIPTOR
            = PropertyFactory.intProperty("methodReportLevel")
            .desc("Cognitive complexity reporting threshold")
            .require(positive())
            .defaultValue(15)
            .build();

    private Deque<String> classNames = new ArrayDeque<>();
    private boolean inTrigger;


    public CognitiveComplexityRule() {
        definePropertyDescriptor(CLASS_LEVEL_DESCRIPTOR);
        definePropertyDescriptor(METHOD_LEVEL_DESCRIPTOR);
    }


    @Override
    public Object visit(ASTUserTrigger node, Object data) {
        inTrigger = true;
        super.visit(node, data);
        inTrigger = false;
        return data;
    }


    @Override
    public Object visit(ASTUserClass node, Object data) {

        classNames.push(node.getSimpleName());
        super.visit(node, data);
        classNames.pop();

        if (ApexMetrics.COGNITIVE_COMPLEXITY.supports(node)) {
            int classCognitive = MetricsUtil.computeMetric(ApexMetrics.COGNITIVE_COMPLEXITY, node);

            Integer classLevelThreshold = getProperty(CLASS_LEVEL_DESCRIPTOR);
            if (classCognitive >= classLevelThreshold) {
                int classHighest = (int) MetricsUtil.computeStatistics(ApexMetrics.COGNITIVE_COMPLEXITY, node.getMethods()).getMax();

                String[] messageParams = {
                    "class",
                    node.getSimpleName(),
                    " total",
                    classCognitive + " (highest " + classHighest + ")",
                    String.valueOf(classLevelThreshold),
                };

                asCtx(data).addViolation(node, (Object[]) messageParams);
            }
        }
        return data;
    }


    @Override
    public final Object visit(ASTMethod node, Object data) {

        if (ApexMetrics.COGNITIVE_COMPLEXITY.supports(node)) {
            int cognitive = MetricsUtil.computeMetric(ApexMetrics.COGNITIVE_COMPLEXITY, node);
            Integer methodLevelThreshold = getProperty(METHOD_LEVEL_DESCRIPTOR);
            if (cognitive >= methodLevelThreshold) {
                String opType = inTrigger ? "trigger"
                        : node.getImage().equals(classNames.peek()) ? "constructor"
                        : "method";

                asCtx(data).addViolation(node, opType,
                        node.getQualifiedName().getOperation(),
                        "",
                        "" + cognitive,
                        String.valueOf(methodLevelThreshold));
            }
        }

        return data;
    }

}
