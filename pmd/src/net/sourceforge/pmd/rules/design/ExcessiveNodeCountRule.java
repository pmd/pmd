/**
 * BSD-style license; for more info see http://pmd.sourceforge.net/license.html
*/
package net.sourceforge.pmd.rules.design;

import net.sourceforge.pmd.ast.SimpleNode;
import net.sourceforge.pmd.stat.DataPoint;
import net.sourceforge.pmd.stat.StatisticalRule;

/**
 * This is a common super class for things which
 * shouldn't have excessive nodes underneath.
 *
 * It expects all "visit" calls to return an
 * Integer.  It will sum all the values it gets,
 * and use that as its score.
 *
 * To use it, override the "visit" for the nodes that
 * need to be counted.  On those return "new Integer(1)"
 *
 * All others will return 0 (or the sum of counted nodes
 * underneath.)
 */

public class ExcessiveNodeCountRule extends StatisticalRule {
    private Class nodeClass;

    public ExcessiveNodeCountRule(Class nodeClass) {
        this.nodeClass = nodeClass;
    }

    public Object visit(SimpleNode node, Object data) {
        int numNodes = 0;

        for (int i = 0; i < node.jjtGetNumChildren(); i++) {
            Integer treeSize = (Integer) (node.jjtGetChild(i)).jjtAccept(this, data);
            numNodes += treeSize.intValue();
        }

        if (nodeClass.isInstance(node)) {
            DataPoint point = new DataPoint();
            point.setLineNumber(node.getBeginLine());
            point.setScore(1.0 * numNodes);
            point.setRule(this);
            point.setMessage(getMessage());
            addDataPoint(point);
        }

        return new Integer(numNodes);
    }
}
