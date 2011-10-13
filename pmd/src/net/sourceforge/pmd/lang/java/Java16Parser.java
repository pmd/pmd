/**
 * BSD-style license; for more info see http://pmd.sourceforge.net/license.html
 */
package net.sourceforge.pmd.lang.java;

import java.io.Reader;

import net.sourceforge.pmd.lang.ParserOptions;
import net.sourceforge.pmd.lang.java.ast.JavaParser;
import net.sourceforge.pmd.lang.java.ast.ParseException;

/**
 * Adapter for the JavaParser, using Java 1.6 grammar.
 */
public class Java16Parser extends AbstractJavaParser {

    public Java16Parser(ParserOptions parserOptions) {
	super(parserOptions);
    }

    @Override
    protected JavaParser createJavaParser(Reader source) throws ParseException {
	JavaParser javaParser = super.createJavaParser(source);
	javaParser.setJdkVersion(6);
	return javaParser;
    }
}
