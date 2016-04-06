package net.sourceforge.pmd.lang.apex.ast;

/**
 * BSD-style license; for more info see http://pmd.sourceforge.net/license.html
 */

import static org.junit.Assert.assertEquals;

import java.io.File;
import java.io.IOException;
import java.io.Reader;
import java.io.StringReader;
import java.io.StringWriter;
import java.util.List;

import org.apache.commons.io.FileUtils;
import org.junit.Assert;
import org.junit.Test;

import apex.jorje.semantic.ast.compilation.Compilation;
import net.sourceforge.pmd.lang.apex.ApexParserOptions;
import net.sourceforge.pmd.lang.ast.Node;

public class ApexParserTest {

	@Test
	public void understandsSimpleFile() {

		// Setup
		String code = "@isTest\n public class SimpleClass {\n" + "    @isTest\n public static void testAnything() {\n"
				+ "        \n" + "    }\n" + "}";

		// Exercise
		ApexNode<Compilation> rootNode = parse(code);

		// Verify
		List<ASTMethod> methods = rootNode.findDescendantsOfType(ASTMethod.class);
		assertEquals(4, methods.size());
	}

	@Test
	public void verifyLineColumNumbers() {
		String code = "public class SimpleClass {\n" // line 1
				+ "    public void method1() {\n" // line 2
				+ "        System.out.println(\"abc\");\n" // line 3
				+ "        // this is a comment\n" // line 4
				+ "    }\n" // line 5
				+ "}\n"; // line 6

		ApexNode<Compilation> rootNode = parse(code);

		assertPosition(rootNode, 1, 1, 6, 2); // whole source code
		// Modifier of the class - doesn't work. This node just sees the
		// identifier ("SimpleClass")
		// assertPosition(rootNode.jjtGetChild(0), 1, 1, 1, 6); // "public"

		// "method1" - starts with identifier until end of source because there
		// is no next sibling
		Node method1 = rootNode.jjtGetChild(1);
		assertPosition(method1, 2, 17, 6, 2);
		// Modifier of method1 - doesn't work. This node just sees the
		// identifier ("method1")
		// assertPosition(method1.jjtGetChild(0), 2, 17, 2, 20); // "public" for
		// method1

		// BlockStatement - the whole method body
		Node blockStatement = method1.jjtGetChild(1);
		assertPosition(blockStatement, 2, 27, 6, 2);

		// the expression ("System.out...") - goes until end of file (no next
		// sibling)
		Node expressionStatement = blockStatement.jjtGetChild(0);
		assertPosition(expressionStatement, 3, 9, 6, 2);
	}

	private static void assertPosition(Node node, int beginLine, int beginColumn, int endLine, int endColumn) {
		assertEquals("Wrong begin line", beginLine, node.getBeginLine());
		assertEquals("Wrong begin column", beginColumn, node.getBeginColumn());
		assertEquals("Wrong end line", endLine, node.getEndLine());
		assertEquals("Wrong end column", endColumn, node.getEndColumn());
	}

	@Test
	public void parsesRealWorldClasses() {
		try {
			File directory = new File("src/test/resources");
			File[] fList = directory.listFiles();

			for (File file : fList) {
				if (file.isFile() && file.getName().endsWith(".cls")) {
					String sourceCode = FileUtils.readFileToString(file);
					ApexNode<Compilation> rootNode = parse(sourceCode);
				}
			}
		}
		catch (IOException e) {
			Assert.fail();
		}

	}

	// TEST HELPER

	private ApexNode<Compilation> parse(String code) {
		ApexParser parser = new ApexParser(new ApexParserOptions());
		Reader reader = new StringReader(code);
		return parser.parse(reader);
	}

	private void dumpNode(Node node) {
		DumpFacade facade = new DumpFacade();
		StringWriter writer = new StringWriter();
		facade.initializeWith(writer, "", true, (ApexNode<?>) node);
		facade.visit((ApexNode<?>) node, "");
		System.out.println(writer.toString());
	}
}
