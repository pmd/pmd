package net.sourceforge.pmd.sourcetypehandlers;

import java.io.Reader;

import net.sourceforge.pmd.ast.ParseException;
import net.sourceforge.pmd.jsp.ast.JspCharStream;
import net.sourceforge.pmd.parsers.Parser;
import net.sourceforge.pmd.symboltable.JspSymbolFacade;

/**
 * Implementation of SourceTypeHandler for the JSP parser.
 * @author pieter_van_raemdonck - Application Engineers NV/SA - www.ae.be
 *
 */
public class JspTypeHandler implements SourceTypeHandler {
	DummyVisitorStarter dummyVisitor = new DummyVisitorStarter();

	public Parser getParser() {
		return new Parser() {
			public Object parse(Reader source) throws ParseException {
				return new net.sourceforge.pmd.jsp.ast.JspParser(new JspCharStream(source))
						.CompilationUnit();
			}
		};
	}

	public VisitorStarter getDataFlowFacade() {
		return dummyVisitor;
	}

	public VisitorStarter getSymbolFacade() {
		return new JspSymbolFacade();
	}

}
