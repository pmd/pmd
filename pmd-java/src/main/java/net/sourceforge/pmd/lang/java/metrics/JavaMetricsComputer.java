/**
 * BSD-style license; for more info see http://pmd.sourceforge.net/license.html
 */

package net.sourceforge.pmd.lang.java.metrics;

import java.util.ArrayList;
import java.util.List;

import net.sourceforge.pmd.lang.java.ast.ASTAnyTypeBodyDeclaration;
import net.sourceforge.pmd.lang.java.ast.ASTAnyTypeDeclaration;
import net.sourceforge.pmd.lang.java.ast.ASTMethodOrConstructorDeclaration;
import net.sourceforge.pmd.lang.metrics.AbstractMetricsComputer;

/**
 * Computes a metric. This relieves ClassStats and OperationStats from that responsibility.
 *
 * @author Clément Fournier
 */
public class JavaMetricsComputer extends AbstractMetricsComputer<ASTAnyTypeDeclaration, ASTMethodOrConstructorDeclaration> {

    static final JavaMetricsComputer INSTANCE = new JavaMetricsComputer();


    private JavaMetricsComputer() {

    }


    @Override
    protected List<ASTMethodOrConstructorDeclaration> findOperations(ASTAnyTypeDeclaration node) {

        List<ASTMethodOrConstructorDeclaration> operations = new ArrayList<>();

        for (ASTAnyTypeBodyDeclaration decl : node.getDeclarations()) {
            if (decl.jjtGetNumChildren() > 0 && decl.jjtGetChild(0) instanceof ASTMethodOrConstructorDeclaration) {
                operations.add((ASTMethodOrConstructorDeclaration) decl.jjtGetChild(0));
            }
        }
        return operations;
    }

}
