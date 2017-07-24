/**
 * BSD-style license; for more info see http://pmd.sourceforge.net/license.html
 */

package net.sourceforge.pmd.lang.java.metrics;

import java.util.Stack;

import net.sourceforge.pmd.lang.java.ast.ASTAnyTypeDeclaration;
import net.sourceforge.pmd.lang.java.ast.ASTFieldDeclaration;
import net.sourceforge.pmd.lang.java.ast.ASTMethodOrConstructorDeclaration;
import net.sourceforge.pmd.lang.java.ast.JavaParserVisitorReducedAdapter;
import net.sourceforge.pmd.lang.java.metrics.signature.FieldSignature;
import net.sourceforge.pmd.lang.java.metrics.signature.OperationSignature;

/**
 * Visitor for the metrics framework, that fills a {@link JavaPackageStats} object with the
 * signatures of operations and fields it encounters.
 *
 * @author Clément Fournier
 */
class JavaMetricsVisitor extends JavaParserVisitorReducedAdapter {

    private Stack<JavaClassStats> stack = new Stack<>();


    @Override
    public Object visit(ASTAnyTypeDeclaration node, Object data) {
        stack.push(((JavaPackageStats) data).getClassStats(node.getQualifiedName(), true));
        super.visit(node, data);
        stack.pop();

        return data;
    }


    @Override
    public Object visit(ASTMethodOrConstructorDeclaration node, Object data) {
        stack.peek().addOperation(node.getQualifiedName().getOperation(), OperationSignature.buildFor(node));
        return super.visit(node, data);
    }


    @Override
    public Object visit(ASTFieldDeclaration node, Object data) {
        stack.peek().addField(node.getVariableName(), FieldSignature.buildFor(node));
        return data; // end recursion
    }

}
