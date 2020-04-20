/**
 * BSD-style license; for more info see http://pmd.sourceforge.net/license.html
 */

package net.sourceforge.pmd.lang.modelica;

import net.sourceforge.pmd.lang.AbstractLanguageVersionHandler;
import net.sourceforge.pmd.lang.Parser;
import net.sourceforge.pmd.lang.ParserOptions;
import net.sourceforge.pmd.lang.VisitorStarter;
import net.sourceforge.pmd.lang.ast.Node;
import net.sourceforge.pmd.lang.modelica.ast.ASTStoredDefinition;
import net.sourceforge.pmd.lang.modelica.resolver.ModelicaSymbolFacade;
import net.sourceforge.pmd.lang.modelica.rule.ModelicaRuleViolationFactory;
import net.sourceforge.pmd.lang.rule.RuleViolationFactory;

public class ModelicaHandler extends AbstractLanguageVersionHandler {

    @Override
    public RuleViolationFactory getRuleViolationFactory() {
        return ModelicaRuleViolationFactory.INSTANCE;
    }

    @Override
    public Parser getParser(ParserOptions parserOptions) {
        return new ModelicaParser(parserOptions);
    }

    @Override
    public VisitorStarter getSymbolFacade() {
        return new VisitorStarter() {
            @Override
            public void start(Node rootNode) {
                new ModelicaSymbolFacade().initializeWith((ASTStoredDefinition) rootNode);
            }
        };
    }

    @Override
    public VisitorStarter getSymbolFacade(ClassLoader classLoader) {
        return getSymbolFacade();
    }
}
