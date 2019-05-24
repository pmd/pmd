package net.sourceforge.pmd.lang.swift;

import java.io.Writer;

import net.sourceforge.pmd.lang.*;
import net.sourceforge.pmd.lang.antlr.AntlrRuleViolationFactory;
import net.sourceforge.pmd.lang.ast.Node;
import net.sourceforge.pmd.lang.ast.xpath.DefaultASTXPathHandler;
import net.sourceforge.pmd.lang.rule.RuleViolationFactory;

public class SwiftHandler extends AbstractPmdLanguageVersionHandler {

    @Override
    public XPathHandler getXPathHandler() {
        return new DefaultASTXPathHandler();
    }

    @Override
    public RuleViolationFactory getRuleViolationFactory() {
        return AntlrRuleViolationFactory.INSTANCE;
    }

    @Override
    public Parser getParser(final ParserOptions parserOptions) {
        return new SwiftParserAdapter(parserOptions);
    }

    @Override
    public VisitorStarter getDumpFacade(final Writer writer, final String prefix, final boolean recurse) {
        return new VisitorStarter() {
            @Override
            public void start(final Node rootNode) {
                // TODO: implement dump for AntlrBaseNode
            }
        };
    }
}