/*
 * BSD-style license; for more info see http://pmd.sourceforge.net/license.html
 */

package net.sourceforge.pmd.lang.apex.rule.internal;

import static net.sourceforge.pmd.properties.constraints.NumericConstraints.positive;

import org.checkerframework.checker.nullness.qual.NonNull;

import net.sourceforge.pmd.lang.apex.ast.ApexNode;
import net.sourceforge.pmd.lang.apex.rule.AbstractApexRule;
import net.sourceforge.pmd.lang.rule.RuleTargetSelector;
import net.sourceforge.pmd.lang.rule.internal.CommonPropertyDescriptors;
import net.sourceforge.pmd.properties.PropertyDescriptor;


/**
 * Abstract class for rules counting some integer metric on some node.
 *
 * @author Clément Fournier
 * @since 7.0.0
 */
public abstract class AbstractCounterCheckRule<T extends ApexNode<?>> extends AbstractApexRule {


    private final PropertyDescriptor<Integer> reportLevel =
        CommonPropertyDescriptors.reportLevelProperty()
                                 .desc("Threshold above which a node is reported")
                                 .require(positive())
                                 .defaultValue(defaultReportLevel()).build();
    private final Class<T> nodeType;


    public AbstractCounterCheckRule(Class<T> nodeType) {
        this.nodeType = nodeType;
        definePropertyDescriptor(reportLevel);
    }

    @Override
    protected @NonNull RuleTargetSelector buildTargetSelector() {
        return RuleTargetSelector.forTypes(nodeType);
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
    public Object visit(ApexNode<?> node, Object data) {
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

    public abstract static class AbstractLineLengthCheckRule<T extends ApexNode<?>> extends AbstractCounterCheckRule<T> {

        public AbstractLineLengthCheckRule(Class<T> nodeType) {
            super(nodeType);
        }

        @Override
        protected int getMetric(T node) {
            return node.getEndLine() - node.getBeginLine();
        }

    }


}
