package net.sourceforge.pmd.sourcetypehandlers;

import net.sourceforge.pmd.parsers.Java14Parser;
import net.sourceforge.pmd.parsers.Parser;

public class Java14Handler extends JavaTypeHandler {

    public Parser getParser() {
        return new Java14Parser();
    }

}
