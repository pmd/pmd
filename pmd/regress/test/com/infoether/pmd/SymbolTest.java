/*
 * User: tom
 * Date: Jun 19, 2002
 * Time: 11:59:24 AM
 */
package test.com.infoether.pmd;

import junit.framework.TestCase;
import com.infoether.pmd.Symbol;

public class SymbolTest extends TestCase {

    public SymbolTest(String name) {
        super(name);
    }

    public void testBasic() {
        Symbol s = new Symbol("foo", 10);
        assertEquals(10, s.getLine());
        assertEquals("foo", s.getImage());
        assertEquals(s, new Symbol("foo", 5));
        assertEquals(s.hashCode(), new Symbol("foo", 6).hashCode());
    }
}
