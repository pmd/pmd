/*
 * BSD-style license; for more info see http://pmd.sourceforge.net/license.html
 */

package net.sourceforge.pmd.lang.vm.rule.design;

import static net.sourceforge.pmd.properties.constraints.NumericConstraints.positive;

import net.sourceforge.pmd.lang.rule.internal.CommonPropertyDescriptors;
import net.sourceforge.pmd.lang.vm.ast.ASTTemplate;
import net.sourceforge.pmd.lang.vm.rule.AbstractVmRule;
import net.sourceforge.pmd.properties.PropertyDescriptor;

public class ExcessiveTemplateLengthRule extends AbstractVmRule {

    private static final PropertyDescriptor<Integer> REPORT_LEVEL =
        CommonPropertyDescriptors.reportLevelProperty()
                                 .desc("Threshold above which a node is reported")
                                 .require(positive())
                                 .defaultValue(1000)
                                 .build();

    public ExcessiveTemplateLengthRule() {
        definePropertyDescriptor(REPORT_LEVEL);
        addRuleChainVisit(ASTTemplate.class);
    }

    @Override
    public Object visit(final ASTTemplate node, final Object data) {

        if (node.getEndLine() - node.getBeginLine() >= getProperty(REPORT_LEVEL)) {
            addViolation(data, node);
        }
        return data;
    }
}
