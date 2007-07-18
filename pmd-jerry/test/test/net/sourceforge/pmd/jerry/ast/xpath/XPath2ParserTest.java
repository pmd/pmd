package test.net.sourceforge.pmd.jerry.ast.xpath;

import java.io.Reader;
import java.io.StringReader;

import junit.framework.TestCase;
import net.sourceforge.pmd.jerry.ast.xpath.ASTXPath;
import net.sourceforge.pmd.jerry.ast.xpath.Node;
import net.sourceforge.pmd.jerry.ast.xpath.ParseException;
import net.sourceforge.pmd.jerry.ast.xpath.TokenMgrError;
import net.sourceforge.pmd.jerry.ast.xpath.XPath2Parser;
import net.sourceforge.pmd.jerry.ast.xpath.visitor.PrintXPath2ParserVisitor;

public class XPath2ParserTest extends TestCase {
	private static final String EOL = "\n";

	private static final class Query {
		private final String xpath;

		private final String abbreviated;

		private final String unabbreviated;

		public Query(final String xpath, final String abbreviated,
				final String unabbreviated) {
			super();
			this.xpath = xpath;
			this.abbreviated = abbreviated;
			this.unabbreviated = unabbreviated;
		}

		public String getXpath() {
			return xpath;
		}

		public String getAbbreviated() {
			return abbreviated != null ? abbreviated : xpath;
		}

		public String getUnabbreviated() {
			return unabbreviated != null ? unabbreviated : xpath;
		}
	}

	public static void main(String[] args) {
		for (int i = 0; i < VALID_QUERIES.length; i++) {
			System.out.println("new Query(\"" + VALID_QUERIES[i]
					+ "\", null, null),");
		}
	}

