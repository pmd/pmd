/**
 * BSD-style license; for more info see http://pmd.sourceforge.net/license.html
 */
package net.sourceforge.pmd.lang.plsql.rule.design;

import java.util.logging.Logger;

import net.sourceforge.pmd.lang.plsql.ast.PLSQLNode;
import net.sourceforge.pmd.lang.plsql.rule.AbstractStatisticalPLSQLRule;
import net.sourceforge.pmd.stat.DataPoint;

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
public class ExcessiveLengthRule extends AbstractStatisticalPLSQLRule {
    private final static Logger LOGGER = Logger.getLogger(ExcessiveLengthRule.class.getPackage().getName()); 
    private Class<?> nodeClass;

    public ExcessiveLengthRule(Class<?> nodeClass) {
	this.nodeClass = nodeClass;
    }

    @Override
    public Object visit(PLSQLNode node, Object data) {
        //LOGGER.entering(CLASS_PATH,"visit(SimpleNode)");
        LOGGER.finest("SimpleNode: line " + node.getBeginLine() +", column " + node.getBeginColumn()
                      + " - is node " + node.getClass().getCanonicalName()
                      + " instanceof " + this.nodeClass.getClass().getCanonicalName()
                     );
	if (nodeClass.isInstance(node)) {
            LOGGER.finest("SimpleNode: YES node " + node.getClass().getCanonicalName()
                          + " IS instanceof " + this.nodeClass.getClass().getCanonicalName()
                          + " with  length == (" + node.getEndLine() + " - " + node.getBeginLine()
                          + " == "  + (node.getEndLine() - node.getBeginLine())
                         );
	    DataPoint point = new DataPoint();
	    point.setNode(node);
	    point.setScore(1.0 * (node.getEndLine() - node.getBeginLine()));
	    point.setMessage(getMessage());
	    addDataPoint(point);
            LOGGER.fine("SimpleNode: Score " + point.getScore() + " for " +  this.nodeClass.getCanonicalName() ) ; 
	}

        //LOGGER.exiting(CLASS_PATH,"visit(SimpleNode)");
	return node.childrenAccept(this, data);
    }

    @Override
    public Object[] getViolationParameters(DataPoint point) {
	return new String[] { String.valueOf((int) point.getScore()) };
    }
}
