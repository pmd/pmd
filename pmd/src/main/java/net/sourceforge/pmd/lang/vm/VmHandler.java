/**
 * BSD-style license; for more info see http://pmd.sourceforge.net/license.html
 */
package net.sourceforge.pmd.lang.vm;

import java.io.Writer;

import net.sf.saxon.sxpath.IndependentContext;
import net.sourceforge.pmd.lang.AbstractLanguageVersionHandler;
import net.sourceforge.pmd.lang.Parser;
import net.sourceforge.pmd.lang.ParserOptions;
import net.sourceforge.pmd.lang.VisitorStarter;
import net.sourceforge.pmd.lang.XPathHandler;
import net.sourceforge.pmd.lang.ast.Node;
import net.sourceforge.pmd.lang.ast.xpath.AbstractASTXPathHandler;
import net.sourceforge.pmd.lang.rule.RuleViolationFactory;
import net.sourceforge.pmd.lang.vm.ast.AbstractVmNode;
import net.sourceforge.pmd.lang.vm.rule.VmRuleViolationFactory;

/**
 * Implementation of LanguageVersionHandler for the VM parser.
 * 
 */
public class VmHandler extends AbstractLanguageVersionHandler {

    @Override
    public XPathHandler getXPathHandler() {
        return new AbstractASTXPathHandler() {
            public void initialize() {
            }

            public void initialize(final IndependentContext context) {
            }
        };
    }

    public RuleViolationFactory getRuleViolationFactory() {
        return VmRuleViolationFactory.INSTANCE;
    }

    public Parser getParser(final ParserOptions parserOptions) {
        return new VmParser(parserOptions);
    }

    @Override
    public VisitorStarter getDumpFacade(final Writer writer, final String prefix, final boolean recurse) {
        return new VisitorStarter() {
            public void start(final Node rootNode) {
            	((AbstractVmNode) rootNode).dump(prefix, recurse, writer);
            }
        };
    }
}
