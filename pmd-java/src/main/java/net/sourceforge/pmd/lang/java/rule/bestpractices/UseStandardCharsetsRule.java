/*
 * BSD-style license; for more info see http://pmd.sourceforge.net/license.html
 */

package net.sourceforge.pmd.lang.java.rule.bestpractices;

import java.util.Arrays;
import java.util.HashSet;
import java.util.Set;

import net.sourceforge.pmd.lang.java.ast.ASTConstructorCall;
import net.sourceforge.pmd.lang.java.ast.ASTMethodCall;
import net.sourceforge.pmd.lang.java.rule.AbstractJavaRulechainRule;
import net.sourceforge.pmd.lang.java.types.InvocationMatcher;
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

    public UseStandardCharsetsRule() {
        super(ASTConstructorCall.class, ASTMethodCall.class);
    }

    @Override
    public Object visit(ASTMethodCall call, Object data) {
        if (CHARSET_FOR_NAME.matchesCall(call)
                && STANDARD_CHARSET_EXISTS.contains(call.getArguments().get(0).getConstValue())
        ) {
            ((RuleContext) data).addViolation(call);
        }

        return data;
    }
}