	private static final Query[] VALID_QUERIES = {
			/*
			 * new Query("1,1", "1, 1", "1, 1"), new
			 * Query("(fn:root(self::node()) treat as document-node())", null,
			 * null), new Query("foo -foo", "foo - foo", "child::foo -
			 * child::foo"), new Query("foo(: This is a comment :)- foo", "foo -
			 * foo", "child::foo - child::foo"), new Query("foo-foo", null,
			 * "child::foo-foo"), new Query( "foo (: commenting out a (: comment :)
			 * may be confusing, but often helpful :)", "foo", "child::foo"),
			 * new Query("for (: set up loop :) $i in $x return $i", "for $i in
			 * $x return $i", "for $i in $x return $i"), new Query( "for (: set
			 * up loop :) $i in $x,$j in $y,$k in $z return $i", "for $i in $x,
			 * $j in $y, $k in $z return $i", "for $i in $x, $j in $y, $k in $z
			 * return $i"), new Query( "5 instance (: strange place for a
			 * comment :) of xs:integer", "5 instance of xs:integer", "5
			 * instance of xs:integer"), new Query("/", null,
			 * "fn:root(self::node()) treat as document-node()"), new
			 * Query("/*", null, "fn:root(self::node()) treat as
			 * document-node()/child::*"), new Query("/ *", "/*",
			 * "fn:root(self::node()) treat as document-node()/child::*"), new
			 * Query("(/) * 5", null, "(fn:root(self::node()) treat as
			 * document-node()) * 5"), new Query("4", null, null), new
			 * Query("-4", null, null), new Query("+4", null, null), new
			 * Query("+--4", null, null), new Query("-++4", null, null), new
			 * Query("-+-+--+4", null, null), new Query("4 + (/) * 5", null, "4 +
			 * (fn:root(self::node()) treat as document-node()) * 5"), new
			 * Query("4 + /", null, "4 + fn:root(self::node()) treat as
			 * document-node()"), new Query("4 | 5", null, "4 union 5"), new
			 * Query("4 union 5", "4 | 5", null), new Query("4 intersect 5",
			 * null, null), new Query("4 except 5", null, null), new Query("4
			 * treat as xs:integer", null, null), new Query( "if
			 * ($widget1/unit-cost < $widget2/unit-cost)" + EOL + "then
			 * $widget1" + EOL + "else $widget2", "if ($widget1/unit-cost <
			 * $widget2/unit-cost) then $widget1 else $widget2", "if
			 * ($widget1/child::unit-cost < $widget2/child::unit-cost) then
			 * $widget1 else $widget2"), new Query( "if ($part/@discounted)" +
			 * EOL + "then $part/wholesale" + EOL + "else $part/retail", "if
			 * ($part/@discounted) then $part/wholesale else $part/retail", "if
			 * ($part/attribute::discounted) then $part/child::wholesale else
			 * $part/child::retail"), new Query("($x div $y) + xs:decimal($z)",
			 * null, null), new Query( "fn:error(xs:QName(\"app:err057\"),
			 * \"Unexpected value\", fn:string($v))", null, null), new
			 * Query("'this is a string with '' apostrophes and \" quotes!'",
			 * null, null), new Query( "\"this is a string with ' apostrophes
			 * and \"\" quotes!\"", null, null), new Query( "//book[author eq
			 * 'Berners-Lee']", null, "fn:root(self::node()) treat as
			 * document-node()/descendant-or-self::node()/child::book[child::author
			 * eq 'Berners-Lee']"), new Query("some $x in $expr1 satisfies $x =
			 * 47", null, null), new Query("some $x in $expr1, $y in $expr2
			 * satisfies $x = 47", null, null), new Query("every $x in $expr1
			 * satisfies $x = 47", null, null), new Query("every $x in $expr1,
			 * $y in $expr2 satisfies $x = 47", null, null), new Query(
			 * "//product[id = 47]", null, "fn:root(self::node()) treat as
			 * document-node()/descendant-or-self::node()/child::product[child::id =
			 * 47]"), new Query( "//product[id = 47]/following::node()/part[id =
			 * 48]", null, "fn:root(self::node()) treat as
			 * document-node()/descendant-or-self::node()/child::product[child::id =
			 * 47]/following::node()/child::part[child::id = 48]"), new Query(
			 * "//part[color eq \"Red\"]", null, "fn:root(self::node()) treat as
			 * document-node()/descendant-or-self::node()/child::part[child::color
			 * eq \"Red\"]"), new Query( "//part[color = \"Red\"][color eq
			 * \"Red\"]", null, "fn:root(self::node()) treat as
			 * document-node()/descendant-or-self::node()/child::part[child::color =
			 * \"Red\"][child::color eq \"Red\"]"), new Query( "$N[@x castable
			 * as xs:date][xs:date(@x) gt xs:date(\"2000-01-01\")]", null,
			 * "$N[attribute::x castable as xs:date][xs:date(attribute::x) gt
			 * xs:date(\"2000-01-01\")]"), new Query( "$N[if (@x castable as
			 * xs:date)" + EOL + "then xs:date(@x) gt xs:date(\"2000-01-01\")" +
			 * EOL + "else false()]", "$N[if (@x castable as xs:date) then
			 * xs:date(@x) gt xs:date(\"2000-01-01\") else false()]",
			 * 
			 * "$N[if (attribute::x castable as xs:date) then
			 * xs:date(attribute::x) gt xs:date(\"2000-01-01\") else false()]"),
			 * new Query("\"12.5\"", null, null), new Query("12", null, null),
			 * new Query("12.5", null, null), new Query("125E2", null, null),
			 * new Query("\"He said, \"\"I don't like it.\"\"\"", null, null),
			 * new Query("9 cast as hatsize", null, null), new Query("9 cast as
			 * hatsize?", null, null), new
			 * Query("fn:doc(\"bib.xml\")/books/book[fn:count(./author)>1]",
			 * "fn:doc(\"bib.xml\")/books/book[fn:count(./author) > 1]",
			 * "fn:doc(\"bib.xml\")/child::books/child::book[fn:count(./child::author) >
			 * 1]"), new Query("(1 to 100)[. mod 5 eq 0]", null, null), new
			 * Query("my:three-argument-function(1, 2, 3)", null, null), new
			 * Query("my:two-argument-function((1, 2), 3)", null, null), new
			 * Query("my:two-argument-function(1, ())", null, null), new
			 * Query("my:one-argument-function((1, 2, 3))", null, null), new
			 * Query("my:one-argument-function(( ))",
			 * "my:one-argument-function(())", "my:one-argument-function(())"),
			 * new Query("my:zero-argument-function( )",
			 * "my:zero-argument-function()", "my:zero-argument-function()"),
			 */
			new Query("child::div1", "div1", null),
			new Query("child::div1/child::para", "div1/para", null),
			new Query("child::chapter[2]", "chapter[2]", null),
			new Query("descendant::toy[attribute::color = \"red\"]",
					"descendant::toy[@color = \"red\"]",
					"descendant::toy[attribute::color = \"red\"]"),
			new Query("child::employee[secretary][assistant]",
					"employee[secretary][assistant]",
					"child::employee[child::secretary][child::assistant]"),
			new Query("div1//para", null,
					"child::div1/descendant-or-self::node()/child::para"),
			new Query("child::div1/descendant-or-self::node()/child::para",
					"div1//para", null),
			new Query("\"This string was terminated properly!\"", null, null),
			new Query("'This string was terminated properly!'", null, null),
			new Query("\"This string has \"\"escaping\"\"!\"", null, null),
			new Query("'This string has ''escaping''!'", null, null), };

