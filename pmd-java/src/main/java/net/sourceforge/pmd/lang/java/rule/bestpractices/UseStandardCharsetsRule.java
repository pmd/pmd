/*
 * BSD-style license; for more info see http://pmd.sourceforge.net/license.html
 */

package net.sourceforge.pmd.lang.java.rule.bestpractices;

import java.lang.reflect.Modifier;
import java.util.Arrays;
import java.util.HashSet;
import java.util.Set;
import java.util.stream.Stream;

import net.sourceforge.pmd.lang.java.ast.ASTConstructorCall;
import net.sourceforge.pmd.lang.java.ast.ASTExpression;
import net.sourceforge.pmd.lang.java.ast.ASTMethodCall;
import net.sourceforge.pmd.lang.java.ast.InvocationNode;
import net.sourceforge.pmd.lang.java.rule.AbstractJavaRulechainRule;
import net.sourceforge.pmd.lang.java.types.InvocationMatcher;
import net.sourceforge.pmd.lang.java.types.JMethodSig;
import net.sourceforge.pmd.lang.java.types.JTypeMirror;
import net.sourceforge.pmd.reporting.RuleContext;

/**
 * Starting with Java 7, StandardCharsets provides constants for common Charset objects, such as UTF-8.
 * Using the constants is less error prone, and can provide a small performance advantage compared to
 * `Charset.forName(...)` since no scan across the internal `Charset` caches is needed.
 *
 * @since 6.34.0 (as XPath) / 7.24.0 (as Java)
 */
public class UseStandardCharsetsRule extends AbstractJavaRulechainRule {

    private static final InvocationMatcher CHARSET_FOR_NAME = InvocationMatcher.parse("java.nio.charset.Charset#forName(java.lang.String)");
    private static final Set<String> STANDARD_CHARSET_EXISTS = new HashSet<>(Arrays.asList("US-ASCII", "ISO-8859-1", "UTF-8", "UTF-16BE", "UTF-16LE", "UTF-16"));
    private static final String CHARSET = "java.nio.charset.Charset";

    public UseStandardCharsetsRule() {
        super(ASTConstructorCall.class, ASTMethodCall.class);
    }

    @Override
    public RuleContext visit(ASTMethodCall call, Object data) {
        RuleContext ctx = (RuleContext) data;

        if (CHARSET_FOR_NAME.matchesCall(call)
                && STANDARD_CHARSET_EXISTS.contains(call.getArguments().get(0).getConstValue())
        ) {
            ctx.addViolation(call);
        }

        for (int i = 0; i < call.getArguments().size(); i++) {
            checkIthArgument(ctx, call, i);
        }

        return ctx;
    }

    @Override
    public RuleContext visit(ASTConstructorCall call, Object data) {
        RuleContext ctx = (RuleContext) data;

        for (int i = 0; i < call.getArguments().size(); i++) {
            checkIthArgument(ctx, call, i);
        }

        return ctx;
    }

    private void checkIthArgument(RuleContext ctx, InvocationNode call, int index) {
        ASTExpression ex = call.getArguments().get(index);
        if (STANDARD_CHARSET_EXISTS.contains(ex.getConstValue())) {
            JMethodSig callSignature = call.getMethodType();
            Stream<JMethodSig> otherSignatures = (call instanceof ASTMethodCall)
                    ? ((ASTMethodCall) call).getQualifier().getTypeMirror().streamMethods(sig -> true)
                    : ((ASTConstructorCall) call).getTypeNode().getTypeMirror().getConstructors().stream();
            long count = otherSignatures
                    .filter(sig -> Modifier.isPublic(sig.getModifiers()))
                    .filter(sig -> isSameSignatureExcept(callSignature, sig, index))
                    .filter(sig -> CHARSET.equals(sig.getFormalParameters().get(index).toString()))
                    .count();
            if (count > 0) {
                ctx.addViolation(call);
            }
        }
    }

    /**
     * returns true iff the two method signatures are identical - except for the indexth argument
     */
    private boolean isSameSignatureExcept(JMethodSig a, JMethodSig b, int index) {
        if (!a.getName().equals(b.getName())) {
            return false;
        }
        if (a.getArity() != b.getArity()) {
            return false;
        }
        for (int i = 0; i < a.getArity(); i++) {
            if (i == index) {
                continue;
            }

            JTypeMirror aParameter = a.getFormalParameters().get(i);
            JTypeMirror bParameter = b.getFormalParameters().get(i);
            if (!aParameter.equals(bParameter)) {
                return false;
            }
        }
        return true;
    }
}
