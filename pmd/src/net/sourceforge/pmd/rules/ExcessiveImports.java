/**
 * BSD-style license; for more info see http://pmd.sourceforge.net/license.html
 */
package net.sourceforge.pmd.rules;

import net.sourceforge.pmd.ast.ASTCompilationUnit;
import net.sourceforge.pmd.ast.ASTImportDeclaration;
import net.sourceforge.pmd.rules.design.ExcessiveNodeCountRule;
import net.sourceforge.pmd.util.NumericConstants;

/**
 * ExcessiveImports attempts to count all unique imports a class
 * contains. This rule will count a "import com.something.*;" as a single
 * import. This is a unqiue situation and I'd like to create an audit type
 * rule that captures those.
 *
 * @author aglover
 * @since Feb 21, 2003
 */
public class ExcessiveImports extends ExcessiveNodeCountRule {

    public ExcessiveImports() {
        super(ASTCompilationUnit.class);
    }

    /**
     * Hook method to count imports. This is a user defined value.
     *
     * @param node
     * @param data
     * @return Object
     */
    public Object visit(ASTImportDeclaration node, Object data) {
        return NumericConstants.ONE;
    }
}
