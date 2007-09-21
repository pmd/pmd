/**
 *
 */
package test.net.sourceforge.pmd.jerry.ast.xpath.visitor;

import static org.junit.Assert.assertEquals;
import net.sourceforge.pmd.jerry.ast.xpath.visitor.AbstractPrintVisitor;

import org.junit.Before;
import org.junit.Test;


/**
 * @author rpelisse
 *
 */
public class AbstractPrintVisitorTest {

	private AbstractPrintVisitor visitor;

	private static final String testString = "A string";
	private static final String testObject = "String as an object";
	/**
	 * Dummy concrete implementation to test Abstract classe.
	 * @author rpelisse
	 *
	 */
	private class ConcretePrintVisitor extends AbstractPrintVisitor {

		public static final int INDENT_LEVEL = 4;

		public int getIndentLevel() {
			return super.indentLevel;
		}
		public ConcretePrintVisitor() {
			super.indentLevel = INDENT_LEVEL;
		}
		public void print(String s){
			super.print(s);
		}

		public void print(Object o){
			super.print(o);
		}

		public void println(String s) {
			super.println(s);
		}

		public void println(Object o) {
			super.println(o);
		}

		public void println() {
			super.println();
		}

		public void applyIndent() {
			super.applyIndent();
		}

		public void incrementIndent() {
			super.incrementIndent();
		}

		public void decrementIndent() {
			super.decrementIndent();
		}
	}

	@Before
	public void buildVisitor()
	{
		this.visitor = new ConcretePrintVisitor();
	}

	@Test
	public void print() {
		// Here we cast to access ConcretePrintVisitor methods
		((ConcretePrintVisitor)visitor).print(testString);
		((ConcretePrintVisitor)visitor).print((Object)testObject);
		// Now we test AbstractPrintVisitor
		assertEquals(testString + testObject,visitor.getOutput());
	}

	@Test
	public void println() {
		// Here we cast to access ConcretePrintVisitor methods
		((ConcretePrintVisitor)visitor).println(testString);
		((ConcretePrintVisitor)visitor).println((Object)testObject);
		String result = visitor.getOutput();
		// keeping up to indentLevel
		String indentLevel = "";
		for (int i = 0; i < ((ConcretePrintVisitor)visitor).getIndentLevel() ; i++ )
			indentLevel += "\t";
		String excepted = testString + AbstractPrintVisitor.EOL +
		indentLevel + testObject + AbstractPrintVisitor.EOL;
		assertEquals(excepted,result);
	}

	@Test
	public void incrementOperations() {
		assertEquals(ConcretePrintVisitor.INDENT_LEVEL,((ConcretePrintVisitor)visitor).getIndentLevel());
		((ConcretePrintVisitor)visitor).incrementIndent();
		assertEquals(ConcretePrintVisitor.INDENT_LEVEL + 1,((ConcretePrintVisitor)visitor).getIndentLevel());
		((ConcretePrintVisitor)visitor).decrementIndent();
		assertEquals(ConcretePrintVisitor.INDENT_LEVEL,((ConcretePrintVisitor)visitor).getIndentLevel());
	}

}