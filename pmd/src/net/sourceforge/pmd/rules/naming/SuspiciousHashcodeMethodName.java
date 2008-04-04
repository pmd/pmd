package net.sourceforge.pmd.rules.naming;

import net.sourceforge.pmd.lang.java.AbstractJavaRule;
import net.sourceforge.pmd.lang.java.ast.ASTMethodDeclaration;
import net.sourceforge.pmd.lang.java.ast.ASTMethodDeclarator;
import net.sourceforge.pmd.lang.java.ast.ASTPrimitiveType;
import net.sourceforge.pmd.lang.java.ast.ASTResultType;
import net.sourceforge.pmd.lang.ast.Node;

public class SuspiciousHashcodeMethodName extends AbstractJavaRule {

    public Object visit(ASTMethodDeclaration node, Object data) {
        /* original XPath rule was
         //MethodDeclaration
        [ResultType
        //PrimitiveType
        [@Image='int']
        [//MethodDeclarator
        [@Image='hashcode' or @Image='HashCode' or @Image='Hashcode']
        [not(FormalParameters/*)]]]
         */

        ASTResultType type = node.getFirstChildOfType(ASTResultType.class);
        ASTMethodDeclarator decl = node.getFirstChildOfType(ASTMethodDeclarator.class);
        String name = decl.getImage();
        if (name.equalsIgnoreCase("hashcode") && !name.equals("hashCode")
                && decl.jjtGetChild(0).jjtGetNumChildren() == 0
                && type.jjtGetNumChildren() != 0) {
            Node t = type.jjtGetChild(0).jjtGetChild(0);
            if (t instanceof ASTPrimitiveType && "int".equals(t.getImage())) {
                addViolation(data, node);
                return data;
            }
        }
        return super.visit(node, data);
    }

}
