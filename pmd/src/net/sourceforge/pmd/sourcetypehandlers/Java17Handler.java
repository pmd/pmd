package net.sourceforge.pmd.sourcetypehandlers;

import net.sourceforge.pmd.parsers.Java17Parser;
import net.sourceforge.pmd.parsers.Parser;

public class Java17Handler extends JavaTypeHandler {

    public Parser getParser() {
        return new Java17Parser();
    }

}
