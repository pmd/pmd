/**
 * BSD-style license; for more info see http://pmd.sourceforge.net/license.html
 */
package net.sourceforge.pmd.lang.java.metrics;

import java.util.ArrayList;
import java.util.List;

import net.sourceforge.pmd.lang.java.ast.ASTConstructorDeclaration;
import net.sourceforge.pmd.lang.java.ast.ASTMethodDeclaration;
import net.sourceforge.pmd.lang.java.ast.ASTMethodOrConstructorDeclaration;

/**
 * Base class for metrics. Metric objects encapsulate the computational logic required to compute a metric from a PackageStats.
 *
 * @author Clément Fournier
 */
public abstract class AbstractMetric {

    protected boolean isAbstractHandler = false;

    protected List<String> findAllCalls(ASTMethodOrConstructorDeclaration node) {
        List<String> result = new ArrayList<>();
        // Find the qualified names of all methods called in that method's block
        return result;
    }

    protected boolean isNotSupported(ASTMethodOrConstructorDeclaration node) {
        if (node instanceof ASTMethodDeclaration) {
            return ((ASTMethodDeclaration) node).isAbstract();
        } else {
            return ((ASTConstructorDeclaration) node).isAbstract();
        }
    }
}
