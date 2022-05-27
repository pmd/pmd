/*
 * BSD-style license; for more info see http://pmd.sourceforge.net/license.html
 */


package net.sourceforge.pmd.lang.html;

import net.sourceforge.pmd.Rule;
import net.sourceforge.pmd.RuleContext;
import net.sourceforge.pmd.RuleViolation;
import net.sourceforge.pmd.lang.AbstractLanguageVersionHandler;
import net.sourceforge.pmd.lang.Parser;
import net.sourceforge.pmd.lang.ParserOptions;
import net.sourceforge.pmd.lang.ast.Node;
import net.sourceforge.pmd.lang.html.ast.HtmlParser;
import net.sourceforge.pmd.lang.rule.AbstractRuleViolationFactory;
import net.sourceforge.pmd.lang.rule.ParametricRuleViolation;
import net.sourceforge.pmd.lang.rule.RuleViolationFactory;

class HtmlHandler extends AbstractLanguageVersionHandler {

    @Override
    public RuleViolationFactory getRuleViolationFactory() {
        return new AbstractRuleViolationFactory() {

            @Override
            protected RuleViolation createRuleViolation(Rule rule, RuleContext ruleContext, Node node, String message) {
                return new ParametricRuleViolation<Node>(rule, ruleContext, node, message);
            }

            @Override
            protected RuleViolation createRuleViolation(Rule rule, RuleContext ruleContext, Node node, String message,
                    int beginLine, int endLine) {
                ParametricRuleViolation<Node> ruleViolation = new ParametricRuleViolation<>(rule, ruleContext, node, message);
                ruleViolation.setLines(beginLine, endLine);
                return ruleViolation;
            }
            
        };
    }

    @Override
    public Parser getParser(ParserOptions parserOptions) {
        return new HtmlParser();
    }

}
