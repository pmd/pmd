/*
 * Created on 14.07.2004
 */
package net.sourceforge.pmd.dfa.variableaccess;

import net.sourceforge.pmd.ast.ASTAllocationExpression;
import net.sourceforge.pmd.ast.ASTArguments;
import net.sourceforge.pmd.ast.ASTAssignmentOperator;
import net.sourceforge.pmd.ast.ASTClassBodyDeclaration;
import net.sourceforge.pmd.ast.ASTConstructorDeclaration;
import net.sourceforge.pmd.ast.ASTEqualityExpression;
import net.sourceforge.pmd.ast.ASTFormalParameter;
import net.sourceforge.pmd.ast.ASTMethodDeclaration;
import net.sourceforge.pmd.ast.ASTName;
import net.sourceforge.pmd.ast.ASTPostfixExpression;
import net.sourceforge.pmd.ast.ASTPreDecrementExpression;
import net.sourceforge.pmd.ast.ASTPreIncrementExpression;
import net.sourceforge.pmd.ast.ASTPrimaryPrefix;
import net.sourceforge.pmd.ast.ASTRelationalExpression;
import net.sourceforge.pmd.ast.ASTReturnStatement;
import net.sourceforge.pmd.ast.ASTStatementExpression;
import net.sourceforge.pmd.ast.ASTType;
import net.sourceforge.pmd.ast.ASTVariableDeclarator;
import net.sourceforge.pmd.ast.ASTVariableDeclaratorId;
import net.sourceforge.pmd.ast.ASTVariableInitializer;
import net.sourceforge.pmd.ast.JavaParserVisitorAdapter;
import net.sourceforge.pmd.ast.Node;
import net.sourceforge.pmd.ast.SimpleNode;
import net.sourceforge.pmd.dfa.IDataFlowNode;

import java.util.LinkedList;
import java.util.List;
import java.util.Vector;
import java.util.ArrayList;

/**
 * @author raik
 *         <p/>
 *         Searches for special nodes and computes based on the sequence, the type of
 *         access of a variable.
 */
public class VariableAccessVisitor extends JavaParserVisitorAdapter {

    private List undefList = new LinkedList();

    public void compute(ASTMethodDeclaration node) {
        if (node.jjtGetParent() instanceof ASTClassBodyDeclaration) {
            this.compute(node, "ASTMethodDeclaration");
        }
    }

    public void compute(ASTConstructorDeclaration node) {
        this.compute(node, "ASTConstructorDeclaration");
    }

    private void compute(SimpleNode node, String name) {

        node.jjtAccept(this, null);

        IDataFlowNode inode = node.getDataFlowNode();

        IDataFlowNode firstINode =
                (IDataFlowNode) inode.getFlow().get(0);

        IDataFlowNode lastINode =
                (IDataFlowNode) inode.getFlow().get(inode.getFlow().size() - 1);

        firstINode.setVariableAccess(this.undefList);
        lastINode.setVariableAccess(this.undefList);
    }
	
	
//  ----------------------------------------------------------------------------
//	STACK OBJECTS - nodes which represent a whole expression or statement
//	like: x = y + 3;
    
    // ALSO ACCESS OBJECT
    public Object visit(ASTEqualityExpression node, Object data) {
        List varAccess = new ArrayList();
        varAccess.add(node);
        super.visit(node, varAccess);
        data = this.computeVariableAccess(node, varAccess);
        return data;
    }

    // ALSO ACCESS OBJECT
    public Object visit(ASTRelationalExpression node, Object data) {
        List varAccess = new ArrayList();
        varAccess.add(node);
        super.visit(node, varAccess);
        data = this.computeVariableAccess(node, varAccess);
        return data;
    }

    // ALSO ACCESS OBJECT
    public Object visit(ASTReturnStatement node, Object data) {
        List varAccess = new ArrayList();
        varAccess.add(node);
        super.visit(node, varAccess);
        data = this.computeVariableAccess(node, varAccess);
        return data;
    }

