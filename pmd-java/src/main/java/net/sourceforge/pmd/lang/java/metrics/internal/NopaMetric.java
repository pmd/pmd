/**
 * BSD-style license; for more info see http://pmd.sourceforge.net/license.html
 */

package net.sourceforge.pmd.lang.java.metrics.internal;

import net.sourceforge.pmd.lang.java.ast.ASTAnyTypeDeclaration;
import net.sourceforge.pmd.lang.java.metrics.AbstractJavaClassMetric;
import net.sourceforge.pmd.lang.java.multifile.signature.JavaFieldSigMask;
import net.sourceforge.pmd.lang.java.multifile.signature.JavaSignature.Visibility;
import net.sourceforge.pmd.lang.metrics.MetricOptions;

/**
 * Number of public attributes.
 *
 * @author Clément Fournier
 * @since 6.0.0
 */
public class NopaMetric extends AbstractJavaClassMetric {

    @Override
    public double computeFor(ASTAnyTypeDeclaration node, MetricOptions options) {

        JavaFieldSigMask mask = new JavaFieldSigMask();
        mask.restrictVisibilitiesTo(Visibility.PUBLIC);

        return countMatchingFieldSigs(node, mask);
    }

}
