/**
 * BSD-style license; for more info see http://pmd.sourceforge.net/license.html
 */

package net.sourceforge.pmd.lang.java.metrics.metrics;

import org.apache.commons.lang3.mutable.MutableInt;

import net.sourceforge.pmd.lang.java.ast.ASTAnyTypeDeclaration;
import net.sourceforge.pmd.lang.java.ast.ASTMethodOrConstructorDeclaration;
import net.sourceforge.pmd.lang.java.ast.JavaParserVisitor;
import net.sourceforge.pmd.lang.java.metrics.metrics.visitors.DefaultNcssVisitor;
import net.sourceforge.pmd.lang.java.metrics.metrics.visitors.JavaNcssVisitor;
import net.sourceforge.pmd.lang.metrics.api.MetricVersion;

/**
 * Non Commenting Source Statements. Similar to LOC but only counts statements, which is roughly equivalent to counting
 * the number of semicolons and opening braces in the program.
 *
 * <p>The standard version's precise rules for counting statements comply with <a href="http://www.kclee.de/clemens/java/javancss/">JavaNCSS
 * rules</a>. The only difference is that import and package statements are not counted.
 *
 * <p>Version {@link NcssVersion#JAVANCSS}: Import and package statements are counted. This version fully complies with
 * JavaNcss rules.
 *
 * @author Clément Fournier
 * @see LocMetric
 * @since June 2017
 */
public final class NcssMetric {


    /** Variants of NCSS. */
    public enum NcssVersion implements MetricVersion {
        /** JavaNCSS compliant cyclo visitor. */
        JAVANCSS
    }

    public static final class NcssClassMetric extends AbstractJavaClassMetric {

        @Override
        public boolean supports(ASTAnyTypeDeclaration node) {
            return true;
        }


        @Override
        public double computeFor(ASTAnyTypeDeclaration node, MetricVersion version) {
            JavaParserVisitor visitor = (NcssVersion.JAVANCSS == version)
                                        ? new JavaNcssVisitor()
                                        : new DefaultNcssVisitor();

            MutableInt ncss = (MutableInt) node.jjtAccept(visitor, new MutableInt(0));
            return (double) ncss.getValue();
        }

    }

    public static final class NcssOperationMetric extends AbstractJavaOperationMetric {

        @Override
        public boolean supports(ASTMethodOrConstructorDeclaration node) {
            return true;
        }


        @Override
        public double computeFor(ASTMethodOrConstructorDeclaration node, MetricVersion version) {
            JavaParserVisitor visitor = (NcssVersion.JAVANCSS.equals(version))
                                        ? new JavaNcssVisitor()
                                        : new DefaultNcssVisitor();

            MutableInt ncss = (MutableInt) node.jjtAccept(visitor, new MutableInt(0));
            return (double) ncss.getValue();
        }

    }

}
