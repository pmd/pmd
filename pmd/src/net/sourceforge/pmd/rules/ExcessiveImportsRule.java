/**
 * BSD-style license; for more info see http://pmd.sourceforge.net/license.html
*/
package net.sourceforge.pmd.rules;

import net.sourceforge.pmd.ast.ASTCompilationUnit;
import net.sourceforge.pmd.ast.ASTImportDeclaration;
import net.sourceforge.pmd.rules.design.ExcessiveNodeCountRule;

/**
 * ExcessiveImportsRule attempts to count all unique imports a class
 * contains. This rule will count a "import com.something.*;" as a single
 * import. This is a unqiue situation and I'd like to create an audit type
 * rule that captures those.
 *
 * @since Feb 21, 2003
 * @author aglover
 *
 */
public class ExcessiveImportsRule extends ExcessiveNodeCountRule {

    /**
     * Hook constructor to pass in parent type
     */
    public ExcessiveImportsRule() {
        super(ASTCompilationUnit.class);
    }

    /**
     * Hook method to count imports. This is a user defined value.
     * @return Object
     * @param ASTImportDeclaration node
     * @param Object data
     */
    public Object visit(ASTImportDeclaration node, Object data) {
        return new Integer(1);
    }
}