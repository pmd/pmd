/**
 * BSD-style license; for more info see http://pmd.sourceforge.net/license.html
 */

package net.sourceforge.pmd.test.lang;

import net.sourceforge.pmd.lang.AbstractPmdLanguageVersionHandler;
import net.sourceforge.pmd.lang.BaseLanguageModule;
import net.sourceforge.pmd.lang.LanguageRegistry;
import net.sourceforge.pmd.lang.LanguageVersion;
import net.sourceforge.pmd.lang.ast.AstInfo;
import net.sourceforge.pmd.lang.ast.Parser;
import net.sourceforge.pmd.lang.ast.Parser.ParserTask;
import net.sourceforge.pmd.lang.ast.RootNode;
import net.sourceforge.pmd.lang.ast.SemanticErrorReporter;
import net.sourceforge.pmd.lang.document.TextDocument;
import net.sourceforge.pmd.lang.document.TextRegion;
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

    public static DummyLanguageModule getInstance() {
        return (DummyLanguageModule) LanguageRegistry.getLanguage(NAME);
    }

    public static DummyRootNode parse(String code, String filename) {
        LanguageVersion version = DummyLanguageModule.getInstance().getDefaultVersion();
        ParserTask task = new ParserTask(
            TextDocument.readOnlyString(code, filename, version),
            SemanticErrorReporter.noop()
        );
        return (DummyRootNode) version.getLanguageVersionHandler().getParser().parse(task);
    }

    public static class Handler extends AbstractPmdLanguageVersionHandler {

        @Override
        public RuleViolationFactory getRuleViolationFactory() {
            return new RuleViolationFactory();
        }

        @Override
        public Parser getParser() {
            return DummyRootNode::new;
        }
    }

    public static class DummyRootNode extends DummyNode implements RootNode {


        private final AstInfo<DummyRootNode> astInfo;

        public DummyRootNode(ParserTask task) {
            this.astInfo = new AstInfo<>(task, this);
            withCoords(task.getTextDocument().getEntireRegion());
            setImage("Foo");
        }

        @Override
        public DummyRootNode withCoords(TextRegion region) {
            super.withCoords(region);
            return this;
        }


        @Override
        public AstInfo<DummyRootNode> getAstInfo() {
            return astInfo;
        }
    }


    public static class RuleViolationFactory extends DefaultRuleViolationFactory {

    }
}
