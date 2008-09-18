package net.sourceforge.pmd.lang.java;

import java.io.Writer;

import net.sf.saxon.sxpath.IndependentContext;
import net.sourceforge.pmd.lang.DataFlowHandler;
import net.sourceforge.pmd.lang.Language;
import net.sourceforge.pmd.lang.LanguageVersionHandler;
import net.sourceforge.pmd.lang.VisitorStarter;
import net.sourceforge.pmd.lang.XPathHandler;
import net.sourceforge.pmd.lang.ast.Node;
import net.sourceforge.pmd.lang.ast.xpath.AbstractASTXPathHandler;
import net.sourceforge.pmd.lang.java.ast.ASTCompilationUnit;
import net.sourceforge.pmd.lang.java.ast.DumpFacade;
import net.sourceforge.pmd.lang.java.ast.JavaNode;
import net.sourceforge.pmd.lang.java.dfa.DataFlowFacade;
import net.sourceforge.pmd.lang.java.rule.JavaRuleViolationFactory;
import net.sourceforge.pmd.lang.java.symboltable.SymbolFacade;
import net.sourceforge.pmd.lang.java.typeresolution.TypeResolutionFacade;
import net.sourceforge.pmd.lang.java.xpath.JavaFunctions;
import net.sourceforge.pmd.lang.java.xpath.TypeOfFunction;
import net.sourceforge.pmd.lang.rule.RuleViolationFactory;

/**
 * Implementation of LanguageVersionHandler for the Java AST. It uses anonymous classes
 * as adapters of the visitors to the VisitorStarter interface.
 *
 * @author pieter_van_raemdonck - Application Engineers NV/SA - www.ae.be
 */
public abstract class AbstractJavaHandler implements LanguageVersionHandler {

    public DataFlowHandler getDataFlowHandler() {
	return new JavaDataFlowHandler();
    }

    public XPathHandler getXPathHandler() {
	return new AbstractASTXPathHandler() {
	    public void initialize() {
		TypeOfFunction.registerSelfInSimpleContext();
	    }

	    public void initialize(IndependentContext context) {
		super.initialize(context, Language.JAVA, JavaFunctions.class);
	    }
	};
    }

    public RuleViolationFactory getRuleViolationFactory() {
	return JavaRuleViolationFactory.INSTANCE;
    }

    public VisitorStarter getDataFlowFacade() {
	return new VisitorStarter() {
	    public void start(Node rootNode) {
		new DataFlowFacade().initializeWith(getDataFlowHandler(), (ASTCompilationUnit) rootNode);
	    }
	};
    }

    public VisitorStarter getSymbolFacade() {
	return new VisitorStarter() {
	    public void start(Node rootNode) {
		new SymbolFacade().initializeWith((ASTCompilationUnit) rootNode);
	    }
	};
    }

    public VisitorStarter getTypeResolutionFacade(final ClassLoader classLoader) {
	return new VisitorStarter() {
	    public void start(Node rootNode) {
		new TypeResolutionFacade().initializeWith(classLoader, (ASTCompilationUnit) rootNode);
	    }
	};
    }

    public VisitorStarter getDumpFacade(final Writer writer, final String prefix, final boolean recurse) {
	return new VisitorStarter() {
	    public void start(Node rootNode) {
		new DumpFacade().initializeWith(writer, prefix, recurse, (JavaNode) rootNode);
	    }
	};
    }
}
