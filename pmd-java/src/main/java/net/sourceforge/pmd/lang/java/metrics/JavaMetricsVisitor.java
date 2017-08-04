/**
 * BSD-style license; for more info see http://pmd.sourceforge.net/license.html
 */

package net.sourceforge.pmd.lang.java.metrics;

import java.util.Stack;

import net.sourceforge.pmd.lang.java.ast.ASTAnyTypeDeclaration;
import net.sourceforge.pmd.lang.java.ast.ASTFieldDeclaration;
import net.sourceforge.pmd.lang.java.ast.ASTMethodOrConstructorDeclaration;
import net.sourceforge.pmd.lang.java.ast.JavaParserVisitorReducedAdapter;
import net.sourceforge.pmd.lang.metrics.ProjectMemoizer;

/**
 * Visitor for the metrics framework, that fills a {@link PackageStats} object with the signatures of operations and
 * fields it encounters.
 *
 * @author Clément Fournier
 */
class JavaMetricsVisitor extends JavaParserVisitorReducedAdapter {

    private final Stack<ClassStats> stack = new Stack<>();
    private final PackageStats toplevel;
    private final JavaProjectMemoizer memoizer;


    JavaMetricsVisitor(PackageStats toplevel, JavaProjectMemoizer memoizer) {
        this.toplevel = toplevel;
        this.memoizer = memoizer;
    }


    @Override
    public Object visit(ASTAnyTypeDeclaration node, Object data) {
        memoizer.addClassMemoizer(node.getQualifiedName());

        stack.push(toplevel.getClassStats(node.getQualifiedName(), true));
        super.visit(node, data);
        stack.pop();

        return data;
    }


    @Override
    public Object visit(ASTMethodOrConstructorDeclaration node, Object data) {
        memoizer.addOperationMemoizer(node.getQualifiedName());

        stack.peek().addOperation(node.getQualifiedName().getOperation(), node.getSignature());
        return super.visit(node, data);
    }


    @Override
    public Object visit(ASTFieldDeclaration node, Object data) {
        stack.peek().addField(node.getVariableName(), node.getSignature());
        return data; // end recursion
    }

}
