/*
 * BSD-style license; for more info see http://pmd.sourceforge.net/license.html
 */

package net.sourceforge.pmd.lang.java.rule.internal;

import static net.sourceforge.pmd.properties.NumericConstraints.positive;

import net.sourceforge.pmd.lang.java.ast.JavaNode;
import net.sourceforge.pmd.lang.java.rule.AbstractJavaRulechainRule;
import net.sourceforge.pmd.lang.rule.internal.CommonPropertyDescriptors;
import net.sourceforge.pmd.properties.PropertyDescriptor;
import net.sourceforge.pmd.reporting.RuleContext;


/**
 * Abstract class for rules counting the length of some node.
 *
 * @author Clément Fournier
 * @since 7.0.0
 */
public abstract class AbstractJavaCounterCheckRule<T extends JavaNode> extends AbstractJavaRulechainRule {


    private final PropertyDescriptor<Integer> reportLevel =
        CommonPropertyDescriptors.reportLevelProperty()
                                 .desc("Threshold at or above which a node is reported")
                                 .require(positive())
                                 .defaultValue(defaultReportLevel()).build();


    public AbstractJavaCounterCheckRule(Class<T> nodeType) {
        super(nodeType);
        definePropertyDescriptor(reportLevel);
    }


    protected abstract int defaultReportLevel();


    /** Return true if the node should be ignored. */
    protected boolean isIgnored(T node) {
        return false;
    }

    protected abstract int getMetric(T node);

    @Override
    public RuleContext visitJavaNode(JavaNode node, RuleContext data) {
        @SuppressWarnings("unchecked")
        T t = (T) node;
        // since we only visit this node, it's ok

        if (!isIgnored(t)) {
            int metric = getMetric(t);
            int threshold = getProperty(reportLevel);
            if (metric >= threshold) {
                data.addViolation(node, metric, threshold);
            }
        }

        return data;
    }
}
