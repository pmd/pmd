/**
 * BSD-style license; for more info see http://pmd.sourceforge.net/license.html
 */

package net.sourceforge.pmd.lang.apex.rule.design;

import static net.sourceforge.pmd.properties.constraints.NumericConstraints.positive;

import java.lang.reflect.Modifier;

import net.sourceforge.pmd.lang.apex.ast.AbstractApexNodeBase;
import net.sourceforge.pmd.lang.apex.ast.ApexNode;
import net.sourceforge.pmd.lang.apex.rule.AbstractApexRule;
import net.sourceforge.pmd.lang.rule.internal.CommonPropertyDescriptors;
import net.sourceforge.pmd.properties.PropertyDescriptor;


/**
 * Abstract class for rules counting some integer metric on some node.
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
        if (!(Modifier.isAbstract(nodeType.getModifiers()) || nodeType.isInterface())) {
            addRuleChainVisit(nodeType);
        } else {
            assert false : "Rule chain visits must be concrete node types";
        }
    }


    protected abstract int defaultReportLevel();


    protected Object[] getViolationParameters(T node, int metric) {
        return new Object[] {metric};
    }


    protected abstract int getMetric(T node);

    /** Return true if the node should be ignored. */
    protected boolean isIgnored(T node) {
        return false;
    }


    @Override
    public Object visit(AbstractApexNodeBase node, Object data) {
        @SuppressWarnings("unchecked")
        T t = (T) node;
        // since we only visit this node, it's ok

        if (!isIgnored(t)) {
            int metric = getMetric(t);
            if (metric >= getProperty(reportLevel)) {
                addViolation(data, node, getViolationParameters(t, metric));
            }
        }

        return data;
    }

    abstract static class AbstractLineLengthCheckRule<T extends ApexNode<?>> extends AbstractCounterCheckRule<T> {

        AbstractLineLengthCheckRule(Class<T> nodeType) {
            super(nodeType);
        }

        @Override
        protected int getMetric(T node) {
            return node.getEndLine() - node.getBeginLine();
        }

    }


}
