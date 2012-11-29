/**
 * BSD-style license; for more info see http://pmd.sourceforge.net/license.html
 */
package net.sourceforge.pmd.lang.java.dfa;

import java.util.logging.Level;
import java.util.logging.Logger;

import net.sourceforge.pmd.lang.DataFlowHandler;
import net.sourceforge.pmd.lang.ast.Node;
import net.sourceforge.pmd.lang.dfa.Linker;
import net.sourceforge.pmd.lang.dfa.LinkerException;
import net.sourceforge.pmd.lang.dfa.NodeType;
import net.sourceforge.pmd.lang.dfa.SequenceException;
import net.sourceforge.pmd.lang.dfa.Structure;
import net.sourceforge.pmd.lang.java.ast.ASTBreakStatement;
import net.sourceforge.pmd.lang.java.ast.ASTConstructorDeclaration;
import net.sourceforge.pmd.lang.java.ast.ASTContinueStatement;
import net.sourceforge.pmd.lang.java.ast.ASTDoStatement;
import net.sourceforge.pmd.lang.java.ast.ASTExpression;
import net.sourceforge.pmd.lang.java.ast.ASTForInit;
import net.sourceforge.pmd.lang.java.ast.ASTForStatement;
import net.sourceforge.pmd.lang.java.ast.ASTForUpdate;
import net.sourceforge.pmd.lang.java.ast.ASTIfStatement;
import net.sourceforge.pmd.lang.java.ast.ASTLabeledStatement;
import net.sourceforge.pmd.lang.java.ast.ASTMethodDeclaration;
import net.sourceforge.pmd.lang.java.ast.ASTReturnStatement;
import net.sourceforge.pmd.lang.java.ast.ASTStatement;
import net.sourceforge.pmd.lang.java.ast.ASTStatementExpression;
import net.sourceforge.pmd.lang.java.ast.ASTSwitchLabel;
import net.sourceforge.pmd.lang.java.ast.ASTSwitchStatement;
import net.sourceforge.pmd.lang.java.ast.ASTThrowStatement;
import net.sourceforge.pmd.lang.java.ast.ASTVariableDeclarator;
import net.sourceforge.pmd.lang.java.ast.ASTWhileStatement;
import net.sourceforge.pmd.lang.java.ast.JavaNode;
import net.sourceforge.pmd.lang.java.ast.JavaParserVisitorAdapter;

/**
 * @author raik
 *         <p/>
 *         Sublayer of DataFlowFacade. Finds all data flow nodes and stores the
 *         type information (@see StackObject). At last it uses this information to
 *         link the nodes.
 */
public class StatementAndBraceFinder extends JavaParserVisitorAdapter {
    private final static Logger LOGGER = Logger.getLogger(StatementAndBraceFinder.class.getName()); 

    private final DataFlowHandler dataFlowHandler;
    private Structure dataFlow;
    
    public StatementAndBraceFinder(DataFlowHandler dataFlowHandler) {
	this.dataFlowHandler = dataFlowHandler;
    }

    public void buildDataFlowFor(JavaNode node) {
        if (!(node instanceof ASTMethodDeclaration) && !(node instanceof ASTConstructorDeclaration)) {
            throw new RuntimeException("Can't build a data flow for anything other than a method or a constructor");
        }

        this.dataFlow = new Structure(dataFlowHandler);
        this.dataFlow.createStartNode(node.getBeginLine());
        this.dataFlow.createNewNode(node);

        node.jjtAccept(this, dataFlow);

        this.dataFlow.createEndNode(node.getEndLine());
        if (LOGGER.isLoggable(Level.FINE))
        {
          LOGGER.fine("DataFlow is " + this.dataFlow.dump() ); // @TODO SRT Remove after development  
        }
        Linker linker = new Linker(dataFlowHandler, dataFlow.getBraceStack(), dataFlow.getContinueBreakReturnStack());
        try {
            linker.computePaths();
        } catch (LinkerException e) {
            e.printStackTrace();
        } catch (SequenceException e) {
            e.printStackTrace();
        }
    }

