/**
 * BSD-style license; for more info see http://pmd.sourceforge.net/license.html
 */

package net.sourceforge.pmd.lang.apex.rule.design;


import static net.sourceforge.pmd.properties.constraints.NumericConstraints.positive;

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


/**
 * Cyclomatic complexity rule using metrics. Uses Wmc to report classes.
 *
 * @author Clément Fournier
 */
public class CyclomaticComplexityRule extends AbstractApexRule {

    private static final PropertyDescriptor<Integer> CLASS_LEVEL_DESCRIPTOR
        = PropertyFactory.intProperty("classReportLevel")
                         .desc("Total class complexity reporting threshold")
                         .require(positive())
                         .defaultValue(40)
                         .build();

    private static final PropertyDescriptor<Integer> METHOD_LEVEL_DESCRIPTOR
        = PropertyFactory.intProperty("methodReportLevel")
                         .desc("Cyclomatic complexity reporting threshold")
                         .require(positive())
                         .defaultValue(10)
                         .build();

    private Deque<String> classNames = new ArrayDeque<>();
    private boolean inTrigger;


    public CyclomaticComplexityRule() {
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

        if (ApexMetrics.WEIGHED_METHOD_COUNT.supports(node)) {
            int classWmc = MetricsUtil.computeMetric(ApexMetrics.WEIGHED_METHOD_COUNT, node);

            if (classWmc >= getProperty(CLASS_LEVEL_DESCRIPTOR)) {
                int classHighest = (int) MetricsUtil.computeStatistics(ApexMetrics.CYCLO, node.getMethods()).getMax();

                String[] messageParams = {"class",
                                          node.getImage(),
                                          " total",
                                          classWmc + " (highest " + classHighest + ")", };

                addViolation(data, node, messageParams);
            }
        }
        return data;
    }


    @Override
    public final Object visit(ASTMethod node, Object data) {

        if (ApexMetrics.CYCLO.supports(node)) {
            int cyclo = MetricsUtil.computeMetric(ApexMetrics.CYCLO, node);
            if (cyclo >= getProperty(METHOD_LEVEL_DESCRIPTOR)) {
                String opType = inTrigger ? "trigger"
                                          : node.getImage().equals(classNames.peek()) ? "constructor"
                                                                                      : "method";

                addViolation(data, node, new String[] {opType,
                                                       node.getQualifiedName().getOperation(),
                                                       "",
                                                       "" + cyclo, });
            }
        }

        return data;
    }

}
