/*
 * User: tom
 * Date: Sep 30, 2002
 * Time: 11:09:24 AM
 */
package net.sourceforge.pmd.symboltable;

import net.sourceforge.pmd.ast.*;

import java.util.StringTokenizer;
import java.util.List;
import java.util.ArrayList;
import java.util.Iterator;

public class SymbolFacade extends JavaParserVisitorAdapter {

    public void initializeWith(ASTCompilationUnit node) {
        // first, traverse the AST and create all the scopes
        ScopeCreator sc = new ScopeCreator();
        node.jjtAccept(sc, null);

        // traverse the AST and pick up all the declarations
        DeclarationFinder df = new DeclarationFinder();
        node.jjtAccept(df, null);

        // finally, traverse the AST and pick up all the name occurrences
        node.jjtAccept(this, null);
    }

    public Object visit(ASTPrimaryExpression node, Object data) {
        NameOccurrences qualifiedNames = new NameOccurrences(node);
        LookupController lookupController = new LookupController();
        lookupController.lookup(qualifiedNames);
        return super.visit(node, data);
    }


}
