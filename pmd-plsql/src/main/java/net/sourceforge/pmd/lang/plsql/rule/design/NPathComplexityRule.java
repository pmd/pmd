/**
 * BSD-style license; for more info see http://pmd.sourceforge.net/license.html
 */

package net.sourceforge.pmd.lang.plsql.rule.design;

import java.util.ArrayList;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

import net.sourceforge.pmd.lang.plsql.ast.ASTCaseStatement;
import net.sourceforge.pmd.lang.plsql.ast.ASTCaseWhenClause;
import net.sourceforge.pmd.lang.plsql.ast.ASTConditionalAndExpression;
import net.sourceforge.pmd.lang.plsql.ast.ASTConditionalOrExpression;
import net.sourceforge.pmd.lang.plsql.ast.ASTElseClause;
import net.sourceforge.pmd.lang.plsql.ast.ASTElsifClause;
import net.sourceforge.pmd.lang.plsql.ast.ASTExpression;
import net.sourceforge.pmd.lang.plsql.ast.ASTForStatement;
import net.sourceforge.pmd.lang.plsql.ast.ASTIfStatement;
import net.sourceforge.pmd.lang.plsql.ast.ASTLoopStatement;
import net.sourceforge.pmd.lang.plsql.ast.ASTMethodDeclaration;
import net.sourceforge.pmd.lang.plsql.ast.ASTProgramUnit;
import net.sourceforge.pmd.lang.plsql.ast.ASTReturnStatement;
import net.sourceforge.pmd.lang.plsql.ast.ASTStatement;
import net.sourceforge.pmd.lang.plsql.ast.ASTTriggerTimingPointSection;
import net.sourceforge.pmd.lang.plsql.ast.ASTTriggerUnit;
import net.sourceforge.pmd.lang.plsql.ast.ASTTypeMethod;
import net.sourceforge.pmd.lang.plsql.ast.ASTWhileStatement;
import net.sourceforge.pmd.lang.plsql.ast.ExecutableCode;
import net.sourceforge.pmd.lang.plsql.ast.PLSQLNode;
import net.sourceforge.pmd.lang.plsql.rule.AbstractStatisticalPLSQLRule;
import net.sourceforge.pmd.stat.DataPoint;
import net.sourceforge.pmd.util.NumericConstants;

/**
 * NPath complexity is a measurement of the acyclic execution paths through a
 * function. See Nejmeh, Communications of the ACM Feb 1988 pp 188-200.
 *
 * @author Jason Bennett
 */
public class NPathComplexityRule extends AbstractStatisticalPLSQLRule {
    private static final String CLASS_NAME = NPathComplexityRule.class.getCanonicalName();
    private static final Logger LOGGER = Logger.getLogger(NPathComplexityRule.class.getName());

    public NPathComplexityRule() {
        super();
        setProperty(MINIMUM_DESCRIPTOR, 200d);
    }

    private int complexityMultipleOf(PLSQLNode node, int npathStart, Object data) {
        LOGGER.entering(CLASS_NAME, "complexityMultipleOf(SimpleNode)");

        int npath = npathStart;
        PLSQLNode n;

        for (int i = 0; i < node.getNumChildren(); i++) {
            n = (PLSQLNode) node.getChild(i);
            npath *= (Integer) n.jjtAccept(this, data);
        }

        LOGGER.exiting(CLASS_NAME, "complexityMultipleOf(SimpleNode)", npath);
        return npath;
    }

    @Override
    public Object visit(ASTMethodDeclaration node, Object data) {
        LOGGER.entering(CLASS_NAME, "visit(ASTMethodDeclaration)");
        int npath = complexityMultipleOf(node, 1, data);

        DataPoint point = new DataPoint();
        point.setNode(node);
        point.setScore(1.0 * npath);
        point.setMessage(getMessage());
        addDataPoint(point);

        if (LOGGER.isLoggable(Level.FINEST)) {
            LOGGER.finest("NPath complexity:  " + npath + " for line " + node.getBeginLine() + ", column "
                    + node.getBeginColumn());
        }
        LOGGER.exiting(CLASS_NAME, "visit(ASTMethodDeclaration)", npath);
        return Integer.valueOf(npath);
    }

    @Override
    public Object visit(ASTProgramUnit node, Object data) {
        LOGGER.entering(CLASS_NAME, "visit(ASTProgramUnit)");
        int npath = complexityMultipleOf(node, 1, data);

        DataPoint point = new DataPoint();
        point.setNode(node);
        point.setScore(1.0 * npath);
        point.setMessage(getMessage());
        addDataPoint(point);

        if (LOGGER.isLoggable(Level.FINEST)) {
            LOGGER.finest("NPath complexity:  " + npath + " for line " + node.getBeginLine() + ", column "
                    + node.getBeginColumn());
        }
        LOGGER.exiting(CLASS_NAME, "visit(ASTProgramUnit)", npath);
        return Integer.valueOf(npath);
    }

