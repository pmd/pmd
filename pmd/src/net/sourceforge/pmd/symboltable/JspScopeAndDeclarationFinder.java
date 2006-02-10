package net.sourceforge.pmd.symboltable;

import net.sourceforge.pmd.jsp.ast.ASTCompilationUnit;

/**
 * Setting the scope in the root of a JSP AST.
 *
 * @author pieter_van_raemdonck - Application Engineers NV/SA - www.ae.be
 */
public class JspScopeAndDeclarationFinder {

    /**
     * Set a DummyScope as scope of the given compilationUnit.
     *
     * @param compilationUnit the ASTCompilationUnit
     */
    public void setJspScope(ASTCompilationUnit compilationUnit) {
        compilationUnit.setScope(new DummyScope());
    }
}
