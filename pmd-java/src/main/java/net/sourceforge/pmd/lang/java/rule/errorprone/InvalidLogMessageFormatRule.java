/**
 * BSD-style license; for more info see http://pmd.sourceforge.net/license.html
 */

package net.sourceforge.pmd.lang.java.rule.errorprone;

import static net.sourceforge.pmd.util.CollectionUtil.immutableSetOf;

import java.util.OptionalInt;
import java.util.Set;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.checkerframework.checker.nullness.qual.NonNull;
import org.checkerframework.checker.nullness.qual.Nullable;

import net.sourceforge.pmd.lang.java.ast.ASTArgumentList;
import net.sourceforge.pmd.lang.java.ast.ASTArrayAllocation;
import net.sourceforge.pmd.lang.java.ast.ASTAssignableExpr.ASTNamedReferenceExpr;
import net.sourceforge.pmd.lang.java.ast.ASTExpression;
import net.sourceforge.pmd.lang.java.ast.ASTMethodCall;
import net.sourceforge.pmd.lang.java.ast.internal.JavaAstUtils;
import net.sourceforge.pmd.lang.java.rule.AbstractJavaRulechainRule;
import net.sourceforge.pmd.lang.java.rule.internal.DataflowPass;
import net.sourceforge.pmd.lang.java.rule.internal.DataflowPass.AssignmentEntry;
import net.sourceforge.pmd.lang.java.rule.internal.DataflowPass.DataflowResult;
import net.sourceforge.pmd.lang.java.rule.internal.DataflowPass.ReachingDefinitionSet;
import net.sourceforge.pmd.lang.java.types.TypeTestUtil;
import net.sourceforge.pmd.util.CollectionUtil;

public class InvalidLogMessageFormatRule extends AbstractJavaRulechainRule {

    /**
     * Finds placeholder for ParameterizedMessages and format specifiers
     * for StringFormattedMessages.
     */
    private static final Pattern PLACEHOLDER_AND_FORMAT_SPECIFIER =
        Pattern.compile("(\\{})|(%(?:\\d\\$)?(?:\\w+)?(?:\\d+)?(?:\\.\\d+)?\\w)");

    private static final Set<String> SLF4J = immutableSetOf("trace", "debug", "info", "warn", "error");
    private static final Set<String> APACHE_SLF4J = immutableSetOf("trace", "debug", "info", "warn", "error", "fatal", "all");

    /**
     * Whitelisted methods of net.logstash.logback.argument.StructuredArguments
     */
    private static final Set<String> STRUCTURED_ARGUMENTS_METHODS = immutableSetOf(
                    "a", "array", "defer", "e",
                    "entries", "f", "fields", "keyValue",
                    "kv", "r", "raw", "v", "value");

    public InvalidLogMessageFormatRule() {
        super(ASTMethodCall.class);
    }

    @Override
    public Object visit(ASTMethodCall call, Object data) {
        if (isLoggerCall(call, "org.slf4j.Logger", SLF4J)
            || isLoggerCall(call, "org.apache.logging.log4j.Logger", APACHE_SLF4J)) {

            ASTArgumentList args = call.getArguments();
            ASTExpression messageParam = args.toStream().first(it -> TypeTestUtil.isA(String.class, it));
            if (messageParam == null) {
                return null;
            }

            OptionalInt expectedArgs = expectedArguments0(messageParam);
            if (!expectedArgs.isPresent()) {
                // ignore if we couldn't analyze the message parameter
                return null;
            }
            int expectedArguments = expectedArgs.getAsInt();

            int providedArguments = args.size() - (messageParam.getIndexInParent() + 1);

            if (providedArguments == 1 && JavaAstUtils.isArrayInitializer(args.getLastChild())) {
                providedArguments = ((ASTArrayAllocation) args.getLastChild()).getArrayInitializer().length();
            } else if (TypeTestUtil.isA(Throwable.class, args.getLastChild())
                && providedArguments > expectedArguments) {
                // Remove throwable param, since it is shown separately.
                // But only, if it is not used as a placeholder argument
                providedArguments--;
            }
            // remove any logstash structured arguments at the end
            // but only, if there are not enough placeholders
            if (providedArguments > expectedArguments) {
                int removed = removePotentialStructuredArguments(providedArguments - expectedArguments, args);
                providedArguments -= removed;
            }

            if (providedArguments < expectedArguments) {
                addViolationWithMessage(
                    data, call,
                    "Missing arguments," + getExpectedMessage(providedArguments, expectedArguments));
            } else if (providedArguments > expectedArguments) {
                addViolationWithMessage(
                    data, call,
                    "Too many arguments," + getExpectedMessage(providedArguments, expectedArguments));
            }

        }

        return null;
    }

