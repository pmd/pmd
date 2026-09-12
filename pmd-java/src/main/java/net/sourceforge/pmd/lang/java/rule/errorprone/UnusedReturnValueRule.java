/*
 * BSD-style license; for more info see http://pmd.sourceforge.net/license.html
 */

package net.sourceforge.pmd.lang.java.rule.errorprone;

import net.sourceforge.pmd.lang.java.ast.ASTExpressionStatement;
import net.sourceforge.pmd.lang.java.ast.ASTMethodCall;
import net.sourceforge.pmd.lang.java.rule.AbstractJavaRulechainRule;
import net.sourceforge.pmd.lang.java.rule.internal.JavaRuleUtil;
import net.sourceforge.pmd.lang.java.symbols.AnnotableSymbol;
import net.sourceforge.pmd.lang.java.symbols.JClassSymbol;
import net.sourceforge.pmd.lang.java.symbols.JExecutableSymbol;
import net.sourceforge.pmd.lang.java.symbols.JTypeDeclSymbol;
import net.sourceforge.pmd.lang.java.types.InvocationMatcher;
import net.sourceforge.pmd.lang.java.types.JMethodSig;
import net.sourceforge.pmd.lang.java.types.Substitution;
import net.sourceforge.pmd.lang.java.types.TypeSystem;
import net.sourceforge.pmd.reporting.RuleContext;

/**
 * @since 7.27.0
 */
public class UnusedReturnValueRule extends AbstractJavaRulechainRule {

    private static final String CHECK_RETURN_VALUE_ANNOTATION = "CheckReturnValue";
    private static final String CAN_IGNORE_RETURN_VALUE_ANNOTATION = "CanIgnoreReturnValue";

    private static final InvocationMatcher.CompoundInvocationMatcher METHODS_RETURNINING_NUMBER_OF_BYTES_READ = InvocationMatcher.parseAll(
            "java.io.InputStream#skip(long)",
            "java.io.InputStream#read(byte[])",
            "java.io.InputStream#read(byte[],int,int)"
    );

    public UnusedReturnValueRule() {
        super(ASTMethodCall.class);
    }

    @Override
    public RuleContext visit(ASTMethodCall call, Object data) {
        RuleContext ctx = (RuleContext) data;

        if (shouldCheckResult(call) && !isResultUsed(call)) {
            ctx.addViolation(call, formatCall(call));
        }

        return ctx;
    }

    private String formatCall(ASTMethodCall call) {
        JMethodSig methodSig = call.getMethodType();

        return ((JClassSymbol) methodSig.getDeclaringType().getSymbol()).getCanonicalName() + "." + methodSig.getName() + "()";
    }

    private boolean shouldCheckResult(ASTMethodCall call) {
        return isCheckReturnValueAnnotated(call)
                || JavaRuleUtil.isKnownPure(call)
                || METHODS_RETURNINING_NUMBER_OF_BYTES_READ.anyMatch(call);
    }

    // visible for testing
    /* private */ static boolean isCheckReturnValueAnnotated(ASTMethodCall call) {
        JExecutableSymbol methodSymbol = call.getMethodType().getSymbol();
        if (methodSymbol.getReturnType(Substitution.EMPTY).isVoid()) {
            return false;
        }

        if (isAnnotatedWith(methodSymbol, CHECK_RETURN_VALUE_ANNOTATION)) {
            return true;
        }

        JTypeDeclSymbol classSymbol = call.getMethodType().getDeclaringType().getSymbol();
        if (isAnnotatedWith(classSymbol, CHECK_RETURN_VALUE_ANNOTATION)
                && !isAnnotatedWith(methodSymbol, CAN_IGNORE_RETURN_VALUE_ANNOTATION)
        ) {
            return true;
        }

        TypeSystem typeSystem = classSymbol.getTypeSystem();
        AnnotableSymbol packageSymbol = typeSystem.getPackageSymbol(classSymbol.getPackageName());
        return packageSymbol != null
                && isAnnotatedWith(packageSymbol, CHECK_RETURN_VALUE_ANNOTATION)
                && !isAnnotatedWith(classSymbol, CAN_IGNORE_RETURN_VALUE_ANNOTATION)
                && !isAnnotatedWith(methodSymbol, CAN_IGNORE_RETURN_VALUE_ANNOTATION);
    }

    private static boolean isAnnotatedWith(AnnotableSymbol symbol, String name) {
        return symbol.getDeclaredAnnotations().stream().anyMatch(annotation -> name.equals(annotation.getSimpleName()));
    }

    private boolean isResultUsed(ASTMethodCall call) {
        return !(call.getParent() instanceof ASTExpressionStatement);
    }
}
