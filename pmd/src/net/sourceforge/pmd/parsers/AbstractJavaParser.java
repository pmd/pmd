/**
 * BSD-style license; for more info see http://pmd.sourceforge.net/license.html
 */
package net.sourceforge.pmd.parsers;

import java.io.Reader;
import java.util.Map;

import net.sourceforge.pmd.ast.JavaCharStream;
import net.sourceforge.pmd.ast.JavaParser;
import net.sourceforge.pmd.ast.ParseException;

/**
 * This is a generic Java specific implementation of the Parser interface. It
 * creates a JavaParser instance, and sets the exclude marker. It also exposes
 * the exclude map from the JavaParser instance.
 * 
 * @see Parser
 * @see AbstractParser
 * @see JavaParser
 */
public abstract class AbstractJavaParser extends AbstractParser {
    private JavaParser parser;

    public TokenManager getTokenManager(Reader source) {
	return new JavaTokenManager(source);
    }

    /**
     * Subclass should override this method to modify the JavaParser as needed.
     */
    protected JavaParser createJavaParser(Reader source) throws ParseException {
	parser = new JavaParser(new JavaCharStream(source));
	String excludeMarker = getExcludeMarker();
	if (excludeMarker != null) {
	    parser.setExcludeMarker(excludeMarker);
	}
	return parser;
    }

    public Object parse(Reader source) throws ParseException {
	return createJavaParser(source).CompilationUnit();
    }

    public Map<Integer, String> getExcludeMap() {
	return parser.getExcludeMap();
    }
}
