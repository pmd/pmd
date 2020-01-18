/**
 * BSD-style license; for more info see http://pmd.sourceforge.net/license.html
 */

package net.sourceforge.pmd.lang.plsql.dfa;

import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

import net.sourceforge.pmd.lang.DataFlowHandler;
import net.sourceforge.pmd.lang.ast.Node;
import net.sourceforge.pmd.lang.dfa.Linker;
import net.sourceforge.pmd.lang.dfa.LinkerException;
import net.sourceforge.pmd.lang.dfa.NodeType;
import net.sourceforge.pmd.lang.dfa.SequenceException;
import net.sourceforge.pmd.lang.dfa.Structure;
import net.sourceforge.pmd.lang.plsql.ast.ASTCaseStatement;
import net.sourceforge.pmd.lang.plsql.ast.ASTCaseWhenClause;
import net.sourceforge.pmd.lang.plsql.ast.ASTCloseStatement;
import net.sourceforge.pmd.lang.plsql.ast.ASTContinueStatement;
import net.sourceforge.pmd.lang.plsql.ast.ASTElseClause;
import net.sourceforge.pmd.lang.plsql.ast.ASTElsifClause;
import net.sourceforge.pmd.lang.plsql.ast.ASTEmbeddedSqlStatement;
import net.sourceforge.pmd.lang.plsql.ast.ASTExitStatement;
import net.sourceforge.pmd.lang.plsql.ast.ASTExpression;
import net.sourceforge.pmd.lang.plsql.ast.ASTFetchStatement;
import net.sourceforge.pmd.lang.plsql.ast.ASTForStatement;
import net.sourceforge.pmd.lang.plsql.ast.ASTGotoStatement;
import net.sourceforge.pmd.lang.plsql.ast.ASTIfStatement;
import net.sourceforge.pmd.lang.plsql.ast.ASTLabelledStatement;
import net.sourceforge.pmd.lang.plsql.ast.ASTLoopStatement;
import net.sourceforge.pmd.lang.plsql.ast.ASTMethodDeclaration;
import net.sourceforge.pmd.lang.plsql.ast.ASTOpenStatement;
import net.sourceforge.pmd.lang.plsql.ast.ASTPipelineStatement;
import net.sourceforge.pmd.lang.plsql.ast.ASTProgramUnit;
import net.sourceforge.pmd.lang.plsql.ast.ASTRaiseStatement;
import net.sourceforge.pmd.lang.plsql.ast.ASTReturnStatement;
import net.sourceforge.pmd.lang.plsql.ast.ASTSqlStatement;
import net.sourceforge.pmd.lang.plsql.ast.ASTStatement;
import net.sourceforge.pmd.lang.plsql.ast.ASTTriggerTimingPointSection;
import net.sourceforge.pmd.lang.plsql.ast.ASTTriggerUnit;
import net.sourceforge.pmd.lang.plsql.ast.ASTTypeMethod;
import net.sourceforge.pmd.lang.plsql.ast.ASTUnlabelledStatement;
import net.sourceforge.pmd.lang.plsql.ast.ASTVariableOrConstantDeclarator;
import net.sourceforge.pmd.lang.plsql.ast.ASTWhileStatement;
import net.sourceforge.pmd.lang.plsql.ast.PLSQLNode;
import net.sourceforge.pmd.lang.plsql.ast.PLSQLParserVisitorAdapter;

/**
 * Sublayer of DataFlowFacade. Finds all data flow nodes and stores the
 * type information (@see StackObject). At last it uses this information
 * to link the nodes.
 *
 * @author raik
 */
public class StatementAndBraceFinder extends PLSQLParserVisitorAdapter {
    private static final Logger LOGGER = Logger.getLogger(StatementAndBraceFinder.class.getName());

    private final DataFlowHandler dataFlowHandler;
    private Structure dataFlow;

    public StatementAndBraceFinder(DataFlowHandler dataFlowHandler) {
        this.dataFlowHandler = dataFlowHandler;
    }

