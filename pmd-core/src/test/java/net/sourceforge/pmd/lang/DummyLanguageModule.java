/**
 * BSD-style license; for more info see http://pmd.sourceforge.net/license.html
 */

package net.sourceforge.pmd.lang;

import java.io.Reader;

import net.sourceforge.pmd.Rule;
import net.sourceforge.pmd.RuleContext;
import net.sourceforge.pmd.RuleViolation;
import net.sourceforge.pmd.lang.ast.DummyAstStages;
import net.sourceforge.pmd.lang.ast.DummyRoot;
import net.sourceforge.pmd.lang.ast.Node;
import net.sourceforge.pmd.lang.ast.ParseException;
import net.sourceforge.pmd.lang.rule.ParametricRuleViolation;
import net.sourceforge.pmd.lang.rule.impl.DefaultRuleViolationFactory;

/**
 * Dummy language used for testing PMD.
 */
public class DummyLanguageModule extends BaseLanguageModule {

    public static final String NAME = "Dummy";
    public static final String TERSE_NAME = "dummy";

    public DummyLanguageModule() {
        super(NAME, null, TERSE_NAME, "dummy");
        addVersion("1.0", new Handler());
        addVersion("1.1", new Handler());
        addVersion("1.2", new Handler());
        addVersion("1.3", new Handler());
        addVersion("1.4", new Handler());
        addVersion("1.5", new Handler(), "5");
        addVersion("1.6", new Handler(), "6");
        addDefaultVersion("1.7", new Handler(), "7");
        addVersion("1.8", new Handler(), "8");
    }

    public static class Handler extends AbstractPmdLanguageVersionHandler {
        public Handler() {
            super(DummyAstStages.class);
        }

        @Override
        public RuleViolationFactory getRuleViolationFactory() {
            return new RuleViolationFactory();
        }


        @Override
        public Parser getParser(ParserOptions parserOptions) {
            return new AbstractParser(parserOptions) {
                @Override
                public DummyRoot parse(String fileName, Reader source) throws ParseException {
                    DummyRoot node = new DummyRoot();
                    node.setCoords(1, 1, 2, 10);
                    node.setImage("Foo");
                    return node;
                }

            };
        }
    }

    public static class RuleViolationFactory extends DefaultRuleViolationFactory {
        @Override
        protected RuleViolation createRuleViolation(Rule rule, RuleContext ruleContext, Node node, String message) {
            return createRuleViolation(rule, ruleContext, node, message, 0, 0);
        }

        @Override
        protected RuleViolation createRuleViolation(Rule rule, RuleContext ruleContext, Node node, String message,
                int beginLine, int endLine) {
            ParametricRuleViolation<Node> rv = new ParametricRuleViolation<Node>(rule, ruleContext, node, message) {
                @Override
                public String getPackageName() {
                    this.packageName = "foo"; // just for testing variable expansion
                    return super.getPackageName();
                }
            };
            rv.setLines(beginLine, endLine);
            return rv;
        }
    }
}
