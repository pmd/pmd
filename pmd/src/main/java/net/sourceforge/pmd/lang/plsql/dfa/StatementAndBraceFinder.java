/**
 * BSD-style license; for more info see http://pmd.sourceforge.net/license.html
 */
package net.sourceforge.pmd.lang.plsql.dfa;

import java.util.logging.Level;
import java.util.logging.Logger;


import net.sourceforge.pmd.lang.DataFlowHandler;
import net.sourceforge.pmd.lang.ast.Node;
import net.sourceforge.pmd.lang.dfa.Linker;
import net.sourceforge.pmd.lang.dfa.LinkerException;
import net.sourceforge.pmd.lang.dfa.NodeType;
import net.sourceforge.pmd.lang.dfa.SequenceException;
import net.sourceforge.pmd.lang.dfa.Structure;
import net.sourceforge.pmd.lang.plsql.ast.ASTExitStatement;
//import net.sourceforge.pmd.lang.plsql.ast.ASTConstructorDeclaration;
import net.sourceforge.pmd.lang.plsql.ast.ASTContinueStatement;
import net.sourceforge.pmd.lang.plsql.ast.ASTExitStatement;
import net.sourceforge.pmd.lang.plsql.ast.ASTGotoStatement;
import net.sourceforge.pmd.lang.plsql.ast.ASTLoopStatement;
import net.sourceforge.pmd.lang.plsql.ast.ASTExpression;
//import net.sourceforge.pmd.lang.plsql.ast.ASTForInit;
import net.sourceforge.pmd.lang.plsql.ast.ASTForStatement;
//import net.sourceforge.pmd.lang.plsql.ast.ASTForUpdate;
import net.sourceforge.pmd.lang.plsql.ast.ASTIfStatement;
import net.sourceforge.pmd.lang.plsql.ast.ASTLabelledStatement;
import net.sourceforge.pmd.lang.plsql.ast.ASTMethodDeclaration;
import net.sourceforge.pmd.lang.plsql.ast.ASTProgramUnit;
import net.sourceforge.pmd.lang.plsql.ast.ASTTriggerUnit;
import net.sourceforge.pmd.lang.plsql.ast.ASTReturnStatement;
import net.sourceforge.pmd.lang.plsql.ast.ASTStatement;
//import net.sourceforge.pmd.lang.plsql.ast.ASTStatementExpression;
//import net.sourceforge.pmd.lang.plsql.ast.ASTSwitchLabel;
import net.sourceforge.pmd.lang.plsql.ast.ASTCaseStatement;
import net.sourceforge.pmd.lang.plsql.ast.ASTCaseWhenClause;
import net.sourceforge.pmd.lang.plsql.ast.ASTCloseStatement;
import net.sourceforge.pmd.lang.plsql.ast.ASTElseClause;
import net.sourceforge.pmd.lang.plsql.ast.ASTElsifClause;
import net.sourceforge.pmd.lang.plsql.ast.ASTEmbeddedSqlStatement;
import net.sourceforge.pmd.lang.plsql.ast.ASTFetchStatement;
import net.sourceforge.pmd.lang.plsql.ast.ASTOpenStatement;
import net.sourceforge.pmd.lang.plsql.ast.ASTPipelineStatement;
import net.sourceforge.pmd.lang.plsql.ast.ASTRaiseStatement;
import net.sourceforge.pmd.lang.plsql.ast.ASTSqlStatement;
import net.sourceforge.pmd.lang.plsql.ast.ASTTypeMethod;
import net.sourceforge.pmd.lang.plsql.ast.ASTVariableOrConstantDeclarator;
import net.sourceforge.pmd.lang.plsql.ast.ASTWhileStatement;
import net.sourceforge.pmd.lang.plsql.ast.SimpleNode;
import net.sourceforge.pmd.lang.plsql.ast.PLSQLParserVisitorAdapter;

/**
 * @author raik
 *         <p/>
 *         Sublayer of DataFlowFacade. Finds all data flow nodes and stores the
 *         type information (@see StackObject). At last it uses this information to
 *         link the nodes.
 */
