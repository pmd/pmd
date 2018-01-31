/**
 * BSD-style license; for more info see http://pmd.sourceforge.net/license.html
 */

package net.sourceforge.pmd.lang.java.metrics;

import java.util.ArrayList;
import java.util.List;

import net.sourceforge.pmd.lang.java.ast.ASTAnyTypeBodyDeclaration;
import net.sourceforge.pmd.lang.java.ast.ASTAnyTypeDeclaration;
import net.sourceforge.pmd.lang.java.ast.ASTMethodOrConstructorDeclaration;
import net.sourceforge.pmd.lang.java.ast.MethodLike;
import net.sourceforge.pmd.lang.metrics.AbstractMetricsComputer;

/**
 * Computes a metric.
 *
 * @author Clément Fournier
 */
public class JavaMetricsComputer extends AbstractMetricsComputer<ASTAnyTypeDeclaration, MethodLike> {

    static final JavaMetricsComputer INSTANCE = new JavaMetricsComputer();


    private JavaMetricsComputer() {

    }

    // TODO: doesn't consider lambdas
    @Override
    protected List<MethodLike> findOperations(ASTAnyTypeDeclaration node) {

        List<MethodLike> operations = new ArrayList<>();

        for (ASTAnyTypeBodyDeclaration decl : node.getDeclarations()) {
            if (decl.jjtGetNumChildren() > 0 && decl.jjtGetChild(0) instanceof ASTMethodOrConstructorDeclaration) {
                operations.add((ASTMethodOrConstructorDeclaration) decl.jjtGetChild(0));
            }
        }
        return operations;
    }

}
