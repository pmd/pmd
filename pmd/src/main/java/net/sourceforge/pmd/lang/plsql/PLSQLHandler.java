package net.sourceforge.pmd.lang.plsql;

import java.io.Writer;

import net.sf.saxon.sxpath.IndependentContext;
import net.sourceforge.pmd.lang.AbstractLanguageVersionHandler;
import net.sourceforge.pmd.lang.DataFlowHandler;
import net.sourceforge.pmd.lang.Language;
import net.sourceforge.pmd.lang.Parser;
import net.sourceforge.pmd.lang.ParserOptions;
import net.sourceforge.pmd.lang.VisitorStarter;
import net.sourceforge.pmd.lang.XPathHandler;
import net.sourceforge.pmd.lang.ast.Node;
import net.sourceforge.pmd.lang.ast.xpath.AbstractASTXPathHandler;
import net.sourceforge.pmd.lang.java.xpath.GetCommentOnFunction;
import net.sourceforge.pmd.lang.java.xpath.JavaFunctions;
import net.sourceforge.pmd.lang.java.xpath.TypeOfFunction;
import net.sourceforge.pmd.lang.plsql.ast.ASTInput;
import net.sourceforge.pmd.lang.plsql.ast.DumpFacade;
import net.sourceforge.pmd.lang.plsql.ast.PLSQLNode;
import net.sourceforge.pmd.lang.plsql.dfa.DataFlowFacade;
import net.sourceforge.pmd.lang.plsql.rule.PLSQLRuleViolationFactory;
import net.sourceforge.pmd.lang.plsql.symboltable.SymbolFacade;
import net.sourceforge.pmd.lang.rule.RuleViolationFactory;
//import net.sourceforge.pmd.lang.java.typeresolution.TypeResolutionFacade;

/**
 * Implementation of LanguageVersionHandler for the PLSQL AST. It uses anonymous classes
 * as adapters of the visitors to the VisitorStarter interface.
 *
 * @author sturton - PLDoc - pldoc.sourceforge.net
 */
public class PLSQLHandler extends AbstractLanguageVersionHandler {

	
    public Parser getParser(ParserOptions parserOptions) {
        return new PLSQLParser(parserOptions);
    }

    @Override
    public XPathHandler getXPathHandler() {
	return new AbstractASTXPathHandler() {
	    public void initialize() {
		TypeOfFunction.registerSelfInSimpleContext();
		GetCommentOnFunction.registerSelfInSimpleContext();
	    }

	    public void initialize(IndependentContext context) {
		super.initialize(context, Language.PLSQL, JavaFunctions.class);
	    }
	};
    }

    public RuleViolationFactory getRuleViolationFactory() {
	return PLSQLRuleViolationFactory.INSTANCE;
    }

    @Override
    public DataFlowHandler getDataFlowHandler() {
	return new PLSQLDataFlowHandler();
    }

    @Override
    public VisitorStarter getDataFlowFacade() {
	return new VisitorStarter() {
	    public void start(Node rootNode) {
		new DataFlowFacade().initializeWith(getDataFlowHandler(), (ASTInput) rootNode);
	    }
	};
    }

    @Override
    public VisitorStarter getSymbolFacade() {
	return new VisitorStarter() {
	    public void start(Node rootNode) {
		new SymbolFacade().initializeWith((ASTInput) rootNode);
	    }
	};
    }

    /*
    @Override
    public VisitorStarter getTypeResolutionFacade(final ClassLoader classLoader) {
	return new VisitorStarter() {
	    public void start(Node rootNode) {
		new TypeResolutionFacade().initializeWith(classLoader, (ASTInput) rootNode);
	    }
	};
    }
    */

    @Override
    public VisitorStarter getDumpFacade(final Writer writer, final String prefix, final boolean recurse) {
	return new VisitorStarter() {
	    public void start(Node rootNode) {
		new DumpFacade().initializeWith(writer, prefix, recurse, (PLSQLNode) rootNode);
	    }
	};
    }
}