public class StatementAndBraceFinder extends PLSQLParserVisitorAdapter {
    private final static Logger LOGGER = Logger.getLogger(StatementAndBraceFinder.class.getName()); 

    private final DataFlowHandler dataFlowHandler;
    private Structure dataFlow;
    
    public StatementAndBraceFinder(DataFlowHandler dataFlowHandler) {
	this.dataFlowHandler = dataFlowHandler;
    }

    public void buildDataFlowFor(SimpleNode node) {
        LOGGER.entering(this.getClass().getCanonicalName(),"buildDataFlowFor");
        if (!(node instanceof ASTMethodDeclaration) && !(node instanceof ASTProgramUnit) && !(node instanceof ASTTypeMethod) && !(node instanceof ASTTriggerUnit) /* SRT && !(node instanceof ASTConstructorDeclaration) */) {
            throw new RuntimeException("Can't build a data flow for anything other than a Method or a Trigger");
        }

        this.dataFlow = new Structure(dataFlowHandler);
        this.dataFlow.createStartNode(node.getBeginLine());
        this.dataFlow.createNewNode(node);

        node.jjtAccept(this, dataFlow);

        this.dataFlow.createEndNode(node.getEndLine());
        LOGGER.fine("DataFlow is generated " + this.dataFlow.dump() ); 

        if (LOGGER.isLoggable(Level.FINEST))
        {
          LOGGER.finest("DataFlow is " + this.dataFlow.dump() ); // @TODO SRT Remove after development  
        }
        Linker linker = new Linker(dataFlowHandler, dataFlow.getBraceStack(), dataFlow.getContinueBreakReturnStack());
        try {
            linker.computePaths();
        } catch (LinkerException e) {
            LOGGER.severe("LinkerException");
            e.printStackTrace();
        } catch (SequenceException e) {
            LOGGER.severe("SequenceException");
            e.printStackTrace();
        }
        LOGGER.exiting(this.getClass().getCanonicalName(),"buildDataFlowFor");
    }

    /* @TODO SRT 
     * Java StatementExpression production seems to be non-control-flow productions under Statement()
     * 
     * As such it seems to be equivalent to these PLSQL productions:-
     * ASTSqlStatement
     * ASTCloseStatement
     * ASTOpenStatement
     * ASTFetchStatement
     * ASTEmbeddedSqlStatement
     * ASTPipelineStatement
     * ASTExpression
     * /

     public Object visit(ASTStatementExpression node, Object data) {
        if (!(data instanceof Structure)) {
            return data;
        }
        Structure dataFlow = (Structure) data;
        dataFlow.createNewNode(node);
        return super.visit(node, data);
    } 
    */ 
    
     public Object visit(ASTSqlStatement node, Object data) {
        if (!(data instanceof Structure)) {
            LOGGER.finest("immediate return ASTSqlStatement: line " + node.getBeginLine() +", column " + node.getBeginColumn());
            return data;
        }
        Structure dataFlow = (Structure) data;
        dataFlow.createNewNode(node);
        LOGGER.finest("createNewNode ASTSqlStatement: line " + node.getBeginLine() +", column " + node.getBeginColumn());
        return super.visit(node, data);
    } 
    
     public Object visit(ASTEmbeddedSqlStatement node, Object data) {
        if (!(data instanceof Structure)) {
            LOGGER.finest("immediate return ASTEmbeddedSqlStatement: line " + node.getBeginLine() +", column " + node.getBeginColumn());
            return data;
        }
        Structure dataFlow = (Structure) data;
        dataFlow.createNewNode(node);
        LOGGER.finest("createNewNode ASTEmbeddedSqlStatement: line " + node.getBeginLine() +", column " + node.getBeginColumn());
        return super.visit(node, data);
    } 
    
     public Object visit(ASTCloseStatement node, Object data) {
        if (!(data instanceof Structure)) {
            return data;
        }
        Structure dataFlow = (Structure) data;
        dataFlow.createNewNode(node);
        LOGGER.finest("createNewNode ASTCloseStatement: line " + node.getBeginLine() +", column " + node.getBeginColumn());
        return super.visit(node, data);
    } 
    
