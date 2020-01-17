/**
 * BSD-style license; for more info see http://pmd.sourceforge.net/license.html
 */

package net.sourceforge.pmd.lang.java.rule.errorprone;

import net.sourceforge.pmd.lang.ast.Node;
import net.sourceforge.pmd.lang.java.ast.ASTMethodDeclaration;
import net.sourceforge.pmd.lang.java.ast.ASTMethodDeclarator;
import net.sourceforge.pmd.lang.java.ast.ASTPrimitiveType;
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

        ASTResultType type = node.getResultType();
        ASTMethodDeclarator decl = node.getFirstChildOfType(ASTMethodDeclarator.class);
        String name = decl.getImage();
        if ("hashcode".equalsIgnoreCase(name) && !"hashCode".equals(name)
                && decl.getChild(0).getNumChildren() == 0 && type.getNumChildren() != 0) {
            Node t = type.getChild(0).getChild(0);
            if (t instanceof ASTPrimitiveType && "int".equals(t.getImage())) {
                addViolation(data, node);
                return data;
            }
        }
        return super.visit(node, data);
    }

}