	/*
	 * private static final String[] VALID_QUERIES = { "1,1",
	 * "(fn:root(self::node()) treat as document-node())", "foo -foo", "foo(:
	 * This is a comment :)- foo", "foo-foo", "foo (: commenting out a (:
	 * comment :) may be confusing, but often helpful :)", "for (: set up loop :)
	 * $i in $x return $i", "for (: set up loop :) $i in $x,$j in $y,$k in $z
	 * return $i", "5 instance (: strange place for a comment :) of xs:integer",
	 * "/", "/*", "/ *", "(/) * 5", "4", "-4", "+4", "+--4", "-++4", "-+-+--+4",
	 * "4 + (/) * 5", "4 + /", "4 | 5", "4 union 5", "4 intersect 5", "4 except
	 * 5", "4 treat as xs:integer", "if ($widget1/unit-cost <
	 * $widget2/unit-cost)" + EOL + "then $widget1" + EOL + "else $widget2", "if
	 * ($part/@discounted)" + EOL + "then $part/wholesale" + EOL + "else
	 * $part/retail", "($x div $y) + xs:decimal($z)",
	 * "fn:error(xs:QName(\"app:err057\"), \"Unexpected value\",
	 * fn:string($v))", "'this is a string with '' apostrophes and \" quotes!'",
	 * "\"this is a string with ' apostrophes and \"\" quotes!\"",
	 * "//book[author eq 'Berners-Lee']", "some $x in $expr1 satisfies $x = 47",
	 * "some $x in $expr1, $y in $expr2 satisfies $x = 47", "every $x in $expr1
	 * satisfies $x = 47", "every $x in $expr1, $y in $expr2 satisfies $x = 47",
	 * "//product[id = 47]", "//product[id = 47]/following::node()/part[id =
	 * 48]", "//part[color eq \"Red\"]", "//part[color = \"Red\"][color eq
	 * \"Red\"]", "$N[@x castable as xs:date][xs:date(@x) gt
	 * xs:date(\"2000-01-01\")]", "$N[if (@x castable as xs:date)" + EOL + "then
	 * xs:date(@x) gt xs:date(\"200-01-01\")" + EOL + "else false()]",
	 * "\"12.5\"", "12", "12.5", "125E2", "\"He said, \"\"I don't like
	 * it.\"\"\"", "9 cast as hatsize", "\"He said, \"\"I don't like it.\"\"\"",
	 * "9 cast as hatsize?",
	 * "fn:doc(\"bib.xml\")/books/book[fn:count(./author)>1]", "(1 to 100)[. mod
	 * 5 eq 0]", "my:three-argument-function(1, 2, 3)",
	 * "my:two-argument-function((1, 2), 3)", "my:two-argument-function(1, ())",
	 * "my:one-argument-function((1, 2, 3))", "my:one-argument-function(( ))",
	 * "my:zero-argument-function( )", "child::div1/child::para",
	 * "child::chapter[2]", "descendant::toy[attribute::color = \"red\"]",
	 * "child::employee[secretary][assistant]", "div1//para",
	 * "child::div1/descendant-or-self::node()/child::para", "\"This string was
	 * terminated properly!\"", "'This string was terminated properly!'",
	 * "\"This string has \"\"escaping\"\"!\"", "'This string has
	 * ''escaping''!'", };
	 * 
	 */
	private static final String[] INVALID_QUERIES = { "foo- foo", "/*5",
			"/ * 5", "4 + / * 5", "10div 3", "10 div3", "10div3",
			"\"This string was not terminated properly!",
			"\'This string was not terminated properly!",
			"\"This string has missing \"escaping\"!\"",
			"'This string has missing 'escaping'!'",
			"5 (: This is a unterminated comment!", };

