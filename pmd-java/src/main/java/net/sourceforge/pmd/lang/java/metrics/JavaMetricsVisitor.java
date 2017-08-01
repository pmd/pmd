/**
 * BSD-style license; for more info see http://pmd.sourceforge.net/license.html
 */

package net.sourceforge.pmd.lang.java.metrics;

import java.util.Stack;

import net.sourceforge.pmd.lang.java.ast.ASTAnyTypeDeclaration;
import net.sourceforge.pmd.lang.java.ast.ASTFieldDeclaration;
import net.sourceforge.pmd.lang.java.ast.ASTMethodOrConstructorDeclaration;
import net.sourceforge.pmd.lang.java.ast.JavaParserVisitorReducedAdapter;

/**
 * Visitor for the metrics framework, that fills a {@link PackageStats} object with the signatures of operations and
 * fields it encounters.
 *
 * @author Clément Fournier
 */
class JavaMetricsVisitor extends JavaParserVisitorReducedAdapter {

    private Stack<ClassStats> stack = new Stack<>();


    @Override
    public Object visit(ASTAnyTypeDeclaration node, Object data) {
        stack.push(((PackageStats) data).getClassStats(node.getQualifiedName(), true));
        super.visit(node, data);
        stack.pop();

        return data;
    }


    @Override
    public Object visit(ASTMethodOrConstructorDeclaration node, Object data) {
        stack.peek().addOperation(node.getQualifiedName().getOperation(), node.getSignature());
        return super.visit(node, data);
    }


    @Override
    public Object visit(ASTFieldDeclaration node, Object data) {
        stack.peek().addField(node.getVariableName(), node.getSignature());
        return data; // end recursion
    }

}
