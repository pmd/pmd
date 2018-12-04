/**
 * BSD-style license; for more info see http://pmd.sourceforge.net/license.html
 */

package net.sourceforge.pmd.lang.java.metrics.impl.visitors;

import java.util.List;

import org.apache.commons.lang3.mutable.MutableInt;

import net.sourceforge.pmd.lang.java.ast.ASTClassOrInterfaceDeclaration;
import net.sourceforge.pmd.lang.java.ast.ASTCompilationUnit;
import net.sourceforge.pmd.lang.java.ast.ASTImportDeclaration;
import net.sourceforge.pmd.lang.java.ast.ASTPackageDeclaration;
import net.sourceforge.pmd.lang.java.ast.JavaParserVisitorDecorator;


/**
 * Decorator which counts imports.
 *
 * @author Clément Fournier
 * @see net.sourceforge.pmd.lang.java.metrics.impl.NcssMetric
 *
 * @deprecated Visitor decorators are deprecated because they lead to fragile code.
 */
@Deprecated
public class NcssCountImportsDecorator extends JavaParserVisitorDecorator {


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
