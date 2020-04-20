/*
 * BSD-style license; for more info see http://pmd.sourceforge.net/license.html
 */

package net.sourceforge.pmd.lang.vm;

import java.io.Writer;

import net.sourceforge.pmd.lang.AbstractLanguageVersionHandler;
import net.sourceforge.pmd.lang.Parser;
import net.sourceforge.pmd.lang.ParserOptions;
import net.sourceforge.pmd.lang.VisitorStarter;
import net.sourceforge.pmd.lang.ast.Node;
import net.sourceforge.pmd.lang.rule.RuleViolationFactory;
import net.sourceforge.pmd.lang.vm.ast.AbstractVmNode;
import net.sourceforge.pmd.lang.vm.rule.VmRuleViolationFactory;

/**
 * Implementation of LanguageVersionHandler for the VM parser.
 *
 */
public class VmHandler extends AbstractLanguageVersionHandler {

    @Override
    public RuleViolationFactory getRuleViolationFactory() {
        return VmRuleViolationFactory.INSTANCE;
    }

    @Override
    public Parser getParser(final ParserOptions parserOptions) {
        return new VmParser(parserOptions);
    }

    @Deprecated
    @Override
    public VisitorStarter getDumpFacade(final Writer writer, final String prefix, final boolean recurse) {
        return new VisitorStarter() {
            @Override
            public void start(final Node rootNode) {
                ((AbstractVmNode) rootNode).dump(prefix, recurse, writer);
            }
        };
    }
}
