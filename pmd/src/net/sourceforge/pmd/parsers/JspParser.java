package net.sourceforge.pmd.parsers;

import java.io.Reader;

import net.sourceforge.pmd.ast.ParseException;
import net.sourceforge.pmd.jsp.ast.JspCharStream;

public class JspParser implements Parser {

	public Object parse(Reader source) throws ParseException {
		return new net.sourceforge.pmd.jsp.ast.JspParser(new JspCharStream(source)).CompilationUnit();
	}

}