    public void buildDataFlowFor(PLSQLNode node) {
        LOGGER.entering(this.getClass().getCanonicalName(), "buildDataFlowFor");
        if (LOGGER.isLoggable(Level.FINEST)) {
            LOGGER.finest("buildDataFlowFor: node class " + node.getClass().getCanonicalName() + " @ line "
                    + node.getBeginLine() + ", column " + node.getBeginColumn() + " --- "
                    + new Throwable().getStackTrace());
        }
        if (!(node instanceof ASTMethodDeclaration) && !(node instanceof ASTProgramUnit)
                && !(node instanceof ASTTypeMethod) && !(node instanceof ASTTriggerUnit)
                && !(node instanceof ASTTriggerTimingPointSection)) {
            throw new RuntimeException("Can't build a data flow for anything other than a Method or a Trigger");
        }

        this.dataFlow = new Structure(dataFlowHandler);
        this.dataFlow.createStartNode(node.getBeginLine());
        this.dataFlow.createNewNode(node);

        node.jjtAccept(this, dataFlow);

        this.dataFlow.createEndNode(node.getEndLine());

        if (LOGGER.isLoggable(Level.FINE)) {
            LOGGER.fine("DataFlow is " + this.dataFlow.dump());
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
        LOGGER.exiting(this.getClass().getCanonicalName(), "buildDataFlowFor");
    }

    @Override
    public Object visit(ASTSqlStatement node, Object data) {
        if (!(data instanceof Structure)) {
            if (LOGGER.isLoggable(Level.FINEST)) {
                LOGGER.finest("immediate return ASTSqlStatement: line " + node.getBeginLine() + ", column "
                        + node.getBeginColumn());
            }
            return data;
        }
        Structure dataFlow = (Structure) data;
        dataFlow.createNewNode(node);
        if (LOGGER.isLoggable(Level.FINEST)) {
            LOGGER.finest(
                    "createNewNode ASTSqlStatement: line " + node.getBeginLine() + ", column " + node.getBeginColumn());
        }
        return super.visit(node, data);
    }

    @Override
    public Object visit(ASTEmbeddedSqlStatement node, Object data) {
        if (!(data instanceof Structure)) {
            if (LOGGER.isLoggable(Level.FINEST)) {
                LOGGER.finest("immediate return ASTEmbeddedSqlStatement: line " + node.getBeginLine() + ", column "
                        + node.getBeginColumn());
            }
            return data;
        }
        Structure dataFlow = (Structure) data;
        dataFlow.createNewNode(node);
        if (LOGGER.isLoggable(Level.FINEST)) {
            LOGGER.finest("createNewNode ASTEmbeddedSqlStatement: line " + node.getBeginLine() + ", column "
                    + node.getBeginColumn());
        }
        return super.visit(node, data);
    }

    @Override
    public Object visit(ASTCloseStatement node, Object data) {
        if (!(data instanceof Structure)) {
            return data;
        }
        Structure dataFlow = (Structure) data;
        dataFlow.createNewNode(node);
        if (LOGGER.isLoggable(Level.FINEST)) {
            LOGGER.finest("createNewNode ASTCloseStatement: line " + node.getBeginLine() + ", column "
                    + node.getBeginColumn());
        }
        return super.visit(node, data);
    }

    @Override
    public Object visit(ASTOpenStatement node, Object data) {
        if (!(data instanceof Structure)) {
            return data;
        }
        Structure dataFlow = (Structure) data;
        dataFlow.createNewNode(node);
        if (LOGGER.isLoggable(Level.FINEST)) {
            LOGGER.finest("createNewNode ASTOpenStatement: line " + node.getBeginLine() + ", column "
                    + node.getBeginColumn());
        }
        return super.visit(node, data);
    }

    @Override
    public Object visit(ASTFetchStatement node, Object data) {
        if (!(data instanceof Structure)) {
            return data;
        }
        Structure dataFlow = (Structure) data;
        dataFlow.createNewNode(node);
        if (LOGGER.isLoggable(Level.FINEST)) {
            LOGGER.finest("createNewNode ASTFetchStatement: line " + node.getBeginLine() + ", column "
                    + node.getBeginColumn());
        }
        return super.visit(node, data);
    }

    @Override
    public Object visit(ASTPipelineStatement node, Object data) {
        if (!(data instanceof Structure)) {
            return data;
        }
        Structure dataFlow = (Structure) data;
        dataFlow.createNewNode(node);
        if (LOGGER.isLoggable(Level.FINEST)) {
            LOGGER.finest("createNewNode ASTPipelineStatement: line " + node.getBeginLine() + ", column "
                    + node.getBeginColumn());
        }
        return super.visit(node, data);
    }

    /* */

    @Override
    public Object visit(ASTVariableOrConstantDeclarator node, Object data) {
        if (!(data instanceof Structure)) {
            return data;
        }
        Structure dataFlow = (Structure) data;
        dataFlow.createNewNode(node);
        if (LOGGER.isLoggable(Level.FINEST)) {
            LOGGER.finest("createNewNode ASTVariableOrConstantDeclarator: line " + node.getBeginLine() + ", column "
                    + node.getBeginColumn());
        }
        return super.visit(node, data);
    }

    @Override
    public Object visit(ASTExpression node, Object data) {
        if (LOGGER.isLoggable(Level.FINEST)) {
            LOGGER.finest("Entry ASTExpression: line " + node.getBeginLine() + ", column " + node.getBeginColumn());
        }
        if (!(data instanceof Structure)) {
            if (LOGGER.isLoggable(Level.FINEST)) {
                LOGGER.finest("immediate return ASTExpression: line " + node.getBeginLine() + ", column "
                        + node.getBeginColumn());
            }
            return data;
        }
        Structure dataFlow = (Structure) data;

        // The equivalent of an ASTExpression is an Expression whose parent is
        // an UnLabelledStatement
        if (node.getParent() instanceof ASTUnlabelledStatement) {
            if (LOGGER.isLoggable(Level.FINEST)) {
                LOGGER.finest("createNewNode ASTSUnlabelledStatement: line " + node.getBeginLine() + ", column "
                        + node.getBeginColumn());
            }
            dataFlow.createNewNode(node);
        } else if (node.getParent() instanceof ASTIfStatement) {
            // TODO what about throw stmts?
            dataFlow.createNewNode(node); // START IF
            dataFlow.pushOnStack(NodeType.IF_EXPR, dataFlow.getLast());
            if (LOGGER.isLoggable(Level.FINEST)) {
                LOGGER.finest("pushOnStack parent IF_EXPR: line " + node.getBeginLine() + ", column "
                        + node.getBeginColumn());
            }
        } else if (node.getParent() instanceof ASTElsifClause) {
            if (LOGGER.isLoggable(Level.FINEST)) {
                LOGGER.finest(
                        "parent (Elsif) IF_EXPR at  " + node.getBeginLine() + ", column " + node.getBeginColumn());
            }
            dataFlow.createNewNode(node); // START IF
            dataFlow.pushOnStack(NodeType.IF_EXPR, dataFlow.getLast());
            if (LOGGER.isLoggable(Level.FINEST)) {
                LOGGER.finest("pushOnStack parent (Elsif) IF_EXPR: line " + node.getBeginLine() + ", column "
                        + node.getBeginColumn());
            }
        } else if (node.getParent() instanceof ASTWhileStatement) {
            dataFlow.createNewNode(node); // START WHILE
            dataFlow.pushOnStack(NodeType.WHILE_EXPR, dataFlow.getLast());
            if (LOGGER.isLoggable(Level.FINEST)) {
                LOGGER.finest("pushOnStack parent WHILE_EXPR: line " + node.getBeginLine() + ", column "
                        + node.getBeginColumn());
            }
        } else if (node.getParent() instanceof ASTCaseStatement) {
            dataFlow.createNewNode(node); // START SWITCH
            dataFlow.pushOnStack(NodeType.SWITCH_START, dataFlow.getLast());
            if (LOGGER.isLoggable(Level.FINEST)) {
                LOGGER.finest("pushOnStack parent SWITCH_START: line " + node.getBeginLine() + ", column "
                        + node.getBeginColumn());
            }
        } else if (node.getParent() instanceof ASTForStatement) {
            /*
             * A PL/SQL loop control: [<REVERSE>] Expression()[".."Expression()]
             */
            if (node.equals(node.getParent().getFirstChildOfType(ASTExpression.class))) {
                dataFlow.createNewNode(node); // FOR EXPR
                dataFlow.pushOnStack(NodeType.FOR_EXPR, dataFlow.getLast());
                if (LOGGER.isLoggable(Level.FINEST)) {
                    LOGGER.finest("pushOnStack parent FOR_EXPR: line " + node.getBeginLine() + ", column "
                            + node.getBeginColumn());
                }
            }
            if (LOGGER.isLoggable(Level.FINEST)) {
                LOGGER.finest(
                        "parent (ASTForStatement): line " + node.getBeginLine() + ", column " + node.getBeginColumn());
            }
        } else if (node.getParent() instanceof ASTLoopStatement) {
            dataFlow.createNewNode(node); // DO EXPR
            dataFlow.pushOnStack(NodeType.DO_EXPR, dataFlow.getLast());
            if (LOGGER.isLoggable(Level.FINEST)) {
                LOGGER.finest("pushOnStack parent DO_EXPR: line " + node.getBeginLine() + ", column "
                        + node.getBeginColumn());
            }
        }

        return super.visit(node, data);
    }

    @Override
    public Object visit(ASTLabelledStatement node, Object data) {
        dataFlow.createNewNode(node);
        dataFlow.pushOnStack(NodeType.LABEL_STATEMENT, dataFlow.getLast());
        if (LOGGER.isLoggable(Level.FINEST)) {
            LOGGER.finest(
                    "pushOnStack LABEL_STATEMENT: line " + node.getBeginLine() + ", column " + node.getBeginColumn());
        }
        return super.visit(node, data);
    }

    /**
     * PL/SQL does not have a do/while statement or repeat/until statement: the
     * equivalent is a LOOP statement. A PL/SQL LOOP statement is exited using
     * an explicit EXIT ( == break;) statement It does not have a test
     * expression, so the Java control processing (on the expression) does not
     * fire. The best way to cope it to push a DO_EXPR after the loop.
     */
    @Override
    public Object visit(ASTLoopStatement node, Object data) {
        if (LOGGER.isLoggable(Level.FINEST)) {
            LOGGER.finest("entry ASTLoopStatement: line " + node.getBeginLine() + ", column " + node.getBeginColumn());
        }
        if (!(data instanceof Structure)) {
            if (LOGGER.isLoggable(Level.FINEST)) {
                LOGGER.finest("immediate return ASTLoopStatement: line " + node.getBeginLine() + ", column "
                        + node.getBeginColumn());
            }
            return data;
        }
        Structure dataFlow = (Structure) data;

        // process the contents on the LOOP statement
        super.visit(node, data);

        dataFlow.createNewNode(node);
        dataFlow.pushOnStack(NodeType.DO_EXPR, dataFlow.getLast());
        if (LOGGER.isLoggable(Level.FINEST)) {
            LOGGER.finest("pushOnStack (ASTLoopStatement) DO_EXPR: line " + node.getBeginLine() + ", column "
                    + node.getBeginColumn());
        }
        return data;
    }

    /**
     * A PL/SQL WHILE statement includes the LOOP statement and all Expressions
     * within it: it does not have a single test expression, so the Java control
     * processing (on the Expression) fires for each Expression in the LOOP. The
     * best way to cope it to push a WHILE_LAST_STATEMENT after the
     * WhileStatement has been processed.
     */
    @Override
    public Object visit(ASTWhileStatement node, Object data) {
        if (LOGGER.isLoggable(Level.FINEST)) {
            LOGGER.finest("entry ASTWhileStatement: line " + node.getBeginLine() + ", column " + node.getBeginColumn());
        }
        if (!(data instanceof Structure)) {
            if (LOGGER.isLoggable(Level.FINEST)) {
                LOGGER.finest("immediate return ASTWhileStatement: line " + node.getBeginLine() + ", column "
                        + node.getBeginColumn());
            }
            return data;
        }

        // process the contents on the WHILE statement
        super.visit(node, data);

        return data;
    }

    // ----------------------------------------------------------------------------
    // BRANCH OUT

    @Override
    public Object visit(ASTStatement node, Object data) {
        if (LOGGER.isLoggable(Level.FINEST)) {
            LOGGER.finest("entry ASTStatement: line " + node.getBeginLine() + ", column " + node.getBeginColumn()
                    + " -> " + node.getClass().getCanonicalName());
        }
        if (!(data instanceof Structure)) {
            if (LOGGER.isLoggable(Level.FINEST)) {
                LOGGER.finest("immediate return ASTStatement: line " + node.getBeginLine() + ", column "
                        + node.getBeginColumn());
            }
            return data;
        }
        Structure dataFlow = (Structure) data;

        if (node.getParent() instanceof ASTForStatement) {
            ASTForStatement st = (ASTForStatement) node.getParent();
            if (node.equals(st.getFirstChildOfType(ASTStatement.class))) {
                addForExpressionNode(node, dataFlow);
                dataFlow.pushOnStack(NodeType.FOR_BEFORE_FIRST_STATEMENT, dataFlow.getLast());
                if (LOGGER.isLoggable(Level.FINEST)) {
                    LOGGER.finest("pushOnStack FOR_BEFORE_FIRST_STATEMENT: line " + node.getBeginLine() + ", column "
                            + node.getBeginColumn());
                }
            }
        } else if (node.getParent() instanceof ASTLoopStatement) {
            ASTLoopStatement st = (ASTLoopStatement) node.getParent();
            if (node.equals(st.getFirstChildOfType(ASTStatement.class))) {
                dataFlow.pushOnStack(NodeType.DO_BEFORE_FIRST_STATEMENT, dataFlow.getLast());
                dataFlow.createNewNode(node.getParent());
                if (LOGGER.isLoggable(Level.FINEST)) {
                    LOGGER.finest("pushOnStack DO_BEFORE_FIRST_STATEMENT: line " + node.getBeginLine() + ", column "
                            + node.getBeginColumn());
                }
            }
        }

        super.visit(node, data);

        if (node.getParent() instanceof ASTElseClause) {
            List<ASTStatement> allStatements = node.getParent().findChildrenOfType(ASTStatement.class);
            if (LOGGER.isLoggable(Level.FINEST)) {
                LOGGER.finest("ElseClause has " + allStatements.size() + " Statements ");
            }

            /*
             * //Restrict to the last Statement of the Else Clause if (node ==
             * allStatements.get(allStatements.size()-1) ) { if
             * (node.getParent().getParent() instanceof ASTCaseStatement)
             * { dataFlow.pushOnStack(NodeType.SWITCH_LAST_DEFAULT_STATEMENT,
             * dataFlow.getLast()); LOGGER.finest(
             * "pushOnStack (Else-Below Case) SWITCH_LAST_DEFAULT_STATEMENT: line "
             * + node.getBeginLine() +", column " + node.getBeginColumn()); }
             * /*SRT else // if (node == node.getParent() instanceof
             * ASTElseClause) { {
             * dataFlow.pushOnStack(NodeType.ELSE_LAST_STATEMENT,
             * dataFlow.getLast()); LOGGER.finest(
             * "pushOnStack (Else-Below If) ELSE_LAST_STATEMENT: line " +
             * node.getBeginLine() +", column " + node.getBeginColumn()); }
             */
            // }
        } else if (node.getParent() instanceof ASTWhileStatement) {
            ASTWhileStatement statement = (ASTWhileStatement) node.getParent();
            List<ASTStatement> children = statement.findChildrenOfType(ASTStatement.class);
            if (LOGGER.isLoggable(Level.FINEST)) {
                LOGGER.finest("(LastChildren): size " + children.size());
            }
            ASTStatement lastChild = children.get(children.size() - 1);

            // Push on stack if this Node is the LAST Statement associated with
            // the FOR Statment
            if (node.equals(lastChild)) {
                dataFlow.pushOnStack(NodeType.WHILE_LAST_STATEMENT, dataFlow.getLast());
                if (LOGGER.isLoggable(Level.FINEST)) {
                    LOGGER.finest("pushOnStack WHILE_LAST_STATEMENT: line " + node.getBeginLine() + ", column "
                            + node.getBeginColumn());
                }
            }
        } else if (node.getParent() instanceof ASTForStatement) {
            ASTForStatement statement = (ASTForStatement) node.getParent();
            List<ASTStatement> children = statement.findChildrenOfType(ASTStatement.class);
            if (LOGGER.isLoggable(Level.FINEST)) {
                LOGGER.finest("(LastChildren): size " + children.size());
            }
            ASTStatement lastChild = children.get(children.size() - 1);

            // Push on stack if this Node is the LAST Statement associated with
            // the FOR Statment
            if (node.equals(lastChild)) {
                dataFlow.pushOnStack(NodeType.FOR_END, dataFlow.getLast());
                if (LOGGER.isLoggable(Level.FINEST)) {
                    LOGGER.finest("pushOnStack (LastChildStatemnt) FOR_END: line " + node.getBeginLine() + ", column "
                            + node.getBeginColumn());
                }
            }
        } else if (node.getParent() instanceof ASTLabelledStatement) {
            dataFlow.pushOnStack(NodeType.LABEL_LAST_STATEMENT, dataFlow.getLast());
            if (LOGGER.isLoggable(Level.FINEST)) {
                LOGGER.finest("pushOnStack LABEL_LAST_STATEMENT: line " + node.getBeginLine() + ", column "
                        + node.getBeginColumn());
            }
        }
        if (LOGGER.isLoggable(Level.FINEST)) {
            LOGGER.finest("exit ASTStatement: line " + node.getBeginLine() + ", column " + node.getBeginColumn()
                    + " -> " + node.getClass().getCanonicalName() + " ->-> "
                    + node.getParent().getClass().getCanonicalName());
        }
        return data;
    }

    @Override
    public Object visit(ASTUnlabelledStatement node, Object data) {
        if (!(data instanceof Structure)) {
            return data;
        }
        Structure dataFlow = (Structure) data;
        super.visit(node, data);
        if (node.getParent() instanceof ASTLabelledStatement) {
            dataFlow.pushOnStack(NodeType.LABEL_LAST_STATEMENT, dataFlow.getLast());
            if (LOGGER.isLoggable(Level.FINEST)) {
                LOGGER.finest("pushOnStack (ASTUnlabelledStatement) LABEL_LAST_STATEMENT: line " + node.getBeginLine()
                        + ", column " + node.getBeginColumn());
            }
        }
        return data;
    }

    @Override
    public Object visit(ASTCaseStatement node, Object data) {
        if (!(data instanceof Structure)) {
            return data;
        }
        Structure dataFlow = (Structure) data;

        /*
         * A PL/SQL CASE statement may be either:- SIMPLE, e.g. CASE
         * case_operand WHEN when_operand THEN statement ; ELSE statement ; END
         * CASE SEARCHED, e.g. CASE WHEN boolean_expression THEN statement ;
         * ELSE statement ; END CASE
         *
         * A SIMPLE CASE statement may be treated as a normal Java SWITCH
         * statement ( see visit(ASTExpression)), but a SEARCHED CASE statement
         * must have an atificial start node
         */
        // CASE is "searched case statement"
        if (null == node.getFirstChildOfType(ASTExpression.class)) {
            dataFlow.createNewNode(node);
            dataFlow.pushOnStack(NodeType.SWITCH_START, dataFlow.getLast());
            if (LOGGER.isLoggable(Level.FINEST)) {
                LOGGER.finest(
                        "pushOnStack SWITCH_START: line " + node.getBeginLine() + ", column " + node.getBeginColumn());
            }
        }

        super.visit(node, data);

        dataFlow.pushOnStack(NodeType.SWITCH_END, dataFlow.getLast());
        if (LOGGER.isLoggable(Level.FINEST)) {
            LOGGER.finest("pushOnStack SWITCH_END: line " + node.getBeginLine() + ", column " + node.getBeginColumn());
        }
        return data;
    }

    @Override
    public Object visit(ASTCaseWhenClause node, Object data) {
        if (!(data instanceof Structure)) {
            return data;
        }
        Structure dataFlow = (Structure) data;

        // Java ASTSwitchLabel
        // SRT dataFlow.createNewNode(node);
        dataFlow.pushOnStack(NodeType.CASE_LAST_STATEMENT, dataFlow.getLast());
        if (LOGGER.isLoggable(Level.FINEST)) {
            LOGGER.finest("pushOnStack CASE_LAST_STATEMENT: line " + node.getBeginLine() + ", column "
                    + node.getBeginColumn());
        }

        super.visit(node, data);

        // dataFlow.createNewNode(node);
        dataFlow.pushOnStack(NodeType.BREAK_STATEMENT, dataFlow.getLast());
        if (LOGGER.isLoggable(Level.FINEST)) {
            LOGGER.finest("pushOnStack (ASTCaseWhenClause) BREAK_STATEMENT: line " + node.getBeginLine() + ", column "
                    + node.getBeginColumn());
        }
        return data;
    }

    @Override
    public Object visit(ASTIfStatement node, Object data) {
        if (!(data instanceof Structure)) {
            return data;
        }
        Structure dataFlow = (Structure) data;
        LOGGER.finest("ElsifClause) super.visit line");
        super.visit(node, data);

        /*
         * PLSQL AST now has explicit ELSIF and ELSE clauses All of the
         * ELSE_END_STATEMENTS in an IF clause should point to the outer last
         * clause because we have to convert a single PL/SQL IF/ELSIF/ELSE
         * satement into the equivalent set of nested Java if/else {if/else
         * {if/else}} statements
         */
        List<ASTElsifClause> elsifs = node.findChildrenOfType(ASTElsifClause.class);
        ASTElseClause elseClause = node.getFirstChildOfType(ASTElseClause.class);
        // The IF statements have no ELSE or ELSIF statements and this is the last
        // statement
        if (null == elseClause && elsifs.isEmpty()) {
            dataFlow.pushOnStack(NodeType.IF_LAST_STATEMENT_WITHOUT_ELSE, dataFlow.getLast());
            if (LOGGER.isLoggable(Level.FINEST)) {
                LOGGER.finest("pushOnStack (ASTIfClause - no ELSIFs) IF_LAST_STATEMENT_WITHOUT_ELSE: line "
                        + node.getBeginLine() + ", column " + node.getBeginColumn());
            }
        } else {
            if (!elsifs.isEmpty()) {

                ASTElsifClause lastElsifClause = elsifs.get(elsifs.size() - 1);
                for (ASTElsifClause elsifClause : elsifs) {

                    /*
                     * If last ELSIF clause is not followed by a ELSE clause
                     * then the last ELSIF is equivalent to an if statement
                     * without an else Otherwise, it is equivalent to an if/else
                     * statement
                     */
                    if (lastElsifClause.equals(elsifClause) && elseClause == null) {
                        dataFlow.pushOnStack(NodeType.IF_LAST_STATEMENT_WITHOUT_ELSE, dataFlow.getLast());
                        if (LOGGER.isLoggable(Level.FINEST)) {
                            LOGGER.finest(
                                    "pushOnStack (ASTIfClause - with ELSIFs) IF_LAST_STATEMENT_WITHOUT_ELSE: line "
                                            + node.getBeginLine() + ", column " + node.getBeginColumn());
                        }
                    }

                    {
                        dataFlow.pushOnStack(NodeType.ELSE_LAST_STATEMENT, dataFlow.getLast());
                        if (LOGGER.isLoggable(Level.FINEST)) {
                            LOGGER.finest("pushOnStack (ASTIfClause - with ELSIFs) ELSE_LAST_STATEMENT : line "
                                    + node.getBeginLine() + ", column " + node.getBeginColumn());
                        }
                    }
                }
            }

            if (null != elseClause) {
                // Output one terminating else
                dataFlow.pushOnStack(NodeType.ELSE_LAST_STATEMENT, dataFlow.getLast());
                if (LOGGER.isLoggable(Level.FINEST)) {
                    LOGGER.finest("pushOnStack (ASTIfClause - with ELSE) ELSE_LAST_STATEMENT : line "
                            + node.getBeginLine() + ", column " + node.getBeginColumn());
                }
            }
        }
        return data;
    }

    @Override
    public Object visit(ASTElseClause node, Object data) {
        if (!(data instanceof Structure)) {
            return data;
        }
        Structure dataFlow = (Structure) data;

        if (node.getParent() instanceof ASTIfStatement) {
            dataFlow.pushOnStack(NodeType.IF_LAST_STATEMENT, dataFlow.getLast());
            if (LOGGER.isLoggable(Level.FINEST)) {
                LOGGER.finest("pushOnStack (Visit ASTElseClause) IF_LAST_STATEMENT: line " + node.getBeginLine()
                        + ", column " + node.getBeginColumn());
                LOGGER.finest("ElseClause) super.visit line");
            }
        } else {
            // SRT dataFlow.createNewNode(node);
            dataFlow.pushOnStack(NodeType.SWITCH_LAST_DEFAULT_STATEMENT, dataFlow.getLast());
            if (LOGGER.isLoggable(Level.FINEST)) {
                LOGGER.finest("pushOnStack SWITCH_LAST_DEFAULT_STATEMENT: line " + node.getBeginLine() + ", column "
                        + node.getBeginColumn());
            }
        }

        super.visit(node, data);

        return data;
    }

    @Override
    public Object visit(ASTElsifClause node, Object data) {
        if (!(data instanceof Structure)) {
            return data;
        }
        Structure dataFlow = (Structure) data;
        dataFlow.pushOnStack(NodeType.IF_LAST_STATEMENT, dataFlow.getLast());
        if (LOGGER.isLoggable(Level.FINEST)) {
            LOGGER.finest("pushOnStack (Visit ASTElsifClause) IF_LAST_STATEMENT: line " + node.getBeginLine()
                    + ", column " + node.getBeginColumn());
            LOGGER.finest("ElsifClause) super.visit line");
        }
        super.visit(node, data);

        return data;
    }

    /**
     * Treat a PLSQL CONTINUE like a Java "continue"
     *
     * @param node
     * @param data
     * @return
     */
    @Override
    public Object visit(ASTContinueStatement node, Object data) {
        if (!(data instanceof Structure)) {
            return data;
        }
        Structure dataFlow = (Structure) data;
        dataFlow.createNewNode(node);
        dataFlow.pushOnStack(NodeType.CONTINUE_STATEMENT, dataFlow.getLast());
        if (LOGGER.isLoggable(Level.FINEST)) {
            LOGGER.finest("pushOnStack (ASTContinueStatement) CONTINUE_STATEMENT: line " + node.getBeginLine()
                    + ", column " + node.getBeginColumn());
        }
        return super.visit(node, data);
    }

    /**
     * Treat a PLSQL EXIT like a Java "break"
     *
     * @param node
     * @param data
     * @return
     */
    @Override
    public Object visit(ASTExitStatement node, Object data) {
        if (!(data instanceof Structure)) {
            return data;
        }
        Structure dataFlow = (Structure) data;
        dataFlow.createNewNode(node);
        dataFlow.pushOnStack(NodeType.BREAK_STATEMENT, dataFlow.getLast());
        if (LOGGER.isLoggable(Level.FINEST)) {
            LOGGER.finest("pushOnStack (ASTExitStatement) BREAK_STATEMENT: line " + node.getBeginLine() + ", column "
                    + node.getBeginColumn());
        }
        return super.visit(node, data);
    }

    /**
     * Treat a PLSQL GOTO like a Java "continue"
     *
     * @param node
     * @param data
     * @return
     */
    @Override
    public Object visit(ASTGotoStatement node, Object data) {
        if (!(data instanceof Structure)) {
            return data;
        }
        Structure dataFlow = (Structure) data;
        dataFlow.createNewNode(node);
        dataFlow.pushOnStack(NodeType.CONTINUE_STATEMENT, dataFlow.getLast());
        if (LOGGER.isLoggable(Level.FINEST)) {
            LOGGER.finest("pushOnStack (ASTGotoStatement) CONTINUE_STATEMENT (GOTO): line " + node.getBeginLine()
                    + ", column " + node.getBeginColumn());
        }
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
        if (LOGGER.isLoggable(Level.FINEST)) {
            LOGGER.finest(
                    "pushOnStack RETURN_STATEMENT: line " + node.getBeginLine() + ", column " + node.getBeginColumn());
        }
        return super.visit(node, data);
    }

    @Override
    public Object visit(ASTRaiseStatement node, Object data) {
        if (!(data instanceof Structure)) {
            return data;
        }
        Structure dataFlow = (Structure) data;
        dataFlow.createNewNode(node);
        dataFlow.pushOnStack(NodeType.THROW_STATEMENT, dataFlow.getLast());
        if (LOGGER.isLoggable(Level.FINEST)) {
            LOGGER.finest("pushOnStack THROW: line " + node.getBeginLine() + ", column " + node.getBeginColumn());
        }
        return super.visit(node, data);
    }

    /*
     * The method handles the special "for" loop. It creates always an
     * expression node even if the loop looks like for(;;).
     */
    @SuppressWarnings("PMD.UnusedFormalParameter") // TODO: dfa implementation in plsql is incomplete
    private void addForExpressionNode(Node node, Structure dataFlow) {
        ASTForStatement parent = (ASTForStatement) node.getParent();
        boolean hasExpressionChild = false;

        for (int i = 0; i < parent.getNumChildren(); i++) {
            if (parent.getChild(i) instanceof ASTExpression) {
                hasExpressionChild = true;
            }

        }
        if (!hasExpressionChild) {
            if (node instanceof ASTStatement) {
                /*
                 * if (!hasForInitNode && !hasForUpdateNode) {
                 * dataFlow.createNewNode(node);
                 * dataFlow.pushOnStack(NodeType.FOR_EXPR, dataFlow.getLast());
                 * }
                 */
            }
        }
    }
}
