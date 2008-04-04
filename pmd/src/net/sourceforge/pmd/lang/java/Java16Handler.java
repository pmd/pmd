package net.sourceforge.pmd.lang.java;

import net.sourceforge.pmd.lang.Parser;

public class Java16Handler extends AbstractJavaHandler {

    public Parser getParser() {
        return new Java16Parser();
    }

}
