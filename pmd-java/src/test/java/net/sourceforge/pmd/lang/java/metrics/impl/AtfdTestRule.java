/**
 * BSD-style license; for more info see http://pmd.sourceforge.net/license.html
 */

package net.sourceforge.pmd.lang.java.metrics.impl;

import net.sourceforge.pmd.lang.ast.Node;
import net.sourceforge.pmd.lang.java.ast.ASTAnyTypeDeclaration;
import net.sourceforge.pmd.lang.java.ast.ASTMethodOrConstructorDeclaration;
import net.sourceforge.pmd.lang.java.metrics.api.JavaMetrics;

/**
 * @author Clément Fournier
 * @since 6.0.0
 */
public class AtfdTestRule extends JavaIntMetricTestRule {

    public AtfdTestRule() {
        super(JavaMetrics.ACCESS_TO_FOREIGN_DATA);
    }

    @Override
    protected boolean reportOn(Node node) {
        return node instanceof ASTMethodOrConstructorDeclaration
            || node instanceof ASTAnyTypeDeclaration;
    }

    @Override
    protected String violationMessage(Node node, Integer result) {
        return super.violationMessage(node, result);
    }
}
