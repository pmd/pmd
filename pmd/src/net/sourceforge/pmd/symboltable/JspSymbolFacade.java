package net.sourceforge.pmd.symboltable;

import net.sourceforge.pmd.jsp.ast.ASTCompilationUnit;
import net.sourceforge.pmd.sourcetypehandlers.VisitorStarter;

/**
 * Symbol Facade for JSP.
 *
 * @author pieter_van_raemdonck - Application Engineers NV/SA - www.ae.be
 */
public class JspSymbolFacade implements VisitorStarter {

    /**
     * Set Scope for JSP AST.
     */
    public void start(Object rootNode) {
        ASTCompilationUnit compilationUnit = (ASTCompilationUnit) rootNode;
        new JspScopeAndDeclarationFinder().setJspScope(compilationUnit);
    }

}
