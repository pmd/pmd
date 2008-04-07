package net.sourceforge.pmd.lang.java;

import java.io.Writer;

import net.sourceforge.pmd.dfa.DataFlowFacade;
import net.sourceforge.pmd.lang.LanguageVersionHandler;
import net.sourceforge.pmd.lang.VisitorStarter;
import net.sourceforge.pmd.lang.XPathFunctionRegister;
import net.sourceforge.pmd.lang.ast.Node;
import net.sourceforge.pmd.lang.java.ast.ASTCompilationUnit;
import net.sourceforge.pmd.lang.java.ast.DumpFacade;
import net.sourceforge.pmd.lang.java.ast.JavaNode;
import net.sourceforge.pmd.lang.java.jaxen.TypeOfFunction;
import net.sourceforge.pmd.lang.java.rule.JavaRuleViolationFactory;
import net.sourceforge.pmd.lang.rule.RuleViolationFactory;
import net.sourceforge.pmd.symboltable.SymbolFacade;
import net.sourceforge.pmd.typeresolution.TypeResolutionFacade;

/**
 * Implementation of LanguageVersionHandler for the Java AST. It uses anonymous classes
 * as adapters of the visitors to the VisitorStarter interface.
 *
 * @author pieter_van_raemdonck - Application Engineers NV/SA - www.ae.be
 */
public abstract class AbstractJavaHandler implements LanguageVersionHandler {

    public XPathFunctionRegister getXPathFunctionRegister() {
	return new XPathFunctionRegister() {
	    public void register() {
		TypeOfFunction.registerSelfInSimpleContext();
	    }
	};
    }

    public RuleViolationFactory getRuleViolationFactory() {
	return JavaRuleViolationFactory.INSTANCE;
    }

    public VisitorStarter getDataFlowFacade() {
	return new VisitorStarter() {
	    public void start(Node rootNode) {
		new DataFlowFacade().initializeWith((ASTCompilationUnit) rootNode);
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
