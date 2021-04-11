/*
 * BSD-style license; for more info see http://pmd.sourceforge.net/license.html
 */

package net.sourceforge.pmd.lang.java.rule.codestyle;

import net.sourceforge.pmd.lang.java.ast.ASTClassOrInterfaceType;
import net.sourceforge.pmd.lang.java.ast.ASTConstructorCall;
import net.sourceforge.pmd.lang.java.ast.ASTTypeArguments;
import net.sourceforge.pmd.lang.java.ast.ExprContext;
import net.sourceforge.pmd.lang.java.rule.AbstractJavaRulechainRule;
import net.sourceforge.pmd.lang.java.symbols.JClassSymbol;
import net.sourceforge.pmd.lang.java.symbols.JTypeDeclSymbol;
import net.sourceforge.pmd.lang.java.types.JTypeMirror;
import net.sourceforge.pmd.lang.java.types.TypeOps;

public class UseDiamondOperatorRule extends AbstractJavaRulechainRule {

    public UseDiamondOperatorRule() {
        super(ASTConstructorCall.class);
    }

    public Object visit(ASTConstructorCall ctorCall, Object data) {
        ASTClassOrInterfaceType newTypeNode = ctorCall.getTypeNode();
        JTypeMirror newType = newTypeNode.getTypeMirror();

        ASTTypeArguments targs = newTypeNode.getTypeArguments();
        if (targs != null && targs.isDiamond()
            // if unresolved we can't know whether the class is generic or not
            || TypeOps.isUnresolved(newType)) {
            return null;
        }

        // targs may be null, in which case this would be a raw type
        if (!newType.isGeneric()) {
            return null;
        }

        ExprContext exprCtx = ctorCall.getConversionContextType();
        if (exprCtx == null) {
            return null; // cannot be converted
        }

        if (!supportsDiamondOnAnonymousClass(ctorCall) && ctorCall.isAnonymousClass()
            || useJava7Rules(ctorCall) && isNecessaryInJava7(newType, exprCtx)) {
            return null;
        }

        if (targs != null) {
            addViolation(data, targs);
        } else {
            addViolation(data, newTypeNode);
        }

        return null;
    }

    private boolean useJava7Rules(ASTConstructorCall ctorCall) {
        return ctorCall.getAstInfo().getLanguageVersion().compareToVersion("1.8") < 0;
    }

    private boolean isNecessaryInJava7(JTypeMirror newType, ExprContext exprContext) {
        JTypeMirror target = exprContext.getTargetType();
        if (target == null) {
            return false;
        }
        JTypeDeclSymbol targetSym = target.getSymbol();
        if (!(targetSym instanceof JClassSymbol)) {
            return false;
        }
        JTypeMirror asSuperType = newType.getAsSuper((JClassSymbol) targetSym);
        return !TypeOps.isSameType(asSuperType, target);
    }

    private boolean supportsDiamondOnAnonymousClass(ASTConstructorCall ctorCall) {
        return ctorCall.getAstInfo().getLanguageVersion().compareToVersion("9") >= 0;
    }

}
