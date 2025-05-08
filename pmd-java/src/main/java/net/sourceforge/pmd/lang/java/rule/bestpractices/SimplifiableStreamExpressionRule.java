/*
 * BSD-style license; for more info see http://pmd.sourceforge.net/license.html
 */

package net.sourceforge.pmd.lang.java.rule.bestpractices;

import net.sourceforge.pmd.lang.LanguageVersion;
import net.sourceforge.pmd.lang.java.ast.ASTMethodCall;
import net.sourceforge.pmd.lang.java.rule.AbstractJavaRulechainRule;
import net.sourceforge.pmd.lang.java.types.InvocationMatcher;

/**
 *
 */
public class SimplifiableStreamExpressionRule extends AbstractJavaRulechainRule {


    private static final InvocationMatcher STREAM_COLLECT = InvocationMatcher.parse("java.util.stream.Stream#collect(_)");
    private static final InvocationMatcher COLLECTORS_TO_LIST = InvocationMatcher.parse("java.util.stream.Collectors#toList()");

    private static final InvocationMatcher ARRAYS_ASLIST = InvocationMatcher.parse("java.util.Arrays#asList(_*)");
    private static final InvocationMatcher COLLECTION_STREAM = InvocationMatcher.parse("java.util.Collection#stream()");

    public SimplifiableStreamExpressionRule() {
        super(ASTMethodCall.class);
    }

    @Override
    public Object visit(ASTMethodCall node, Object data) {
        LanguageVersion javaVersion = node.getAstInfo().getLanguageProcessor().getLanguageVersion();

        boolean hasStreamToList = javaVersion.compareToVersion("16") >= 0;
        if (hasStreamToList && STREAM_COLLECT.matchesCall(node) && COLLECTORS_TO_LIST.matchesCall(node.getArguments().get(0))) {
            asCtx(data).addViolation(node, "replace `.collect(Collectors.toList())` with `.toList()`");
            return null;
        }
        if (COLLECTION_STREAM.matchesCall(node) && ARRAYS_ASLIST.matchesCall(node.getQualifier())) {
            asCtx(data).addViolation(node, "replace `Arrays.asList(..).stream()` with `Arrays.stream(..)` or `Stream.of(..)`");
            return null;
        }

        // todo there are tons more

        return null;
    }
}
