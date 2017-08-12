/**
 * BSD-style license; for more info see http://pmd.sourceforge.net/license.html
 */

package net.sourceforge.pmd.lang.java.metrics;

import net.sourceforge.pmd.lang.java.ast.ASTAnyTypeDeclaration;
import net.sourceforge.pmd.lang.java.ast.ASTMethodOrConstructorDeclaration;
import net.sourceforge.pmd.lang.java.ast.JavaParserVisitorReducedAdapter;

/**
 * Visitor for the metrics framework, that creates the memoizers.
 *
 * @author Clément Fournier
 */
class JavaMetricsVisitor extends JavaParserVisitorReducedAdapter {

    private final JavaProjectMemoizer memoizer;


    JavaMetricsVisitor(JavaProjectMemoizer memoizer) {
        this.memoizer = memoizer;
    }


    @Override
    public Object visit(ASTAnyTypeDeclaration node, Object data) {
        memoizer.addClassMemoizer(node.getQualifiedName());
        return super.visit(node, data);
    }


    @Override
    public Object visit(ASTMethodOrConstructorDeclaration node, Object data) {
        memoizer.addOperationMemoizer(node.getQualifiedName());
        return super.visit(node, data);
    }

}
