/*
 * User: tom
 * Date: Jun 19, 2002
 * Time: 9:31:16 AM
 */
package net.sourceforge.pmd.symboltable;

import net.sourceforge.pmd.ast.Node;
import net.sourceforge.pmd.ast.SimpleNode;

import java.util.*;

public class SymbolTable {

    private ContextManager cm;
    private LookupController lookupController;

    public SymbolTable() {
        // this sharing of the scopes ArrayList seems evil.  Time will tell.
        List scopes = new ArrayList();
        lookupController = new LookupController(scopes);
        cm = new ContextManager(scopes);
        cm.openScope(new LocalScope()); // TODO this should be ClassScope, probably
    }

    public void openScope(Scope scope) {
        cm.openScope(scope);
    }

    public void leaveScope() {
        cm.leaveScope();
    }

    public void addDeclaration(NameDeclaration nameDecl) {
        cm.getCurrentScope().addDeclaration(nameDecl);
    }

    public void lookup(NameOccurrence nameOccurrence) {
        lookupController.lookup(nameOccurrence);
    }

    public Iterator getUnusedNameDeclarations() {
        return cm.getCurrentScope().getUnusedDeclarations();
    }

    public Scope getCurrentScope() {
        return cm.getCurrentScope();
    }
}
