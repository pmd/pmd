/*
 * BSD-style license; for more info see http://pmd.sourceforge.net/license.html
 */


package net.sourceforge.pmd.lang.plsql.ast;

import net.sourceforge.pmd.annotation.InternalApi;
import net.sourceforge.pmd.lang.TokenManager;
import net.sourceforge.pmd.lang.ast.impl.javacc.CharStream;
import net.sourceforge.pmd.lang.ast.impl.javacc.JavaccToken;
import net.sourceforge.pmd.lang.document.TextDocument;
import net.sourceforge.pmd.lang.symboltable.NameDeclaration;
import net.sourceforge.pmd.lang.symboltable.Scope;

/**
 * Internal API.
 *
 * <p>Acts as a bridge between outer parts of PMD and the restricted access
 * internal API of this package.
 *
 * <p><b>None of this is published API, and compatibility can be broken anytime!</b>
 * Use this only at your own risk.
 *
 * @apiNote Internal API
 */
@InternalApi
public final class InternalApiBridge {

    private InternalApiBridge() {}

    public static void setScope(PLSQLNode node, Scope decl) {
        ((AbstractPLSQLNode) node).setScope(decl);
    }

    public static void setNameDeclaration(ASTName node, NameDeclaration decl) {
        node.setNameDeclaration(decl);
    }

    public static void setNameDeclaration(ASTVariableOrConstantDeclaratorId node, NameDeclaration decl) {
        node.setNameDeclaration(decl);
    }

    public static TokenManager<JavaccToken> newTokenManager(TextDocument doc) {
        return PLSQLTokenKinds.newTokenManager(CharStream.create(doc, PLSQLParser.TOKEN_BEHAVIOR));
    }
}
