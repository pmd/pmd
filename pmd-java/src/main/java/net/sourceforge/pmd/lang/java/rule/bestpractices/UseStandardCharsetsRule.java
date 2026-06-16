/*
 * BSD-style license; for more info see http://pmd.sourceforge.net/license.html
 */

package net.sourceforge.pmd.lang.java.rule.bestpractices;

import static net.sourceforge.pmd.util.CollectionUtil.immutableSetOf;

import java.lang.reflect.Modifier;
import java.util.Locale;
import java.util.Set;
import java.util.stream.Stream;

import net.sourceforge.pmd.lang.LanguageVersion;
import net.sourceforge.pmd.lang.java.ast.ASTConstructorCall;
import net.sourceforge.pmd.lang.java.ast.ASTMethodCall;
import net.sourceforge.pmd.lang.java.ast.InvocationNode;
import net.sourceforge.pmd.lang.java.rule.AbstractJavaRulechainRule;
import net.sourceforge.pmd.lang.java.types.InvocationMatcher;
import net.sourceforge.pmd.lang.java.types.JMethodSig;
import net.sourceforge.pmd.lang.java.types.JTypeMirror;
import net.sourceforge.pmd.reporting.RuleContext;

/**
 * Starting with Java 7, StandardCharsets provides constants for common Charset objects, such as UTF-8.
 * Using the constants is less error-prone, and can provide a small performance advantage compared to
 * `Charset.forName(...)` or `String.getBytes(String)` since no scan across the internal `Charset` caches is needed.
 *
 * @since 6.34.0 (as XPath) / 7.25.0 (as Java)
 */
public class UseStandardCharsetsRule extends AbstractJavaRulechainRule {

    private static final InvocationMatcher CHARSET_FOR_NAME = InvocationMatcher.parse("java.nio.charset.Charset#forName(java.lang.String)");
    private static final Set<String> STANDARD_CHARSETS = immutableSetOf("US-ASCII", "ISO-8859-1", "UTF-8", "UTF-16BE", "UTF-16LE", "UTF-16");
    private static final Set<String> STANDARD_CHARSETS_JAVA_22 = immutableSetOf("UTF-32BE", "UTF-32LE", "UTF-32");
    private static final String CHARSET = "java.nio.charset.Charset";

    public UseStandardCharsetsRule() {
        super(ASTConstructorCall.class, ASTMethodCall.class);
    }

    @Override
    public RuleContext visit(ASTMethodCall call, Object data) {
        RuleContext ctx = (RuleContext) data;

        if (CHARSET_FOR_NAME.matchesCall(call)) {
            String callArgument = (String) call.getArguments().get(0).getConstValue();
            if (callArgument != null && standardCharsetExists(call.getLanguageVersion(), callArgument)) {
                ctx.addViolation(call);
            }
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
        if (index >= call.getMethodType().getArity()) {  // this can happen if we're looking at varargs!
            return;
        }

        Object callArgument = call.getArguments().get(index).getConstValue();
        if (callArgument instanceof String) {
            String stringArgument = (String) callArgument;
            if (standardCharsetExists(call.getLanguageVersion(), stringArgument)) {
                JMethodSig callSignature = call.getMethodType();
                Stream<JMethodSig> otherSignatures = streamMethodSignatures(call);
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
    }

    private Stream<JMethodSig> streamMethodSignatures(InvocationNode call) {
        if (call instanceof ASTConstructorCall) {
            return ((ASTConstructorCall) call).getTypeNode().getTypeMirror().getConstructors().stream();
        }
        ASTMethodCall methodCall = (ASTMethodCall) call;
        return methodCall.getMethodType().getDeclaringType().streamMethods(sig -> true);
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

    private boolean standardCharsetExists(LanguageVersion languageVersion, String charset) {
        return STANDARD_CHARSETS.contains(charset.toUpperCase(Locale.ROOT))
                || (languageVersion.compareToVersion("22") >= 0
                        && STANDARD_CHARSETS_JAVA_22.contains(charset.toUpperCase(Locale.ROOT)));
    }
}
