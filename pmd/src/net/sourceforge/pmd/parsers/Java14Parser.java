package net.sourceforge.pmd.parsers;

import net.sourceforge.pmd.ast.JavaCharStream;
import net.sourceforge.pmd.ast.JavaParser;
import net.sourceforge.pmd.ast.ParseException;

import java.io.Reader;

/**
 * Adapter for the JavaParser, using Java 1.4 grammar.
 *
 * @author Pieter_Van_Raemdonck - Application Engineers NV/SA - www.ae.be
 */
public class Java14Parser implements Parser {

    public Object parse(Reader source) throws ParseException {
        JavaParser parser = new JavaParser(new JavaCharStream(source));
        Object rootNode = parser.CompilationUnit();
        return rootNode;
    }

}
