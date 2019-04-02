/*
 * BSD-style license; for more info see http://pmd.sourceforge.net/license.html
 */

/*
 * BSD-style license; for more info see http://pmd.sourceforge.net/license.html
 */

/**
 * BSD-style license; for more info see http://pmd.sourceforge.net/license.html
 */

package net.sourceforge.pmd.lang.java.rule.design;

import static net.sourceforge.pmd.properties.constraints.NumericConstraints.positive;

import java.lang.reflect.Modifier;

import net.sourceforge.pmd.lang.java.ast.JavaNode;
import net.sourceforge.pmd.lang.java.rule.AbstractJavaRule;
import net.sourceforge.pmd.lang.rule.internal.CommonPropertyDescriptors;
import net.sourceforge.pmd.properties.PropertyDescriptor;


/**
 * Abstract class for rules counting the length of some node.
 *
 * @author Clément Fournier
 * @since 6.7.0
 */
abstract class AbstractCounterCheckRule<T extends JavaNode> extends AbstractJavaRule {


    private final PropertyDescriptor<Integer> reportLevel =
        CommonPropertyDescriptors.reportLevelProperty()
                                 .desc("Threshold above which a node is reported")
                                 .require(positive())
                                 .defaultValue(defaultReportLevel()).build();


    @SafeVarargs
    AbstractCounterCheckRule(Class<T> nodeType, Class<? extends T>... concreteNodeTypes) {
        definePropertyDescriptor(reportLevel);
        if (!(Modifier.isAbstract(nodeType.getModifiers()) || nodeType.isInterface())) {
            addRuleChainVisit(nodeType);
        } else {
            assert concreteNodeTypes.length > 1;
        }
        for (Class<? extends T> concreteNode : concreteNodeTypes) {
            assert !Modifier.isAbstract(concreteNode.getModifiers()) && !concreteNode.isInterface();
            addRuleChainVisit(concreteNode);
        }
    }


    protected abstract int defaultReportLevel();


    /** Return true if the node should be ignored. */
    protected boolean isIgnored(T node) {
        return false;
    }

    protected abstract boolean isViolation(T node, int reportLevel);


    @Override
    public Object visit(JavaNode node, Object data) {
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

    static abstract class AbstractLineLengthCheckRule<T extends JavaNode> extends AbstractCounterCheckRule<T> {

        @SafeVarargs
        AbstractLineLengthCheckRule(Class<T> nodeType, Class<? extends T>... concreteNodes) {
            super(nodeType, concreteNodes);
        }

        @Override
        protected final boolean isViolation(T node, int reportLevel) {
            return node.getEndLine() - node.getBeginLine() > reportLevel;
        }
    }


}
