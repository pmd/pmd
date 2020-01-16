/**
 * BSD-style license; for more info see http://pmd.sourceforge.net/license.html
 */

package net.sourceforge.pmd.lang.apex.multifile;

import java.util.Stack;

import net.sourceforge.pmd.lang.apex.ast.ASTMethod;
import net.sourceforge.pmd.lang.apex.ast.ASTUserClassOrInterface;
import net.sourceforge.pmd.lang.apex.ast.ASTUserTrigger;
import net.sourceforge.pmd.lang.apex.ast.ApexParserVisitorReducedAdapter;

/**
 * @author Clément Fournier
 */
public class ApexMultifileVisitor extends ApexParserVisitorReducedAdapter {

    private final ApexProjectMirror mirror;

    private final Stack<ApexClassStats> stack = new Stack<>();


    public ApexMultifileVisitor(ApexProjectMirror mirror) {
        this.mirror = mirror;
    }


    @Override
    public Object visit(ASTUserClassOrInterface<?> node, Object data) {
        stack.push(mirror.getClassStats(node.getQualifiedName(), true));
        super.visit(node, data);
        stack.pop();

        return data;
    }

    @Override
    public Object visit(ASTUserTrigger node, Object data) {
        return data; // ignore
    }


    @Override
    public Object visit(ASTMethod node, Object data) {
        stack.peek().addOperation(node.getQualifiedName().getOperation(), node.getSignature());
        return data;
    }

}
