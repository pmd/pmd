/**
 * BSD-style license; for more info see http://pmd.sourceforge.net/license.html
 */

package net.sourceforge.pmd.lang.apex.rule.design;

import static net.sourceforge.pmd.properties.constraints.NumericConstraints.positive;

import java.util.Stack;

import net.sourceforge.pmd.lang.apex.ast.ASTMethod;
import net.sourceforge.pmd.lang.apex.ast.ASTUserClass;
import net.sourceforge.pmd.lang.apex.ast.ASTUserTrigger;
import net.sourceforge.pmd.lang.apex.metrics.ApexMetrics;
import net.sourceforge.pmd.lang.apex.metrics.api.ApexClassMetricKey;
import net.sourceforge.pmd.lang.apex.metrics.api.ApexOperationMetricKey;
import net.sourceforge.pmd.lang.apex.rule.AbstractApexRule;
import net.sourceforge.pmd.lang.metrics.MetricsUtil;
import net.sourceforge.pmd.lang.metrics.ResultOption;
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

    private Stack<String> classNames = new Stack<>();
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

        classNames.push(node.getImage());
        super.visit(node, data);
        classNames.pop();

        if (ApexClassMetricKey.COGNITIVE.supports(node)) {
            int classCognitive = (int) MetricsUtil.computeMetric(ApexClassMetricKey.COGNITIVE, node);

            if (classCognitive >= getProperty(CLASS_LEVEL_DESCRIPTOR)) {
                int classHighest = (int) ApexMetrics.get(ApexOperationMetricKey.COGNITIVE, node, ResultOption.HIGHEST);

                String[] messageParams = {
                    "class",
                    node.getImage(),
                    " total",
                    classCognitive + " (highest " + classHighest + ")",
                };

                addViolation(data, node, messageParams);
            }
        }
        return data;
    }


    @Override
    public final Object visit(ASTMethod node, Object data) {

        if (ApexOperationMetricKey.COGNITIVE.supports(node)) {
            int cognitive = (int) MetricsUtil.computeMetric(ApexOperationMetricKey.COGNITIVE, node);
            if (cognitive >= getProperty(METHOD_LEVEL_DESCRIPTOR)) {
                String opType = inTrigger ? "trigger"
                        : node.getImage().equals(classNames.peek()) ? "constructor"
                        : "method";

                addViolation(data, node, new String[] {
                    opType,
                    node.getQualifiedName().getOperation(),
                    "",
                    "" + cognitive,
                });
            }
        }

        return data;
    }

}
