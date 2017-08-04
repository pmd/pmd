/**
 * BSD-style license; for more info see http://pmd.sourceforge.net/license.html
 */

package net.sourceforge.pmd.lang.apex.metrics;

import java.util.Stack;

import net.sourceforge.pmd.lang.apex.ast.ASTMethod;
import net.sourceforge.pmd.lang.apex.ast.ASTUserClassOrInterface;
import net.sourceforge.pmd.lang.apex.ast.ApexParserVisitorReducedAdapter;

/**
 * @author Clément Fournier
 */
public class ApexMetricsVisitor extends ApexParserVisitorReducedAdapter {

    private final ApexProjectMemoizer memoizer;
    private final ApexProjectMirror mirror;

    private final Stack<ApexClassStats> stack = new Stack<>();


    public ApexMetricsVisitor(ApexProjectMemoizer memoizer, ApexProjectMirror mirror) {
        this.memoizer = memoizer;
        this.mirror = mirror;
    }

    @Override
    public Object visit(ASTUserClassOrInterface<?> node, Object data) {
        memoizer.addClassMemoizer(node.getQualifiedName());
        stack.push(mirror.getClassStats(node.getQualifiedName(), true));
        super.visit(node, data);
        stack.pop();

        return data;
    }




    @Override
    public Object visit(ASTMethod node, Object data) {
        memoizer.addOperationMemoizer(node.getQualifiedName());

        stack.peek().addOperation(node.getQualifiedName().getOperation(), node.getSignature());
        return data;
    }

}
