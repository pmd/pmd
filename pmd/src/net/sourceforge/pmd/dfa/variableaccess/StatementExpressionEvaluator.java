/*
 * Created on 14.07.2004
 */
package net.sourceforge.pmd.dfa.variableaccess;

import net.sourceforge.pmd.ast.ASTArguments;
import net.sourceforge.pmd.ast.ASTAssignmentOperator;
import net.sourceforge.pmd.ast.ASTEqualityExpression;
import net.sourceforge.pmd.ast.ASTFormalParameter;
import net.sourceforge.pmd.ast.ASTName;
import net.sourceforge.pmd.ast.ASTPostfixExpression;
import net.sourceforge.pmd.ast.ASTPreDecrementExpression;
import net.sourceforge.pmd.ast.ASTPreIncrementExpression;
import net.sourceforge.pmd.ast.ASTPrimaryPrefix;
import net.sourceforge.pmd.ast.ASTRelationalExpression;
import net.sourceforge.pmd.ast.ASTReturnStatement;
import net.sourceforge.pmd.ast.ASTVariableDeclaratorId;
import net.sourceforge.pmd.ast.ASTVariableInitializer;
import net.sourceforge.pmd.ast.SimpleNode;

import java.util.LinkedList;
import java.util.List;

/**
 * @author raik
 */
public class StatementExpressionEvaluator {

    private List varAccess;

    public StatementExpressionEvaluator(List varAccess) {
        this.varAccess = varAccess;
    }

    /*
     * Creates a list of VariableAccess objects which contains the name of a 
     * variable and the type of access.
     * */
    public List computeAccess() {
        LinkedList ret = new LinkedList();
        SimpleNode o;
        
        // e.g x++;, because i can't recognize a postincrementation
        if (this.varAccess.size() == 1) {
            o = (SimpleNode) this.varAccess.get(0);
            if (o instanceof ASTName) {
                ret.addFirst(new VariableAccess(VariableAccess.DEFINITION, o.getImage()));
            }
        }

        for (int i = 1; i < varAccess.size(); i++) {

            Object current = this.varAccess.get(i);
            if (current instanceof ASTName ||
                    current instanceof ASTVariableDeclaratorId) {

                o = this.getLastAccessObject(i);
                if (o == null) {
                    continue;
                } else {
                    String image = ((SimpleNode) current).getImage();
                    if (o instanceof ASTAssignmentOperator) {
                        ret.addFirst(new VariableAccess(VariableAccess.REFERENCING, image));
                        continue;
                    } else if (o instanceof ASTArguments) {
                        ret.addFirst(new VariableAccess(VariableAccess.REFERENCING, image));
                        continue;
                    } else if (o instanceof ASTPreIncrementExpression) {
                        ret.addFirst(new VariableAccess(VariableAccess.DEFINITION, image));
                        SimpleNode tmp = this.getLastAccessObject(i - 1);
                        if (tmp != null) {
                            ret.addFirst(new VariableAccess(VariableAccess.REFERENCING, image));
                        }
                        continue;
                    } else if (o instanceof ASTPreDecrementExpression) {
                        ret.addFirst(new VariableAccess(VariableAccess.DEFINITION, image));
                        SimpleNode tmp = this.getLastAccessObject(i - 1);
                        if (tmp != null) {
                            ret.addFirst(new VariableAccess(VariableAccess.REFERENCING, image));
                        }
                        continue;
                    } else if (o instanceof ASTPostfixExpression) {
                        ret.addFirst(new VariableAccess(VariableAccess.REFERENCING, image));
                        ret.addFirst(new VariableAccess(VariableAccess.DEFINITION, image));
                        continue;
                    } else if (o instanceof ASTRelationalExpression) {
                        ret.addFirst(new VariableAccess(VariableAccess.REFERENCING, image));
                        continue;
                    } else if (o instanceof ASTEqualityExpression) {
                        ret.addFirst(new VariableAccess(VariableAccess.REFERENCING, image));
                        continue;
                    } else if (o instanceof ASTName) {
                        ret.addFirst(new VariableAccess(VariableAccess.REFERENCING, image));
                        ret.addFirst(new VariableAccess(VariableAccess.REFERENCING, o.getImage()));
                        continue;
                    } else if (o instanceof ASTReturnStatement) {
                        ret.addFirst(new VariableAccess(VariableAccess.REFERENCING, image));
                    } else if (o instanceof ASTFormalParameter) {
                        ret.addFirst(new VariableAccess(VariableAccess.DEFINITION, image));
                    } else if (o instanceof ASTPrimaryPrefix) {
                        ret.addFirst(new VariableAccess(VariableAccess.REFERENCING, image));
                    }

                }
            } else if (this.varAccess.get(i) instanceof ASTAssignmentOperator ||
                    this.varAccess.get(i) instanceof ASTVariableInitializer) {
                o = (SimpleNode) this.varAccess.get(i - 1);
                if (o instanceof ASTName || o instanceof ASTVariableDeclaratorId) {
                    ret.addFirst(new VariableAccess(VariableAccess.DEFINITION, o.getImage()));
                    continue;
                }
            } else if (this.varAccess.get(i) instanceof ASTArguments) {
                o = (SimpleNode) this.varAccess.get(i - 1);
                if (o instanceof ASTName || o instanceof ASTVariableDeclaratorId) {
                    ret.addFirst(new VariableAccess(VariableAccess.REFERENCING, o.getImage()));
                    continue;
                }
            }
        }

        return ret;
    }

    private SimpleNode getLastAccessObject(int i) {
        boolean isFirstLoop = true;
        while (i > 0) {
            i--;
            Object ret = this.varAccess.get(i);
            if (this.isAccessObject(ret)) {
                if (!isFirstLoop) {
                    if (ret instanceof ASTPostfixExpression ||
                            ret instanceof ASTPreDecrementExpression ||
                            ret instanceof ASTPreIncrementExpression) {
                        continue;
                    }
                }
                return (SimpleNode) ret;
            }
            isFirstLoop = false;
        }
        return null;
    }

    private boolean isAccessObject(Object o) {
        return o instanceof ASTArguments ||
                o instanceof ASTAssignmentOperator ||
                o instanceof ASTPostfixExpression ||
                o instanceof ASTPreDecrementExpression ||
                o instanceof ASTPreIncrementExpression ||
                o instanceof ASTVariableInitializer ||
                o instanceof ASTEqualityExpression ||
                o instanceof ASTRelationalExpression ||
                o instanceof ASTName ||
                o instanceof ASTReturnStatement ||
                o instanceof ASTFormalParameter ||
                o instanceof ASTPrimaryPrefix;
    }
}












