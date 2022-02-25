/**
 * BSD-style license; for more info see http://pmd.sourceforge.net/license.html
 */

package net.sourceforge.pmd.lang.apex.rule.bestpractices;

import static net.sourceforge.pmd.properties.PropertyFactory.stringProperty;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Locale;
import java.util.Optional;
import java.util.Set;
import java.util.regex.Pattern;

import org.apache.commons.lang3.StringUtils;

import net.sourceforge.pmd.lang.apex.ast.ASTBlockStatement;
import net.sourceforge.pmd.lang.apex.ast.ASTMethod;
import net.sourceforge.pmd.lang.apex.ast.ASTMethodCallExpression;
import net.sourceforge.pmd.lang.apex.ast.ASTStatement;
import net.sourceforge.pmd.lang.apex.ast.ApexNode;
import net.sourceforge.pmd.lang.apex.rule.AbstractApexUnitTestRule;
import net.sourceforge.pmd.properties.PropertyDescriptor;

/**
 * Apex unit tests should have System.assert methods in them
 *
 * @author a.subramanian
 */
public class ApexUnitTestClassShouldHaveAssertsRule extends AbstractApexUnitTestRule {

    private static final Set<String> ASSERT_METHODS = new HashSet<>();

    static {
        ASSERT_METHODS.add("system.assert");
        ASSERT_METHODS.add("system.assertequals");
        ASSERT_METHODS.add("system.assertnotequals");
        // Fully-qualified variants...rare but still valid/possible
        ASSERT_METHODS.add("system.system.assert");
        ASSERT_METHODS.add("system.system.assertequals");
        ASSERT_METHODS.add("system.system.assertnotequals");
    }

    // Using a string property instead of a regex property to ensure that the compiled pattern can be case-insensitive
    private static final PropertyDescriptor<String> ADDITIONAL_ASSERT_METHOD_PATTERN_DESCRIPTOR =
            stringProperty("additionalAssertMethodPattern")
                    .desc("A regular expression for one or more custom test assertion method patterns.").defaultValue("").build();

    // A simple compiled pattern cache to ensure that we only ever try to compile the configured pattern once for a given run
    private Optional<Pattern> compiledAdditionalAssertMethodPattern = null;

    public ApexUnitTestClassShouldHaveAssertsRule() {
        definePropertyDescriptor(ADDITIONAL_ASSERT_METHOD_PATTERN_DESCRIPTOR);
    }

    @Override
    public Object visit(ASTMethod node, Object data) {
        if (!isTestMethodOrClass(node)) {
            return data;
        }

        return checkForAssertStatements(node, data);
    }

    private Object checkForAssertStatements(ApexNode<?> node, Object data) {
        final List<ASTBlockStatement> blockStatements = node.findDescendantsOfType(ASTBlockStatement.class);
        final List<ASTStatement> statements = new ArrayList<>();
        final List<ASTMethodCallExpression> methodCalls = new ArrayList<>();
        for (ASTBlockStatement blockStatement : blockStatements) {
            statements.addAll(blockStatement.findDescendantsOfType(ASTStatement.class));
            methodCalls.addAll(blockStatement.findDescendantsOfType(ASTMethodCallExpression.class));
        }
        boolean isAssertFound = false;

        for (final ASTMethodCallExpression methodCallExpression : methodCalls) {
            if (ASSERT_METHODS.contains(methodCallExpression.getFullMethodName().toLowerCase(Locale.ROOT))) {
                isAssertFound = true;
                break;
            }
        }

        // If we didn't find assert method invocations the simple way and we have a configured pattern, try it
        if (!isAssertFound) {
            final String additionalAssertMethodPattern = getProperty(ADDITIONAL_ASSERT_METHOD_PATTERN_DESCRIPTOR);
            final Pattern compiledPattern = getCompiledAdditionalAssertMethodPattern(additionalAssertMethodPattern);
            if (compiledPattern != null) {
                for (final ASTMethodCallExpression methodCallExpression : methodCalls) {
                    final String fullMethodName = methodCallExpression.getFullMethodName();
                    if (compiledPattern.matcher(fullMethodName).matches()) {
                        isAssertFound = true;
                        break;
                    }
                }
            }
        }

        if (!isAssertFound) {
            addViolation(data, node);
        }

        return data;
    }

    private Pattern getCompiledAdditionalAssertMethodPattern(String additionalAssertMethodPattern) {
        if (StringUtils.isNotBlank(additionalAssertMethodPattern)) {
            // Check for presence first since we will cache a null value for patterns that don't compile
            if (compiledAdditionalAssertMethodPattern == null) {
                try {
                    compiledAdditionalAssertMethodPattern = Optional.of(Pattern.compile(additionalAssertMethodPattern, Pattern.CASE_INSENSITIVE));
                } catch (IllegalArgumentException e) {
                    // Cache a null compiled pattern so that we won't try to compile this one again during the run
                    compiledAdditionalAssertMethodPattern = Optional.ofNullable(null);
                    throw e;
                }
            }
        }

        return compiledAdditionalAssertMethodPattern != null ? compiledAdditionalAssertMethodPattern.get() : null;
    }
}
