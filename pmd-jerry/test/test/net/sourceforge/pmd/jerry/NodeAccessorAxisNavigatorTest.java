/**
 * BSD-style license; for more info see http://pmd.sourceforge.net/license.html
 */
package test.net.sourceforge.pmd.jerry;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.fail;
import net.sourceforge.pmd.jerry.xpath.AxisEnum;
import net.sourceforge.pmd.jerry.xpath.NodeAccessorAxisNavigator;

import org.junit.Ignore;
import org.junit.Test;


/**
 * @author rpelisse
 *
 */
public class NodeAccessorAxisNavigatorTest {

	@Test
	public void constructorAndSetters() {
		NodeAccessorAxisNavigator navigator = new NodeAccessorAxisNavigator();
		assertNotNull(navigator);
		navigator = new NodeAccessorAxisNavigator(null);
		assertNotNull(navigator);
		assertNull(navigator.getNodeAccessor());
		navigator.setNodeAccessor(null);// FIXME:
		assertEquals(null,navigator.getNodeAccessor());
	}

	@Test(expected= IllegalStateException.class) public void getAxisIterator() {
		NodeAccessorAxisNavigator navigator = new NodeAccessorAxisNavigator();
		assertNotNull(navigator);
		Object node = new Object();
		navigator.getAxisIterator(node, AxisEnum.ANCESTOR);
	}

	@Ignore
	@Test public void getAxisIteratorNotSupported() {
		NodeAccessorAxisNavigator navigator = new NodeAccessorAxisNavigator();
		assertNotNull(navigator);
		Object node = new Object();
		this.m(navigator,AxisEnum.ANCESTOR_OR_SELF,node);
		this.m(navigator,AxisEnum.ANCESTOR,node);
		this.m(navigator,AxisEnum.PRECEDING_SIBLING,node);
		this.m(navigator,AxisEnum.PRECEDING,node);
		this.m(navigator,AxisEnum.DESCENDANT,node);
		this.m(navigator,AxisEnum.DESCENDANT_OR_SELF,node);
		this.m(navigator,AxisEnum.FOLLOWING,node);
		this.m(navigator,AxisEnum.FOLLOWING_SIBLING,node);
	}

	private void m(NodeAccessorAxisNavigator navigator,AxisEnum state,Object node) {

		try
		{
			navigator.getAxisIterator(node, state);
			fail();
		}
		catch ( UnsupportedOperationException e)
		{
			return ;
		}
		fail();
	}
}
