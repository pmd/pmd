/**
 * BSD-style license; for more info see http://pmd.sourceforge.net/license.html
 */

package net.sourceforge.pmd.lang.java.rule.errorprone;

import net.sourceforge.pmd.lang.java.ast.ASTMethodDeclaration;
import net.sourceforge.pmd.lang.java.ast.ASTMethodDeclarator;
import net.sourceforge.pmd.lang.java.ast.ASTResultType;
import net.sourceforge.pmd.lang.java.rule.AbstractJavaRule;

public class SuspiciousHashcodeMethodNameRule extends AbstractJavaRule {

    @Override
    public Object visit(ASTMethodDeclaration node, Object data) {
        /*
         * original XPath rule was //MethodDeclaration [ResultType
         * //PrimitiveType [@Image='int'] [//MethodDeclarator [@Image='hashcode'
         * or @Image='HashCode' or @Image='Hashcode']
         * [not(FormalParameters/*)]]]
         */

        boolean isIntReturn =
            node.getResultType()
                .getTypeNode()
                .map(t -> t.isPrimitiveType() && t.getTypeImage().equals("int"))
                .orElse(false);

        String name = node.getMethodName();
        if ("hashcode".equalsIgnoreCase(name) && (!"hashCode".equals(name) || !isIntReturn)) {
            addViolation(data, node);
            return data;
        }
        return super.visit(node, data);
    }

}
