/**
 * BSD-style license; for more info see http://pmd.sourceforge.net/license.html
 */

package net.sourceforge.pmd.lang.java;

import java.io.Writer;

import net.sourceforge.pmd.lang.AbstractLanguageVersionHandler;
import net.sourceforge.pmd.lang.DataFlowHandler;
import net.sourceforge.pmd.lang.LanguageRegistry;
import net.sourceforge.pmd.lang.VisitorStarter;
import net.sourceforge.pmd.lang.XPathHandler;
import net.sourceforge.pmd.lang.ast.Node;
import net.sourceforge.pmd.lang.ast.xpath.DefaultASTXPathHandler;
import net.sourceforge.pmd.lang.dfa.DFAGraphRule;
import net.sourceforge.pmd.lang.java.ast.ASTAnyTypeDeclaration;
import net.sourceforge.pmd.lang.java.ast.ASTCompilationUnit;
import net.sourceforge.pmd.lang.java.ast.DumpFacade;
import net.sourceforge.pmd.lang.java.ast.JavaNode;
import net.sourceforge.pmd.lang.java.ast.MethodLikeNode;
import net.sourceforge.pmd.lang.java.dfa.DataFlowFacade;
import net.sourceforge.pmd.lang.java.dfa.JavaDFAGraphRule;
import net.sourceforge.pmd.lang.java.metrics.JavaMetricsProvider;
import net.sourceforge.pmd.lang.java.multifile.MultifileVisitorFacade;
import net.sourceforge.pmd.lang.java.qname.QualifiedNameResolver;
import net.sourceforge.pmd.lang.java.rule.JavaRuleViolationFactory;
import net.sourceforge.pmd.lang.java.symboltable.SymbolFacade;
import net.sourceforge.pmd.lang.java.typeresolution.TypeResolutionFacade;
import net.sourceforge.pmd.lang.java.xpath.GetCommentOnFunction;
import net.sourceforge.pmd.lang.java.xpath.JavaFunctions;
import net.sourceforge.pmd.lang.java.xpath.MetricFunction;
import net.sourceforge.pmd.lang.java.xpath.TypeIsExactlyFunction;
import net.sourceforge.pmd.lang.java.xpath.TypeIsFunction;
import net.sourceforge.pmd.lang.java.xpath.TypeOfFunction;
import net.sourceforge.pmd.lang.metrics.LanguageMetricsProvider;
import net.sourceforge.pmd.lang.rule.RuleViolationFactory;

import net.sf.saxon.sxpath.IndependentContext;

/**
 * Implementation of LanguageVersionHandler for the Java AST. It uses anonymous
 * classes as adapters of the visitors to the VisitorStarter interface.
 *
 * @author pieter_van_raemdonck - Application Engineers NV/SA - www.ae.be
 *
 * @deprecated For removal, the abstraction is not useful.
 */
@Deprecated
public abstract class AbstractJavaHandler extends AbstractLanguageVersionHandler {

    private final LanguageMetricsProvider<ASTAnyTypeDeclaration, MethodLikeNode> myMetricsProvider = new JavaMetricsProvider();

    @Override
    public DataFlowHandler getDataFlowHandler() {
        return new JavaDataFlowHandler();
    }

    @Override
    public XPathHandler getXPathHandler() {
        return new DefaultASTXPathHandler() {
            @Override
            public void initialize() {
                TypeOfFunction.registerSelfInSimpleContext();
                GetCommentOnFunction.registerSelfInSimpleContext();
                MetricFunction.registerSelfInSimpleContext();
                TypeIsFunction.registerSelfInSimpleContext();
                TypeIsExactlyFunction.registerSelfInSimpleContext();
            }

            @Override
            public void initialize(IndependentContext context) {
                super.initialize(context, LanguageRegistry.getLanguage(JavaLanguageModule.NAME), JavaFunctions.class);
            }
        };
    }

    @Override
    public RuleViolationFactory getRuleViolationFactory() {
        return JavaRuleViolationFactory.INSTANCE;
    }

    @Override
    public VisitorStarter getDataFlowFacade() {
        return new VisitorStarter() {
            @Override
            public void start(Node rootNode) {
                new DataFlowFacade().initializeWith(getDataFlowHandler(), (ASTCompilationUnit) rootNode);
            }
        };
    }

    @Override
    public VisitorStarter getSymbolFacade() {
        return new VisitorStarter() {
            @Override
            public void start(Node rootNode) {
                new SymbolFacade().initializeWith(null, (ASTCompilationUnit) rootNode);
            }
        };
    }

    @Override
    public VisitorStarter getSymbolFacade(final ClassLoader classLoader) {
        return new VisitorStarter() {
            @Override
            public void start(Node rootNode) {
                new SymbolFacade().initializeWith(classLoader, (ASTCompilationUnit) rootNode);
            }
        };
    }

    @Override
    public VisitorStarter getTypeResolutionFacade(final ClassLoader classLoader) {
        return new VisitorStarter() {
            @Override
            public void start(Node rootNode) {
                new TypeResolutionFacade().initializeWith(classLoader, (ASTCompilationUnit) rootNode);
            }
        };
    }

    @Deprecated
    @Override
    public VisitorStarter getDumpFacade(final Writer writer, final String prefix, final boolean recurse) {
        return new VisitorStarter() {
            @Override
            public void start(Node rootNode) {
                new DumpFacade().initializeWith(writer, prefix, recurse, (JavaNode) rootNode);
            }
        };
    }

    @Override
    public VisitorStarter getMultifileFacade() {
        return new VisitorStarter() {
            @Override
            public void start(Node rootNode) {
                new MultifileVisitorFacade().initializeWith((ASTCompilationUnit) rootNode);
            }
        };
    }


    @Override
    public VisitorStarter getQualifiedNameResolutionFacade(final ClassLoader classLoader) {
        return new VisitorStarter() {
            @Override
            public void start(Node rootNode) {
                new QualifiedNameResolver().initializeWith(classLoader, (ASTCompilationUnit) rootNode);
            }
        };
    }


    @Override
    public DFAGraphRule getDFAGraphRule() {
        return new JavaDFAGraphRule();
    }


    @Override
    public LanguageMetricsProvider<ASTAnyTypeDeclaration, MethodLikeNode> getLanguageMetricsProvider() {
        return myMetricsProvider;
    }
}
