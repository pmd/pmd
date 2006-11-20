/*
 * Created on 11.07.2004
 */
package net.sourceforge.pmd.dfa;

import net.sourceforge.pmd.ast.*;

/**
 * @author raik
 *         <p/>
 *         Sublayer of DataFlowFacade. Finds all data flow nodes and stores the
 *         type information (@see StackObject). At last it uses this information to
 *         link the nodes.
 */
public class StatementAndBraceFinder extends JavaParserVisitorAdapter {

    private Structure dataFlow;

    public void buildDataFlowFor(SimpleJavaNode node) {
        if (!(node instanceof ASTMethodDeclaration) && !(node instanceof ASTConstructorDeclaration)) {
            throw new RuntimeException("Can't build a data flow for anything other than a method or a constructor");
        }

        this.dataFlow = new Structure();
        this.dataFlow.createStartNode(node.getBeginLine());
        this.dataFlow.createNewNode(node);

        node.jjtAccept(this, dataFlow);

        this.dataFlow.createEndNode(node.getEndLine());

        Linker linker = new Linker(dataFlow.getBraceStack(), dataFlow.getContinueBreakReturnStack());
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
        dataFlow.createNewNode(node);
        return super.visit(node, data);
    }

    public Object visit(ASTVariableDeclarator node, Object data) {
        if (!(data instanceof Structure)) {
            return data;
        }
        Structure dataFlow = (Structure) data;
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
        } else if (node.jjtGetParent() instanceof ASTWhileStatement) {
            dataFlow.createNewNode(node); // START WHILE
            dataFlow.pushOnStack(NodeType.WHILE_EXPR, dataFlow.getLast());
        } else if (node.jjtGetParent() instanceof ASTSwitchStatement) {
            dataFlow.createNewNode(node); // START SWITCH
            dataFlow.pushOnStack(NodeType.SWITCH_START, dataFlow.getLast());
        } else if (node.jjtGetParent() instanceof ASTForStatement) {
            dataFlow.createNewNode(node); // FOR EXPR
            dataFlow.pushOnStack(NodeType.FOR_EXPR, dataFlow.getLast());
        } else if (node.jjtGetParent() instanceof ASTDoStatement) {
            dataFlow.createNewNode(node); // DO EXPR
            dataFlow.pushOnStack(NodeType.DO_EXPR, dataFlow.getLast());
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
        this.addForExpressionNode(node, dataFlow);
        return data;
    }

    public Object visit(ASTLabeledStatement node, Object data) {
        dataFlow.createNewNode(node);
        dataFlow.pushOnStack(NodeType.LABEL_STATEMENT, dataFlow.getLast());
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
        } else if (node.jjtGetParent() instanceof ASTDoStatement) {
            dataFlow.pushOnStack(NodeType.DO_BEFORE_FIRST_STATEMENT, dataFlow.getLast());
            dataFlow.createNewNode((SimpleNode) node.jjtGetParent());
        } 

        super.visit(node, data);

        if (node.jjtGetParent() instanceof ASTIfStatement) {
            ASTIfStatement st = (ASTIfStatement) node.jjtGetParent();
            if (!st.hasElse()) {
                dataFlow.pushOnStack(NodeType.IF_LAST_STATEMENT_WITHOUT_ELSE, dataFlow.getLast());
            } else if (st.hasElse() && !st.jjtGetChild(1).equals(node)) {
                dataFlow.pushOnStack(NodeType.ELSE_LAST_STATEMENT, dataFlow.getLast());
            } else {
                dataFlow.pushOnStack(NodeType.IF_LAST_STATEMENT, dataFlow.getLast());
            }
        } else if (node.jjtGetParent() instanceof ASTWhileStatement) {
            dataFlow.pushOnStack(NodeType.WHILE_LAST_STATEMENT, dataFlow.getLast());
        } else if (node.jjtGetParent() instanceof ASTForStatement) {
            dataFlow.pushOnStack(NodeType.FOR_END, dataFlow.getLast());
        } else if (node.jjtGetParent() instanceof ASTLabeledStatement) {
            dataFlow.pushOnStack(NodeType.LABEL_LAST_STATEMENT, dataFlow.getLast());
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
        } else {
            dataFlow.pushOnStack(NodeType.CASE_LAST_STATEMENT, dataFlow.getLast());
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
        return super.visit(node, data);
    }


    public Object visit(ASTContinueStatement node, Object data) {
        if (!(data instanceof Structure)) {
            return data;
        }
        Structure dataFlow = (Structure) data;
        dataFlow.createNewNode(node);
        dataFlow.pushOnStack(NodeType.CONTINUE_STATEMENT, dataFlow.getLast());
        return super.visit(node, data);
    }

    public Object visit(ASTReturnStatement node, Object data) {
        if (!(data instanceof Structure)) {
            return data;
        }
        Structure dataFlow = (Structure) data;
        dataFlow.createNewNode(node);
        dataFlow.pushOnStack(NodeType.RETURN_STATEMENT, dataFlow.getLast());
        return super.visit(node, data);
    }

    /*
     * The method handles the special "for" loop. It creates always an
     * expression node even if the loop looks like for(;;).
     * */
    private void addForExpressionNode(SimpleNode node, Structure dataFlow) {
        ASTForStatement parent = (ASTForStatement) node.jjtGetParent();
        boolean hasExpressionChild = false;
        boolean hasForInitNode = false;
        boolean hasForUpdateNode = false;

        for (int i = 0; i < parent.jjtGetNumChildren(); i++) {
            if (parent.jjtGetChild(i) instanceof ASTExpression)
                hasExpressionChild = true;
            else if (parent.jjtGetChild(i) instanceof ASTForUpdate)
                hasForUpdateNode = true;
            else if (parent.jjtGetChild(i) instanceof ASTForInit)
                hasForInitNode = true;
        }
        if (!hasExpressionChild) {
            if (node instanceof ASTForInit) {
                dataFlow.createNewNode(node);
                dataFlow.pushOnStack(NodeType.FOR_EXPR, dataFlow.getLast());
            } else if (node instanceof ASTForUpdate) {
                if (!hasForInitNode) {
                    dataFlow.createNewNode(node);
                    dataFlow.pushOnStack(NodeType.FOR_EXPR, dataFlow.getLast());
                }
            } else if (node instanceof ASTStatement) {
                if (!hasForInitNode && !hasForUpdateNode) {
                    dataFlow.createNewNode(node);
                    dataFlow.pushOnStack(NodeType.FOR_EXPR, dataFlow.getLast());
                }
            }
        }
    }
}
