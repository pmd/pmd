/*
 * BSD-style license; for more info see http://pmd.sourceforge.net/license.html
 */

package net.sourceforge.pmd.lang.java.rule.bestpractices;

import net.sourceforge.pmd.lang.java.ast.ASTClassDeclaration;
import net.sourceforge.pmd.lang.java.ast.JModifier;
import net.sourceforge.pmd.lang.java.rule.AbstractJavaRulechainRule;
import net.sourceforge.pmd.lang.java.types.JMethodSig;
import net.sourceforge.pmd.lang.java.types.TypeOps;
import net.sourceforge.pmd.reporting.RuleContext;

public class ImplicitFunctionalInterfaceRule extends AbstractJavaRulechainRule {
    public ImplicitFunctionalInterfaceRule() {
        super(ASTClassDeclaration.class);
    }

    @Override
    public RuleContext visit(ASTClassDeclaration node, RuleContext data) {
        if (node.isRegularInterface()
            && !node.isAnnotationPresent(FunctionalInterface.class)
            && !node.hasModifiers(JModifier.SEALED)) {
            JMethodSig fun = TypeOps.findFunctionalInterfaceMethod(node.getTypeMirror());
            if (fun != null) {
                data.addViolation(node);
            }
        }
        return null;
    }
}
