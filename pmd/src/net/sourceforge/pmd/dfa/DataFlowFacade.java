/*
 * Created on 10.07.2004
 */
package net.sourceforge.pmd.dfa;

import net.sourceforge.pmd.ast.ASTCompilationUnit;
import net.sourceforge.pmd.ast.ASTConstructorDeclaration;
import net.sourceforge.pmd.ast.ASTMethodDeclaration;
import net.sourceforge.pmd.ast.JavaParserVisitorAdapter;
import net.sourceforge.pmd.dfa.variableaccess.VariableAccessVisitor;

/**
 * @author raik
 *         <p/>
 *         TODO What about initializers?  This only processes methods and constructors
 */
public class DataFlowFacade extends JavaParserVisitorAdapter {

    private StatementAndBraceFinder sbf;
    private VariableAccessVisitor vav;

    public void initializeWith(ASTCompilationUnit node) {
        sbf = new StatementAndBraceFinder();
        vav = new VariableAccessVisitor();
        node.jjtAccept(this, null);
    }

    public Object visit(ASTMethodDeclaration node, Object data) {
        sbf.buildDataFlowFor(node);
        vav.compute(node);
        return data;
    }

    public Object visit(ASTConstructorDeclaration node, Object data) {
        sbf.buildDataFlowFor(node);
        vav.compute(node);
        return data;
    }
}
