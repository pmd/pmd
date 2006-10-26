package net.sourceforge.pmd.sourcetypehandlers;

import net.sourceforge.pmd.ast.ASTCompilationUnit;
import net.sourceforge.pmd.dfa.DataFlowFacade;
import net.sourceforge.pmd.symboltable.SymbolFacade;
import net.sourceforge.pmd.typeresolution.TypeResolutionFacade;

/**
 * Implementation of VisitorsFactory for the Java AST. It uses anonymous classes
 * as adapters of the visitors to the VisitorStarter interface.
 *
 * @author pieter_van_raemdonck - Application Engineers NV/SA - www.ae.be
 */
public abstract class JavaTypeHandler implements SourceTypeHandler {
    private DataFlowFacade dataFlowFacade = new DataFlowFacade();
    private SymbolFacade stb = new SymbolFacade();
    private TypeResolutionFacade tr = new TypeResolutionFacade();

    public VisitorStarter getDataFlowFacade() {
        return new VisitorStarter() {
            public void start(Object rootNode) {
                dataFlowFacade.initializeWith((ASTCompilationUnit) rootNode);
            }
        };
    }

    public VisitorStarter getSymbolFacade() {
        return new VisitorStarter() {
            public void start(Object rootNode) {
                stb.initializeWith((ASTCompilationUnit) rootNode);
            }
        };
    }
    
    public VisitorStarter getTypeResolutionFacade() {
        return new VisitorStarter() {
            public void start(Object rootNode) {
                tr.initializeWith((ASTCompilationUnit) rootNode);
            }
        };
    }
    
    
}
