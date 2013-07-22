/**
 * BSD-style license; for more info see http://pmd.sourceforge.net/license.html
 */
package net.sourceforge.pmd.lang.plsql.rule.design;

import net.sourceforge.pmd.lang.plsql.ast.PLSQLNode;
import net.sourceforge.pmd.lang.plsql.rule.AbstractStatisticalPLSQLRule;
import net.sourceforge.pmd.stat.DataPoint;

/**
 * This is a common super class for things which
 * shouldn't have excessive nodes underneath.
 * <p/>
 * It expects all "visit" calls to return an
 * Integer.  It will sum all the values it gets,
 * and use that as its score.
 * <p/>
 * To use it, override the "visit" for the nodes that
 * need to be counted.  On those return "new Integer(1)"
 * <p/>
 * All others will return 0 (or the sum of counted nodes
 * underneath.)
 */

public class ExcessiveNodeCountRule extends AbstractStatisticalPLSQLRule {
    private Class<?> nodeClass;

    public ExcessiveNodeCountRule(Class<?> nodeClass) {
	this.nodeClass = nodeClass;
    }

    @Override
    public Object visit(PLSQLNode node, Object data) {
	int numNodes = 0;

	for (int i = 0; i < node.jjtGetNumChildren(); i++) {
	    Integer treeSize = (Integer) ((PLSQLNode) node.jjtGetChild(i)).jjtAccept(this, data);
	    numNodes += treeSize;
	}

	if (nodeClass.isInstance(node)) {
	    DataPoint point = new DataPoint();
	    point.setNode(node);
	    point.setScore(1.0 * numNodes);
	    point.setMessage(getMessage());
	    addDataPoint(point);
	}

	return Integer.valueOf(numNodes);
    }
}
