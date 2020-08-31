/**
 * BSD-style license; for more info see http://pmd.sourceforge.net/license.html
 */

package net.sourceforge.pmd.test.lang;

import org.checkerframework.checker.nullness.qual.NonNull;

import net.sourceforge.pmd.lang.AbstractPmdLanguageVersionHandler;
import net.sourceforge.pmd.lang.BaseLanguageModule;
import net.sourceforge.pmd.lang.LanguageVersion;
import net.sourceforge.pmd.lang.Parser;
import net.sourceforge.pmd.lang.ParserOptions;
import net.sourceforge.pmd.lang.ast.RootNode;
import net.sourceforge.pmd.lang.rule.impl.DefaultRuleViolationFactory;
import net.sourceforge.pmd.test.lang.ast.DummyNode;
import net.sourceforge.pmd.util.document.TextDocument;

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
            return task -> {
                DummyRootNode node = new DummyRootNode();
                node.setCoords(1, 1, 1, 2);
                node.setLanguageVersion(task.getLanguageVersion());
                node.setImage("Foo");
                return node;
            };
        }
    }

    public static class DummyRootNode extends DummyNode implements RootNode {

        @Override
        public @NonNull TextDocument getTextDocument() {
            return TextDocument.readOnlyString("dummy text", languageVersion);
        }

        private LanguageVersion languageVersion;

        public DummyRootNode setLanguageVersion(LanguageVersion languageVersion) {
            this.languageVersion = languageVersion;
            return this;
        }
    }


    public static class RuleViolationFactory extends DefaultRuleViolationFactory {

    }
}
