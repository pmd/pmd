/**
 * BSD-style license; for more info see http://pmd.sourceforge.net/license.html
 */
package test.net.sourceforge.pmd.jerry.ast.xpath;

import junit.framework.TestCase;
import net.sourceforge.pmd.jerry.ast.xpath.Node;
import net.sourceforge.pmd.jerry.ast.xpath.SimpleNode;

import org.junit.Test;


/**
 * @author Romain PELISSE, belaran@gmail.com
 *
 */
public class SimpleNodeTest extends TestCase {

	private static final int ID = 1;

	@Test
	public void testAddingChildrenBeyondActualLimits() {
		final int POS = 3;
		Node node = new SimpleNode(ID);
		assertTrue(POS > node.jjtGetNumChildren());
		Node child = new SimpleNode(ID + 1);
		node.jjtAddChild(child, POS);
		assertEquals(child,node.jjtGetChild(POS));
		// Now that children is no longer 'null', we'll add an another
		// beyond the limit
		Node secondChild = new SimpleNode(ID + 2);
		node.jjtAddChild(secondChild, POS + 1);
		assertEquals(secondChild,node.jjtGetChild(POS + 1));
	}
}
