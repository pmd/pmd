/**
 * BSD-style license; for more info see http://pmd.sourceforge.net/license.html
 */

package net.sourceforge.pmd.lang.ast.xpath;

import net.sf.saxon.sxpath.IndependentContext;

public class DefaultASTXPathHandler extends AbstractASTXPathHandler {
    @Override
    public void initialize() {
        // override if needed
    }

    @Override
    public void initialize(IndependentContext context) {
        // override if needed
    }
}
