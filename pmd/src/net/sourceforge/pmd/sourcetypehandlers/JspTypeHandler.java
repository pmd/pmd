package net.sourceforge.pmd.sourcetypehandlers;

import net.sourceforge.pmd.ast.ParseException;
import net.sourceforge.pmd.jsp.ast.JspCharStream;
import net.sourceforge.pmd.parsers.Parser;
import net.sourceforge.pmd.symboltable.JspSymbolFacade;

import java.io.Reader;
import java.util.HashMap;
import java.util.Map;

/**
 * Implementation of SourceTypeHandler for the JSP parser.
 *
 * @author pieter_van_raemdonck - Application Engineers NV/SA - www.ae.be
 */
public class JspTypeHandler implements SourceTypeHandler {
    
    public Parser getParser() {
        return new Parser() {
            public Object parse(Reader source) throws ParseException {
                return new net.sourceforge.pmd.jsp.ast.JspParser(new JspCharStream(source))
                        .CompilationUnit();
            }
            public Map<Integer, String> getExcludeMap() {
                return new HashMap<Integer, String>();
            }
            public void setExcludeMarker(String marker) {}
        };
    }

    public VisitorStarter getDataFlowFacade() {
        return VisitorStarter.dummy;
    }

    public VisitorStarter getSymbolFacade() {
        return new JspSymbolFacade();
    }

    public VisitorStarter getTypeResolutionFacade(ClassLoader classLoader) {
        return VisitorStarter.dummy;
    }

}
