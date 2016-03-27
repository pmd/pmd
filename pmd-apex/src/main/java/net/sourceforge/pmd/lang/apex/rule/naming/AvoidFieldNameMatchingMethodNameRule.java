/**
 * BSD-style license; for more info see http://pmd.sourceforge.net/license.html
 */
package net.sourceforge.pmd.lang.apex.rule.naming;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import net.sourceforge.pmd.lang.apex.ast.ASTCompilation;
import net.sourceforge.pmd.lang.apex.ast.ASTFieldDeclaration;
import net.sourceforge.pmd.lang.apex.ast.ASTMethod;
import net.sourceforge.pmd.lang.apex.rule.AbstractApexRule;
import net.sourceforge.pmd.lang.ast.Node;

public class AvoidFieldNameMatchingMethodNameRule extends AbstractApexRule {

    @Override
    public Object visit(ASTCompilation node, Object data) {
        int n = node.jjtGetNumChildren();
        List<ASTFieldDeclaration> fields = new ArrayList<>();
        Set<String> methodNames = new HashSet<>();
        for (int i = 0; i < n; i++) {
            Node child = node.jjtGetChild(i);
            if (child.jjtGetNumChildren() == 0) {
                continue;
            }
            child = child.jjtGetChild(child.jjtGetNumChildren() - 1);
            if (child instanceof ASTFieldDeclaration) {
                fields.add((ASTFieldDeclaration) child);
            } else if (child instanceof ASTMethod) {
                methodNames.add(((ASTMethod) child).getNode().getMethodInfo().getName().toLowerCase());
            }
        }
        for (ASTFieldDeclaration field : fields) {
            String varName = field.getNode().getFieldInfo().getName().toLowerCase();
            if (methodNames.contains(varName)) {
                addViolation(data, field, field.getNode().getFieldInfo().getName());
            }
        }
        return super.visit(node, data);
    }

}
