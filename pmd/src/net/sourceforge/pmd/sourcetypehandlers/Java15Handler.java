package net.sourceforge.pmd.sourcetypehandlers;

import net.sourceforge.pmd.parsers.Java15Parser;
import net.sourceforge.pmd.parsers.Parser;

public class Java15Handler extends JavaTypeHandler {

    public Parser getParser() {
        return new Java15Parser();
    }

}
