/**
 * BSD-style license; for more info see http://pmd.sourceforge.net/license.html
 */

package net.sourceforge.pmd.lang.java.oom.visitor;

import net.sourceforge.pmd.lang.java.ast.ASTClassOrInterfaceDeclaration;
import net.sourceforge.pmd.lang.java.ast.ASTConstructorDeclaration;
import net.sourceforge.pmd.lang.java.ast.ASTMethodDeclaration;
import net.sourceforge.pmd.lang.java.ast.JavaParserVisitorAdapter;

/**
 * @author Clément Fournier
 */
public class MetricsVisitor extends JavaParserVisitorAdapter {

    @Override
    public Object visit(ASTClassOrInterfaceDeclaration node, Object data) {
        PackageStats stats = (PackageStats) data;
        System.err.println("Visiting class " + node.getQualifiedName());
        return super.visit(node, data);
    }


    @Override
    public Object visit(ASTConstructorDeclaration node, Object data) {
        PackageStats stats = (PackageStats) data;
        System.err.println("Visiting constructor " + node.getQualifiedName());
        return super.visit(node, data);
    }

    @Override
    public Object visit(ASTMethodDeclaration node, Object data) {
        PackageStats stats = (PackageStats) data;
        System.err.println("Visiting method " + node.getQualifiedName());
        return super.visit(node, data);
    }
}
