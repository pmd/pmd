package net.sourceforge.pmd.lang.java.rule.basic;

import java.math.BigDecimal;
import java.math.BigInteger;

import net.sourceforge.pmd.RuleContext;
import net.sourceforge.pmd.lang.java.ast.ASTAllocationExpression;
import net.sourceforge.pmd.lang.java.ast.ASTArguments;
import net.sourceforge.pmd.lang.java.ast.ASTArrayDimsAndInits;
import net.sourceforge.pmd.lang.java.ast.ASTClassOrInterfaceType;
import net.sourceforge.pmd.lang.java.ast.ASTLiteral;
import net.sourceforge.pmd.lang.java.rule.AbstractJavaRule;
import net.sourceforge.pmd.lang.java.typeresolution.TypeHelper;
import net.sourceforge.pmd.lang.LanguageVersion;
import net.sourceforge.pmd.lang.ast.Node;

public class BigIntegerInstantiationRule extends AbstractJavaRule {

    public Object visit(ASTAllocationExpression node, Object data) {
        Node type = node.jjtGetChild(0);
        
        if (!(type instanceof ASTClassOrInterfaceType)) {
            return super.visit(node, data);            
        }
        
        boolean jdk15 = ((RuleContext) data).getLanguageVersion().compareTo(LanguageVersion.JAVA_15) >= 0;
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
                if (img.length() > 2 && img.charAt(0) == '"') {
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
