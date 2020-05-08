/*
 * BSD-style license; for more info see http://pmd.sourceforge.net/license.html
 */


package net.sourceforge.pmd.lang.plsql.ast;

import net.sourceforge.pmd.annotation.InternalApi;
import net.sourceforge.pmd.lang.symboltable.NameDeclaration;
import net.sourceforge.pmd.lang.symboltable.Scope;

/**
 * Acts as a bridge between outer parts (e.g. symbol table) and the restricted
 * access internal API of this package.
 * 
 * <p>Note: This is internal API.
 */
@InternalApi
public final class InternalApiBridge {

    private InternalApiBridge() {
        
    }

    public static void setScope(PLSQLNode node, Scope decl) {
        ((AbstractPLSQLNode) node).setScope(decl);
    }

    public static void setNameDeclaration(ASTName node, NameDeclaration decl) {
        node.setNameDeclaration(decl);
    }

    public static void setNameDeclaration(ASTVariableOrConstantDeclaratorId node, NameDeclaration decl) {
        node.setNameDeclaration(decl);
    }
}