    public Object visit(ASTStatementExpression node, Object data) {
        if (!(data instanceof Structure)) {
            return data;
        }
        Structure dataFlow = (Structure) data;
        LOGGER.finest("createNewNode ASTStatementExpression: line " + node.getBeginLine() +", column " + node.getBeginColumn());
        dataFlow.createNewNode(node);
        return super.visit(node, data);
    }

    public Object visit(ASTVariableDeclarator node, Object data) {
        if (!(data instanceof Structure)) {
            return data;
        }
        Structure dataFlow = (Structure) data;
        LOGGER.finest("createNewNode ASTVariableDeclarator: line " + node.getBeginLine() +", column " + node.getBeginColumn());
        dataFlow.createNewNode(node);
        return super.visit(node, data);
    }

    public Object visit(ASTExpression node, Object data) {
        if (!(data instanceof Structure)) {
            return data;
        }
        Structure dataFlow = (Structure) data;

        // TODO what about throw stmts?
        if (node.jjtGetParent() instanceof ASTIfStatement) {
            dataFlow.createNewNode(node); // START IF
            dataFlow.pushOnStack(NodeType.IF_EXPR, dataFlow.getLast());
            LOGGER.finest("pushOnStack parent IF_EXPR: line " + node.getBeginLine() +", column " + node.getBeginColumn());
        } else if (node.jjtGetParent() instanceof ASTWhileStatement) {
            dataFlow.createNewNode(node); // START WHILE
            dataFlow.pushOnStack(NodeType.WHILE_EXPR, dataFlow.getLast());
            LOGGER.finest("pushOnStack parent WHILE_EXPR: line " + node.getBeginLine() +", column " + node.getBeginColumn());
        } else if (node.jjtGetParent() instanceof ASTSwitchStatement) {
            dataFlow.createNewNode(node); // START SWITCH
            dataFlow.pushOnStack(NodeType.SWITCH_START, dataFlow.getLast());
            LOGGER.finest("pushOnStack parent SWITCH_START: line " + node.getBeginLine() +", column " + node.getBeginColumn());
        } else if (node.jjtGetParent() instanceof ASTForStatement) {
            dataFlow.createNewNode(node); // FOR EXPR
            dataFlow.pushOnStack(NodeType.FOR_EXPR, dataFlow.getLast());
            LOGGER.finest("pushOnStack parent FOR_EXPR: line " + node.getBeginLine() +", column " + node.getBeginColumn());
        } else if (node.jjtGetParent() instanceof ASTDoStatement) {
            dataFlow.createNewNode(node); // DO EXPR
            dataFlow.pushOnStack(NodeType.DO_EXPR, dataFlow.getLast());
            LOGGER.finest("pushOnStack parent DO_EXPR: line " + node.getBeginLine() +", column " + node.getBeginColumn());
        }

        return super.visit(node, data);
    }

    public Object visit(ASTForInit node, Object data) {
        if (!(data instanceof Structure)) {
            return data;
        }
        Structure dataFlow = (Structure) data;
        super.visit(node, data);
        dataFlow.pushOnStack(NodeType.FOR_INIT, dataFlow.getLast());
        LOGGER.finest("pushOnStack FOR_INIT: line " + node.getBeginLine() +", column " + node.getBeginColumn());
        this.addForExpressionNode(node, dataFlow);
        return data;
    }

    public Object visit(ASTLabeledStatement node, Object data) {
        dataFlow.createNewNode(node);
        dataFlow.pushOnStack(NodeType.LABEL_STATEMENT, dataFlow.getLast());
        LOGGER.finest("pushOnStack LABEL_STATEMENT: line " + node.getBeginLine() +", column " + node.getBeginColumn());
        return super.visit(node, data);
    }

    public Object visit(ASTForUpdate node, Object data) {
        if (!(data instanceof Structure)) {
            return data;
        }
        Structure dataFlow = (Structure) data;
        this.addForExpressionNode(node, dataFlow);
        super.visit(node, data);
        dataFlow.pushOnStack(NodeType.FOR_UPDATE, dataFlow.getLast());
        LOGGER.finest("pushOnStack FOR_UPDATE: line " + node.getBeginLine() +", column " + node.getBeginColumn());
        return data;
    }

// 	----------------------------------------------------------------------------
//  BRANCH OUT

