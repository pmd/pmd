/**
 * BSD-style license; for more info see http://pmd.sourceforge.net/license.html
 */

package net.sourceforge.pmd.lang.java.rule.performance;

import java.math.BigDecimal;
import java.math.BigInteger;

import net.sourceforge.pmd.RuleContext;
import net.sourceforge.pmd.lang.LanguageRegistry;
import net.sourceforge.pmd.lang.ast.Node;
import net.sourceforge.pmd.lang.java.JavaLanguageModule;
import net.sourceforge.pmd.lang.java.ast.ASTAllocationExpression;
import net.sourceforge.pmd.lang.java.ast.ASTArguments;
import net.sourceforge.pmd.lang.java.ast.ASTArrayDimsAndInits;
import net.sourceforge.pmd.lang.java.ast.ASTClassOrInterfaceType;
import net.sourceforge.pmd.lang.java.ast.ASTLiteral;
import net.sourceforge.pmd.lang.java.rule.AbstractJavaRule;
import net.sourceforge.pmd.lang.java.typeresolution.TypeHelper;

/**
 * Rule that marks instantiations of new {@link BigInteger} or
 * {@link BigDecimal} objects, when there is a well-known constant available,
 * such as {@link BigInteger#ZERO}.
 */
public class BigIntegerInstantiationRule extends AbstractJavaRule {

    @Override
    public Object visit(ASTAllocationExpression node, Object data) {
        Node type = node.getChild(0);

        if (!(type instanceof ASTClassOrInterfaceType)) {
            return super.visit(node, data);
        }

        boolean jdk15 = ((RuleContext) data).getLanguageVersion()
                .compareTo(LanguageRegistry.getLanguage(JavaLanguageModule.NAME).getVersion("1.5")) >= 0;
        if ((TypeHelper.isA((ASTClassOrInterfaceType) type, BigInteger.class)
                || jdk15 && TypeHelper.isA((ASTClassOrInterfaceType) type, BigDecimal.class))
                && !node.hasDescendantOfType(ASTArrayDimsAndInits.class)) {
            ASTArguments args = node.getFirstChildOfType(ASTArguments.class);
            if (args.getArgumentCount() == 1) {
                ASTLiteral literal = node.getFirstDescendantOfType(ASTLiteral.class);
                if (literal == null
                        || literal.getParent().getParent().getParent().getParent().getParent() != args) {
                    return super.visit(node, data);
                }

                String img = literal.getImage();
                if (literal.isStringLiteral()) {
                    img = img.substring(1, img.length() - 1);
                }

                if ("0".equals(img) || "1".equals(img) || jdk15 && "10".equals(img)) {
                    addViolation(data, node);
                    return data;
                }
            }
        }
        return super.visit(node, data);
    }

}