     public Object visit(ASTOpenStatement node, Object data) {
        if (!(data instanceof Structure)) {
            return data;
        }
        Structure dataFlow = (Structure) data;
        dataFlow.createNewNode(node);
        LOGGER.finest("createNewNode ASTOpenStatement: line " + node.getBeginLine() +", column " + node.getBeginColumn());
        return super.visit(node, data);
    } 
    
     public Object visit(ASTFetchStatement node, Object data) {
        if (!(data instanceof Structure)) {
            return data;
        }
        Structure dataFlow = (Structure) data;
        dataFlow.createNewNode(node);
        LOGGER.finest("createNewNode ASTFetchStatement: line " + node.getBeginLine() +", column " + node.getBeginColumn());
        return super.visit(node, data);
    } 
    
     public Object visit(ASTPipelineStatement node, Object data) {
        if (!(data instanceof Structure)) {
            return data;
        }
        Structure dataFlow = (Structure) data;
        dataFlow.createNewNode(node);
        LOGGER.finest("createNewNode ASTPipelineStatement: line " + node.getBeginLine() +", column " + node.getBeginColumn());
        return super.visit(node, data);
    } 
    
    /* */

    public Object visit(ASTVariableOrConstantDeclarator node, Object data) {
        if (!(data instanceof Structure)) {
            return data;
        }
        Structure dataFlow = (Structure) data;
        dataFlow.createNewNode(node);
        LOGGER.finest("createNewNode ASTVariableOrConstantDeclarator: line " + node.getBeginLine() +", column " + node.getBeginColumn());
        return super.visit(node, data);
    }

    public Object visit(ASTExpression node, Object data) {
        LOGGER.finest("Entry ASTExpression: line " + node.getBeginLine() +", column " + node.getBeginColumn());
        if (!(data instanceof Structure)) {
            LOGGER.finest("immediate return ASTExpression: line " + node.getBeginLine() +", column " + node.getBeginColumn());
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
        } else if (node.jjtGetParent() instanceof ASTCaseStatement) {
            dataFlow.createNewNode(node); // START SWITCH
            dataFlow.pushOnStack(NodeType.SWITCH_START, dataFlow.getLast());
            LOGGER.finest("pushOnStack parent SWITCH_EXPR: line " + node.getBeginLine() +", column " + node.getBeginColumn());
        } else if (node.jjtGetParent() instanceof ASTForStatement) {
            dataFlow.createNewNode(node); // FOR EXPR
            dataFlow.pushOnStack(NodeType.FOR_EXPR, dataFlow.getLast());
            LOGGER.finest("pushOnStack parent FOR_EXPR: line " + node.getBeginLine() +", column " + node.getBeginColumn());
        } else if (node.jjtGetParent() instanceof ASTLoopStatement) {
            dataFlow.createNewNode(node); // DO EXPR
            dataFlow.pushOnStack(NodeType.DO_EXPR, dataFlow.getLast());
            LOGGER.finest("pushOnStack parent DO_EXPR: line " + node.getBeginLine() +", column " + node.getBeginColumn());
        }

        return super.visit(node, data);
    }

    /* SRT public Object visit(ASTForInit node, Object data) {
        if (!(data instanceof Structure)) {
            return data;
        }
        Structure dataFlow = (Structure) data;
        super.visit(node, data);
        dataFlow.pushOnStack(NodeType.FOR_INIT, dataFlow.getLast());
        this.addForExpressionNode(node, dataFlow);
        return data;
    }*/

    public Object visit(ASTLabelledStatement node, Object data) {
        dataFlow.createNewNode(node);
        dataFlow.pushOnStack(NodeType.LABEL_STATEMENT, dataFlow.getLast());
        LOGGER.finest("pushOnStack LABEL_STATEMENT: line " + node.getBeginLine() +", column " + node.getBeginColumn());
        return super.visit(node, data);
    } 