	public void testValidQueries() {
		for (int i = 0; i < VALID_QUERIES.length; i++) {
			Query query = VALID_QUERIES[i];
			try {
				ASTXPath xpath = parse(query.getXpath());
				// TODO Test the following assertions are true:
				// 1) Abbreviate(XPath) ==
				// Abbreviate(Unabbreviate(Abbreviate(XPath)))
				// 2) Unabbreviate(XPath) ==
				// Unabbreviate(Abbreviate(Unabbreviate(XPath)))

				PrintXPath2ParserVisitor printVisitor = new PrintXPath2ParserVisitor(
						PrintXPath2ParserVisitor.PrintModeEnum.ABBREVIATE);
				xpath.jjtAccept(printVisitor, null);
				System.out
						.println("Abbreviated:   " + printVisitor.getOutput());
				assertEquals("Abbreviated", query.getAbbreviated(),
						printVisitor.getOutput());

				printVisitor = new PrintXPath2ParserVisitor(
						PrintXPath2ParserVisitor.PrintModeEnum.UNABBREVIATE);
				xpath.jjtAccept(printVisitor, null);
				System.out
						.println("Unabbreviated: " + printVisitor.getOutput());
				assertEquals("Abbreviated", query.getUnabbreviated(),
						printVisitor.getOutput());

				// CoreXPath2ParserVisitor visitor = new
				// CoreXPath2ParserVisitor();
			} catch (ParseException e) {
				e.printStackTrace();
				fail("Should have been able to parse query: " + query);
			} catch (TokenMgrError e) {
				e.printStackTrace();
				fail("Should have been able to parse query: " + query);
			}
		}
	}

	public void testInvalidQueries() {
		for (int i = 0; i < INVALID_QUERIES.length; i++) {
			String query = INVALID_QUERIES[i];
			try {
				ASTXPath xpath = parse(query);
				System.out.println("Should not parse: "
						+ getSyntaxStructure(xpath));
				fail("Should not have been able to parse query: " + query);
			} catch (ParseException e) {
				// Good!
			} catch (TokenMgrError e) {
				// Good!
			}
		}
	}

	private ASTXPath parse(String query) throws ParseException {
		System.out.println();
		System.out.println("Parsing:       " + query);
		Reader reader = new StringReader(query);
		XPath2Parser parser = new XPath2Parser(reader);
		return parser.XPath();
	}

	private String getSyntaxStructure(Node node) {
		StringBuffer buf = new StringBuffer();
		getSyntaxStructure(buf, node);
		return buf.toString();
	}

	private void getSyntaxStructure(StringBuffer buf, Node node) {
		buf.append(node);
		for (int i = 0; i < node.jjtGetNumChildren(); i++) {
			buf.append("[");
			getSyntaxStructure(buf, node.jjtGetChild(i));
			buf.append("]");
		}
	}
}