    @Override
    public Object visit(ASTTypeMethod node, Object data) {
        LOGGER.entering(CLASS_NAME, "visit(ASTTypeMethod)");
        int npath = complexityMultipleOf(node, 1, data);

        DataPoint point = new DataPoint();
        point.setNode(node);
        point.setScore(1.0 * npath);
        point.setMessage(getMessage());
        addDataPoint(point);

        if (LOGGER.isLoggable(Level.FINEST)) {
            LOGGER.finest("NPath complexity:  " + npath + " for line " + node.getBeginLine() + ", column "
                    + node.getBeginColumn());
        }
        LOGGER.exiting(CLASS_NAME, "visit(ASTTypeMethod)", npath);
        return Integer.valueOf(npath);
    }

    @Override
    public Object visit(ASTTriggerUnit node, Object data) {
        LOGGER.entering(CLASS_NAME, "visit(ASTTriggerUnit)");
        int npath = complexityMultipleOf(node, 1, data);

        DataPoint point = new DataPoint();
        point.setNode(node);
        point.setScore(1.0 * npath);
        point.setMessage(getMessage());
        addDataPoint(point);

        if (LOGGER.isLoggable(Level.FINEST)) {
            LOGGER.finest("NPath complexity:  " + npath + " for line " + node.getBeginLine() + ", column "
                    + node.getBeginColumn());
        }
        LOGGER.exiting(CLASS_NAME, "visit(ASTTriggerUnit)", npath);
        return Integer.valueOf(npath);
    }

    @Override
    public Object visit(ASTTriggerTimingPointSection node, Object data) {
        LOGGER.entering(CLASS_NAME, "visit(ASTTriggerTimingPointSection)");
        int npath = complexityMultipleOf(node, 1, data);

        DataPoint point = new DataPoint();
        point.setNode(node);
        point.setScore(1.0 * npath);
        point.setMessage(getMessage());
        addDataPoint(point);

        if (LOGGER.isLoggable(Level.FINEST)) {
            LOGGER.finest("NPath complexity:  " + npath + " for line " + node.getBeginLine() + ", column "
                    + node.getBeginColumn());
        }
        LOGGER.exiting(CLASS_NAME, "visit(ASTTriggerTimingPointSection)", npath);
        return Integer.valueOf(npath);
    }

    @Override
    public Object visit(PLSQLNode node, Object data) {
        LOGGER.entering(CLASS_NAME, "visit(SimpleNode)");
        int npath = complexityMultipleOf(node, 1, data);
        LOGGER.exiting(CLASS_NAME, "visit(SimpleNode)", npath);
        return Integer.valueOf(npath);
    }

    @Override
    public Object visit(ASTIfStatement node, Object data) {
        LOGGER.entering(CLASS_NAME, "visit(ASTIfStatement)");
        // (npath of if + npath of else (or 1) + bool_comp of if) * npath of
        // next

        int boolCompIf = sumExpressionComplexity(node.getFirstChildOfType(ASTExpression.class));

        int complexity = 0;

        List<PLSQLNode> statementChildren = new ArrayList<>();
        for (int i = 0; i < node.getNumChildren(); i++) {
            if (node.getChild(i).getClass() == ASTStatement.class
                    || node.getChild(i).getClass() == ASTElsifClause.class
                    || node.getChild(i).getClass() == ASTElseClause.class) {
                statementChildren.add((PLSQLNode) node.getChild(i));
            }
        }
        if (LOGGER.isLoggable(Level.FINEST)) {
            LOGGER.finest(statementChildren.size() + " statementChildren found for IF statement " + node.getBeginLine()
                    + ", column " + node.getBeginColumn());
        }

        /*
         * SRT if (statementChildren.isEmpty() || statementChildren.size() == 1
         * && ( null != node.getFirstChildOfType(ASTElseClause.class) )
         * //.hasElse() || statementChildren.size() != 1 && ( null ==
         * node.getFirstChildOfType(ASTElseClause.class) ) // !node.hasElse() )
         * { throw new
         * IllegalStateException("If node has wrong number of children"); }
         */

        /*
         * @TODO Any explicit Elsif clause(s) and Else clause are included in
         * the list of statements // add path for not taking if if (null ==
         * node.getFirstChildOfType(ASTElsifClause.class) ) //
         * !node.hasElse()!node.hasElse()) { complexity++; }
         *
         * if (null == node.getFirstChildOfType(ASTElseClause.class) ) //
         * !node.hasElse()!node.hasElse()) { complexity++; }
         */

        for (PLSQLNode element : statementChildren) {
            complexity += (Integer) element.jjtAccept(this, data);
        }

        LOGGER.exiting(CLASS_NAME, "visit(ASTIfStatement)", boolCompIf + complexity);
        return Integer.valueOf(boolCompIf + complexity);
    }

