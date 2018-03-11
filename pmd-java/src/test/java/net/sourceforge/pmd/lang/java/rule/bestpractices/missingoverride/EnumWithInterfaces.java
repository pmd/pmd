/**
 * BSD-style license; for more info see http://pmd.sourceforge.net/license.html
 */

package net.sourceforge.pmd.lang.java.rule.bestpractices.missingoverride;

import net.sourceforge.pmd.lang.java.ast.ASTAnyTypeDeclaration;
import net.sourceforge.pmd.lang.metrics.Metric;
import net.sourceforge.pmd.lang.metrics.MetricKey;


/**
 * @author Clément Fournier
 * @since 6.2.0
 */
public enum EnumWithInterfaces implements MetricKey<ASTAnyTypeDeclaration> {
    Foo {
        @Override
        public Metric<ASTAnyTypeDeclaration> getCalculator() {
            return null;
        }
    };

    @Override
    public Metric<ASTAnyTypeDeclaration> getCalculator() {
        return null;
    }


    @Override
    public boolean supports(ASTAnyTypeDeclaration node) {
        return false;
    }
}
