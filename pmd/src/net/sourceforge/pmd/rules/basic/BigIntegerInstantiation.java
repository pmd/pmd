package net.sourceforge.pmd.rules.basic;

import java.math.BigDecimal;
import java.math.BigInteger;

import net.sourceforge.pmd.AbstractRule;
import net.sourceforge.pmd.RuleContext;
import net.sourceforge.pmd.SourceType;
import net.sourceforge.pmd.ast.ASTAllocationExpression;
import net.sourceforge.pmd.ast.ASTArguments;
import net.sourceforge.pmd.ast.ASTArrayDimsAndInits;
import net.sourceforge.pmd.ast.ASTClassOrInterfaceType;
import net.sourceforge.pmd.ast.ASTLiteral;
import net.sourceforge.pmd.ast.Node;
import net.sourceforge.pmd.typeresolution.TypeHelper;

public class BigIntegerInstantiation extends AbstractRule {

    public Object visit(ASTAllocationExpression node, Object data) {
        Node type = node.jjtGetChild(0);
        
        if (!(type instanceof ASTClassOrInterfaceType)) {
            return super.visit(node, data);            
        }
        
        boolean jdk15 = ((RuleContext) data).getSourceType().compareTo(SourceType.JAVA_15) >= 0;
        if ((TypeHelper.isA((ASTClassOrInterfaceType) type, BigInteger.class) || (jdk15 && TypeHelper.isA((ASTClassOrInterfaceType) type, BigDecimal.class))) &&
                (node.getFirstChildOfType(ASTArrayDimsAndInits.class) == null)
        ) {
            ASTArguments args = node.getFirstChildOfType(ASTArguments.class);
            if (args.getArgumentCount() == 1) {
                ASTLiteral literal = node.getFirstChildOfType(ASTLiteral.class);
                if (literal == null || literal.jjtGetParent().jjtGetParent().jjtGetParent().jjtGetParent().jjtGetParent() != args) {
                    return super.visit(node, data);
                }

                String img = literal.getImage();
                if ((img.length() > 2 && img.charAt(0) == '"')) {
                    img = img.substring(1, img.length() - 1);
                }
                
                if ("0".equals(img) || "1".equals(img) || (jdk15 && "10".equals(img))) {
                    addViolation(data, node);
                    return data;                
                }
            }
        }
        return super.visit(node, data);
    }

}
