/*
 * BSD-style license; for more info see http://pmd.sourceforge.net/license.html
 */

package net.sourceforge.pmd.lang.velocity.rule.design;

import static net.sourceforge.pmd.properties.NumericConstraints.positive;

import org.checkerframework.checker.nullness.qual.NonNull;

import net.sourceforge.pmd.lang.rule.RuleTargetSelector;
import net.sourceforge.pmd.lang.rule.internal.CommonPropertyDescriptors;
import net.sourceforge.pmd.lang.velocity.ast.ASTTemplate;
import net.sourceforge.pmd.lang.velocity.rule.AbstractVtlRule;
import net.sourceforge.pmd.properties.PropertyDescriptor;
import net.sourceforge.pmd.reporting.RuleContext;

public class ExcessiveTemplateLengthRule extends AbstractVtlRule {

    private static final PropertyDescriptor<Integer> REPORT_LEVEL =
        CommonPropertyDescriptors.reportLevelProperty()
                                 .desc("Threshold at or above which a node is reported")
                                 .require(positive())
                                 .defaultValue(1000)
                                 .build();

    public ExcessiveTemplateLengthRule() {
        definePropertyDescriptor(REPORT_LEVEL);
    }

    @Override
    protected @NonNull RuleTargetSelector buildTargetSelector() {
        return RuleTargetSelector.forTypes(ASTTemplate.class);
    }

    @Override
    public RuleContext visit(final ASTTemplate node, final RuleContext data) {

        if (node.getEndLine() - node.getBeginLine() >= getProperty(REPORT_LEVEL)) {
            data.addViolation(node);
        }
        return data;
    }
}