    @Override
    public Object visit(ASTElsifClause node, Object data) {
        LOGGER.entering(CLASS_NAME, "visit(ASTElsifClause)");
        // (npath of if + npath of else (or 1) + bool_comp of if) * npath of
        // next

        int boolCompIf = sumExpressionComplexity(node.getFirstChildOfType(ASTExpression.class));

        int complexity = 0;

        List<PLSQLNode> statementChildren = new ArrayList<>();
        for (int i = 0; i < node.getNumChildren(); i++) {
            if (node.getChild(i).getClass() == ASTStatement.class) {
                statementChildren.add((PLSQLNode) node.getChild(i));
            }
        }
        if (LOGGER.isLoggable(Level.FINEST)) {
            LOGGER.finest(statementChildren.size() + " statementChildren found for ELSIF statement "
                    + node.getBeginLine() + ", column " + node.getBeginColumn());
        }

        /*
         * SRT if (statementChildren.isEmpty() || statementChildren.size() == 1
         * && ( null != node.getFirstChildOfType(ASTElseClause.class) )
         * //.hasElse() || statementChildren.size() != 1 && ( null ==
         * node.getFirstChildOfType(ASTElseClause.class) ) // !node.hasElse() )
         * { throw new
         * IllegalStateException("If node has wrong number of children"); }
         */

        for (PLSQLNode element : statementChildren) {
            complexity += (Integer) element.jjtAccept(this, data);
        }

        LOGGER.exiting(CLASS_NAME, "visit(ASTElsifClause)", boolCompIf + complexity);
        return Integer.valueOf(boolCompIf + complexity);
    }

    @Override
    public Object visit(ASTElseClause node, Object data) {
        LOGGER.entering(CLASS_NAME, "visit(ASTElseClause)");
        // (npath of if + npath of else (or 1) + bool_comp of if) * npath of
        // next

        int complexity = 0;

        List<PLSQLNode> statementChildren = new ArrayList<>();
        for (int i = 0; i < node.getNumChildren(); i++) {
            if (node.getChild(i).getClass() == ASTStatement.class) {
                statementChildren.add((PLSQLNode) node.getChild(i));
            }
        }
        if (LOGGER.isLoggable(Level.FINEST)) {
            LOGGER.finest(statementChildren.size() + " statementChildren found for ELSE clause statement "
                    + node.getBeginLine() + ", column " + node.getBeginColumn());
        }

        for (PLSQLNode element : statementChildren) {
            complexity += (Integer) element.jjtAccept(this, data);
        }

        LOGGER.exiting(CLASS_NAME, "visit(ASTElseClause)", complexity);
        return Integer.valueOf(complexity);
    }

    @Override
    public Object visit(ASTWhileStatement node, Object data) {
        LOGGER.entering(CLASS_NAME, "visit(ASTWhileStatement)");
        // (npath of while + bool_comp of while + 1) * npath of next

        int boolCompWhile = sumExpressionComplexity(node.getFirstChildOfType(ASTExpression.class));

        Integer nPathWhile = (Integer) ((PLSQLNode) node.getFirstChildOfType(ASTStatement.class)).jjtAccept(this, data);

        LOGGER.exiting(CLASS_NAME, "visit(ASTWhileStatement)", boolCompWhile + nPathWhile + 1);
        return Integer.valueOf(boolCompWhile + nPathWhile + 1);
    }

    @Override
    public Object visit(ASTLoopStatement node, Object data) {
        LOGGER.entering(CLASS_NAME, "visit(ASTLoopStatement)");
        // (npath of do + bool_comp of do + 1) * npath of next

        int boolCompDo = sumExpressionComplexity(node.getFirstChildOfType(ASTExpression.class));

        Integer nPathDo = (Integer) ((PLSQLNode) node.getFirstChildOfType(ASTStatement.class)).jjtAccept(this, data);

        LOGGER.exiting(CLASS_NAME, "visit(ASTLoopStatement)", boolCompDo + nPathDo + 1);
        return Integer.valueOf(boolCompDo + nPathDo + 1);
    }

    @Override
    public Object visit(ASTForStatement node, Object data) {
        LOGGER.entering(CLASS_NAME, "visit(ASTForStatement)");
        // (npath of for + bool_comp of for + 1) * npath of next

        int boolCompFor = sumExpressionComplexity(node.getFirstDescendantOfType(ASTExpression.class));

        Integer nPathFor = (Integer) ((PLSQLNode) node.getFirstChildOfType(ASTStatement.class)).jjtAccept(this, data);

        LOGGER.exiting(CLASS_NAME, "visit(ASTForStatement)", boolCompFor + nPathFor + 1);
        return Integer.valueOf(boolCompFor + nPathFor + 1);
    }

