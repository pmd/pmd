/*
 * User: tom
 * Date: Jun 19, 2002
 * Time: 9:31:16 AM
 */
package net.sourceforge.pmd;

import java.util.HashMap;
import java.util.List;
import java.util.ArrayList;
import java.util.Iterator;

public class SymbolTable {

    private SymbolTable parent;
    private HashMap usageCounts = new HashMap();

    public SymbolTable() {}

    public SymbolTable(SymbolTable parent) {
        this.parent = parent;
    }

    public SymbolTable getParent() {
        return parent;
    }

    public void add(Symbol symbol) {
        if (usageCounts.containsKey(symbol)) {
            throw new RuntimeException(symbol + " is already in the symbol table");
        }
        if (parent != null && parent.contains(symbol)) {
            throw new RuntimeException(symbol + " is already in the parent symbol table");
        }
        usageCounts.put(symbol, new Integer(0));
    }

    public void recordPossibleUsageOf(Symbol symbol) {
        if (!usageCounts.containsKey(symbol) && parent != null) {
            parent.recordPossibleUsageOf(symbol);
            return;
        }
        if (!usageCounts.containsKey(symbol) ) {
            return;
        }
        Integer usageCount = (Integer)usageCounts.get(symbol);
        usageCount = new Integer(usageCount.intValue() + 1);
        usageCounts.put(symbol, usageCount);
    }

    public Iterator getUnusedSymbols() {
        List list = new ArrayList();
        for (Iterator i = usageCounts.keySet().iterator(); i.hasNext();) {
            Symbol symbol = (Symbol)i.next();
            int usageCount = ((Integer)(usageCounts.get(symbol))).intValue();
            if (usageCount == 0) {
                list.add(symbol);
            }
        }
        return list.iterator();
    }

    public String toString() {
        StringBuffer buf = new StringBuffer();
        for (Iterator i = usageCounts.keySet().iterator(); i.hasNext();) {
            Symbol symbol = (Symbol)i.next();
            int usageCount = ((Integer)(usageCounts.get(symbol))).intValue();
            buf.append(symbol + "," + usageCount +":");
        }
        return buf.toString();
    }

    protected boolean contains(Symbol symbol) {
        if (usageCounts.containsKey(symbol)) {
            return true;
        }
        if (parent == null) {
            return false;
        }
        return parent.contains(symbol);
    }

}
