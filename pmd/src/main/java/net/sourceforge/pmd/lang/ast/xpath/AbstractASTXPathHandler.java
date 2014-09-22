/**
 * BSD-style license; for more info see http://pmd.sourceforge.net/license.html
 */
package net.sourceforge.pmd.lang.ast.xpath;

import net.sf.saxon.sxpath.IndependentContext;
import net.sourceforge.pmd.lang.Language;
import net.sourceforge.pmd.lang.XPathHandler;

import org.jaxen.Navigator;

public abstract class AbstractASTXPathHandler implements XPathHandler {

    public Navigator getNavigator() {
	return new DocumentNavigator();
    }

    public void initialize(IndependentContext context, Language language, Class<?> functionsClass) {
	context.declareNamespace("pmd-" + language.getTerseName(), "java:" + functionsClass.getName());
    }
}
