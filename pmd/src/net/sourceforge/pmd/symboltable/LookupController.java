/*
 * User: tom
 * Date: Oct 1, 2002
 * Time: 3:34:17 PM
 */
package net.sourceforge.pmd.symboltable;

import net.sourceforge.pmd.ast.SimpleNode;
import net.sourceforge.pmd.ast.ASTVariableDeclaratorId;
import net.sourceforge.pmd.ast.ASTLocalVariableDeclaration;

import java.util.List;

public class LookupController {

    private SymbolTable symbolTable;

    public LookupController(SymbolTable scopes) {
        this.symbolTable = scopes;
    }

    public void lookup(NameOccurrence nameOccurrence) {
        lookup(nameOccurrence, symbolTable.size()-1);
    }

    private void lookup(NameOccurrence nameOccurrence, int startingDepth) {
        Scope scope = (Scope)symbolTable.get(startingDepth);
        if (!scope.contains(nameOccurrence) && startingDepth>1) {
            lookup(nameOccurrence, startingDepth-1);
        }
        if (scope.contains(nameOccurrence)) {
            scope.addOccurrence(nameOccurrence);
        }
    }
}
