/**
 * BSD-style license; for more info see http://pmd.sourceforge.net/license.html
 */
package net.sourceforge.pmd.lang.java;

import java.io.Reader;

import net.sourceforge.pmd.lang.ParserOptions;
import net.sourceforge.pmd.lang.java.ast.JavaParser;
import net.sourceforge.pmd.lang.java.ast.ParseException;

/**
 * Adapter for the JavaParser, using Java 1.5 grammar.
 *
 * @author Pieter_Van_Raemdonck - Application Engineers NV/SA - www.ae.be
 */
public class Java15Parser extends AbstractJavaParser {

    public Java15Parser(ParserOptions parserOptions) {
	super(parserOptions);
    }

    @Override
    protected JavaParser createJavaParser(Reader source) throws ParseException {
	JavaParser javaParser = super.createJavaParser(source);
	javaParser.setJdkVersion(5);
	return javaParser;
    }
}
