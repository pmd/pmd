/**
 * BSD-style license; for more info see http://pmd.sourceforge.net/license.html
 */

package net.sourceforge.pmd.lang.java.rule.performance;

import net.sourceforge.pmd.lang.java.ast.ASTExpression;
import net.sourceforge.pmd.lang.java.ast.ASTMethodCall;
import net.sourceforge.pmd.lang.java.ast.ASTTypeExpression;
import net.sourceforge.pmd.lang.java.rule.AbstractJavaRule;
import net.sourceforge.pmd.lang.java.typeresolution.TypeHelper;

public class StringToStringRule extends AbstractJavaRule {

    @Override
    public Object visit(ASTMethodCall node, Object data) {
        if ("toString".equals(node.getMethodName())
                && node.getArguments().size() == 0) {
            ASTExpression qualifier = node.getQualifier();
            if (qualifier != null
                    && !(qualifier instanceof ASTTypeExpression)
                    && TypeHelper.symbolEquals(String.class, qualifier.getTypeMirror())) {
                addViolation(data, node);
            }
        }

        return super.visit(node, data);
    }

}
