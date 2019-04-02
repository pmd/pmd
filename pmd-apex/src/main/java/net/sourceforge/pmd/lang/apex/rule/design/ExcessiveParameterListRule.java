/**
 * BSD-style license; for more info see http://pmd.sourceforge.net/license.html
 */

package net.sourceforge.pmd.lang.apex.rule.design;

import static net.sourceforge.pmd.properties.constraints.NumericConstraints.positive;

import net.sourceforge.pmd.lang.apex.ast.ASTMethod;
import net.sourceforge.pmd.lang.apex.rule.AbstractApexRule;
import net.sourceforge.pmd.lang.rule.internal.CommonPropertyDescriptors;
import net.sourceforge.pmd.properties.PropertyDescriptor;

import apex.jorje.semantic.ast.member.Method;

/**
 * This rule detects an abnormally long parameter list. Note: This counts Nodes,
 * and not necessarily parameters, so the numbers may not match up. (But
 * topcount and sigma should work.)
 */
public class ExcessiveParameterListRule extends AbstractApexRule {


    private static final PropertyDescriptor<Integer> REPORT_LEVEL =
        CommonPropertyDescriptors.reportLevelProperty()
                                 .desc("Minimum number of parameters to trigger a violation")
                                 .defaultValue(4)
                                 .require(positive())
                                 .build();

    public ExcessiveParameterListRule() {
        definePropertyDescriptor(REPORT_LEVEL);
        addRuleChainVisit(ASTMethod.class);
    }

    @Override
    public Object visit(ASTMethod node, Object data) {

        Method node1 = node.getNode();
        if (node1.getMethodInfo().getParameters().size() > getProperty(REPORT_LEVEL)) {
            addViolation(data, node);
        }

        return data;
    }
}
