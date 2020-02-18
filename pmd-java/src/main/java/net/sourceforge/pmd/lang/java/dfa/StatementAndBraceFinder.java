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
import net.sourceforge.pmd.lang.java.ast.ASTAssertStatement;
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
 * Sublayer of DataFlowFacade. Finds all data flow nodes and stores the
 * type information (@see StackObject). At last it uses this information
 * to link the nodes.
 *
 * @author raik
 */
public class StatementAndBraceFinder extends JavaParserVisitorAdapter {
    private static final Logger LOGGER = Logger.getLogger(StatementAndBraceFinder.class.getName());

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
        if (LOGGER.isLoggable(Level.FINE)) {
            // TODO SRT Remove after development
            LOGGER.fine("DataFlow is " + this.dataFlow.dump());
        }
        Linker linker = new Linker(dataFlowHandler, dataFlow.getBraceStack(), dataFlow.getContinueBreakReturnStack());
        try {
            linker.computePaths();
        } catch (SequenceException | LinkerException e) {
            e.printStackTrace();
        }
    }


    private void tryToLog(String tag, NodeType type, Node node) {
        if (LOGGER.isLoggable(Level.FINEST)) {
            LOGGER.finest("pushOnStack " + tag + " " + type + ": line " + node.getBeginLine()
                + ", column " + node.getBeginColumn());
        }
    }

    private void tryToLog(NodeType type, Node node) {
        tryToLog("", type, node);
    }

    @Override
    public Object visit(ASTStatementExpression node, Object data) {
        if (!(data instanceof Structure)) {
            return data;
        }
        Structure dataFlow = (Structure) data;
        if (LOGGER.isLoggable(Level.FINEST)) {
            LOGGER.finest("createNewNode ASTStatementExpression: line " + node.getBeginLine() + ", column "
                + node.getBeginColumn());
        }
        dataFlow.createNewNode(node);
        return super.visit(node, data);
    }

    @Override
    public Object visit(ASTVariableDeclarator node, Object data) {
        if (!(data instanceof Structure)) {
            return data;
        }
        Structure dataFlow = (Structure) data;
        if (LOGGER.isLoggable(Level.FINEST)) {
            LOGGER.finest("createNewNode ASTVariableDeclarator: line " + node.getBeginLine() + ", column "
                + node.getBeginColumn());
        }
        dataFlow.createNewNode(node);
        return super.visit(node, data);
    }

    @Override
    public Object visit(ASTExpression node, Object data) {
        if (!(data instanceof Structure)) {
            return data;
        }
        Structure dataFlow = (Structure) data;

        String loggerTag = "parent";

        Node parent = node.getParent();

        // TODO what about throw stmts?
        if (parent instanceof ASTIfStatement) {
            dataFlow.createNewNode(node); // START IF
            dataFlow.pushOnStack(NodeType.IF_EXPR, dataFlow.getLast());
            tryToLog(loggerTag, NodeType.IF_EXPR, node);
        } else if (parent instanceof ASTWhileStatement) {
            dataFlow.createNewNode(node); // START WHILE
            dataFlow.pushOnStack(NodeType.WHILE_EXPR, dataFlow.getLast());
            tryToLog(loggerTag, NodeType.WHILE_EXPR, node);
        } else if (parent instanceof ASTSwitchStatement) {
            dataFlow.createNewNode(node); // START SWITCH
            dataFlow.pushOnStack(NodeType.SWITCH_START, dataFlow.getLast());
            tryToLog(loggerTag, NodeType.SWITCH_START, node);
        } else if (parent instanceof ASTForStatement) {
            dataFlow.createNewNode(node); // FOR EXPR
            dataFlow.pushOnStack(NodeType.FOR_EXPR, dataFlow.getLast());
            tryToLog(loggerTag, NodeType.FOR_EXPR, node);
        } else if (parent instanceof ASTDoStatement) {
            dataFlow.createNewNode(node); // DO EXPR
            dataFlow.pushOnStack(NodeType.DO_EXPR, dataFlow.getLast());
            tryToLog(loggerTag, NodeType.DO_EXPR, node);
        } else if (parent instanceof ASTAssertStatement) {
            dataFlow.createNewNode(node);
            dataFlow.pushOnStack(NodeType.ASSERT_STATEMENT, dataFlow.getLast());
            tryToLog(loggerTag, NodeType.ASSERT_STATEMENT, node);
        }

        return super.visit(node, data);
    }


    @Override
    public Object visit(ASTForInit node, Object data) {
        if (!(data instanceof Structure)) {
            return data;
        }

        Structure dataFlow = (Structure) data;
        super.visit(node, data);

        dataFlow.pushOnStack(NodeType.FOR_INIT, dataFlow.getLast());

        tryToLog(NodeType.FOR_INIT, node);
        this.addForExpressionNode(node, dataFlow);

        return data;
    }


    @Override
    public Object visit(ASTLabeledStatement node, Object data) {
        dataFlow.createNewNode(node);
        dataFlow.pushOnStack(NodeType.LABEL_STATEMENT, dataFlow.getLast());
        tryToLog(NodeType.LABEL_STATEMENT, node);
        return super.visit(node, data);
    }

    @Override
    public Object visit(ASTForUpdate node, Object data) {
        if (!(data instanceof Structure)) {
            return data;
        }
        Structure dataFlow = (Structure) data;
        this.addForExpressionNode(node, dataFlow);
        super.visit(node, data);
        dataFlow.pushOnStack(NodeType.FOR_UPDATE, dataFlow.getLast());
        tryToLog(NodeType.FOR_UPDATE, node);
        return data;
    }

    // ----------------------------------------------------------------------------
    // BRANCH OUT

    @Override
    public Object visit(ASTStatement node, Object data) {
        if (!(data instanceof Structure)) {
            return data;
        }
        Structure dataFlow = (Structure) data;

        if (node.getParent() instanceof ASTForStatement) {
            this.addForExpressionNode(node, dataFlow);
            dataFlow.pushOnStack(NodeType.FOR_BEFORE_FIRST_STATEMENT, dataFlow.getLast());
            tryToLog(NodeType.FOR_BEFORE_FIRST_STATEMENT, node);
        } else if (node.getParent() instanceof ASTDoStatement) {
            dataFlow.pushOnStack(NodeType.DO_BEFORE_FIRST_STATEMENT, dataFlow.getLast());
            dataFlow.createNewNode(node.getParent());
            tryToLog(NodeType.DO_BEFORE_FIRST_STATEMENT, node);
        }

        super.visit(node, data);

        if (node.getParent() instanceof ASTIfStatement) {
            ASTIfStatement st = (ASTIfStatement) node.getParent();
            if (!st.hasElse()) {
                dataFlow.pushOnStack(NodeType.IF_LAST_STATEMENT_WITHOUT_ELSE, dataFlow.getLast());
                tryToLog(NodeType.IF_LAST_STATEMENT_WITHOUT_ELSE, node);
            } else if (st.hasElse() && !st.getChild(1).equals(node)) {
                dataFlow.pushOnStack(NodeType.ELSE_LAST_STATEMENT, dataFlow.getLast());
                tryToLog(NodeType.ELSE_LAST_STATEMENT, node);
            } else {
                dataFlow.pushOnStack(NodeType.IF_LAST_STATEMENT, dataFlow.getLast());
                tryToLog(NodeType.IF_LAST_STATEMENT, node);
            }
        } else if (node.getParent() instanceof ASTWhileStatement) {
            dataFlow.pushOnStack(NodeType.WHILE_LAST_STATEMENT, dataFlow.getLast());
            tryToLog(NodeType.WHILE_LAST_STATEMENT, node);
        } else if (node.getParent() instanceof ASTForStatement) {
            dataFlow.pushOnStack(NodeType.FOR_END, dataFlow.getLast());
            tryToLog(NodeType.FOR_END, node);
        } else if (node.getParent() instanceof ASTLabeledStatement) {
            dataFlow.pushOnStack(NodeType.LABEL_LAST_STATEMENT, dataFlow.getLast());
            tryToLog(NodeType.LABEL_LAST_STATEMENT, node);
        }
        return data;
    }

    @Override
    public Object visit(ASTSwitchStatement node, Object data) {
        if (!(data instanceof Structure)) {
            return data;
        }
        Structure dataFlow = (Structure) data;
        super.visit(node, data);
        dataFlow.pushOnStack(NodeType.SWITCH_END, dataFlow.getLast());
        tryToLog(NodeType.SWITCH_END, node);
        return data;
    }

    @Override
    public Object visit(ASTSwitchLabel node, Object data) {
        if (!(data instanceof Structure)) {
            return data;
        }
        Structure dataFlow = (Structure) data;
        // super.visit(node, data);
        if (node.getNumChildren() == 0) {
            dataFlow.pushOnStack(NodeType.SWITCH_LAST_DEFAULT_STATEMENT, dataFlow.getLast());
            tryToLog(NodeType.SWITCH_LAST_DEFAULT_STATEMENT, node);
        } else {
            dataFlow.pushOnStack(NodeType.CASE_LAST_STATEMENT, dataFlow.getLast());
            tryToLog(NodeType.CASE_LAST_STATEMENT, node);
        }
        return data;
    }

    @Override
    public Object visit(ASTBreakStatement node, Object data) {
        if (!(data instanceof Structure)) {
            return data;
        }
        Structure dataFlow = (Structure) data;
        dataFlow.createNewNode(node);
        dataFlow.pushOnStack(NodeType.BREAK_STATEMENT, dataFlow.getLast());
        tryToLog(NodeType.BREAK_STATEMENT, node);
        return super.visit(node, data);
    }

    @Override
    public Object visit(ASTContinueStatement node, Object data) {
        if (!(data instanceof Structure)) {
            return data;
        }
        Structure dataFlow = (Structure) data;
        dataFlow.createNewNode(node);
        dataFlow.pushOnStack(NodeType.CONTINUE_STATEMENT, dataFlow.getLast());
        tryToLog(NodeType.CONTINUE_STATEMENT, node);
        return super.visit(node, data);
    }

    @Override
    public Object visit(ASTReturnStatement node, Object data) {
        if (!(data instanceof Structure)) {
            return data;
        }
        Structure dataFlow = (Structure) data;
        dataFlow.createNewNode(node);
        dataFlow.pushOnStack(NodeType.RETURN_STATEMENT, dataFlow.getLast());
        tryToLog(NodeType.RETURN_STATEMENT, node);
        return super.visit(node, data);
    }

    @Override
    public Object visit(ASTThrowStatement node, Object data) {
        if (!(data instanceof Structure)) {
            return data;
        }
        Structure dataFlow = (Structure) data;
        dataFlow.createNewNode(node);
        dataFlow.pushOnStack(NodeType.THROW_STATEMENT, dataFlow.getLast());
        tryToLog(NodeType.THROW_STATEMENT, node);
        return super.visit(node, data);
    }

    /*
     * The method handles the special "for" loop. It creates always an
     * expression node even if the loop looks like for(;;).
     */
    private void addForExpressionNode(Node node, Structure dataFlow) {
        ASTForStatement parent = (ASTForStatement) node.getParent();
        boolean hasExpressionChild = false;
        boolean hasForInitNode = false;
        boolean hasForUpdateNode = false;

        for (int i = 0; i < parent.getNumChildren(); i++) {
            if (parent.getChild(i) instanceof ASTExpression) {
                hasExpressionChild = true;
            } else if (parent.getChild(i) instanceof ASTForUpdate) {
                hasForUpdateNode = true;
            } else if (parent.getChild(i) instanceof ASTForInit) {
                hasForInitNode = true;
            }
        }
        if (!hasExpressionChild) {
            if (node instanceof ASTForInit) {
                dataFlow.createNewNode(node);
                dataFlow.pushOnStack(NodeType.FOR_EXPR, dataFlow.getLast());
                tryToLog(NodeType.FOR_EXPR, node);
            } else if (node instanceof ASTForUpdate) {
                if (!hasForInitNode) {
                    dataFlow.createNewNode(node);
                    dataFlow.pushOnStack(NodeType.FOR_EXPR, dataFlow.getLast());
                    tryToLog(NodeType.FOR_EXPR, node);
                }
            } else if (node instanceof ASTStatement) {
                if (!hasForInitNode && !hasForUpdateNode) {
                    dataFlow.createNewNode(node);
                    dataFlow.pushOnStack(NodeType.FOR_EXPR, dataFlow.getLast());
                    tryToLog(NodeType.FOR_EXPR, node);
                }
            }
        }
    }
}
