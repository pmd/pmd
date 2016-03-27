/**
 * BSD-style license; for more info see http://pmd.sourceforge.net/license.html
 */
package net.sourceforge.pmd.lang.apex.rule.naming;

import net.sourceforge.pmd.lang.apex.ast.ASTCompilation;
import net.sourceforge.pmd.lang.apex.ast.ASTFieldDeclaration;
import net.sourceforge.pmd.lang.apex.ast.ASTUserClass;
import net.sourceforge.pmd.lang.apex.ast.ASTUserInterface;
import net.sourceforge.pmd.lang.apex.rule.AbstractApexRule;

public class AvoidFieldNameMatchingTypeNameRule extends AbstractApexRule {

    public Object visit(ASTUserInterface node, Object data) {
        return data;
    }

    public Object visit(ASTUserClass node, Object data) {
        return super.visit(node, data);
    }

    public Object visit(ASTFieldDeclaration node, Object data) {
        ASTCompilation cl = node.getFirstParentOfType(ASTCompilation.class);
        if (cl != null && node.getNode().getFieldInfo().getName().equalsIgnoreCase(cl.getImage())) {
            addViolation(data, node);
        }
        return data;
    }
}
