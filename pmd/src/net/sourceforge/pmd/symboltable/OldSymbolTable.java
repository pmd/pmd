/*
 * User: tom
 * Date: Oct 1, 2002
 * Time: 1:49:24 PM
 */
package net.sourceforge.pmd.symboltable;

import net.sourceforge.pmd.ast.Node;

import java.util.*;

public class OldSymbolTable {

    private OldSymbolTable parent;
    private int depth;

    // a Map of Symbols to the Nodes from which they are referenced
    // symbol->List(Node,Node,...)
    // TODO
    // a single SymbolDeclaration can be referenced by several SymbolOccurrences - should these be objects?
    // TODO
    private Map symbols = new HashMap();

    public OldSymbolTable() {}

    public OldSymbolTable(OldSymbolTable parent, int depth) {
        this.parent = parent;
        this.depth = depth;
    }

    public OldSymbolTable getParent() {
        return parent;
    }

    public void add(OldSymbol symbol) {
        if (symbols.containsKey(symbol)) {
            throw new RuntimeException(symbol + " is already in the symbol table");
        }
        symbols.put(symbol, new ArrayList());
    }

    public void recordPossibleUsageOf(OldSymbol symbol, Node node) {
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
            OldSymbol symbol = (OldSymbol)i.next();
            List usages = (List)symbols.get(symbol);
            if (usages.isEmpty()) {
                unused.add(symbol);
            }
        }
        return unused.iterator();
    }

    public String toString() {
        String x = "Symbol table:" +depth+":";
        for (Iterator i = symbols.keySet().iterator(); i.hasNext();) {
            OldSymbol symbol = (OldSymbol)i.next();
            x += symbol.getImage() + ",";
        }
        return x;
    }

}
