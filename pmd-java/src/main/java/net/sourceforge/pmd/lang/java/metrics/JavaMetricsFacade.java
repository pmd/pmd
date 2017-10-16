/**
 * BSD-style license; for more info see http://pmd.sourceforge.net/license.html
 */

package net.sourceforge.pmd.lang.java.metrics;

import net.sourceforge.pmd.lang.java.ast.ASTAnyTypeDeclaration;
import net.sourceforge.pmd.lang.java.ast.ASTMethodOrConstructorDeclaration;
import net.sourceforge.pmd.lang.metrics.AbstractMetricsFacade;
import net.sourceforge.pmd.lang.metrics.MetricsComputer;

/**
 * Inner façade of the Java metrics framework. The static façade delegates to an instance of this class.
 *
 * @author Clément Fournier
 */
class JavaMetricsFacade extends AbstractMetricsFacade<ASTAnyTypeDeclaration, ASTMethodOrConstructorDeclaration> {

    private final JavaProjectMemoizer memoizer = new JavaProjectMemoizer();


    /** Resets the entire data structure. Used for tests. */
    void reset() {
        memoizer.reset();
    }


    @Override
    public JavaProjectMemoizer getLanguageSpecificProjectMemoizer() {
        return memoizer;
    }


    @Override
    protected MetricsComputer<ASTAnyTypeDeclaration, ASTMethodOrConstructorDeclaration> getLanguageSpecificComputer() {
        return JavaMetricsComputer.INSTANCE;
    }

}
