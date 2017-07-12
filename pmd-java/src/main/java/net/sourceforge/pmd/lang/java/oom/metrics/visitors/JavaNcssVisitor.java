/**
 * BSD-style license; for more info see http://pmd.sourceforge.net/license.html
 */

package net.sourceforge.pmd.lang.java.oom.metrics.visitors;

import java.util.List;

import org.apache.commons.lang3.mutable.MutableInt;

import net.sourceforge.pmd.lang.java.ast.ASTClassOrInterfaceDeclaration;
import net.sourceforge.pmd.lang.java.ast.ASTCompilationUnit;
import net.sourceforge.pmd.lang.java.ast.ASTImportDeclaration;
import net.sourceforge.pmd.lang.java.ast.ASTPackageDeclaration;

/**
 * JavaNcss compliant visitor for the calculation of Ncss.
 *
 * @author Clément Fournier
 * @see net.sourceforge.pmd.lang.java.oom.metrics.NcssMetric
 */
public class JavaNcssVisitor extends DefaultNcssVisitor {


    @Override
    public Object visit(ASTClassOrInterfaceDeclaration node, Object data) {

        ASTCompilationUnit acu = node.getFirstParentOfType(ASTCompilationUnit.class);
        List<ASTImportDeclaration> imports = acu.findChildrenOfType(ASTImportDeclaration.class);

        int increment = imports.size();
        if (!acu.findChildrenOfType(ASTPackageDeclaration.class).isEmpty()) {
            increment++;
        }

        ((MutableInt) data).add(increment);
        return super.visit(node, data);
    }
}
