/*
 * BSD-style license; for more info see http://pmd.sourceforge.net/license.html
 */

package net.sourceforge.pmd.lang;

import net.sourceforge.pmd.annotation.Experimental;
import net.sourceforge.pmd.lang.ast.AstInfo;
import net.sourceforge.pmd.lang.ast.Parser;
import net.sourceforge.pmd.lang.ast.RootNode;
import net.sourceforge.pmd.lang.ast.SourceCodePositioner;
import net.sourceforge.pmd.lang.ast.impl.AbstractNode;

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
@Experimental
public final class PlainTextLanguage extends BaseLanguageModule {

    private static final Language INSTANCE = new PlainTextLanguage();

    static final String TERSE_NAME = "text";

    private PlainTextLanguage() {
        super("Plain text", "Plain text", TERSE_NAME, "plain-text-file-goo-extension");
        addVersion("default", new TextLvh(), true);
    }

    /**
     * Returns the singleton instance of this language.
     */
    public static Language getInstance() {
        return INSTANCE;
    }

    private static final class TextLvh implements LanguageVersionHandler {
        @Override
        public Parser getParser() {
            return parserTask -> new PlainTextFile(parserTask);
        }
    }

    /**
     * The only node produced by the parser of {@link PlainTextLanguage}.
     */
    public static final class PlainTextFile extends AbstractNode implements RootNode {
        private final int beginLine;
        private final int beginColumn;
        private final int endLine;
        private final int endColumn;

        private final AstInfo<PlainTextFile> astInfo;

        PlainTextFile(Parser.ParserTask parserTask) {
            SourceCodePositioner positioner = new SourceCodePositioner(parserTask.getSourceText());
            this.beginLine = 1;
            this.beginColumn = 1;
            this.endLine = positioner.getLastLine();
            this.endColumn = positioner.getLastLineColumn();
            this.astInfo = new AstInfo<>(parserTask, this);
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
        public int getBeginLine() {
            return beginLine;
        }

        @Override
        public int getBeginColumn() {
            return beginColumn;
        }

        @Override
        public int getEndLine() {
            return endLine;
        }

        @Override
        public int getEndColumn() {
            return endColumn;
        }

        @Override
        public void remove() {
            throw new UnsupportedOperationException();
        }

        @Override
        public void removeChildAtIndex(int childIndex) {
            throw new IndexOutOfBoundsException();
        }

        @Override
        public String toString() {
            return "Plain text file (" + endLine + "lines)";
        }

        @Override
        public AstInfo<? extends RootNode> getAstInfo() {
            return astInfo;
        }
    }

}
