/*
 * User: tom
 * Date: Jun 19, 2002
 * Time: 9:31:16 AM
 */
package net.sourceforge.pmd;

import java.util.*;

public class SymbolTable {

    private SymbolTable parent;
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
        symbols.put(symbol, Boolean.FALSE);
    }

    public void recordPossibleUsageOf(Symbol symbol) {
        if (!symbols.containsKey(symbol) && parent != null) {
            parent.recordPossibleUsageOf(symbol);
            return;
        }
        if (!symbols.containsKey(symbol) ) {
            return;
        }
        symbols.put(symbol, Boolean.TRUE);
    }

    public Iterator getUnusedSymbols() {
        List list = new ArrayList();
        for (Iterator i = symbols.keySet().iterator(); i.hasNext();) {
            Symbol symbol = (Symbol)i.next();
            if (((Boolean)symbols.get(symbol)).equals(Boolean.FALSE)) {
                list.add(symbol);
            }
        }
        return list.iterator();
    }

    public String toString() {
        StringBuffer buf = new StringBuffer();
        for (Iterator i = symbols.keySet().iterator(); i.hasNext();) {
            Symbol symbol = (Symbol)i.next();
            int usageCount = ((Integer)(symbols.get(symbol))).intValue();
            buf.append(symbol + "," + usageCount +":");
        }
        return buf.toString();
    }

}
