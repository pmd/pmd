/**
 * BSD-style license; for more info see http://pmd.sourceforge.net/license.html
 */
package net.sourceforge.pmd.lang.java.ast;

import static org.junit.Assert.assertEquals;

import java.util.Set;

import net.sourceforge.pmd.PMD;
import net.sourceforge.pmd.lang.java.ParserTst;

import org.junit.Test;

public class ASTPackageDeclarationTest extends ParserTst {

	private static final String PACKAGE_INFO_ANNOTATED = "@Deprecated" + PMD.EOL
			+ "package net.sourceforge.pmd.foobar;" + PMD.EOL;

	/**
	 * Regression test for bug 3524607.
	 * @throws Throwable any error
	 */
	@Test
	public void testPackageName() throws Throwable {
		Set<ASTPackageDeclaration> nodes = getNodes(ASTPackageDeclaration.class, PACKAGE_INFO_ANNOTATED);
		
		assertEquals(1, nodes.size());
		ASTPackageDeclaration packageNode = nodes.iterator().next();
		assertEquals("net.sourceforge.pmd.foobar", packageNode.getPackageNameImage());
	}
}
