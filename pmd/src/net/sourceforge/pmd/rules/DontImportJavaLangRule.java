/*
 * User: tom
 * Date: Jul 14, 2002
 * Time: 6:45:18 PM
 */
package net.sourceforge.pmd.rules;

import net.sourceforge.pmd.AbstractRule;
import net.sourceforge.pmd.RuleContext;
import net.sourceforge.pmd.ast.ASTImportDeclaration;

public class DontImportJavaLangRule extends AbstractRule {

    public Object visit(ASTImportDeclaration node, Object data) {
        if (node.getImportedNameNode().getImage().startsWith("java.lang") && !node.getImportedNameNode().getImage().startsWith("java.lang.ref") && !node.getImportedNameNode().getImage().startsWith("java.lang.reflect")) {
            ((RuleContext) data).getReport().addRuleViolation(createRuleViolation((RuleContext) data, node.getBeginLine()));
        }
        return data;
    }
}
