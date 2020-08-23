/*
 * BSD-style license; for more info see http://pmd.sourceforge.net/license.html
 */

package net.sourceforge.pmd.lang.plsql.rule.design;

import java.util.ArrayList;
import java.util.List;

import net.sourceforge.pmd.lang.plsql.ast.ASTCaseStatement;
import net.sourceforge.pmd.lang.plsql.ast.ASTCaseWhenClause;
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
import net.sourceforge.pmd.lang.plsql.ast.PLSQLParserVisitorAdapter;
import net.sourceforge.pmd.util.NumericConstants;

/**
 * @author Clément Fournier
 */
class NPathComplexityVisitor extends PLSQLParserVisitorAdapter {


    public int compute(ExecutableCode root) {
        return (int) root.jjtAccept(this, null);
    }

    private int complexityMultipleOf(PLSQLNode node, Object data) {

        int npath = 1;
        PLSQLNode n;

        for (int i = 0; i < node.getNumChildren(); i++) {
            n = (PLSQLNode) node.getChild(i);
            npath *= (Integer) n.jjtAccept(this, data);
        }

        return npath;
    }

    @Override
    public Object visit(ASTMethodDeclaration node, Object data) {
        return complexityMultipleOf(node, data);
    }

    @Override
    public Object visit(ASTProgramUnit node, Object data) {
        return complexityMultipleOf(node, data);
    }

    @Override
    public Object visit(ASTTypeMethod node, Object data) {
        return complexityMultipleOf(node, data);
    }

    @Override
    public Object visit(ASTTriggerUnit node, Object data) {
        return complexityMultipleOf(node, data);
    }

    @Override
    public Object visit(ASTTriggerTimingPointSection node, Object data) {
        return complexityMultipleOf(node, data);
    }

    @Override
    public Object visitPLSQLNode(PLSQLNode node, Object data) {
        return complexityMultipleOf(node, data);
    }

    @Override
    public Object visit(ASTIfStatement node, Object data) {
        // (npath of if + npath of else (or 1) + bool_comp of if) * npath of
        // next

        int boolCompIf = NPathComplexityRule.sumExpressionComplexity(node.getFirstChildOfType(ASTExpression.class));

        int complexity = 0;

        List<PLSQLNode> statementChildren = new ArrayList<>();
        for (int i = 0; i < node.getNumChildren(); i++) {
            if (node.getChild(i).getClass() == ASTStatement.class
                || node.getChild(i).getClass() == ASTElsifClause.class
                || node.getChild(i).getClass() == ASTElseClause.class) {
                statementChildren.add(node.getChild(i));
            }
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

        return boolCompIf + complexity;
    }

    @Override
    public Object visit(ASTElsifClause node, Object data) {
        // (npath of if + npath of else (or 1) + bool_comp of if) * npath of
        // next

        int boolCompIf = NPathComplexityRule.sumExpressionComplexity(node.getFirstChildOfType(ASTExpression.class));

        int complexity = 0;

        List<PLSQLNode> statementChildren = new ArrayList<>();
        for (int i = 0; i < node.getNumChildren(); i++) {
            if (node.getChild(i).getClass() == ASTStatement.class) {
                statementChildren.add((PLSQLNode) node.getChild(i));
            }
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

        return boolCompIf + complexity;
    }

    @Override
    public Object visit(ASTElseClause node, Object data) {
        // (npath of if + npath of else (or 1) + bool_comp of if) * npath of
        // next

        int complexity = 0;

        List<PLSQLNode> statementChildren = new ArrayList<>();
        for (int i = 0; i < node.getNumChildren(); i++) {
            if (node.getChild(i).getClass() == ASTStatement.class) {
                statementChildren.add((PLSQLNode) node.getChild(i));
            }
        }

        for (PLSQLNode element : statementChildren) {
            complexity += (Integer) element.jjtAccept(this, data);
        }

        return complexity;
    }

    @Override
    public Object visit(ASTWhileStatement node, Object data) {
        // (npath of while + bool_comp of while + 1) * npath of next

        int boolCompWhile = NPathComplexityRule.sumExpressionComplexity(node.getFirstChildOfType(ASTExpression.class));

        Integer nPathWhile = (Integer) ((PLSQLNode) node.getFirstChildOfType(ASTStatement.class)).jjtAccept(this, data);

        return boolCompWhile + nPathWhile + 1;
    }

    @Override
    public Object visit(ASTLoopStatement node, Object data) {
        // (npath of do + bool_comp of do + 1) * npath of next

        int boolCompDo = NPathComplexityRule.sumExpressionComplexity(node.getFirstChildOfType(ASTExpression.class));

        Integer nPathDo = (Integer) ((PLSQLNode) node.getFirstChildOfType(ASTStatement.class)).jjtAccept(this, data);

        return boolCompDo + nPathDo + 1;
    }

    @Override
    public Object visit(ASTForStatement node, Object data) {
        // (npath of for + bool_comp of for + 1) * npath of next

        int boolCompFor = NPathComplexityRule.sumExpressionComplexity(node.getFirstDescendantOfType(ASTExpression.class));

        Integer nPathFor = (Integer) ((PLSQLNode) node.getFirstChildOfType(ASTStatement.class)).jjtAccept(this, data);

        return boolCompFor + nPathFor + 1;
    }

    @Override
    public Object visit(ASTReturnStatement node, Object data) {
        // return statements are valued at 1, or the value of the boolean
        // expression

        ASTExpression expr = node.getFirstChildOfType(ASTExpression.class);

        if (expr == null) {
            return NumericConstants.ONE;
        }

        int boolCompReturn = NPathComplexityRule.sumExpressionComplexity(expr);
        int conditionalExpressionComplexity = complexityMultipleOf(expr, data);

        if (conditionalExpressionComplexity > 1) {
            boolCompReturn += conditionalExpressionComplexity;
        }

        if (boolCompReturn > 0) {
            return boolCompReturn;
        }
        return NumericConstants.ONE;
    }

    @Override
    public Object visit(ASTCaseWhenClause node, Object data) {
        // bool_comp of switch + sum(npath(case_range))

        int boolCompSwitch = NPathComplexityRule.sumExpressionComplexity(node.getFirstChildOfType(ASTExpression.class));

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
        return boolCompSwitch + npath;
    }

    @Override
    public Object visit(ASTCaseStatement node, Object data) {
        // bool_comp of switch + sum(npath(case_range))

        int boolCompSwitch = NPathComplexityRule.sumExpressionComplexity(node.getFirstChildOfType(ASTExpression.class));

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
        return boolCompSwitch + npath;
    }

    @Override
    public Object visit(ASTConditionalOrExpression node, Object data) {
        return NumericConstants.ONE;
    }

}
