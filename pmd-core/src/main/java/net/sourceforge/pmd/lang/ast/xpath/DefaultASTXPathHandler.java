/**
 * BSD-style license; for more info see http://pmd.sourceforge.net/license.html
 */

package net.sourceforge.pmd.lang.ast.xpath;

import net.sourceforge.pmd.annotation.InternalApi;
import net.sourceforge.pmd.lang.ast.xpath.internal.CoreXPathFunctions;
import net.sourceforge.pmd.lang.ast.xpath.internal.FileNameXPathFunction;

import net.sf.saxon.sxpath.IndependentContext;


@Deprecated
@InternalApi
public class DefaultASTXPathHandler extends AbstractASTXPathHandler {

    @Override
    public void initialize() {
        FileNameXPathFunction.registerSelfInSimpleContext();
    }

    @Override
    public void initialize(IndependentContext context) {
        context.declareNamespace("pmd", "java:" + CoreXPathFunctions.class.getName());
    }

}