    @Override
    public Object visit(ASTReturnStatement node, Object data) {
        LOGGER.entering(CLASS_NAME, "visit(ASTReturnStatement)");
        // return statements are valued at 1, or the value of the boolean
        // expression

        ASTExpression expr = node.getFirstChildOfType(ASTExpression.class);

        if (expr == null) {
            return NumericConstants.ONE;
        }

        int boolCompReturn = sumExpressionComplexity(expr);
        int conditionalExpressionComplexity = complexityMultipleOf(expr, 1, data);

        if (conditionalExpressionComplexity > 1) {
            boolCompReturn += conditionalExpressionComplexity;
        }

        if (boolCompReturn > 0) {
            return Integer.valueOf(boolCompReturn);
        }
        LOGGER.entering(CLASS_NAME, "visit(ASTReturnStatement)", NumericConstants.ONE);
        return NumericConstants.ONE;
    }

    @Override
    public Object visit(ASTCaseWhenClause node, Object data) {
        LOGGER.entering(CLASS_NAME, "visit(ASTCaseWhenClause)");
        // bool_comp of switch + sum(npath(case_range))

        int boolCompSwitch = sumExpressionComplexity(node.getFirstChildOfType(ASTExpression.class));

        int npath = 1;
        int caseRange = 0;
        for (int i = 0; i < node.getNumChildren(); i++) {
            PLSQLNode n = (PLSQLNode) node.getChild(i);

            // Fall-through labels count as 1 for complexity
            Integer complexity = (Integer) n.jjtAccept(this, data);
            caseRange *= complexity;
        }
        // add in npath of last label
        npath += caseRange;
        LOGGER.exiting(CLASS_NAME, "visit(ASTCaseWhenClause)", boolCompSwitch + npath);
        return Integer.valueOf(boolCompSwitch + npath);
    }

    @Override
    public Object visit(ASTCaseStatement node, Object data) {
        LOGGER.entering(CLASS_NAME, "visit(ASTCaseStatement)");
        // bool_comp of switch + sum(npath(case_range))

        int boolCompSwitch = sumExpressionComplexity(node.getFirstChildOfType(ASTExpression.class));

        int npath = 0;
        int caseRange = 0;
        for (int i = 0; i < node.getNumChildren(); i++) {
            PLSQLNode n = (PLSQLNode) node.getChild(i);

            // Fall-through labels count as 1 for complexity
            Integer complexity = (Integer) n.jjtAccept(this, data);
            caseRange *= complexity;
        }
        // add in npath of last label
        npath += caseRange;
        LOGGER.exiting(CLASS_NAME, "visit(ASTCaseStatement)", boolCompSwitch + npath);
        return Integer.valueOf(boolCompSwitch + npath);
    }

    @Override
    public Object visit(ASTConditionalOrExpression node, Object data) {
        return NumericConstants.ONE;
    }

    /**
     * Calculate the boolean complexity of the given expression. NPath boolean
     * complexity is the sum of &amp;&amp; and || tokens. This is calculated by summing
     * the number of children of the &amp;&amp;'s (minus one) and the children of the
     * ||'s (minus one).
     *
     * <p>Note that this calculation applies to Cyclomatic Complexity as well.</p>
     *
     * @param expr
     *            control structure expression
     * @return complexity of the boolean expression
     */
    public static int sumExpressionComplexity(ASTExpression expr) {
        LOGGER.entering(CLASS_NAME, "visit(ASTExpression)");
        if (expr == null) {
            LOGGER.exiting(CLASS_NAME, "visit(ASTExpression)", 0);
            return 0;
        }

        List<ASTConditionalAndExpression> andNodes = expr.findDescendantsOfType(ASTConditionalAndExpression.class);
        List<ASTConditionalOrExpression> orNodes = expr.findDescendantsOfType(ASTConditionalOrExpression.class);

        int children = 0;

        for (ASTConditionalOrExpression element : orNodes) {
            children += element.getNumChildren();
            children--;
        }

        for (ASTConditionalAndExpression element : andNodes) {
            children += element.getNumChildren();
            children--;
        }

        LOGGER.exiting(CLASS_NAME, "visit(ASTExpression)", children);
        return children;
    }

    @Override
    public Object[] getViolationParameters(DataPoint point) {
        return new String[] { ((ExecutableCode) point.getNode()).getMethodName(),
            String.valueOf((int) point.getScore()), };
    }
}
