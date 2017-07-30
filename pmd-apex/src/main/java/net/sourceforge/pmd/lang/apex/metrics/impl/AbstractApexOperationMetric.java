/**
 * BSD-style license; for more info see http://pmd.sourceforge.net/license.html
 */

package net.sourceforge.pmd.lang.apex.metrics.impl;

import net.sourceforge.pmd.lang.apex.ast.ASTMethod;
import net.sourceforge.pmd.lang.apex.metrics.AbstractApexMetric;
import net.sourceforge.pmd.lang.apex.metrics.api.ApexOperationMetric;

/**
 * @author Clément Fournier
 */
public abstract class AbstractApexOperationMetric extends AbstractApexMetric implements ApexOperationMetric {

    @Override
    public boolean supports(ASTMethod node) {
        return true;
    }
}