    private boolean isLoggerCall(ASTMethodCall call, String loggerType, Set<String> methodNames) {
        return TypeTestUtil.isA(loggerType, call.getQualifier()) && methodNames.contains(call.getMethodName());
    }

    private static int countPlaceHolders(@NonNull String constValue) {
        int result = 0;
        Matcher matcher = PLACEHOLDER_AND_FORMAT_SPECIFIER.matcher(constValue);
        while (matcher.find()) {
            String format = matcher.group();
            if (!"%%".equals(format) && !"%n".equals(format)) {
                result++;
            }
        }
        return result;
    }

    private static OptionalInt expectedArguments0(final ASTExpression node) {
        if (node.getConstValue() instanceof String) {
            return OptionalInt.of(countPlaceHolders((String) node.getConstValue()));
        } else if (node instanceof ASTNamedReferenceExpr) {
            DataflowResult dataflow = DataflowPass.getDataflowResult(node.getRoot());
            ReachingDefinitionSet reaching = dataflow.getReachingDefinitions((ASTNamedReferenceExpr) node);
            if (reaching.isNotFullyKnown()) {
                return OptionalInt.empty();
            }

            AssignmentEntry assignment = CollectionUtil.asSingle(reaching.getReaching());
            if (assignment == null) {
                return OptionalInt.empty();
            }

            ASTExpression rhs = assignment.getRhsAsExpression();
            if (rhs != null && rhs.getConstValue() instanceof String) {
                return OptionalInt.of(countPlaceHolders((String) rhs.getConstValue()));
            }
        }
        return OptionalInt.empty();
    }

    private String getExpectedMessage(final int providedArguments, final int expectedArguments) {
        return " expected " + expectedArguments
            + (expectedArguments > 1 ? " arguments " : " argument ")
            + "but found " + providedArguments;
    }

    /**
     * Removes up to {@code maxArgumentsToRemove} arguments from the end of the {@code argumentList},
     * if the argument is a method call to one of the whitelisted StructuredArguments methods.
     *
     * @param maxArgumentsToRemove
     * @param argumentList
     */
    private int removePotentialStructuredArguments(int maxArgumentsToRemove, ASTArgumentList argumentList) {
        int removed = 0;
        int lastIndex = argumentList.size() - 1;
        while (argumentList.size() > 0 && removed < maxArgumentsToRemove) {
            ASTExpression argument = argumentList.get(lastIndex);
            if (isStructuredArgumentMethodCall(argument)) {
                removed++;
            } else {
                // stop if something else is encountered
                break;
            }
            lastIndex--;
        }
        return removed;
    }

    private boolean isStructuredArgumentMethodCall(ASTExpression argument) {
        if (argument instanceof ASTMethodCall) {
            ASTMethodCall methodCall = (ASTMethodCall) argument;
            @Nullable
            ASTExpression qualifier = methodCall.getQualifier();
            return TypeTestUtil.isA("net.logstash.logback.argument.StructuredArguments", qualifier)
                    || STRUCTURED_ARGUMENTS_METHODS.contains(methodCall.getMethodName());
        }
        return false;
    }
}
