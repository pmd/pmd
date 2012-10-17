/**
 * BSD-style license; for more info see http://pmd.sourceforge.net/license.html
 */
package net.sourceforge.pmd.lang.plsql.dfa;

import net.sourceforge.pmd.lang.DataFlowHandler;
import net.sourceforge.pmd.lang.plsql.ast.ASTInput;
//import net.sourceforge.pmd.lang.plsql.ast.ASTConstructorDeclaration;
import net.sourceforge.pmd.lang.plsql.ast.ASTMethodDeclaration;
import net.sourceforge.pmd.lang.plsql.ast.ASTProgramUnit;
import net.sourceforge.pmd.lang.plsql.ast.ASTTriggerUnit;
import net.sourceforge.pmd.lang.plsql.ast.ASTTypeMethod;
import net.sourceforge.pmd.lang.plsql.ast.PLSQLParserVisitorAdapter;

/**
 * @author raik
 *         <p/>
 *         TODO What about initializers?  This only processes methods and constructors
 */
public class DataFlowFacade extends PLSQLParserVisitorAdapter {

    private StatementAndBraceFinder sbf;
    private VariableAccessVisitor vav;

    public void initializeWith(DataFlowHandler dataFlowHandler, ASTInput node) {
        sbf = new StatementAndBraceFinder(dataFlowHandler);
        vav = new VariableAccessVisitor();
        node.jjtAccept(this, null);
    }

    public Object visit(ASTMethodDeclaration node, Object data) {
        sbf.buildDataFlowFor(node);
        vav.compute(node);
        return data;
    }

    public Object visit(ASTTriggerUnit node, Object data) {
        sbf.buildDataFlowFor(node);
        vav.compute(node);
        return data;
    }

    public Object visit(ASTProgramUnit node, Object data) {
        sbf.buildDataFlowFor(node);
        vav.compute(node);
        return data;
    }

    public Object visit(ASTTypeMethod node, Object data) {
        sbf.buildDataFlowFor(node);
        vav.compute(node);
        return data;
    }

    /*
    public Object visit(ASTConstructorDeclaration node, Object data) {
        sbf.buildDataFlowFor(node);
        vav.compute(node);
        return data;
    }
    */
}
