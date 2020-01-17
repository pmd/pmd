/**
 * BSD-style license; for more info see http://pmd.sourceforge.net/license.html
 */

package net.sourceforge.pmd.test.lang;

import java.io.Reader;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.Map;

import net.sourceforge.pmd.Rule;
import net.sourceforge.pmd.RuleContext;
import net.sourceforge.pmd.RuleViolation;
import net.sourceforge.pmd.lang.AbstractLanguageVersionHandler;
import net.sourceforge.pmd.lang.AbstractParser;
import net.sourceforge.pmd.lang.BaseLanguageModule;
import net.sourceforge.pmd.lang.Parser;
import net.sourceforge.pmd.lang.ParserOptions;
import net.sourceforge.pmd.lang.TokenManager;
import net.sourceforge.pmd.lang.ast.Node;
import net.sourceforge.pmd.lang.ast.ParseException;
import net.sourceforge.pmd.lang.rule.AbstractRuleChainVisitor;
import net.sourceforge.pmd.lang.rule.AbstractRuleViolationFactory;
import net.sourceforge.pmd.lang.rule.ParametricRuleViolation;
import net.sourceforge.pmd.test.lang.ast.DummyNode;

/**
 * Dummy language used for testing PMD.
 */
public class DummyLanguageModule extends BaseLanguageModule {

    public static final String NAME = "Dummy";
    public static final String TERSE_NAME = "dummy";

    public DummyLanguageModule() {
        super(NAME, null, TERSE_NAME, DummyRuleChainVisitor.class, "dummy");
        addVersion("1.0", new Handler(), false);
        addVersion("1.1", new Handler(), false);
        addVersion("1.2", new Handler(), false);
        addVersion("1.3", new Handler(), false);
        addVersion("1.4", new Handler(), false);
        addVersion("1.5", new Handler(), false);
        addVersion("1.6", new Handler(), false);
        addVersion("1.7", new Handler(), true);
        addVersion("1.8", new Handler(), false);
    }

    public static class DummyRuleChainVisitor extends AbstractRuleChainVisitor {
        @Override
        protected void visit(Rule rule, Node node, RuleContext ctx) {
            rule.apply(Arrays.asList(node), ctx);
        }

        @Override
        protected void indexNodes(List<Node> nodes, RuleContext ctx) {
            for (Node n : nodes) {
                indexNode(n);
                List<Node> childs = new ArrayList<>();
                for (int i = 0; i < n.getNumChildren(); i++) {
                    childs.add(n.getChild(i));
                }
                indexNodes(childs, ctx);
            }
        }
    }

    public static class Handler extends AbstractLanguageVersionHandler {
        @Override
        public RuleViolationFactory getRuleViolationFactory() {
            return new RuleViolationFactory();
        }

        @Override
        public Parser getParser(ParserOptions parserOptions) {
            return new AbstractParser(parserOptions) {
                @Override
                public Node parse(String fileName, Reader source) throws ParseException {
                    DummyNode node = new DummyNode(1);
                    node.testingOnlySetBeginLine(1);
                    node.testingOnlySetBeginColumn(1);
                    node.setImage("Foo");
                    return node;
                }

                @Override
                public Map<Integer, String> getSuppressMap() {
                    return Collections.emptyMap();
                }

                @Override
                public boolean canParse() {
                    return true;
                }

                @Override
                protected TokenManager createTokenManager(Reader source) {
                    return null;
                }
            };
        }
    }

    public static class RuleViolationFactory extends AbstractRuleViolationFactory {
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
