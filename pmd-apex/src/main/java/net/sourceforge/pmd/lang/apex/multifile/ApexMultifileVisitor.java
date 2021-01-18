/**
 * BSD-style license; for more info see http://pmd.sourceforge.net/license.html
 */

package net.sourceforge.pmd.lang.apex.multifile;

import java.util.Stack;

import net.sourceforge.pmd.lang.apex.ast.ASTMethod;
import net.sourceforge.pmd.lang.apex.ast.ASTUserClass;
import net.sourceforge.pmd.lang.apex.ast.ASTUserClassOrInterface;
import net.sourceforge.pmd.lang.apex.ast.ASTUserEnum;
import net.sourceforge.pmd.lang.apex.ast.ASTUserInterface;
import net.sourceforge.pmd.lang.apex.ast.ASTUserTrigger;
import net.sourceforge.pmd.lang.apex.ast.ApexParserVisitorAdapter;

/**
 * @author Clément Fournier
 */
public class ApexMultifileVisitor extends ApexParserVisitorAdapter {

    private final ApexProjectMirror mirror;

    private final Stack<ApexClassStats> stack = new Stack<>();


    public ApexMultifileVisitor(ApexProjectMirror mirror) {
        this.mirror = mirror;
    }


    public Object visitTypeDecl(ASTUserClassOrInterface<?> node, Object data) {
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
    public Object visit(ASTUserEnum node, Object data) {
        return data; // ignore
    }


    @Override
    public Object visit(ASTMethod node, Object data) {
        stack.peek().addOperation(node.getQualifiedName().getOperation(), node.getSignature());
        return data;
    }

    @Override
    public final Object visit(ASTUserInterface node, Object data) {
        return visitTypeDecl(node, data);
    }


    @Override
    public final Object visit(ASTUserClass node, Object data) {
        return visitTypeDecl(node, data);
    }
}
