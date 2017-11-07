/**
 * BSD-style license; for more info see http://pmd.sourceforge.net/license.html
 */

package net.sourceforge.pmd.lang.apex.rule.design;


import java.util.Stack;

import net.sourceforge.pmd.lang.apex.ast.ASTMethod;
import net.sourceforge.pmd.lang.apex.ast.ASTUserClass;
import net.sourceforge.pmd.lang.apex.metrics.ApexMetrics;
import net.sourceforge.pmd.lang.apex.metrics.api.ApexClassMetricKey;
import net.sourceforge.pmd.lang.apex.metrics.api.ApexOperationMetricKey;
import net.sourceforge.pmd.lang.apex.rule.AbstractApexRule;
import net.sourceforge.pmd.lang.metrics.ResultOption;
import net.sourceforge.pmd.properties.IntegerProperty;

/**
 * Cyclomatic complexity rule using metrics. Uses Wmc to report classes (the Java rule will be updated as well in an
 * upcoming PR)
 *
 * @author Clément Fournier
 */
public class CyclomaticComplexityRule extends AbstractApexRule {

    private static final IntegerProperty CLASS_LEVEL_DESCRIPTOR = new IntegerProperty(
        "classReportLevel", "Total class complexity reporting threshold", 1, 200, 40, 1.0f);

    private static final IntegerProperty METHOD_LEVEL_DESCRIPTOR = new IntegerProperty(
        "methodReportLevel", "Cyclomatic complexity reporting threshold", 1, 30, 10, 1.0f);

    Stack<String> classNames = new Stack<>();


    public CyclomaticComplexityRule() {
        definePropertyDescriptor(CLASS_LEVEL_DESCRIPTOR);
        definePropertyDescriptor(METHOD_LEVEL_DESCRIPTOR);
    }


    @Override
    public Object visit(ASTUserClass node, Object data) {

        classNames.push(node.getImage());
        super.visit(node, data);
        classNames.pop();

        if (ApexClassMetricKey.WMC.supports(node)) {
            int classWmc = (int) ApexMetrics.get(ApexClassMetricKey.WMC, node);

            if (classWmc >= getProperty(CLASS_LEVEL_DESCRIPTOR)) {
                int classHighest = (int) ApexMetrics.get(ApexOperationMetricKey.CYCLO, node, ResultOption.HIGHEST);

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

        int cyclo = (int) ApexMetrics.get(ApexOperationMetricKey.CYCLO, node);
        if (cyclo >= getProperty(METHOD_LEVEL_DESCRIPTOR)) {
            addViolation(data, node, new String[] {node.getImage().equals(classNames.peek()) ? "constructor" : "method",
                                                   node.getQualifiedName().getOperation(),
                                                   "",
                                                   "" + cyclo, });
        }

        return data;
    }

}
