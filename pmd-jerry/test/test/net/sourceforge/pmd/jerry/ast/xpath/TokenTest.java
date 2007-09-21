/**
 *
 */
package test.net.sourceforge.pmd.jerry.ast.xpath;

import junit.framework.TestCase;

import org.junit.Test;

import net.sourceforge.pmd.jerry.ast.xpath.Token;


/**
 * @author rpelisse
 *
 */
public class TokenTest extends TestCase {

	private static final String IMAGE = "image";

	@Test
	public void testToString() {
		Token token = new Token();
		token.image = IMAGE;
		assertEquals(IMAGE, token.toString());
	}
}
