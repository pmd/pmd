/**
 * BSD-style license; for more info see http://pmd.sourceforge.net/license.html
 */

package net.sourceforge.pmd.lang.ast.xpath;

import net.sourceforge.pmd.Rule;
import net.sourceforge.pmd.annotation.InternalApi;
import net.sourceforge.pmd.lang.rule.XPathRule;
import net.sourceforge.pmd.lang.rule.xpath.XPathVersion;

import net.sf.saxon.sxpath.IndependentContext;


@Deprecated
@InternalApi
public class DefaultASTXPathHandler extends AbstractASTXPathHandler {

    @Override
    public Rule newXPathRule(XPathVersion version, String xpathExpression) {
        return new XPathRule(version, xpathExpression);
    }

    @Override
    public void initialize() {
        // override if needed
    }

    @Override
    public void initialize(IndependentContext context) {
        // override if needed
    }
}
