/*
 * User: tom
 * Date: Jun 19, 2002
 * Time: 9:31:16 AM
 */
package net.sourceforge.pmd;

import java.util.*;

public class SymbolTable {

    private SymbolTable parent;
    private Set symbols = new HashSet();

    public SymbolTable() {}

    public SymbolTable(SymbolTable parent) {
        this.parent = parent;
    }

    public SymbolTable getParent() {
        return parent;
    }

    public void add(Symbol symbol) {
        if (symbols.contains(symbol)) {
            throw new RuntimeException(symbol + " is already in the symbol table");
        }
        symbols.add(symbol);
    }

    public void recordPossibleUsageOf(Symbol symbol) {
        if (!symbols.contains(symbol) && parent != null) {
            parent.recordPossibleUsageOf(symbol);
            return;
        }
        if (!symbols.contains(symbol) ) {
            return;
        }
        symbols.remove(symbol);
    }

    public Iterator getUnusedSymbols() {
        return symbols.iterator();
    }

}
