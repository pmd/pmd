/*
 * BSD-style license; for more info see http://pmd.sourceforge.net/license.html
 */

package net.sourceforge.pmd.lang.java.rule.bestpractices;

import net.sourceforge.pmd.lang.LanguageVersion;
import net.sourceforge.pmd.lang.java.ast.ASTLambdaExpression;
import net.sourceforge.pmd.lang.java.ast.ASTMethodCall;
import net.sourceforge.pmd.lang.java.ast.internal.JavaAstUtils;
import net.sourceforge.pmd.lang.java.rule.AbstractJavaRulechainRule;
import net.sourceforge.pmd.lang.java.types.InvocationMatcher;

/**
 *
 */
public class SimplifiableStreamExpressionRule extends AbstractJavaRulechainRule {


    private static final InvocationMatcher STREAM_COLLECT = InvocationMatcher.parse("java.util.stream.Stream#collect(_)");
    private static final InvocationMatcher STREAM_FOREACH = InvocationMatcher.parse("java.util.stream.Stream#forEach(_)");
    private static final InvocationMatcher STREAM_ANYMATCH = InvocationMatcher.parse("java.util.stream.Stream#anyMatch(_)");
    private static final InvocationMatcher STREAM_NONEMATCH = InvocationMatcher.parse("java.util.stream.Stream#noneMatch(_)");
    private static final InvocationMatcher STREAM_ALLMATCH = InvocationMatcher.parse("java.util.stream.Stream#allMatch(_)");

    private static final InvocationMatcher COLLECTORS_TO_LIST = InvocationMatcher.parse("java.util.stream.Collectors#toList()");

    private static final InvocationMatcher ARRAYS_ASLIST = InvocationMatcher.parse("java.util.Arrays#asList(_*)");
    private static final InvocationMatcher COLLECTION_STREAM = InvocationMatcher.parse("java.util.Collection#stream()");

    public SimplifiableStreamExpressionRule() {
        super(ASTMethodCall.class);
    }

    /// IntelliJ inspection:
    /// - [x] collection.stream().forEach() → collection.forEach()
    /// - [] collection.stream().collect(toList/toSet/toCollection()) → new CollectionType<>(collection)
    /// - [] collection.stream().toArray() → collection.toArray()
    /// - [x] Arrays.asList().stream() → Arrays.stream() or Stream.of()
    /// - [] IntStream.range(0, array.length).mapToObj(idx -> array[idx]) → Arrays.stream(array)
    /// - [] IntStream.range(0, list.size()).mapToObj(idx -> list.get(idx)) → list.stream()
    /// - [] Collections.singleton().stream() → Stream.of()
    /// - [] Collections.emptyList().stream() → Stream.empty()
    /// - [] stream.filter().findFirst().isPresent() → stream.anyMatch()
    /// - [] stream.collect(counting()) → stream.count()
    /// - [] stream.collect(maxBy()) → stream.max()
    /// - [] stream.collect(mapping()) → stream.map().collect()
    /// - [] stream.collect(reducing()) → stream.reduce()
    /// - [] stream.collect(summingInt()) → stream.mapToInt().sum()
    /// - [] stream.mapToObj(x -> x) → stream.boxed()
    /// - [] stream.map(x -> {...; return x;}) → stream.peek(x -> ...)
    /// - [x] !stream.anyMatch() → stream.noneMatch()
    /// - [x] !stream.anyMatch(x -> !(...)) → stream.allMatch()
    /// - [] stream.map().anyMatch(Boolean::booleanValue) → stream.anyMatch()
    /// - [] IntStream.range(expr1, expr2).mapToObj(x -> array[x]) → Arrays.stream(array, expr1, expr2)
    /// - [] Collection.nCopies(count, ...) → Stream.generate().limit(count)
    /// - [] stream.sorted(comparator).findFirst() → Stream.min(comparator)
    /// - [] optional.orElseGet(() -> { throw new ...; }) → optional.orElseThrow()
    @Override
    public Object visit(ASTMethodCall call, Object data) {
        LanguageVersion javaVersion = call.getAstInfo().getLanguageProcessor().getLanguageVersion();

        boolean hasStreamToList = javaVersion.compareToVersion("16") >= 0;
        if (hasStreamToList && STREAM_COLLECT.matchesCall(call)
            && COLLECTORS_TO_LIST.matchesCall(call.getArguments().get(0))) {
            reportCall(call, data, "replace `.collect(Collectors.toList())` with `.toList()`");
            return null;
        }
        if (COLLECTION_STREAM.matchesCall(call) && ARRAYS_ASLIST.matchesCall(call.getQualifier())) {
            reportCall(call, data, "replace `Arrays.asList(..).stream()` with `Arrays.stream(..)` or `Stream.of(..)`");
            return null;
        }
        if (STREAM_FOREACH.matchesCall(call) && COLLECTION_STREAM.matchesCall(call.getQualifier())) {
            reportCall(call, data, "replace `.stream().forEach(..)` with `.forEach(..)`");
            return null;
        }
        if (reportAnyAllNoneMatchMethods(call, data)) {
            return null;
        }

        // todo there are tons more

        return null;
    }

    private boolean reportAnyAllNoneMatchMethods(ASTMethodCall call, Object data) {
        boolean isTerminalNegated = JavaAstUtils.isBooleanNegation(call.getParent());
        boolean isArgNegatedLambda = isArgNegatedLambda(call);
        if (isTerminalNegated && isArgNegatedLambda) {
            if (STREAM_ANYMATCH.matchesCall(call)) {
                reportCall(call, data, "replace `!stream.anyMatch(x -> !(..))` with `stream.allMatch(..)`");
                return true;
            } else if (STREAM_ALLMATCH.matchesCall(call)) {
                reportCall(call, data, "replace `!stream.allMatch(x -> !(..))` with `stream.anyMatch(..)`");
                return true;
            } else if (STREAM_NONEMATCH.matchesCall(call)) {
                reportCall(call, data, "replace `!stream.noneMatch(x -> !(..))` with `!stream.allMatch(..)`");
                return true;
            }
        } else if (isArgNegatedLambda) {
            if (STREAM_ALLMATCH.matchesCall(call)) {
                reportCall(call, data, "replace `stream.allMatch(x -> !(..))` with `stream.noneMatch(..)`");
                return true;
            } else if (STREAM_NONEMATCH.matchesCall(call)) {
                reportCall(call, data, "replace `stream.noneMatch(x -> !(..))` with `stream.allMatch(..)`");
                return true;
            }
        } else if (isTerminalNegated) {
            if (STREAM_ANYMATCH.matchesCall(call)) {
                reportCall(call, data, "replace `!stream.anyMatch(..)` with `stream.noneMatch(..)`");
                return true;
            } else if (STREAM_NONEMATCH.matchesCall(call)) {
                reportCall(call, data, "replace `!stream.noneMatch(..)` with `stream.anyMatch(..)`");
                return true;
            }
        }
        return false;
    }

    private static boolean isArgNegatedLambda(ASTMethodCall call) {
        return call.getArguments().toStream().take(1)
                   .filterIs(ASTLambdaExpression.class)
                   .map(ASTLambdaExpression::getExpressionBody)
                   .filter(JavaAstUtils::isBooleanNegation)
                   .nonEmpty();
    }

    private void reportCall(ASTMethodCall node, Object data, String reason) {
        asCtx(data).at(node.atToken(node.getNameToken()))
                   .warnWithArgs(reason);
    }
}
