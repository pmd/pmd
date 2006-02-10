package net.sourceforge.pmd.sourcetypehandlers;

import net.sourceforge.pmd.ast.ASTCompilationUnit;
import net.sourceforge.pmd.dfa.DataFlowFacade;
import net.sourceforge.pmd.symboltable.SymbolFacade;

/**
 * Implementation of VisitorsFactory for the Java AST. It uses anonymous classes
 * as adapters of the visitors to the VisitorStarter interface.
 *
 * @author pieter_van_raemdonck - Application Engineers NV/SA - www.ae.be
 */
public abstract class JavaTypeHandler implements SourceTypeHandler {
    private DataFlowFacade dataFlowFacade = new DataFlowFacade();
    private SymbolFacade stb = new SymbolFacade();


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
}
