/**
 * BSD-style license; for more info see http://pmd.sourceforge.net/license.html
 */

package net.sourceforge.pmd.lang.java.rule.codesmells;

import net.sourceforge.pmd.lang.java.ast.ASTClassOrInterfaceDeclaration;
import net.sourceforge.pmd.lang.java.rule.AbstractJavaRulechainRule;

public class MattsRule extends AbstractJavaRulechainRule {

    public MattsRule() {
        super(ASTClassOrInterfaceDeclaration.class);
    }

    @Override
    public Object visit(ASTClassOrInterfaceDeclaration node, Object data) {
        addViolation(data, node);
        return data;
    }
}