    /* SRT public Object visit(ASTForUpdate node, Object data) {
        if (!(data instanceof Structure)) {
            return data;
        }
        Structure dataFlow = (Structure) data;
        this.addForExpressionNode(node, dataFlow);
        super.visit(node, data);
        dataFlow.pushOnStack(NodeType.FOR_UPDATE, dataFlow.getLast());
        return data;
    }*/

// 	----------------------------------------------------------------------------
//  BRANCH OUT

    public Object visit(ASTStatement node, Object data) {
        LOGGER.finest("entry ASTStatement: line " + node.getBeginLine() +", column " + node.getBeginColumn());
        if (!(data instanceof Structure)) {
            LOGGER.finest("immediate return ASTStatement: line " + node.getBeginLine() +", column " + node.getBeginColumn());
            return data;
        }
        Structure dataFlow = (Structure) data;

        if (node.jjtGetParent() instanceof ASTForStatement) {
            this.addForExpressionNode(node, dataFlow);
            dataFlow.pushOnStack(NodeType.FOR_BEFORE_FIRST_STATEMENT, dataFlow.getLast());
            LOGGER.finest("pushOnStack FOR_BEFORE_FIRST_STATEMENT: line " + node.getBeginLine() +", column " + node.getBeginColumn());
        } else if (node.jjtGetParent() instanceof ASTLoopStatement) {
            dataFlow.pushOnStack(NodeType.DO_BEFORE_FIRST_STATEMENT, dataFlow.getLast());
            dataFlow.createNewNode(node.jjtGetParent());
            LOGGER.finest("pushOnStack DO_BEFORE_FIRST_STATEMENT: line " + node.getBeginLine() +", column " + node.getBeginColumn());
        }

        super.visit(node, data);

        /*
         * PLSQL AST now has explicit ELSIF and ELSE clauses
         * These can be used instead of the hasElse() calls 
         */
        if (node.jjtGetParent() instanceof ASTIfStatement) {
            ASTIfStatement st = (ASTIfStatement) node.jjtGetParent();
            if (null == st.getFirstChildOfType(ASTElseClause.class) ) {
                dataFlow.pushOnStack(NodeType.IF_LAST_STATEMENT_WITHOUT_ELSE, dataFlow.getLast());
                LOGGER.finest("pushOnStack IF_LAST_STATEMENT_WITHOUT_ELSE: line " + node.getBeginLine() +", column " + node.getBeginColumn());
            } else if (null != st.getFirstChildOfType(ASTElseClause.class) && !st.jjtGetChild(1).equals( (Node) node)) {
                dataFlow.pushOnStack(NodeType.ELSE_LAST_STATEMENT, dataFlow.getLast());
                LOGGER.finest("pushOnStack ELSE_LAST_STATEMENT: line " + node.getBeginLine() +", column " + node.getBeginColumn());
            } else {
                dataFlow.pushOnStack(NodeType.IF_LAST_STATEMENT, dataFlow.getLast());
                LOGGER.finest("pushOnStack IF_LAST_STATEMENT: line " + node.getBeginLine() +", column " + node.getBeginColumn());
            }
        } else if (node.jjtGetParent() instanceof ASTElseClause) {
            dataFlow.pushOnStack(NodeType.ELSE_LAST_STATEMENT, dataFlow.getLast());
                LOGGER.finest("pushOnStack ELSE_LAST_STATEMENT: line " + node.getBeginLine() +", column " + node.getBeginColumn());
        } else if (node.jjtGetParent() instanceof ASTWhileStatement) {
            dataFlow.pushOnStack(NodeType.WHILE_LAST_STATEMENT, dataFlow.getLast());
                LOGGER.finest("pushOnStack WHILE_LAST_STATEMENT: line " + node.getBeginLine() +", column " + node.getBeginColumn());
        } else if (node.jjtGetParent() instanceof ASTForStatement) {
            dataFlow.pushOnStack(NodeType.FOR_END, dataFlow.getLast());
                LOGGER.finest("pushOnStack FOR_END: line " + node.getBeginLine() +", column " + node.getBeginColumn());
        } else if (node.jjtGetParent() instanceof ASTLabelledStatement) {
            dataFlow.pushOnStack(NodeType.LABEL_LAST_STATEMENT, dataFlow.getLast());
                LOGGER.finest("pushOnStack LABEL_LAST_STATEMENT: line " + node.getBeginLine() +", column " + node.getBeginColumn());
        } 
        return data;
    }

