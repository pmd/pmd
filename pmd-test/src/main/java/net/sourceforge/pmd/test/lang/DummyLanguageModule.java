/**
 * BSD-style license; for more info see http://pmd.sourceforge.net/license.html
 */

package net.sourceforge.pmd.test.lang;

import java.io.Reader;

import org.checkerframework.checker.nullness.qual.NonNull;

import net.sourceforge.pmd.Rule;
import net.sourceforge.pmd.RuleViolation;
import net.sourceforge.pmd.lang.AbstractParser;
import net.sourceforge.pmd.lang.AbstractPmdLanguageVersionHandler;
import net.sourceforge.pmd.lang.BaseLanguageModule;
import net.sourceforge.pmd.lang.Parser;
import net.sourceforge.pmd.lang.ParserOptions;
import net.sourceforge.pmd.lang.ast.Node;
import net.sourceforge.pmd.lang.ast.ParseException;
import net.sourceforge.pmd.lang.ast.RootNode;
import net.sourceforge.pmd.lang.rule.ParametricRuleViolation;
import net.sourceforge.pmd.lang.rule.impl.DefaultRuleViolationFactory;
import net.sourceforge.pmd.test.lang.ast.DummyNode;

/**
 * Dummy language used for testing PMD.
 */
public class DummyLanguageModule extends BaseLanguageModule {

    public static final String NAME = "Dummy";
    public static final String TERSE_NAME = "dummy";

    public DummyLanguageModule() {
        super(NAME, null, TERSE_NAME, "dummy");
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

    public static class Handler extends AbstractPmdLanguageVersionHandler {
        @Override
        public RuleViolationFactory getRuleViolationFactory() {
            return new RuleViolationFactory();
        }

        @Override
        public Parser getParser(ParserOptions parserOptions) {
            return new AbstractParser(parserOptions) {
                @Override
                public DummyRootNode parse(String fileName, Reader source) throws ParseException {
                    DummyRootNode node = new DummyRootNode();
                    node.setCoords(1, 1, 1, 2);
                    node.setImage("Foo");
                    return node;
                }

            };
        }
    }

    public static class DummyRootNode extends DummyNode implements RootNode {

    }


    public static class RuleViolationFactory extends DefaultRuleViolationFactory {
        @Override
        public RuleViolation createViolation(Rule rule, @NonNull Node location, @NonNull String filename, @NonNull String formattedMessage) {
            return new ParametricRuleViolation<Node>(rule, filename, location, formattedMessage) {
                @Override
                public String getPackageName() {
                    this.packageName = "foo"; // just for testing variable expansion
                    return super.getPackageName();
                }
            };
        }
    }
}
