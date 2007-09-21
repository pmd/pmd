/**
 *
 */
package test.net.sourceforge.pmd.jerry.ast.xpath;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNull;
import net.sourceforge.pmd.jerry.ast.xpath.ParseException;
import net.sourceforge.pmd.jerry.ast.xpath.Token;

import org.junit.Test;

/**
 * @author rpelisse
 *
 */
public class ParseExceptionTest {

	private static final String MSSG = "Message";
	private static final int SIZE = 5;

	@Test
	public void testGetMessage()
	{
		// First case : exception with no message
		ParseException parseException = new ParseException();
		assertNull(parseException.getMessage());

		// Simple message
		parseException = new ParseException(MSSG);
		assertEquals(MSSG,parseException.getMessage());

		// specialConstructor message
		int[][] exceptedToken = new int[SIZE][];
	    for (int i = 0; i < SIZE; i++) {
	      int[] row = new int[1];
	      row[0] = 1;
	      exceptedToken[i] = row;
	    }
		String[] tokenImageValue = new String[1];
		tokenImageValue[0] = "dummy-string";
		parseException = new ParseException(new Token(),exceptedToken,tokenImageValue);
		assertNull(parseException.getMessage());
	}
}
