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

    private ContextManager cm = new ContextManager();

    public SymbolTable() {
        cm.openScope();
    }

    public void openScope() {
        cm.openScope();
    }

    public void leaveScope() {
        cm.leaveScope();
    }

    public void add(NameDeclaration nameDecl) {
        cm.getCurrentScope().addDeclaration(nameDecl);
    }

    public void recordOccurrence(NameOccurrence nameOccurrence) {
        cm.recordOccurrence(nameOccurrence);
    }

    public Iterator getUnusedNameDeclarations() {
        return cm.getCurrentScope().getUnusedDeclarations();
    }

    public Scope getCurrentScope() {
        return cm.getCurrentScope();
    }
}
