/**
 * BSD-style license; for more info see http://pmd.sourceforge.net/license.html
 */

package net.sourceforge.pmd.lang.apex.metrics.impl;

import net.sourceforge.pmd.lang.apex.ast.ASTUserClassOrInterface;
import net.sourceforge.pmd.lang.apex.ast.ASTUserClassOrInterface.TypeKind;
import net.sourceforge.pmd.lang.apex.metrics.AbstractApexMetric;
import net.sourceforge.pmd.lang.apex.metrics.api.ApexClassMetric;

/**
 * Base class for Apex metrics.
 *
 * @author Clément Fournier
 */
public abstract class AbstractApexClassMetric extends AbstractApexMetric<ASTUserClassOrInterface<?>>
    implements ApexClassMetric {

    @Override
    public boolean supports(ASTUserClassOrInterface<?> node) {
        return node.getTypeKind() == TypeKind.CLASS;
    }
}
