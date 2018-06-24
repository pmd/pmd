/**
 * BSD-style license; for more info see http://pmd.sourceforge.net/license.html
 */

package net.sourceforge.pmd.lang.java.rule;

import net.sourceforge.pmd.lang.java.ast.ASTAnnotationTypeDeclaration;
import net.sourceforge.pmd.lang.java.ast.ASTAnyTypeDeclaration;
import net.sourceforge.pmd.lang.java.ast.ASTClassOrInterfaceDeclaration;
import net.sourceforge.pmd.lang.java.ast.ASTConstructorDeclaration;
import net.sourceforge.pmd.lang.java.ast.ASTEnumDeclaration;
import net.sourceforge.pmd.lang.java.ast.ASTLambdaExpression;
import net.sourceforge.pmd.lang.java.ast.ASTMethodDeclaration;
import net.sourceforge.pmd.lang.java.ast.ASTMethodOrConstructorDeclaration;
import net.sourceforge.pmd.lang.java.ast.JavaNode;
import net.sourceforge.pmd.lang.java.ast.MethodLikeNode;


/**
 * Java Rule with convenient visit methods to e.g. treat contructors and methods the same.
 *
 * @author Clément Fournier
 */
public abstract class AbstractJavaMetricsRule extends AbstractJavaRule {

    @Override
    public final Object visit(ASTClassOrInterfaceDeclaration node, Object data) {
        return visit((ASTAnyTypeDeclaration) node, data);
    }


    @Override
    public final Object visit(ASTAnnotationTypeDeclaration node, Object data) {
        return visit((ASTAnyTypeDeclaration) node, data);
    }


    @Override
    public final Object visit(ASTEnumDeclaration node, Object data) {
        return visit((ASTAnyTypeDeclaration) node, data);
    }


    public Object visit(ASTAnyTypeDeclaration node, Object data) {
        return visit((JavaNode) node, data);
    }


    @Override
    public final Object visit(ASTMethodDeclaration node, Object data) {
        return visit((ASTMethodOrConstructorDeclaration) node, data);
    }


    @Override
    public final Object visit(ASTConstructorDeclaration node, Object data) {
        return visit((ASTMethodOrConstructorDeclaration) node, data);
    }

    @Override
    public final Object visit(ASTLambdaExpression node, Object data) {
        return visit((MethodLikeNode) node, data);
    }


    public Object visit(ASTMethodOrConstructorDeclaration node, Object data) {
        return visit((MethodLikeNode) node, data);
    }


    public Object visit(MethodLikeNode node, Object data) {
        return visit((JavaNode) node, data);
    }

}
