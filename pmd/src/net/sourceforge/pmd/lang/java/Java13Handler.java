package net.sourceforge.pmd.lang.java;

import net.sourceforge.pmd.lang.Parser;
import net.sourceforge.pmd.lang.ParserOptions;

public class Java13Handler extends AbstractJavaHandler {

    public Parser getParser(ParserOptions parserOptions) {
        return new Java13Parser(parserOptions);
    }
}
