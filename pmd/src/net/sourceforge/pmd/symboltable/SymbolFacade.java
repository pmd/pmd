/*
 * User: tom
 * Date: Sep 30, 2002
 * Time: 11:09:24 AM
 */
package net.sourceforge.pmd.symboltable;

import net.sourceforge.pmd.ast.ASTCompilationUnit;
import net.sourceforge.pmd.ast.ASTPrimaryExpression;
import net.sourceforge.pmd.ast.JavaParserVisitorAdapter;

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
        NameDeclaration decl = null;
        for (Iterator i = qualifiedNames.iterator(); i.hasNext();) {
            NameOccurrence occ = (NameOccurrence)i.next();
            Search search = new Search(occ);
            if (decl == null) {
                // doing the first name lookup
                search.execute();
                decl = search.getResult();
                if (decl == null) {
                    // we can't find it, so just give up
                    // when we decide searches across compilation units like a compiler would, we'll
                    // force this to either find a symbol or throw a "cannot resolve symbol" Exception
                    break;
                }
            } else {
                // now we've got a scope we're starting with, so work from there
                search.execute(decl.getScope());
                decl = search.getResult();
            }
        }
        return super.visit(node, data);
    }

}
