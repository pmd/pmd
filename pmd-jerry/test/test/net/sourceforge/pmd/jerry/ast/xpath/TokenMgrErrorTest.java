/**
 *
 */
package test.net.sourceforge.pmd.jerry.ast.xpath;

import static org.junit.Assert.assertEquals;
import net.sourceforge.pmd.jerry.ast.xpath.TokenMgrError;

import org.junit.Before;
import org.junit.Test;

/**
 * @author rpelisse
 *
 */
public class TokenMgrErrorTest {

	private static final String MESSAGE = "Message";
	private static final int REASON = 0;

	private TokenMgrError tokenMgr;

	@Before
	public void buildTestTarget() {
		tokenMgr = new TokenMgrError(MESSAGE, REASON);
	}

	@Test
	public void getMessage() {
		assertEquals(MESSAGE,tokenMgr.getMessage());
	}

	@Test
	public void addEscapes() {
		TokenMgrErrorDummy dummy = new TokenMgrErrorDummy();
		assertEquals(dummy.escapes("\b"),"\\b");
		assertEquals(dummy.escapes("\t"),"\\t");
		assertEquals(dummy.escapes("\n"),"\\n");
		assertEquals(dummy.escapes("\f"),"\\f");
		assertEquals(dummy.escapes("\r"),"\\r");
		assertEquals(dummy.escapes("\\"),"\\\\");
	}

	private class TokenMgrErrorDummy extends TokenMgrError {

		private static final long serialVersionUID = 1L;

		public String escapes(String str) {
			return super.addEscapes(str);
		}
	}
}
