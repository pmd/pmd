/*
 * User: tom
 * Date: Jun 19, 2002
 * Time: 11:09:06 AM
 */
package test.net.sourceforge.pmd;

import junit.framework.TestCase;
import net.sourceforge.pmd.SymbolTable;
import net.sourceforge.pmd.Symbol;

import java.util.HashMap;

public class SymbolTableTest extends TestCase {

    private static final Symbol FOO = new Symbol("foo", 10);

    public SymbolTableTest(String name) {
        super(name);
    }

    public void testAdd() {
        SymbolTable s = new SymbolTable();
        s.add(FOO);
        try {
            s.add(FOO);
        } catch (RuntimeException e) {
            return; // cool
        }
        throw new RuntimeException("Should have thrown RuntimeException");
    }

    public void testParent() {
        SymbolTable parent = new SymbolTable();
        SymbolTable child = new SymbolTable(parent);
        assertEquals(child.getParent(), parent);
    }

    public void testAddSameSymbol() {
        SymbolTable parent = new SymbolTable();
        parent.add(FOO);
        SymbolTable child = new SymbolTable(parent);
        try {
            child.add(FOO);
        } catch (RuntimeException e) {
            return; // cool
        }
        throw new RuntimeException("Should have thrown RuntimeException");
    }

    public void testParentContains2() {
        SymbolTable parent = new SymbolTable();
        SymbolTable child = new SymbolTable(parent);
        child.add(new Symbol("bar", 12));
        child.add(new Symbol("baz", 12));
        assertTrue(!parent.getUnusedSymbols().hasNext());
        assertTrue(child.getUnusedSymbols().hasNext());
    }

    public void testRecordUsage() {
        SymbolTable s = new SymbolTable();
        s.add(FOO);
        assertTrue(s.getUnusedSymbols().hasNext());
        s.recordPossibleUsageOf(FOO);
        assertTrue(!s.getUnusedSymbols().hasNext());
    }

    public void testRecordPossibleUsage() {
        SymbolTable parent = new SymbolTable();
        SymbolTable child = new SymbolTable(parent);
        child.recordPossibleUsageOf(new Symbol("bar", 10));
        assertTrue(!parent.getUnusedSymbols().hasNext());
    }

    public void testRecordPossibleUsage2() {
        SymbolTable s = new SymbolTable();
        s.recordPossibleUsageOf(new Symbol("bar", 10));
        assertTrue(!s.getUnusedSymbols().hasNext());
    }

    public void testRecordUsageParent() {
        SymbolTable parent = new SymbolTable();
        parent.add(FOO);
        SymbolTable child = new SymbolTable(parent);
        assertEquals(FOO, parent.getUnusedSymbols().next());
    }

    public void testRecordUsageParent2() {
        SymbolTable parent = new SymbolTable();
        parent.add(FOO);
        SymbolTable child = new SymbolTable(parent);
        child.recordPossibleUsageOf(FOO);
        assertTrue(!parent.getUnusedSymbols().hasNext());
    }
}