    public Object visit(ASTStatement node, Object data) {
        if (!(data instanceof Structure)) {
            return data;
        }
        Structure dataFlow = (Structure) data;

        if (node.jjtGetParent() instanceof ASTForStatement) {
            this.addForExpressionNode(node, dataFlow);
            dataFlow.pushOnStack(NodeType.FOR_BEFORE_FIRST_STATEMENT, dataFlow.getLast());
            LOGGER.finest("pushOnStack FOR_BEFORE_FIRST_STATEMENT: line " + node.getBeginLine() +", column " + node.getBeginColumn());
        } else if (node.jjtGetParent() instanceof ASTDoStatement) {
            dataFlow.pushOnStack(NodeType.DO_BEFORE_FIRST_STATEMENT, dataFlow.getLast());
            dataFlow.createNewNode(node.jjtGetParent());
            LOGGER.finest("pushOnStack DO_BEFORE_FIRST_STATEMENT: line " + node.getBeginLine() +", column " + node.getBeginColumn());
        }

        super.visit(node, data);

        if (node.jjtGetParent() instanceof ASTIfStatement) {
            ASTIfStatement st = (ASTIfStatement) node.jjtGetParent();
            if (!st.hasElse()) {
                dataFlow.pushOnStack(NodeType.IF_LAST_STATEMENT_WITHOUT_ELSE, dataFlow.getLast());
                LOGGER.finest("pushOnStack IF_LAST_STATEMENT_WITHOUT_ELSE: line " + node.getBeginLine() +", column " + node.getBeginColumn());
            } else if (st.hasElse() && !st.jjtGetChild(1).equals(node)) {
                dataFlow.pushOnStack(NodeType.ELSE_LAST_STATEMENT, dataFlow.getLast());
                LOGGER.finest("pushOnStack ELSE_LAST_STATEMENT: line " + node.getBeginLine() +", column " + node.getBeginColumn());
            } else {
                dataFlow.pushOnStack(NodeType.IF_LAST_STATEMENT, dataFlow.getLast());
                LOGGER.finest("pushOnStack IF_LAST_STATEMENT: line " + node.getBeginLine() +", column " + node.getBeginColumn());
            }
        } else if (node.jjtGetParent() instanceof ASTWhileStatement) {
            dataFlow.pushOnStack(NodeType.WHILE_LAST_STATEMENT, dataFlow.getLast());
            LOGGER.finest("pushOnStack WHILE_LAST_STATEMENT: line " + node.getBeginLine() +", column " + node.getBeginColumn());
        } else if (node.jjtGetParent() instanceof ASTForStatement) {
            dataFlow.pushOnStack(NodeType.FOR_END, dataFlow.getLast());
            LOGGER.finest("pushOnStack FOR_END: line " + node.getBeginLine() +", column " + node.getBeginColumn());
        } else if (node.jjtGetParent() instanceof ASTLabeledStatement) {
            dataFlow.pushOnStack(NodeType.LABEL_LAST_STATEMENT, dataFlow.getLast());
            LOGGER.finest("pushOnStack LABEL_LAST_STATEMENT: line " + node.getBeginLine() +", column " + node.getBeginColumn());
        }
        return data;
    }

    public Object visit(ASTSwitchStatement node, Object data) {
        if (!(data instanceof Structure)) {
            return data;
        }
        Structure dataFlow = (Structure) data;
        super.visit(node, data);
        dataFlow.pushOnStack(NodeType.SWITCH_END, dataFlow.getLast());
        LOGGER.finest("pushOnStack SWITCH_END: line " + node.getBeginLine() +", column " + node.getBeginColumn());
        return data;
    }

    public Object visit(ASTSwitchLabel node, Object data) {
        if (!(data instanceof Structure)) {
            return data;
        }
        Structure dataFlow = (Structure) data;
        //super.visit(node, data);
        if (node.jjtGetNumChildren() == 0) {
            dataFlow.pushOnStack(NodeType.SWITCH_LAST_DEFAULT_STATEMENT, dataFlow.getLast());
            LOGGER.finest("pushOnStack SWITCH_LAST_DEFAULT_STATEMENT: line " + node.getBeginLine() +", column " + node.getBeginColumn());
        } else {
            dataFlow.pushOnStack(NodeType.CASE_LAST_STATEMENT, dataFlow.getLast());
            LOGGER.finest("pushOnStack CASE_LAST_STATEMENT: line " + node.getBeginLine() +", column " + node.getBeginColumn());
        }
        return data;
    }

