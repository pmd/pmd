/*
 * User: tom
 * Date: Oct 1, 2002
 * Time: 3:34:17 PM
 */
package net.sourceforge.pmd.symboltable;

import net.sourceforge.pmd.ast.SimpleNode;
import net.sourceforge.pmd.ast.ASTVariableDeclaratorId;

import java.util.List;

public class LookupController {

    private SymbolTable symbolTable;

    public LookupController(SymbolTable scopes) {
        this.symbolTable = scopes;
    }

    public void lookup(NameOccurrence nameOccurrence) {
        lookup(nameOccurrence, symbolTable.peek());
    }

    private void lookup(NameOccurrence nameOccurrence, Scope scope) {
        if (!scope.contains(nameOccurrence) && scope.getParent() != null) {
            lookup(nameOccurrence, scope.getParent());
        }
        if (scope.contains(nameOccurrence)) {
            scope.addOccurrence(nameOccurrence);
        }
    }
}
