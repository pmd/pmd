/*
 * BSD-style license; for more info see http://pmd.sourceforge.net/license.html
 */

package net.sourceforge.pmd.lang.kotlin.rule.xpath.internal;

import java.util.HashSet;
import java.util.List;
import java.util.Set;

import org.checkerframework.checker.nullness.qual.Nullable;

import net.sourceforge.pmd.lang.ast.Node;
import net.sourceforge.pmd.lang.rule.xpath.impl.XPathFunctionException;

import nl.stokpop.typemapper.model.CallSiteAst;
import nl.stokpop.typemapper.model.SignatureMatcherKt;

/**
 * XPath function {@code pmd-kotlin:matchesSig(sig)}.
 *
 * <p>Returns {@code true} when the context node corresponds to a call site whose
 * signature matches {@code sig}. The signature format mirrors PMD Java's
 * {@code matchesSig}, but both Java FQCNs and Kotlin FQNs are accepted for
 * receiver and parameter types (e.g. {@code java.lang.String} <-> {@code kotlin.String}).
 *
 * <p>Signature format: {@code [receiverType#]methodName(paramType,...)}
 * <ul>
 *   <li>{@code _} wildcard accepted for receiver and each parameter type</li>
 *   <li>{@code *} accepts any parameter list</li>
 *   <li>{@code <init>} matches constructors</li>
 * </ul>
 *
 * <p>Requires a pre-analyzed {@link KotlinTypeAnalysisContext}; returns {@code false}
 * gracefully if no analysis data is available.
 *
 * <p><b>How type resolution works:</b> Before any rules are evaluated, the Kotlin K1 compiler
 * (via kotlin-type-mapper) analyzes all source files using the aux classpath jars to resolve types
 * and record call site signatures into {@link KotlinTypeAnalysisContext}. At rule evaluation time,
 * {@code matchesSig} only queries that pre-computed data -- no jars are needed then.
 * If a required jar is missing from the classpath, the type will be unresolved and the
 * {@code UnresolvedType} rule will fire as a signal, while {@code matchesSig} returns {@code false}.
 *
 * <p><b>Multi-line chain support:</b> When a {@code PostfixUnaryExpression} spans multiple lines
 * (e.g. a method call split across lines), call sites are restricted to lines where a direct
 * {@code PostfixUnarySuffix} child starts -- covering all chain links while excluding call sites
 * that belong to nested lambda bodies or block arguments. Block-like expressions
 * ({@code try}, {@code when}, etc.) that have no direct {@code PostfixUnarySuffix} children
 * are never matched, preventing false positives from enclosing blocks. Example of a correctly
 * matched multi-line chain:
 * <pre>{@code
 * val expr = xpath
 *     .compile("//book") // call on line N+1 -- correctly matched
 * }</pre>
 *
 * <p>Example XPath:
 * <pre>{@code
 * //PostfixUnaryExpression[pmd-kotlin:matchesSig('java.util.regex.Pattern#matches(java.lang.String,java.lang.CharSequence)')]
 * //PostfixUnaryExpression[pmd-kotlin:matchesSig('java.util.regex.Pattern#compile(_)')]
 * }</pre>
 */
public final class KotlinMatchesSigFunction extends BaseKotlinXPathFunction {

    public static final KotlinMatchesSigFunction INSTANCE = new KotlinMatchesSigFunction();

    private KotlinMatchesSigFunction() {
        super("matchesSig");
    }

    @Override
    public Type[] getArgumentTypes() {
        return new Type[]{Type.SINGLE_STRING};
    }

    @Override
    public Type getResultType() {
        return Type.SINGLE_BOOLEAN;
    }

    @Override
    public boolean dependsOnContext() {
        return true;
    }

    @Override
    public FunctionCall makeCallExpression() {
        return new MatchesSigFunctionCall();
    }

    private static final class MatchesSigFunctionCall implements FunctionCall {
        @Override
        public void staticInit(Object[] arguments) throws XPathFunctionException {
            try {
                SignatureMatcherKt.parseSig((String) arguments[0]);
            } catch (IllegalArgumentException e) {
                throw new XPathFunctionException(
                        "Invalid matchesSig argument: " + e.getMessage(), e);
            }
        }

        @Override
        public Object call(@Nullable Node contextNode, Object[] arguments) throws XPathFunctionException {
            if (contextNode == null) {
                return false;
            }
            if (!"PostfixUnaryExpression".equals(contextNode.getXPathNodeName())) {
                return false;
            }
            String sig = (String) arguments[0];
            String absPath = contextNode.getTextDocument().getFileId().getAbsolutePath();
            int beginLine = contextNode.getBeginLine();
            int endLine = contextNode.getEndLine();
            int beginCol = contextNode.getBeginColumn();
            int endCol = contextNode.getEndColumn();
            boolean singleLine = endLine == beginLine;

            KotlinTypeAnalysisContext ctx = KotlinTypeAnalysisContextHolder.get();
            List<CallSiteAst> sites = ctx.callSitesInRange(absPath, beginLine, endLine);

            // For multi-line nodes, restrict to call sites on lines where direct
            // PostfixUnarySuffix children begin.  This prevents enclosing blocks
            // (try {}, synchronized {}, lambda bodies) from collecting inner call
            // sites that belong to nested PostfixUnaryExpression children.
            Set<Integer> suffixBeginLines = null;
            if (!singleLine) {
                suffixBeginLines = new HashSet<>();
                for (Node child : contextNode.children()) {
                    if ("PostfixUnarySuffix".equals(child.getXPathNodeName())) {
                        suffixBeginLines.add(child.getBeginLine());
                    }
                }
                if (suffixBeginLines.isEmpty()) {
                    // No direct PostfixUnarySuffix children: this node is a block-like
                    // expression (try, when, ...), not a direct method-call chain.
                    return false;
                }
            }

            for (CallSiteAst call : sites) {
                boolean callSiteMatch = matchesCallSite(call, beginCol, endCol,
                        singleLine, suffixBeginLines);
                boolean sigMatch = callSiteMatch && SignatureMatcherKt.matchesSigPolymorphic(call, sig, ctx::isSubtypeOf);
                if (sigMatch) {
                    return true;
                }
            }
            return false;
        }

        private static boolean matchesCallSite(CallSiteAst call,
                                               int beginCol, int endCol,
                                               boolean singleLine,
                                               @Nullable Set<Integer> suffixBeginLines) {
            if (singleLine) {
                // Single-line: filter by column range to distinguish multiple calls on the same line
                int col = call.getColumn();
                return col >= beginCol && col <= endCol;
            }
            // Multi-line: only accept call sites on lines where a direct PostfixUnarySuffix starts.
            // This constrains matching to the chain links of this expression and avoids picking up
            // call sites that are deeply nested inside lambda/block arguments.
            return suffixBeginLines != null && suffixBeginLines.contains(call.getLine());
        }
    }
}