    public Object visit(ASTBreakStatement node, Object data) {
        if (!(data instanceof Structure)) {
            return data;
        }
        Structure dataFlow = (Structure) data;
        dataFlow.createNewNode(node);
        dataFlow.pushOnStack(NodeType.BREAK_STATEMENT, dataFlow.getLast());
        LOGGER.finest("pushOnStack BREAK_STATEMENT: line " + node.getBeginLine() +", column " + node.getBeginColumn());
        return super.visit(node, data);
    }


    public Object visit(ASTContinueStatement node, Object data) {
        if (!(data instanceof Structure)) {
            return data;
        }
        Structure dataFlow = (Structure) data;
        dataFlow.createNewNode(node);
        dataFlow.pushOnStack(NodeType.CONTINUE_STATEMENT, dataFlow.getLast());
        LOGGER.finest("pushOnStack CONTINUE_STATEMENT: line " + node.getBeginLine() +", column " + node.getBeginColumn());
        return super.visit(node, data);
    }

    public Object visit(ASTReturnStatement node, Object data) {
        if (!(data instanceof Structure)) {
            return data;
        }
        Structure dataFlow = (Structure) data;
        dataFlow.createNewNode(node);
        dataFlow.pushOnStack(NodeType.RETURN_STATEMENT, dataFlow.getLast());
        LOGGER.finest("pushOnStack RETURN_STATEMENT: line " + node.getBeginLine() +", column " + node.getBeginColumn());
        return super.visit(node, data);
    }

    public Object visit(ASTThrowStatement node, Object data) {
        if (!(data instanceof Structure)) {
            return data;
        }
        Structure dataFlow = (Structure) data;
        dataFlow.createNewNode(node);
        dataFlow.pushOnStack(NodeType.THROW_STATEMENT, dataFlow.getLast());
        LOGGER.finest("pushOnStack THROW_STATEMENT: line " + node.getBeginLine() +", column " + node.getBeginColumn());
        return super.visit(node, data);
    }

    /*
     * The method handles the special "for" loop. It creates always an
     * expression node even if the loop looks like for(;;).
     * */
    private void addForExpressionNode(Node node, Structure dataFlow) {
        ASTForStatement parent = (ASTForStatement) node.jjtGetParent();
        boolean hasExpressionChild = false;
        boolean hasForInitNode = false;
        boolean hasForUpdateNode = false;

        for (int i = 0; i < parent.jjtGetNumChildren(); i++) {
            if (parent.jjtGetChild(i) instanceof ASTExpression) {
                hasExpressionChild = true;
            } else if (parent.jjtGetChild(i) instanceof ASTForUpdate) {
                hasForUpdateNode = true;
            } else if (parent.jjtGetChild(i) instanceof ASTForInit) {
                hasForInitNode = true;
            }
        }
        if (!hasExpressionChild) {
            if (node instanceof ASTForInit) {
                dataFlow.createNewNode(node);
                dataFlow.pushOnStack(NodeType.FOR_EXPR, dataFlow.getLast());
                LOGGER.finest("pushOnStack FOR_EXPR: line " + node.getBeginLine() +", column " + node.getBeginColumn());
            } else if (node instanceof ASTForUpdate) {
                if (!hasForInitNode) {
                    dataFlow.createNewNode(node);
                    dataFlow.pushOnStack(NodeType.FOR_EXPR, dataFlow.getLast());
                    LOGGER.finest("pushOnStack FOR_EXPR: line " + node.getBeginLine() +", column " + node.getBeginColumn());
                }
            } else if (node instanceof ASTStatement) {
                if (!hasForInitNode && !hasForUpdateNode) {
                    dataFlow.createNewNode(node);
                    dataFlow.pushOnStack(NodeType.FOR_EXPR, dataFlow.getLast());
                    LOGGER.finest("pushOnStack FOR_EXPR: line " + node.getBeginLine() +", column " + node.getBeginColumn());
                }
            }
        }
    }
}
