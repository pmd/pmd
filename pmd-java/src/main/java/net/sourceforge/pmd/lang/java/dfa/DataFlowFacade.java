/**
 * BSD-style license; for more info see http://pmd.sourceforge.net/license.html
 */

package net.sourceforge.pmd.lang.java.dfa;

import net.sourceforge.pmd.lang.DataFlowHandler;
import net.sourceforge.pmd.lang.java.ast.ASTCompilationUnit;
import net.sourceforge.pmd.lang.java.ast.ASTConstructorDeclaration;
import net.sourceforge.pmd.lang.java.ast.ASTMethodDeclaration;
import net.sourceforge.pmd.lang.java.ast.JavaParserVisitorAdapter;

/**
 * TODO What about initializers? This only processes methods and
 * constructors.
 *
 * @author raik
 */
public class DataFlowFacade extends JavaParserVisitorAdapter {

    private StatementAndBraceFinder sbf;
    private VariableAccessVisitor vav;

    public void initializeWith(DataFlowHandler dataFlowHandler, ASTCompilationUnit node) {
        sbf = new StatementAndBraceFinder(dataFlowHandler);
        vav = new VariableAccessVisitor();
        node.jjtAccept(this, null);
    }

    @Override
    public Object visit(ASTMethodDeclaration node, Object data) {
        sbf.buildDataFlowFor(node);
        vav.compute(node);
        return data;
    }

    @Override
    public Object visit(ASTConstructorDeclaration node, Object data) {
        sbf.buildDataFlowFor(node);
        vav.compute(node);
        return data;
    }
}
