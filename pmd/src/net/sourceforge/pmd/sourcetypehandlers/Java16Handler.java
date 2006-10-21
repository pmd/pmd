package net.sourceforge.pmd.sourcetypehandlers;

import net.sourceforge.pmd.parsers.Java16Parser;
import net.sourceforge.pmd.parsers.Parser;

public class Java16Handler extends JavaTypeHandler {

    public Parser getParser() {
        return new Java16Parser();
    }

}
