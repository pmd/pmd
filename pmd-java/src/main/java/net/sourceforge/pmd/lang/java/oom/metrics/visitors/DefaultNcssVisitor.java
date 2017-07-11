/**
 * BSD-style license; for more info see http://pmd.sourceforge.net/license.html
 */

package net.sourceforge.pmd.lang.java.oom.metrics.visitors;

import net.sourceforge.pmd.lang.java.ast.ASTImportDeclaration;
import net.sourceforge.pmd.lang.java.ast.ASTPackageDeclaration;

/**
 * Default visitor for the calculation of Ncss.
 *
 * @author Clément Fournier
 * @see net.sourceforge.pmd.lang.java.oom.metrics.NcssMetric
 */
public class DefaultNcssVisitor extends JavaNcssVisitor {

    @Override
    public Object visit(ASTImportDeclaration node, Object data) {
        return data;
    }


    @Override
    public Object visit(ASTPackageDeclaration node, Object data) {
        return data;
    }

}
