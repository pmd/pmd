/**
 * BSD-style license; for more info see http://pmd.sourceforge.net/license.html
 */

package net.sourceforge.pmd.lang.apex.metrics.impl;

import net.sourceforge.pmd.lang.apex.ast.ASTUserClass;
import net.sourceforge.pmd.lang.apex.metrics.AbstractApexMetric;
import net.sourceforge.pmd.lang.apex.metrics.api.ApexClassMetric;

/**
 * @author Clément Fournier
 */
public abstract class AbstractApexClassMetric extends AbstractApexMetric implements ApexClassMetric {

    @Override
    public boolean supports(ASTUserClass node) {
        return true;
    }
}
