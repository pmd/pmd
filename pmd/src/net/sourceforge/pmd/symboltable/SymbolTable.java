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

    private SymbolTable parent;

    // a Map of Symbols to the Nodes from which they are referenced
    // symbol->List(Node,Node,...)
    private Map symbols = new HashMap();

    public SymbolTable() {}

    public SymbolTable(SymbolTable parent) {
        this.parent = parent;
    }

    public SymbolTable getParent() {
        return parent;
    }

    public void add(Symbol symbol) {
        if (symbols.containsKey(symbol)) {
            throw new RuntimeException(symbol + " is already in the symbol table");
        }
        symbols.put(symbol, new ArrayList());
    }

    public void recordPossibleUsageOf(Symbol symbol, Node node) {
        if (!symbols.containsKey(symbol) && parent != null) {
            parent.recordPossibleUsageOf(symbol, node);
            return;
        }
        if (!symbols.containsKey(symbol) ) {
            return;
        }
        List symbolUsages = (List)symbols.get(symbol);
        symbolUsages.add(node);
    }

    public Iterator getUnusedSymbols() {
        List unused = new ArrayList();
        for (Iterator i = symbols.keySet().iterator(); i.hasNext();) {
            Symbol symbol = (Symbol)i.next();
            List usages = (List)symbols.get(symbol);
            if (usages.isEmpty()) {
                unused.add(symbol);
            }
        }
        return unused.iterator();
    }

}
