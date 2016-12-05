/**
 * BSD-style license; for more info see http://pmd.sourceforge.net/license.html
 */

package net.sourceforge.pmd.lang.plsql.symboltable;

import java.util.HashSet;
import java.util.Set;
import java.util.logging.Level;
import java.util.logging.Logger;

import net.sourceforge.pmd.lang.symboltable.NameDeclaration;
import net.sourceforge.pmd.lang.symboltable.Scope;

public class Search {
    private static final Logger LOGGER = Logger.getLogger(Search.class.getName());

    private PLSQLNameOccurrence occ;
    private Set<NameDeclaration> declarations = new HashSet<>();

    public Search(PLSQLNameOccurrence occ) {
        if (LOGGER.isLoggable(Level.FINEST)) {
            LOGGER.finest(
                    "new search for " + (occ.isMethodOrConstructorInvocation() ? "method" : "variable") + " " + occ);
        }
        this.occ = occ;
    }

    public void execute() {
        Set<NameDeclaration> found = searchUpward(occ, occ.getLocation().getScope());
        if (LOGGER.isLoggable(Level.FINEST)) {
            LOGGER.finest("found " + found);
        }
        declarations.addAll(found);
    }

    public void execute(Scope startingScope) {
        Set<NameDeclaration> found = searchUpward(occ, startingScope);
        if (LOGGER.isLoggable(Level.FINEST)) {
            LOGGER.finest("found " + found);
        }
        declarations.addAll(found);
    }

    public Set<NameDeclaration> getResult() {
        return declarations;
    }

    private Set<NameDeclaration> searchUpward(PLSQLNameOccurrence nameOccurrence, Scope scope) {
        if (LOGGER.isLoggable(Level.FINEST)) {
            LOGGER.finest("checking scope " + scope + " for name occurrence " + nameOccurrence);
        }
        if (!scope.contains(nameOccurrence) && scope.getParent() != null) {
            if (LOGGER.isLoggable(Level.FINEST)) {
                LOGGER.finest("moving up fm " + scope + " to " + scope.getParent());
            }
            return searchUpward(nameOccurrence, scope.getParent());
        }
        if (scope.contains(nameOccurrence)) {
            if (LOGGER.isLoggable(Level.FINEST)) {
                LOGGER.finest("found it!");
            }
            return scope.addNameOccurrence(nameOccurrence);
        }
        return new HashSet<>();
    }
}
