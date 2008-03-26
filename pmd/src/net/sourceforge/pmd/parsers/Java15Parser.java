/**
 * BSD-style license; for more info see http://pmd.sourceforge.net/license.html
 */
package net.sourceforge.pmd.parsers;

import java.io.Reader;

import net.sourceforge.pmd.ast.JavaParser;
import net.sourceforge.pmd.ast.ParseException;

/**
 * Adapter for the JavaParser, using Java 1.5 grammar.
 *
 * @author Pieter_Van_Raemdonck - Application Engineers NV/SA - www.ae.be
 */
public class Java15Parser extends AbstractJavaParser {

    @Override
    protected JavaParser createJavaParser(Reader source) throws ParseException {
	JavaParser javaParser = super.createJavaParser(source);
	javaParser.setJDK15();
	return javaParser;
    }
}