    public Object visit(ASTCaseStatement node, Object data) {
        if (!(data instanceof Structure)) {
            return data;
        }
        Structure dataFlow = (Structure) data;
        super.visit(node, data);
        dataFlow.pushOnStack(NodeType.SWITCH_END, dataFlow.getLast());
        LOGGER.finest("pushOnStack SWITCH_END: line " + node.getBeginLine() +", column " + node.getBeginColumn());
        return data;
    }

    /*SRT public Object visit(ASTSwitchLabel node, Object data) {
        if (!(data instanceof Structure)) {
            return data;
        }
        Structure dataFlow = (Structure) data;
        //super.visit(node, data);
        if (node.jjtGetNumChildren() == 0) {
            dataFlow.pushOnStack(NodeType.SWITCH_LAST_DEFAULT_STATEMENT, dataFlow.getLast());
        } else {
            dataFlow.pushOnStack(NodeType.CASE_LAST_STATEMENT, dataFlow.getLast());
        }
        return data;
    }
    */


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

    /**
     * Treat a PLSQL GOTO like a Java "break"
     * 
     * @param node
     * @param data
     * @return 
     */
    public Object visit(ASTExitStatement node, Object data) {
        if (!(data instanceof Structure)) {
            return data;
        }
        Structure dataFlow = (Structure) data;
        dataFlow.createNewNode(node);
        dataFlow.pushOnStack(NodeType.BREAK_STATEMENT, dataFlow.getLast());
        LOGGER.finest("pushOnStack BREAK_STATEMENT: line " + node.getBeginLine() +", column " + node.getBeginColumn());
        return super.visit(node, data);
    }
	
    /**
     * Treat a PLSQL GOTO like a Java "break"
     * 
     * @param node
     * @param data
     * @return 
     */
    public Object visit(ASTGotoStatement node, Object data) {
        if (!(data instanceof Structure)) {
            return data;
        }
        Structure dataFlow = (Structure) data;
        dataFlow.createNewNode(node);
        dataFlow.pushOnStack(NodeType.BREAK_STATEMENT, dataFlow.getLast());
        LOGGER.finest("pushOnStack BREAK_STATEMENT (GOTO): line " + node.getBeginLine() +", column " + node.getBeginColumn());
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

    public Object visit(ASTRaiseStatement node, Object data) {
        if (!(data instanceof Structure)) {
            return data;
        }
        Structure dataFlow = (Structure) data;
        dataFlow.createNewNode(node);
        dataFlow.pushOnStack(NodeType.THROW_STATEMENT, dataFlow.getLast());
        LOGGER.finest("pushOnStack THROW: line " + node.getBeginLine() +", column " + node.getBeginColumn());
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
            } /* SRT else if (parent.jjtGetChild(i) instanceof ASTForUpdate) {
                hasForUpdateNode = true;
            } else if (parent.jjtGetChild(i) instanceof ASTForInit) {
                hasForInitNode = true;
            }*/
        }
        if (!hasExpressionChild) {
            /*if (node instanceof ASTForInit) {
                dataFlow.createNewNode(node);
                dataFlow.pushOnStack(NodeType.FOR_EXPR, dataFlow.getLast());
            } else if (node instanceof ASTForUpdate) {
                if (!hasForInitNode) {
                    dataFlow.createNewNode(node);
                    dataFlow.pushOnStack(NodeType.FOR_EXPR, dataFlow.getLast());
                }
            } else */ if (node instanceof ASTStatement) {
                /* if (!hasForInitNode && !hasForUpdateNode) {
                    dataFlow.createNewNode(node);
                    dataFlow.pushOnStack(NodeType.FOR_EXPR, dataFlow.getLast());
                } */
            }
        }
    }
}
