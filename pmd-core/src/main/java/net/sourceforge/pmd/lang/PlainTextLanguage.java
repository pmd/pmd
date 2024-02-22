/*
 * BSD-style license; for more info see http://pmd.sourceforge.net/license.html
 */

package net.sourceforge.pmd.lang;

import net.sourceforge.pmd.cpd.AnyCpdLexer;
import net.sourceforge.pmd.cpd.CpdCapableLanguage;
import net.sourceforge.pmd.cpd.CpdLexer;
import net.sourceforge.pmd.lang.ast.AstInfo;
import net.sourceforge.pmd.lang.ast.Parser;
import net.sourceforge.pmd.lang.ast.Parser.ParserTask;
import net.sourceforge.pmd.lang.ast.RootNode;
import net.sourceforge.pmd.lang.ast.impl.AbstractNode;
import net.sourceforge.pmd.lang.document.TextRegion;
import net.sourceforge.pmd.lang.impl.SimpleLanguageModuleBase;

/**
 * A dummy language implementation whose parser produces a single node.
 * This is provided for cases where a non-null language is required, but
 * the parser is not useful. This is useful eg to mock rules when no other
 * language is on the classpath. This language is not exposed by {@link LanguageRegistry}
 * and can only be used explicitly with {@link #getInstance()}.
 *
 * @author Clément Fournier
 * @since 6.48.0
 */
public final class PlainTextLanguage extends SimpleLanguageModuleBase implements CpdCapableLanguage {
    private static final String ID = "text";

    private static final PlainTextLanguage INSTANCE = new PlainTextLanguage();

    private PlainTextLanguage() {
        super(LanguageMetadata.withId(ID).name("Plain text")
                              .extensions("plain-text-file-goo-extension")
                              .addDefaultVersion("default"),
              new TextLvh());
    }

    /**
     * Returns the singleton instance of this language.
     */
    public static PlainTextLanguage getInstance() {
        return INSTANCE; // note: this language is _not_ exposed via LanguageRegistry (no entry in META-INF/services)
    }

    @Override
    public CpdLexer createCpdLexer(LanguagePropertyBundle bundle) {
        return new AnyCpdLexer();
    }

    private static final class TextLvh implements LanguageVersionHandler {
        @Override
        public Parser getParser() {
            return PlainTextFile::new;
        }
    }

    /**
     * The only node produced by the parser of {@link PlainTextLanguage}.
     */
    public static class PlainTextFile extends AbstractNode<PlainTextFile, PlainTextFile> implements RootNode {

        private final AstInfo<PlainTextFile> astInfo;


        PlainTextFile(ParserTask task) {
            this.astInfo = new AstInfo<>(task, this);
        }

        @Override
        public TextRegion getTextRegion() {
            return getTextDocument().getEntireRegion();
        }

        @Override
        public String getXPathNodeName() {
            return "TextFile";
        }

        @Override
        public String getImage() {
            return null;
        }

        @Override
        public String toString() {
            return "Plain text file (" + getEndLine() + " lines)";
        }

        @Override
        public AstInfo<? extends RootNode> getAstInfo() {
            return astInfo;
        }
    }

}
