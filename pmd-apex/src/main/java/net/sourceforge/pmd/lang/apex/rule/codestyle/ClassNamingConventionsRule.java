/**
 * BSD-style license; for more info see http://pmd.sourceforge.net/license.html
 */

package net.sourceforge.pmd.lang.apex.rule.codestyle;

import net.sourceforge.pmd.lang.apex.ast.ASTUserClass;
import net.sourceforge.pmd.lang.apex.ast.ASTUserInterface;
import net.sourceforge.pmd.lang.apex.rule.AbstractApexRule;

public class ClassNamingConventionsRule extends AbstractApexRule {

    public ClassNamingConventionsRule() {
        setProperty(CODECLIMATE_CATEGORIES, "Style");
        // Note: x10 as Apex has not automatic refactoring
        setProperty(CODECLIMATE_REMEDIATION_MULTIPLIER, 5);
        setProperty(CODECLIMATE_BLOCK_HIGHLIGHTING, false);
    }

    @Override
    public Object visit(ASTUserClass node, Object data) {
        if (Character.isLowerCase(node.getImage().charAt(0))) {
            addViolation(data, node);
        }
        return data;
    }

    @Override
    public Object visit(ASTUserInterface node, Object data) {
        if (Character.isLowerCase(node.getImage().charAt(0))) {
            addViolation(data, node);
        }
        return data;
    }
}
