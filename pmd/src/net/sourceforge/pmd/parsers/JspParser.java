package net.sourceforge.pmd.parsers;

import net.sourceforge.pmd.ast.ParseException;
import net.sourceforge.pmd.jsp.ast.JspCharStream;

import java.io.Reader;
import java.util.HashMap;
import java.util.Map;

public class JspParser implements Parser {

    public Object parse(Reader source) throws ParseException {
        return new net.sourceforge.pmd.jsp.ast.JspParser(new JspCharStream(source)).CompilationUnit();
    }

    public Map getExcludeMap() {
        return new HashMap(); // FIXME
    }

    public void setExcludeMarker(String marker) {
        // FIXME
    }


}
