package net.sourceforge.pmd.sourcetypehandlers;

import net.sourceforge.pmd.parsers.Java13Parser;
import net.sourceforge.pmd.parsers.Parser;

public class Java13Handler extends JavaTypeHandler {

    public Parser getParser() {
        return new Java13Parser();
    }

}
