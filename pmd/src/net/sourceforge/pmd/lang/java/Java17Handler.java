package net.sourceforge.pmd.lang.java;

import net.sourceforge.pmd.lang.Parser;

public class Java17Handler extends AbstractJavaHandler {

    public Parser getParser() {
        return new Java17Parser();
    }

}
