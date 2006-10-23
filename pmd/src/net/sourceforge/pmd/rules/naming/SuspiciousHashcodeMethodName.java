package net.sourceforge.pmd.rules.naming;

import net.sourceforge.pmd.AbstractRule;
import net.sourceforge.pmd.ast.ASTMethodDeclaration;
import net.sourceforge.pmd.ast.ASTMethodDeclarator;
import net.sourceforge.pmd.ast.ASTPrimitiveType;
import net.sourceforge.pmd.ast.ASTResultType;
import net.sourceforge.pmd.ast.SimpleJavaNode;

public class SuspiciousHashcodeMethodName extends AbstractRule {

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

        ASTResultType type = (ASTResultType) node.getFirstChildOfType(ASTResultType.class);
        ASTMethodDeclarator decl = (ASTMethodDeclarator) node.getFirstChildOfType(ASTMethodDeclarator.class);
        String name = decl.getImage();
        if (name.equalsIgnoreCase("hashcode") && !name.equals("hashCode")
                && decl.jjtGetChild(0).jjtGetNumChildren() == 0
                && type.jjtGetNumChildren() != 0) {
            SimpleJavaNode t = (SimpleJavaNode) type.jjtGetChild(0).jjtGetChild(0);
            if (t instanceof ASTPrimitiveType && "int".equals(t.getImage())) {
                addViolation(data, node);
                return data;
            }
        }
        return super.visit(node, data);
    }

}
