package net.sourceforge.pmd.symboltable;

import net.sourceforge.pmd.ast.ASTPrimaryExpression;
import net.sourceforge.pmd.ast.JavaParserVisitorAdapter;

import java.util.Iterator;

public class OccurrenceFinder extends JavaParserVisitorAdapter {

    public Object visit(ASTPrimaryExpression node, Object data) {
        NameOccurrences qualifiedNames = new NameOccurrences(node);
        NameDeclaration decl = null;
        for (Iterator i = qualifiedNames.iterator(); i.hasNext();) {
            NameOccurrence occ = (NameOccurrence) i.next();
            //System.out.println("searching for " + occ.getImage());
            Search search = new Search(occ);
            if (decl == null) {
                // doing the first name lookup
                search.execute();
                decl = search.getResult();
                if (decl == null) {
                    // we can't find it, so just give up
                    // when we decide to do full symbol resolution
                    // force this to either find a symbol or throw a SymbolNotFoundException
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
