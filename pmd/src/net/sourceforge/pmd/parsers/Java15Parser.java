package net.sourceforge.pmd.parsers;

import java.io.Reader;

import net.sourceforge.pmd.ast.JavaCharStream;
import net.sourceforge.pmd.ast.JavaParser;
import net.sourceforge.pmd.ast.ParseException;

/**
 * Adapter for the JavaParser, using Java 1.5 grammar.
 * @author Pieter_Van_Raemdonck - Application Engineers NV/SA - www.ae.be
 */
public class Java15Parser implements Parser {

	public Object parse(Reader source) throws ParseException {
		JavaParser parser = new JavaParser(new JavaCharStream(source));
        parser.setJDK15();
		Object rootNode = parser.CompilationUnit();
		return rootNode;
	}

}
