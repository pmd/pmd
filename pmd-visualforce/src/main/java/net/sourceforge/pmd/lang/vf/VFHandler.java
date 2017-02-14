/**
 * BSD-style license; for more info see http://pmd.sourceforge.net/license.html
 */

package net.sourceforge.pmd.lang.vf;

import java.io.Writer;

import net.sourceforge.pmd.lang.AbstractLanguageVersionHandler;
import net.sourceforge.pmd.lang.Parser;
import net.sourceforge.pmd.lang.ParserOptions;
import net.sourceforge.pmd.lang.VisitorStarter;
import net.sourceforge.pmd.lang.XPathHandler;
import net.sourceforge.pmd.lang.ast.Node;
import net.sourceforge.pmd.lang.ast.xpath.AbstractASTXPathHandler;
import net.sourceforge.pmd.lang.rule.RuleViolationFactory;
import net.sourceforge.pmd.lang.vf.ast.DumpFacade;
import net.sourceforge.pmd.lang.vf.ast.VfNode;
import net.sourceforge.pmd.lang.vf.rule.VFRuleViolationFactory;

import net.sf.saxon.sxpath.IndependentContext;

public class VFHandler extends AbstractLanguageVersionHandler {

    @Override
    public XPathHandler getXPathHandler() {
        return new AbstractASTXPathHandler() {
            public void initialize() {
            }

            public void initialize(IndependentContext context) {
            }
        };
    }

    public RuleViolationFactory getRuleViolationFactory() {
        return VFRuleViolationFactory.INSTANCE;
    }

    public Parser getParser(ParserOptions parserOptions) {
        return new VFParser(parserOptions);
    }

    @Override
    public VisitorStarter getDumpFacade(final Writer writer, final String prefix, final boolean recurse) {
        return new VisitorStarter() {
            public void start(Node rootNode) {
                new DumpFacade().initializeWith(writer, prefix, recurse, (VfNode) rootNode);
            }
        };
    }
}
