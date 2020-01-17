/**
 * BSD-style license; for more info see http://pmd.sourceforge.net/license.html
 */

package net.sourceforge.pmd.lang.java.metrics;

import java.util.ArrayList;
import java.util.List;

import net.sourceforge.pmd.annotation.InternalApi;
import net.sourceforge.pmd.lang.java.ast.ASTAnyTypeBodyDeclaration;
import net.sourceforge.pmd.lang.java.ast.ASTAnyTypeDeclaration;
import net.sourceforge.pmd.lang.java.ast.ASTMethodOrConstructorDeclaration;
import net.sourceforge.pmd.lang.java.ast.MethodLikeNode;
import net.sourceforge.pmd.lang.metrics.AbstractMetricsComputer;

/**
 * Computes a metric.
 *
 * @author Clément Fournier
 */
public final class JavaMetricsComputer extends AbstractMetricsComputer<ASTAnyTypeDeclaration, MethodLikeNode> {

    private static final JavaMetricsComputer INSTANCE = new JavaMetricsComputer();


    private JavaMetricsComputer() {
    }


    @InternalApi
    public static JavaMetricsComputer getInstance() {
        return INSTANCE;
    }


    // TODO: doesn't consider lambdas
    @Override
    protected List<MethodLikeNode> findOperations(ASTAnyTypeDeclaration node) {

        List<MethodLikeNode> operations = new ArrayList<>();

        for (ASTAnyTypeBodyDeclaration decl : node.getDeclarations()) {
            if (decl.getNumChildren() > 0 && decl.getChild(0) instanceof ASTMethodOrConstructorDeclaration) {
                operations.add((MethodLikeNode) decl.getChild(0));
            }
        }
        return operations;
    }

}
