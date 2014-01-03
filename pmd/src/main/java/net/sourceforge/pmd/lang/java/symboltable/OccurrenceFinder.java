/**
 * BSD-style license; for more info see http://pmd.sourceforge.net/license.html
 */
package net.sourceforge.pmd.lang.java.symboltable;

import java.util.List;

import net.sourceforge.pmd.lang.java.ast.ASTPrimaryExpression;
import net.sourceforge.pmd.lang.java.ast.JavaParserVisitorAdapter;
import net.sourceforge.pmd.lang.symboltable.NameDeclaration;

public class OccurrenceFinder extends JavaParserVisitorAdapter {

    public Object visit(ASTPrimaryExpression node, Object data) {
        NameFinder nameFinder = new NameFinder(node);

        // Maybe do some sort of State pattern thingy for when NameDeclaration
        // is null/not null?
        NameDeclaration decl = null;

        List<JavaNameOccurrence> names = nameFinder.getNames();
        for (JavaNameOccurrence occ: names) {
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

                if (decl == null) {
                    // nothing found
                    // This seems to be a lack of type resolution here.
                    // Theoretically we have the previous declaration node and know from there the Type of
                    // the variable. The current occurrence (occ) should then be found in the declaration of
                    // this type. The type however may or may not be known to PMD (see aux classpath).

                    // we can't find it, so just give up
                    // when we decide to do full symbol resolution
                    // force this to either find a symbol or throw a SymbolNotFoundException
                    break;
                }
            }
        }
        return super.visit(node, data);
    }

}
