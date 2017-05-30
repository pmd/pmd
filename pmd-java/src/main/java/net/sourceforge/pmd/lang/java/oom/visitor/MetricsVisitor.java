/**
 * BSD-style license; for more info see http://pmd.sourceforge.net/license.html
 */

package net.sourceforge.pmd.lang.java.oom.visitor;

import net.sourceforge.pmd.lang.java.ast.ASTClassOrInterfaceDeclaration;
import net.sourceforge.pmd.lang.java.ast.ASTConstructorDeclaration;
import net.sourceforge.pmd.lang.java.ast.ASTFieldDeclaration;
import net.sourceforge.pmd.lang.java.ast.ASTMethodDeclaration;
import net.sourceforge.pmd.lang.java.ast.JavaParserVisitorAdapter;
import net.sourceforge.pmd.lang.java.ast.QualifiedName;

/**
 * @author Clément Fournier
 */
public class MetricsVisitor extends JavaParserVisitorAdapter {

    private ClassStats classContext = null;

    @Override
    public Object visit(ASTClassOrInterfaceDeclaration node, Object data) {
        PackageStats stats = (PackageStats) data;

        classContext = stats.getClassStats(node.getQualifiedName(), true);

        return super.visit(node, data);
    }

    @Override
    public Object visit(ASTConstructorDeclaration node, Object data) {
        PackageStats stats = (PackageStats) data;

        QualifiedName qname = node.getQualifiedName();

        stats.getClassStats(qname, true).addOperation(qname,
                OperationSignature.buildFor(node));

        return super.visit(node, data);
    }

    @Override
    public Object visit(ASTMethodDeclaration node, Object data) {
        PackageStats stats = (PackageStats) data;

        QualifiedName qname = node.getQualifiedName();

        stats.getClassStats(qname, true).addOperation(qname,
                OperationSignature.buildFor(node));

        return super.visit(node, data);
    }

    @Override
    public Object visit(ASTFieldDeclaration node, Object data) {
        PackageStats stats = (PackageStats) data;

        classContext.addField(node.getVariableName(), FieldSignature.buildFor(node));

        return data; // end recursion
    }

}
