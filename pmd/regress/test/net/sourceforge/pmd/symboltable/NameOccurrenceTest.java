/*
 * User: tom
 * Date: Oct 2, 2002
 * Time: 9:15:27 AM
 */
package test.net.sourceforge.pmd.symboltable;

import junit.framework.TestCase;
import net.sourceforge.pmd.symboltable.NameOccurrence;

public class NameOccurrenceTest extends TestCase {

    public void testConstructor() {
        NameOccurrence occ = new NameOccurrence(NameDeclarationTest.FOO_NODE);
        assertEquals("foo", occ.getObjectName());
        occ = new NameOccurrence(NameDeclarationTest.createNode("foo.bar",10));
        assertEquals("foo", occ.getObjectName());
    }

}