    // ALSO ACCESS OBJECT
    public Object visit(ASTFormalParameter node, Object data) {
        List varAccess = new ArrayList();
        varAccess.add(node);
        super.visit(node, varAccess);
        data = this.computeVariableAccess(node, varAccess);
        return data;
    }

    // ALSO ACCESS OBJECT
    public Object visit(ASTPrimaryPrefix node, Object data) {
        List varAccess = new ArrayList();
        varAccess.add(node);
        super.visit(node, varAccess);
        data = this.computeVariableAccess(node, varAccess);
        return data;
    }

    public Object visit(ASTStatementExpression node, Object data) {
        List varAccess = new ArrayList();
        super.visit(node, varAccess);
        data = this.computeVariableAccess(node, varAccess);
        return data;
    }

    public Object visit(ASTVariableDeclarator node, Object data) {
        List varAccess = new ArrayList();
        super.visit(node, varAccess);
        data = this.computeVariableAccess(node, varAccess);
        return data;
    }

    private Object computeVariableAccess(SimpleNode node, List data) {
        StatementExpressionEvaluator see = new StatementExpressionEvaluator(data);
        IDataFlowNode dfn = node.getDataFlowNode();

        if (dfn == null) {
            System.out.println("VariableAccessVisitor - " + node.getClass().getName() + " - IDataFlowNode == null");
            return data; //TODO redefinition
        }

        dfn.setVariableAccess(see.computeAccess()); //TODO throw exception

        LinkedList ret = new LinkedList();
        for (int i = 0; i < dfn.getVariableAccess().size(); i++) {
            VariableAccess va = (VariableAccess) dfn.getVariableAccess().get(i);
            ret.add(new VariableAccess(VariableAccess.UNDEFINITION, va.getVariableName()));
        }
        //System.out.println("l: "+ret);
        
        
        // no duplicate values TODO refactoring
        for (int i = 0; i < ret.size(); i++) {
            VariableAccess va = (VariableAccess) ret.get(i);
            boolean write = true;
            for (int j = 0; j < this.undefList.size(); j++) {
                VariableAccess vaTmp = (VariableAccess) this.undefList.get(j);
                if (va.getVariableName().equals(vaTmp.getVariableName())) {
                    write = false;
                    break;
                }
            }
            if (write) {
                this.undefList.add(va);
            }
        }
        return dfn.getVariableAccess();
    }

//  ----------------------------------------------------------------------------
//	VALUE OBJECTS - nodes which represent a variable.
    
    public Object visit(ASTName node, Object data) {
        if (data != null) {
            Node n = node.jjtGetParent();
            if (!(n instanceof ASTType || n instanceof ASTAllocationExpression)) {
                List v = (List) data;
                v.add(node);
            }
        }
        return super.visit(node, data);
    }

    public Object visit(ASTVariableDeclaratorId node, Object data) {
        if (data != null) {
            List v = (List) data;
            v.add(node);
        }
        return super.visit(node, data);
    }    
    
    
    
//  ----------------------------------------------------------------------------
//	ACCESS OBJECTS - nodes which determines the access of a variable.

    public Object visit(ASTArguments node, Object data) {
        if (data != null) { // because error do_while
            List v = (List) data;
            v.add(node);
        }
        return super.visit(node, data);
    }

    public Object visit(ASTAssignmentOperator node, Object data) {
        List v = (List) data;
        v.add(node);
        return super.visit(node, data);
    }

    public Object visit(ASTPostfixExpression node, Object data) {
        List v = (List) data;
        v.add(node);
        return super.visit(node, data);
    }

    public Object visit(ASTPreDecrementExpression node, Object data) {
        List v = (List) data;
        v.add(node);
        return super.visit(node, data);
    }

    public Object visit(ASTPreIncrementExpression node, Object data) {
        List v = (List) data;
        v.add(node);
        return super.visit(node, data);
    }

    public Object visit(ASTVariableInitializer node, Object data) {
        List v = (List) data;
        v.add(node);
        return super.visit(node, data);
    }
}
