package net.sourceforge.pmd.lang.java;

import net.sourceforge.pmd.lang.Parser;

public class Java14Handler extends AbstractJavaHandler {

    public Parser getParser() {
        return new Java14Parser();
    }

}
