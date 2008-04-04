package net.sourceforge.pmd.symboltable;

import net.sourceforge.pmd.lang.VisitorStarter;
import net.sourceforge.pmd.lang.ast.Node;
import net.sourceforge.pmd.lang.jsp.ast.ASTCompilationUnit;

/**
 * Symbol Facade for JSP.
 *
 * @author pieter_van_raemdonck - Application Engineers NV/SA - www.ae.be
 */
public class JspSymbolFacade implements VisitorStarter {

    /**
     * Set Scope for JSP AST.
     */
    public void start(Node rootNode) {
        ASTCompilationUnit compilationUnit = (ASTCompilationUnit) rootNode;
        new JspScopeAndDeclarationFinder().setJspScope(compilationUnit);
    }

}
