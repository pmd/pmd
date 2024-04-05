package net.sourceforge.pmd.lang.java.rule.bestpractices;

import net.sourceforge.pmd.lang.java.ast.ASTClassDeclaration;
import net.sourceforge.pmd.lang.java.rule.AbstractJavaRulechainRule;
import net.sourceforge.pmd.lang.java.types.JMethodSig;
import net.sourceforge.pmd.lang.java.types.TypeOps;

public class ImplicitFunctionalInterfaceRule extends AbstractJavaRulechainRule {
    public ImplicitFunctionalInterfaceRule() {
        super(ASTClassDeclaration.class);
    }

    @Override
    public Object visit(ASTClassDeclaration node, Object data) {
        if (node.isInterface() && !node.isAnnotationPresent(FunctionalInterface.class)) {
            JMethodSig fun = TypeOps.findFunctionalInterfaceMethod(node.getTypeMirror());
            if (fun != null) {
                asCtx(data).addViolation(node);
            }
        }
        return null;
    }
}
