/**
 * BSD-style license; for more info see http://pmd.sourceforge.net/license.html
 */

package net.sourceforge.pmd.lang.java.metrics;

import net.sourceforge.pmd.lang.java.ast.ASTAnyTypeDeclaration;
import net.sourceforge.pmd.lang.java.ast.ASTMethodOrConstructorDeclaration;
import net.sourceforge.pmd.lang.metrics.AbstractMetricsFacade;
import net.sourceforge.pmd.lang.metrics.MetricsComputer;
import net.sourceforge.pmd.lang.metrics.ProjectMirror;

/**
 * Inner façade of the Java metrics framework. The static façade delegates to an instance of this class.
 *
 * @author Clément Fournier
 */
class JavaMetricsFacade extends AbstractMetricsFacade<ASTAnyTypeDeclaration, ASTMethodOrConstructorDeclaration> {

    private final PackageStats topLevelPackageStats = new PackageStats();


    /**
     * Returns the top level package statistics.
     *
     * @return The top level package stats
     */
    PackageStats getTopLevelPackageStats() {
        return topLevelPackageStats;
    }


    /** Resets the entire data structure. Used for tests. */
    void reset() {
        topLevelPackageStats.reset();
    }


    @Override
    protected MetricsComputer<ASTAnyTypeDeclaration, ASTMethodOrConstructorDeclaration> getLanguageSpecificComputer() {
        return JavaMetricsComputer.INSTANCE;
    }


    @Override
    protected ProjectMirror<ASTAnyTypeDeclaration, ASTMethodOrConstructorDeclaration> getLanguageSpecificMirror() {
        return topLevelPackageStats;
    }
}
