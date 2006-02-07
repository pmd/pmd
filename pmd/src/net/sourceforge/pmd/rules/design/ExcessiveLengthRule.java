/**
 * BSD-style license; for more info see http://pmd.sourceforge.net/license.html
 */
package net.sourceforge.pmd.rules.design;

import net.sourceforge.pmd.ast.SimpleJavaNode;
import net.sourceforge.pmd.stat.DataPoint;
import net.sourceforge.pmd.stat.StatisticalRule;

/**
 * This is a common super class for things which
 * have excessive length.
 * <p/>
 * i.e. LongMethod and LongClass rules.
 * <p/>
 * To implement an ExcessiveLength rule, you pass
 * in the Class of node you want to check, and this
 * does the rest for you.
 */
public class ExcessiveLengthRule extends StatisticalRule {
    private Class nodeClass;

    public ExcessiveLengthRule(Class nodeClass) {
        this.nodeClass = nodeClass;
    }

    public Object visit(SimpleJavaNode node, Object data) {
        if (nodeClass.isInstance(node)) {
            DataPoint point = new DataPoint();
            point.setNode(node);
            point.setScore(1.0 * (node.getEndLine() - node.getBeginLine()));
            point.setMessage(getMessage());
            addDataPoint(point);
        }

        return node.childrenAccept(this, data);
    }
}


