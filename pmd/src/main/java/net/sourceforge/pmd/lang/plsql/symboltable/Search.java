/**
 * BSD-style license; for more info see http://pmd.sourceforge.net/license.html
 */
package net.sourceforge.pmd.lang.plsql.symboltable;

import java.util.logging.Level;
import java.util.logging.Logger;

public class Search {
    private final static Logger LOGGER = Logger.getLogger(Search.class.getName()); 

    private NameOccurrence occ;
    private NameDeclaration decl;

    public Search(NameOccurrence occ) {
        if (LOGGER.isLoggable(Level.FINEST)) {
            LOGGER.finest("new search for " + (occ.isMethodOrConstructorInvocation() ? "method" : "variable") + " " + occ);
        }
        this.occ = occ;
    }

    public void execute() {
        decl = searchUpward(occ, occ.getLocation().getScope());
        if (LOGGER.isLoggable(Level.FINEST)) {
            LOGGER.finest("found " + decl);
        }
    }

    public void execute(Scope startingScope) {
        decl = searchUpward(occ, startingScope);
        if (LOGGER.isLoggable(Level.FINEST)) {
            LOGGER.finest("found " + decl);
        }
    }

    public NameDeclaration getResult() {
        return decl;
    }

    private NameDeclaration searchUpward(NameOccurrence nameOccurrence, Scope scope) {
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
            return scope.addVariableNameOccurrence(nameOccurrence);
        }
        return null;
    }
}
