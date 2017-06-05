/**
 * BSD-style license; for more info see http://pmd.sourceforge.net/license.html
 */

package net.sourceforge.pmd.lang.java.oom.visitor;

import net.sourceforge.pmd.lang.java.ast.ASTClassOrInterfaceDeclaration;
import net.sourceforge.pmd.lang.java.ast.ASTConstructorDeclaration;
import net.sourceforge.pmd.lang.java.ast.ASTFieldDeclaration;
import net.sourceforge.pmd.lang.java.ast.ASTMethodDeclaration;
import net.sourceforge.pmd.lang.java.ast.JavaParserVisitorAdapter;

/**
 * Visitor for the metrics framework, that fills a {@link PackageStats} object with the
 * signatures of operations and fields it encounters.
 *
 * @author Clément Fournier
 */
public class MetricsVisitor extends JavaParserVisitorAdapter {

    private ClassStats classContext = null;

    @Override
    public Object visit(ASTClassOrInterfaceDeclaration node, Object data) {

        classContext = ((PackageStats) data).getClassStats(node.getQualifiedName(), true);

        return super.visit(node, data);
    }

    @Override
    public Object visit(ASTConstructorDeclaration node, Object data) {

        classContext.addOperation(node.getQualifiedName(), OperationSignature.buildFor(node));

        return super.visit(node, data);
    }

    @Override
    public Object visit(ASTMethodDeclaration node, Object data) {

        classContext.addOperation(node.getQualifiedName(), OperationSignature.buildFor(node));

        return super.visit(node, data);
    }

    @Override
    public Object visit(ASTFieldDeclaration node, Object data) {

        classContext.addField(node.getVariableName(), FieldSignature.buildFor(node));

        return data; // end recursion
    }

}
