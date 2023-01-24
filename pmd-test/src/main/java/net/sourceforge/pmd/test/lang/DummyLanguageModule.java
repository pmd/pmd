/**
 * BSD-style license; for more info see http://pmd.sourceforge.net/license.html
 */

package net.sourceforge.pmd.test.lang;

import net.sourceforge.pmd.annotation.InternalApi;
import net.sourceforge.pmd.lang.AbstractPmdLanguageVersionHandler;
import net.sourceforge.pmd.lang.LanguageRegistry;
import net.sourceforge.pmd.lang.ast.AstInfo;
import net.sourceforge.pmd.lang.ast.Parser;
import net.sourceforge.pmd.lang.ast.Parser.ParserTask;
import net.sourceforge.pmd.lang.ast.RootNode;
import net.sourceforge.pmd.lang.document.TextRegion;
import net.sourceforge.pmd.lang.impl.SimpleLanguageModuleBase;
import net.sourceforge.pmd.test.lang.ast.DummyNode;

/**
 * Dummy language used for testing PMD.
 *
 * @deprecated Don't use this directly. We can probably remove this in favour of plaintextlanguage
 *  when https://github.com/pmd/pmd/issues/3918 is merged
 */
@Deprecated
@InternalApi
public class DummyLanguageModule extends SimpleLanguageModuleBase {

    public static final String NAME = "Dummy";
    public static final String TERSE_NAME = "dummy";

    public DummyLanguageModule() {
        super(LanguageMetadata.withId(TERSE_NAME).name(NAME).extensions("dummy")
                              .addVersion("1.0")
                              .addVersion("1.1")
                              .addVersion("1.2")
                              .addVersion("1.3")
                              .addVersion("1.4")
                              .addVersion("1.5", "5")
                              .addVersion("1.6", "6")
                              .addDefaultVersion("1.7", "7")
                              .addVersion("1.8", "8"), new Handler());
    }

    public static DummyLanguageModule getInstance() {
        return (DummyLanguageModule) LanguageRegistry.PMD.getLanguageByFullName(NAME);
    }

    public static class Handler extends AbstractPmdLanguageVersionHandler {

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

}
