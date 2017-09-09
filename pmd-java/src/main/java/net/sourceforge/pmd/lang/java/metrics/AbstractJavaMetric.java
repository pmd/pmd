/**
 * BSD-style license; for more info see http://pmd.sourceforge.net/license.html
 */

package net.sourceforge.pmd.lang.java.metrics;

import java.util.ArrayList;
import java.util.List;

import net.sourceforge.pmd.lang.ast.Node;
import net.sourceforge.pmd.lang.java.ast.ASTMethodOrConstructorDeclaration;
import net.sourceforge.pmd.lang.java.ast.JavaQualifiedName;
import net.sourceforge.pmd.lang.metrics.AbstractMetric;


/**
 * Base class for metrics. Metric objects encapsulate the computational logic required to compute a metric from a
 * PackageStats and node. They're stateless.
 *
 * @param <N> Type of node the metric can be computed on
 *
 * @author Clément Fournier
 */
public abstract class AbstractJavaMetric<N extends Node> extends AbstractMetric<N> {


    protected List<JavaQualifiedName> findAllCalls(ASTMethodOrConstructorDeclaration node) {
        List<JavaQualifiedName> result = new ArrayList<>();
        // TODO:cf findAllCalls
        // Needs TypeRes
        // Find the qualified names of all methods called in that method's block
        return result;
    }


    /**
     * Gives access to a signature matcher to metrics. They can use it to perform signature matching.
     *
     * @return A signature matcher
     */
    protected static JavaSignatureMatcher getSignatureMatcher() {
        return JavaMetrics.getFacade().getTopLevelPackageStats();
    }

}
