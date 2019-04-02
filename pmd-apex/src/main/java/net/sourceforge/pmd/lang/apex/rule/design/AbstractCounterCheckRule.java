/**
 * BSD-style license; for more info see http://pmd.sourceforge.net/license.html
 */

package net.sourceforge.pmd.lang.apex.rule.design;

import static net.sourceforge.pmd.properties.constraints.NumericConstraints.positive;

import net.sourceforge.pmd.lang.apex.ast.AbstractApexNodeBase;
import net.sourceforge.pmd.lang.apex.ast.ApexNode;
import net.sourceforge.pmd.lang.apex.rule.AbstractApexRule;
import net.sourceforge.pmd.lang.apex.rule.CommonPropertyDescriptors;
import net.sourceforge.pmd.properties.PropertyDescriptor;


/**
 * Abstract class for rules counting the length of some node.
 *
 * @author Clément Fournier
 * @since 6.7.0
 */
abstract class AbstractCounterCheckRule<T extends ApexNode<?>> extends AbstractApexRule {


    private final PropertyDescriptor<Integer> reportLevel =
        CommonPropertyDescriptors.reportLevelProperty()
                                 .desc("Threshold above which a node is reported")
                                 .require(positive())
                                 .defaultValue(defaultReportLevel()).build();


    AbstractCounterCheckRule(Class<T> nodeType) {
        definePropertyDescriptor(reportLevel);
        addRuleChainVisit(nodeType);
    }


    protected abstract int defaultReportLevel();


    /** Return true if the node should be ignored. */
    protected boolean isIgnored(T node) {
        return false;
    }

    protected abstract boolean isViolation(T node, int reportLevel);


    @Override
    public Object visit(AbstractApexNodeBase node, Object data) {
        @SuppressWarnings("unchecked")
        T t = (T) node;
        // since we only visit this node, it's ok

        if (!isIgnored(t)) {
            if (isViolation(t, getProperty(reportLevel))) {
                addViolation(data, node);
            }
        }

        return data;
    }

    static abstract class AbstractLineLengthCheckRule<T extends ApexNode<?>> extends AbstractCounterCheckRule<T> {

        AbstractLineLengthCheckRule(Class<T> nodeType) {
            super(nodeType);
        }

        @Override
        protected final boolean isViolation(T node, int reportLevel) {
            return node.getEndLine() - node.getBeginLine() > reportLevel;
        }
    }


}
