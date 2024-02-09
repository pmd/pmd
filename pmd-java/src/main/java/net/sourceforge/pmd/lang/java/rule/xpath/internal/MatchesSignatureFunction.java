/*
 * BSD-style license; for more info see http://pmd.sourceforge.net/license.html
 */

package net.sourceforge.pmd.lang.java.rule.xpath.internal;

import net.sourceforge.pmd.lang.java.ast.InvocationNode;
import net.sourceforge.pmd.lang.java.types.InvocationMatcher;
import net.sourceforge.pmd.lang.rule.xpath.impl.XPathFunctionException;

public final class MatchesSignatureFunction extends BaseRewrittenFunction<InvocationMatcher, InvocationNode> {

    public static final MatchesSignatureFunction INSTANCE = new MatchesSignatureFunction();

    private MatchesSignatureFunction() {
        super("matchesSig", InvocationNode.class);
    }

    @Override
    protected boolean matches(InvocationNode contextNode, String arg, InvocationMatcher parsedArg, boolean isConstant) {
        return parsedArg.matchesCall(contextNode);
    }

    @Override
    protected InvocationMatcher parseArgument(String arg) throws XPathFunctionException {
        try {
            return InvocationMatcher.parse(arg);
        } catch (IllegalArgumentException e) {
            throw new XPathFunctionException("Could not parse arg " + arg, e);
        }
    }

}
