/**
 * BSD-style license; for more info see http://pmd.sourceforge.net/license.html
 */

package net.sourceforge.pmd.lang.java.metrics.impl;

import net.sourceforge.pmd.lang.java.ast.ASTAnyTypeDeclaration;
import net.sourceforge.pmd.lang.java.ast.ASTAnyTypeDeclaration.TypeKind;
import net.sourceforge.pmd.lang.java.metrics.AbstractJavaMetric;
import net.sourceforge.pmd.lang.java.metrics.api.JavaClassMetric;

/**
 * Base class for class metrics.
 *
 * @author Clément Fournier
 */
public abstract class AbstractJavaClassMetric extends AbstractJavaMetric<ASTAnyTypeDeclaration> implements JavaClassMetric {


    /**
     * Returns true if the metric can be computed on this type declaration. By default, annotation and interface
     * declarations are filtered out.
     *
     * @param node The type declaration
     *
     * @return True if the metric can be computed on this type declaration
     */
    public boolean supports(ASTAnyTypeDeclaration node) {
        return node.getTypeKind() != TypeKind.ANNOTATION && node.getTypeKind() != TypeKind.INTERFACE;
    }
}
