/**
 * BSD-style license; for more info see http://pmd.sourceforge.net/license.html
 */

package net.sourceforge.pmd.lang.plsql.symboltable;

import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.logging.Level;
import java.util.logging.Logger;

import net.sourceforge.pmd.lang.plsql.ast.ASTPrimaryExpression;
import net.sourceforge.pmd.lang.plsql.ast.PLSQLParserVisitorAdapter;
import net.sourceforge.pmd.lang.symboltable.NameDeclaration;
import net.sourceforge.pmd.lang.symboltable.Scope;

public class OccurrenceFinder extends PLSQLParserVisitorAdapter {
    private static final Logger LOGGER = Logger.getLogger(OccurrenceFinder.class.getName());

    @Override
    public Object visit(ASTPrimaryExpression node, Object data) {
        NameFinder nameFinder = new NameFinder(node);

        // Maybe do some sort of State pattern thingy for when NameDeclaration
        // is empty/not empty
        Set<NameDeclaration> declarations = new HashSet<>();

        List<PLSQLNameOccurrence> names = nameFinder.getNames();
        for (PLSQLNameOccurrence occ : names) {
            Search search = new Search(occ);
            if (declarations.isEmpty()) {
                // doing the first name lookup
                search.execute();
                declarations.addAll(search.getResult());
                if (declarations.isEmpty()) {
                    // we can't find it, so just give up
                    // when we decide to do full symbol resolution
                    // force this to either find a symbol or throw a
                    // SymbolNotFoundException
                    break;
                }
            } else {
                Set<NameDeclaration> additionalDeclarations = new HashSet<>();
                for (NameDeclaration decl : declarations) {
                    // now we've got a scope we're starting with, so work from
                    // there
                    Scope scope = decl.getScope();
                    if (null == scope) {
                        if (LOGGER.isLoggable(Level.FINEST)) {
                            LOGGER.finest("NameOccurrence has no Scope:" + decl.getClass().getCanonicalName() + "=>"
                                    + decl.getImage());
                        }
                        break;
                    }
                    search.execute(scope);
                    Set<NameDeclaration> found = search.getResult();
                    additionalDeclarations.addAll(found);
                    if (found.isEmpty()) {
                        // nothing found
                        // This seems to be a lack of type resolution here.
                        // Theoretically we have the previous declaration node
                        // and know from there the Type of
                        // the variable. The current occurrence (occ) should
                        // then be found in the declaration of
                        // this type. The type however may or may not be known
                        // to PMD (see aux classpath).

                        // we can't find it, so just give up
                        // when we decide to do full symbol resolution
                        // force this to either find a symbol or throw a
                        // SymbolNotFoundException
                        break;
                    }
                }
                declarations.addAll(additionalDeclarations);
            }
        }
        return super.visit(node, data);
    }

}
