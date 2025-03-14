/**
 * BSD-style license; for more info see http://pmd.sourceforge.net/license.html
 */

package net.sourceforge.pmd.lang.plsql.symboltable;

import java.util.HashSet;
import java.util.Set;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import net.sourceforge.pmd.lang.symboltable.NameDeclaration;
import net.sourceforge.pmd.lang.symboltable.Scope;

public class Search {
    private static final Logger LOG = LoggerFactory.getLogger(Search.class);

    private PLSQLNameOccurrence occ;
    private Set<NameDeclaration> declarations = new HashSet<>();

    public Search(PLSQLNameOccurrence occ) {
        LOG.trace(
                "new search for {} {}",
                occ.isMethodOrConstructorInvocation() ? "method" : "variable",
                occ);
        this.occ = occ;
    }

    public void execute() {
        Set<NameDeclaration> found = searchUpward(occ, occ.getLocation().getScope());
        LOG.trace("found {}", found);
        declarations.addAll(found);
    }

    public void execute(Scope startingScope) {
        Set<NameDeclaration> found = searchUpward(occ, startingScope);
        LOG.trace("found {}", found);
        declarations.addAll(found);
    }

    public Set<NameDeclaration> getResult() {
        return declarations;
    }

    private Set<NameDeclaration> searchUpward(PLSQLNameOccurrence nameOccurrence, Scope scope) {
        LOG.trace("checking scope {} for name occurrence {}", scope, nameOccurrence);
        if (!scope.contains(nameOccurrence) && scope.getParent() != null) {
            LOG.trace("moving up fm {} to {}", scope, scope.getParent());
            return searchUpward(nameOccurrence, scope.getParent());
        }
        if (scope.contains(nameOccurrence)) {
            LOG.trace("found it!");
            return scope.addNameOccurrence(nameOccurrence);
        }
        return new HashSet<>();
    }
}
