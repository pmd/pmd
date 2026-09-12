/*
 * BSD-style license; for more info see http://pmd.sourceforge.net/license.html
 */

package net.sourceforge.pmd.lang.java.rule.design;

import org.pcollections.PSet;

import net.sourceforge.pmd.lang.java.ast.ASTClassDeclaration;
import net.sourceforge.pmd.lang.java.ast.ASTConstructorCall;
import net.sourceforge.pmd.lang.java.ast.ASTExpression;
import net.sourceforge.pmd.lang.java.ast.ASTMethodCall;
import net.sourceforge.pmd.lang.java.rule.AbstractJavaRulechainRule;
import net.sourceforge.pmd.lang.java.rule.internal.TestFrameworksUtil;
import net.sourceforge.pmd.lang.java.symbols.JAccessibleElementSymbol;
import net.sourceforge.pmd.lang.java.symbols.JClassSymbol;
import net.sourceforge.pmd.lang.java.symbols.JExecutableSymbol;
import net.sourceforge.pmd.lang.java.symbols.SymbolicValue;
import net.sourceforge.pmd.lang.java.types.JClassType;
import net.sourceforge.pmd.lang.java.types.JMethodSig;
import net.sourceforge.pmd.lang.java.types.JTypeMirror;
import net.sourceforge.pmd.reporting.RuleContext;

public class InternalApiUsageRule extends AbstractJavaRulechainRule {

    public InternalApiUsageRule() {
        super(ASTMethodCall.class, ASTConstructorCall.class);
    }

    @Override
    public Object visit(ASTMethodCall node, Object data) {
        RuleContext ctx = asCtx(data);
        visitCall(node, node.getMethodType(), ctx, "Method");
        return null;
    }

    @Override
    public Object visit(ASTConstructorCall node, Object data) {
        RuleContext ctx = asCtx(data);
        visitCall(node, node.getMethodType(), ctx, "Constructor");
        return null;
    }

    private void visitCall(ASTExpression node, JMethodSig methodType, RuleContext data, String nodeType) {
        ASTClassDeclaration parentClass = node.ancestors(ASTClassDeclaration.class).first();
        JTypeMirror declaringType = methodType.getDeclaringType();
        boolean sameUnit = parentClass != null && declaringType instanceof JClassType
                && getEnclosingTypes((JClassType) declaringType, parentClass.getTypeMirror());
        JExecutableSymbol methodSymbol = methodType.getSymbol();
        String methodName = methodType.getName();
        checkAnnotations(data, node, nodeType, methodSymbol, methodName, declaringType, sameUnit);
        checkAnnotations(data, node, "Class", declaringType.getSymbol(),
                declaringType.getSymbol().getSimpleName(), declaringType, sameUnit);

    }

    private void checkAnnotations(RuleContext ctx, ASTExpression node, String nodeType,
                                  JAccessibleElementSymbol methodSymbol, String methodName,
                                  JTypeMirror declaringType,
                                  boolean sameUnit) {
        ASTClassDeclaration parentClass = node.ancestors(ASTClassDeclaration.class).first();
        PSet<SymbolicValue.SymAnnot> annotations = methodSymbol.getDeclaredAnnotations();
        for (SymbolicValue.SymAnnot annotation: annotations) {
            if ("TestOnly".equals(annotation.getSimpleName())
                    || !sameUnit && "VisibleForTesting".equals(annotation.getSimpleName())) {
                if (!node.ancestors(ASTClassDeclaration.class).any(TestFrameworksUtil::isTestClass)) {
                    ctx.addViolation(node, nodeType, methodName, "tests");
                }
                return;
            }
            if (!sameUnit && "org.apiguardian.api.API".equals(annotation.getBinaryName())) {
                SymbolicValue status = annotation.getAttribute("status");
                SymbolicValue consumers = annotation.getAttribute("consumers");
                if (consumers instanceof SymbolicValue.SymArray && !((SymbolicValue.SymArray) consumers).containsValue("*")) {
                    SymbolicValue.SymArray consumerArray = (SymbolicValue.SymArray) consumers;
                    String packageName = parentClass == null ? null : parentClass.getPackageName();
                    if (packageName != null
                            && "org.apiguardian.api.API$Status#INTERNAL".equals(status.toString())
                            && !consumerArray.anyMatch(v -> v.valueEquals(packageName))) {
                        ctx.addViolation(node, nodeType, methodName, "specific packages");
                    }
                } else if (isExternal(declaringType)) {
                    if (status != null && "org.apiguardian.api.API$Status#INTERNAL".equals(status.toString())) {
                        ctx.addViolation(node, nodeType, methodName, "defining library");
                    }
                }
            }
        }
    }

    private boolean isExternal(JTypeMirror declaringType) {
        return declaringType.getSymbol() instanceof JClassSymbol
                && ((JClassSymbol) declaringType.getSymbol()).isExternal();
    }

    public boolean getEnclosingTypes(JClassType start, Object other) {
        JClassType t = start;
        do {
            if (t.equals(other)) {
                return true;
            }
            t = t.getEnclosingType();
        } while (t != null);
        return false;
    }

}
