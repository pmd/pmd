/*
 * User: tom
 * Date: Jun 19, 2002
 * Time: 9:31:16 AM
 */
package net.sourceforge.pmd;

import java.util.*;

public class SymbolTable {

    private SymbolTable parent;
    private Set unusedSymbols = new HashSet();

    public SymbolTable() {}

    public SymbolTable(SymbolTable parent) {
        this.parent = parent;
    }

    public SymbolTable getParent() {
        return parent;
    }

    public void add(Symbol symbol) {
        if (unusedSymbols.contains(symbol)) {
            throw new RuntimeException(symbol + " is already in the symbol table");
        }
        unusedSymbols.add(symbol);
    }

    public void recordPossibleUsageOf(Symbol symbol) {
        if (!unusedSymbols.contains(symbol) && parent != null) {
            parent.recordPossibleUsageOf(symbol);
            return;
        }
        if (!unusedSymbols.contains(symbol) ) {
            return;
        }
        unusedSymbols.remove(symbol);
    }

    public Iterator getUnusedSymbols() {
        return unusedSymbols.iterator();
    }

}
