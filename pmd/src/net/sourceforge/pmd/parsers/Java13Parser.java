package net.sourceforge.pmd.parsers;

import net.sourceforge.pmd.ast.JavaCharStream;
import net.sourceforge.pmd.ast.JavaParser;
import net.sourceforge.pmd.ast.ParseException;

import java.io.Reader;

/**
 * Adapter for the JavaParser, using Java 1.3 grammar.
 *
 * @author Pieter_Van_Raemdonck - Application Engineers NV/SA - www.ae.be
 */
public class Java13Parser implements Parser {

    public Object parse(Reader source) throws ParseException {
        JavaParser parser = new JavaParser(new JavaCharStream(source));
        parser.setJDK13();
        Object rootNode = parser.CompilationUnit();
        return rootNode;
    }

}
