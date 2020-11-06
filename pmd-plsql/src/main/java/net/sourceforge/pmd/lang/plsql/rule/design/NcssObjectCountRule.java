/**
 * BSD-style license; for more info see http://pmd.sourceforge.net/license.html
 */

package net.sourceforge.pmd.lang.plsql.rule.design;

import java.util.logging.Level;
import java.util.logging.Logger;

import net.sourceforge.pmd.lang.plsql.ast.ASTFieldDeclaration;
import net.sourceforge.pmd.lang.plsql.ast.ASTGlobal;
import net.sourceforge.pmd.lang.plsql.ast.ASTProgramUnit;
import net.sourceforge.pmd.lang.plsql.ast.OracleObject;
import net.sourceforge.pmd.lang.plsql.ast.PLSQLNode;
import net.sourceforge.pmd.stat.DataPoint;
import net.sourceforge.pmd.util.NumericConstants;

/**
 * Non-commented source statement counter for Oracle Object declarations.
 *
 * @author Stuart Turton
 */
public class NcssObjectCountRule extends AbstractNcssCountRule {
    private static final String CLASS_NAME = NcssObjectCountRule.class.getName();
    private static final Logger LOGGER = Logger.getLogger(NcssObjectCountRule.class.getName());

    /**
     * Count type declarations. This includes Oracle Objects.
     */
    public NcssObjectCountRule() {
        super(OracleObject.class);
        setProperty(MINIMUM_DESCRIPTOR, 1500d);
    }

    // @Override
    public Object visit(OracleObject node, Object data) {
        LOGGER.entering(CLASS_NAME, "visit(NcssObjectCountRule)");
        // Treat Schema-level ProgramUnits as Oracle Objects, otherwise as
        // subprograms
        if (node.getParent() instanceof ASTGlobal) {
            LOGGER.fine("Schema-level");
            return super.visit(node, data);
        }

        LOGGER.fine("not Schema-level");
        return countNodeChildren(node, data);
    }

    /**
     * Override super.visit(PLSQLNode, Object) for ASTProgramUnit nodes, only
     * adding DataPoints for Schema-level Functions and Procedures
     */
    @Override
    public Object visit(ASTProgramUnit node, Object data) {
        int numNodes = 0;

        for (int i = 0; i < node.getNumChildren(); i++) {
            PLSQLNode n = (PLSQLNode) node.getChild(i);
            Integer treeSize = (Integer) n.jjtAccept(this, data);
            numNodes += treeSize.intValue();
        }

        // This override is necessary because only Schema-level OracleObject
        // instances should result in DataPoints
        if (node instanceof OracleObject && node.getParent() instanceof ASTGlobal) {

            // Add 1 to account for base node
            numNodes++;
            DataPoint point = new DataPoint();
            point.setNode(node);
            point.setScore(1.0 * numNodes);
            point.setMessage(getMessage());
            addDataPoint(point);
            if (LOGGER.isLoggable(Level.FINE)) {
                LOGGER.fine("Running score is " + point.getScore());
            }
        }

        return Integer.valueOf(numNodes);
    }

    @Override
    public Object visit(ASTFieldDeclaration node, Object data) {
        LOGGER.entering(CLASS_NAME, "visit(ASTFieldDeclaration)");
        return NumericConstants.ONE;
    }

    @Override
    public Object[] getViolationParameters(DataPoint point) {
        LOGGER.entering(CLASS_NAME, "visit(getViolationParameters)");
        if (LOGGER.isLoggable(Level.FINE)) {
            LOGGER.fine("Node Count ==" + point.getScore());
        }
        return new String[] { String.valueOf((int) point.getScore()) };
    }

}
