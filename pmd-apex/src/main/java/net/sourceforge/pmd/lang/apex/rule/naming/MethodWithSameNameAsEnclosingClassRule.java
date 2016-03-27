/**
 * BSD-style license; for more info see http://pmd.sourceforge.net/license.html
 */
package net.sourceforge.pmd.lang.apex.rule.naming;

import java.util.List;

import net.sourceforge.pmd.lang.apex.ast.ASTCompilation;
import net.sourceforge.pmd.lang.apex.ast.ASTMethod;
import net.sourceforge.pmd.lang.apex.rule.AbstractApexRule;

public class MethodWithSameNameAsEnclosingClassRule extends AbstractApexRule {

    @Override
    public Object visit(ASTCompilation node, Object data) {
        List<ASTMethod> methods = node.findDescendantsOfType(ASTMethod.class);
        for (ASTMethod m : methods) {
            if (m.hasImageEqualTo(node.getImage())) {
                addViolation(data, m);
            }
        }
        return super.visit(node, data);
    }
}
