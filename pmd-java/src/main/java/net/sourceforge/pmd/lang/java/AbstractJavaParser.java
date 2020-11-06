/**
 * BSD-style license; for more info see http://pmd.sourceforge.net/license.html
 */

package net.sourceforge.pmd.lang.java;

import java.io.Reader;
import java.util.Map;

import net.sourceforge.pmd.lang.AbstractParser;
import net.sourceforge.pmd.lang.ParserOptions;
import net.sourceforge.pmd.lang.TokenManager;
import net.sourceforge.pmd.lang.ast.AbstractTokenManager;
import net.sourceforge.pmd.lang.ast.JavaCharStream;
import net.sourceforge.pmd.lang.ast.Node;
import net.sourceforge.pmd.lang.java.ast.JavaParser;
import net.sourceforge.pmd.lang.java.ast.ParseException;

/**
 * This is a generic Java specific implementation of the Parser interface. It
 * creates a JavaParser instance, and sets the exclude marker. It also exposes
 * the exclude map from the JavaParser instance.
 *
 * @see AbstractParser
 * @see JavaParser
 *
 * @deprecated For removal, the abstraction is not useful.
 */
@Deprecated
public abstract class AbstractJavaParser extends AbstractParser {
    private JavaParser parser;

    public AbstractJavaParser(ParserOptions parserOptions) {
        super(parserOptions);
    }

    @Override
    public TokenManager createTokenManager(Reader source) {
        return new JavaTokenManager(source);
    }

    /**
     * Subclass should override this method to modify the JavaParser as needed.
     */
    protected JavaParser createJavaParser(Reader source) throws ParseException {
        parser = new JavaParser(new JavaCharStream(source));
        String suppressMarker = getParserOptions().getSuppressMarker();
        if (suppressMarker != null) {
            parser.setSuppressMarker(suppressMarker);
        }
        return parser;
    }

    @Override
    public boolean canParse() {
        return true;
    }

    @Override
    public Node parse(String fileName, Reader source) throws ParseException {
        AbstractTokenManager.setFileName(fileName);
        return createJavaParser(source).CompilationUnit();
    }

    @Override
    public Map<Integer, String> getSuppressMap() {
        return parser.getSuppressMap();
    }
}
