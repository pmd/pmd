/**
 * BSD-style license; for more info see http://pmd.sourceforge.net/license.html
 */
package net.sourceforge.pmd.lang.java.rule.comments;

import static org.junit.Assert.assertEquals;
import net.sourceforge.pmd.lang.java.ast.FormalComment;
import net.sourceforge.pmd.lang.java.ast.MultiLineComment;
import net.sourceforge.pmd.lang.java.ast.Token;

import org.junit.Test;

public class AbstractCommentRuleTest {

	private AbstractCommentRule testSubject = new AbstractCommentRule() {};

	/**
	 * Blank lines in comments should not raise an exception.
	 * See bug #1048.
	 */
	@Test
	public void testFilteredCommentIn() {
		Token token = new Token();
		token.image = "/* multi line comment with blank lines\n\n\n */";

		String filtered = testSubject.filteredCommentIn(new MultiLineComment(token));
		assertEquals("multi line comment with blank lines", filtered);

		token.image = "/** a formal comment with blank lines\n\n\n */";
		filtered = testSubject.filteredCommentIn(new FormalComment(token));
		assertEquals("a formal comment with blank lines", filtered);
	}

}
