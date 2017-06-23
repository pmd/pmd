/**
 * BSD-style license; for more info see http://pmd.sourceforge.net/license.html
 */

package net.sourceforge.pmd.lang.java.oom;

import java.util.Stack;

import net.sourceforge.pmd.lang.java.ast.ASTClassOrInterfaceDeclaration;
import net.sourceforge.pmd.lang.java.ast.ASTConstructorDeclaration;
import net.sourceforge.pmd.lang.java.ast.ASTEnumDeclaration;
import net.sourceforge.pmd.lang.java.ast.ASTFieldDeclaration;
import net.sourceforge.pmd.lang.java.ast.ASTMethodDeclaration;
import net.sourceforge.pmd.lang.java.ast.JavaParserVisitorAdapter;
import net.sourceforge.pmd.lang.java.oom.signature.FieldSignature;
import net.sourceforge.pmd.lang.java.oom.signature.OperationSignature;

/**
 * Visitor for the metrics framework, that fills a {@link PackageStats} object with the
 * signatures of operations and fields it encounters.
 *
 * @author Clément Fournier
 */
class MetricsVisitor extends JavaParserVisitorAdapter {

    private Stack<ClassStats> stack = new Stack<>();

    @Override
    public Object visit(ASTClassOrInterfaceDeclaration node, Object data) {
        stack.push(((PackageStats) data).getClassStats(node.getQualifiedName(), true));
        super.visit(node, data);
        stack.pop();

        return data;
    }

    @Override
    public Object visit(ASTEnumDeclaration node, Object data) {
        stack.push(((PackageStats) data).getClassStats(node.getQualifiedName(), true));
        super.visit(node, data);
        stack.pop();

        return data;
    }

    @Override
    public Object visit(ASTConstructorDeclaration node, Object data) {
        stack.peek().addOperation(node.getQualifiedName().getOperation(), OperationSignature.buildFor(node));
        return super.visit(node, data);
    }

    @Override
    public Object visit(ASTMethodDeclaration node, Object data) {
        stack.peek().addOperation(node.getQualifiedName().getOperation(), OperationSignature.buildFor(node));
        return super.visit(node, data);
    }

    @Override
    public Object visit(ASTFieldDeclaration node, Object data) {
        stack.peek().addField(node.getVariableName(), FieldSignature.buildFor(node));
        return data; // end recursion
    }
}
