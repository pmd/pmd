package test.net.sourceforge.pmd.jerry.ast.xpath;

import java.io.Reader;
import java.io.StringReader;

import junit.framework.TestCase;
import net.sourceforge.pmd.jerry.ast.xpath.ASTXPath;
import net.sourceforge.pmd.jerry.ast.xpath.Node;
import net.sourceforge.pmd.jerry.ast.xpath.ParseException;
import net.sourceforge.pmd.jerry.ast.xpath.TokenMgrError;
import net.sourceforge.pmd.jerry.ast.xpath.XPath2Parser;
import net.sourceforge.pmd.jerry.ast.xpath.visitor.CoreXPath2ParserVisitor;
import net.sourceforge.pmd.jerry.ast.xpath.visitor.PrintXPath2ParserVisitor;
import test.net.sourceforge.pmd.jerry.ast.Query;

public class XPath2ParserTest extends TestCase {
	private static final boolean AST_DUMP = false;

	private static final String EOL = "\n";

	private static final Query[] VALID_QUERIES = {
			new Query("1,1", "1, 1", "1, 1", "{1, 1}"),
			new Query(
					"((fn:root(self::node()) treat as document-node()))",
					"(/)",
					null,
					"{((typeswitch(fn:root(self::node())) case $fs:new as document-node() return $fs:new default $fs:new return fn:error()))}"),
			new Query(
					"foo -foo",
					"foo - foo",
					"child::foo - child::foo",
					"{fs:minus(fs:convert-operand(fn:data((child::foo)), 1.0E0), fs:convert-operand(fn:data((child::foo)), 1.0E0))}"),
			new Query(
					"foo(: This is a comment :)- foo",
					"foo - foo",
					"child::foo - child::foo",
					"{fs:minus(fs:convert-operand(fn:data((child::foo)), 1.0E0), fs:convert-operand(fn:data((child::foo)), 1.0E0))}"),
			new Query("foo-foo", null, "child::foo-foo", "{child::foo-foo}"),
			new Query(
					"foo (: commenting out a (: comment :) may be confusing, but often helpful :)",
					"foo", "child::foo", "{child::foo}"),
			new Query("for (: set up loop :) $i in $x return $i",
					"for $i in $x return $i", "for $i in $x return $i",
					"{for $i in $x return $i}"),
			new Query(
					"for (: set up loop :) $i in $x,$j in $y,$k in $z return $i",
					"for $i in $x, $j in $y, $k in $z return $i",
					"for $i in $x, $j in $y, $k in $z return $i",
					"{for $i in $x for $j in $y for $k in $z return $i}"),
			new Query(
					"5 instance (: strange place for a comment :) of xs:integer",
					"5 instance of xs:integer",
					"5 instance of xs:integer",
					"{typeswitch(5) case $fs:new as xs:integer return fn:true() default $fs:new return fn:false()}"),
			new Query(
					"fn:doc(\"zoo.xml\")/fn:id('tiger')",
					null,
					null,
					"{fs:apply-ordering-mode(fs:distinct-doc-order-or-atomic-sequence(let $fs:sequence as node()* := fn:doc(\"zoo.xml\") return let $fs:last := fn:count($fs:sequence) return for $fs:dot at $fs:position in $fs:sequence return fn:id('tiger')))}"),
			new Query(
					"/",
					null,
					"(fn:root(self::node()) treat as document-node())",
					"{(typeswitch(fn:root(self::node())) case $fs:new as document-node() return $fs:new default $fs:new return fn:error())}"),
			new Query(
					"/*",
					null,
					"(fn:root(self::node()) treat as document-node())/child::*",
					"TODO"),
			new Query(
					"/ *",
					"/*",
					"(fn:root(self::node()) treat as document-node())/child::*",
					"TODO"),
			new Query("(/) * 5", null,
					"((fn:root(self::node()) treat as document-node())) * 5",
					"TODO"),
			new Query("4", null, null, "TODO"),
			new Query("-4", null, null, "TODO"),
			new Query("+4", null, null, "TODO"),
			new Query("+--4", null, null, "TODO"),
			new Query("-++4", null, null, "TODO"),
			new Query("-+-+--+4", null, null, "TODO"),
			new Query(
					"4 + (/) * 5",
					null,
					"4 + ((fn:root(self::node()) treat as document-node())) * 5",
					"TODO"),
			new Query("4 + /", null,
					"4 + (fn:root(self::node()) treat as document-node())",
					"TODO"),
			new Query("4 | 5", null, "4 union 5", "TODO"),
			new Query("4 union 5", "4 | 5", null, "TODO"),
			new Query("4 intersect 5", null, null, "TODO"),
			new Query("4 except 5", null, null, "TODO"),
			new Query("4 treat as xs:integer", null, null, "TODO"),
			new Query("4 treat as xs:integer?", null, null, "TODO"),
			new Query("4 treat as xs:integer+", null, null, "TODO"),
			new Query("4 treat as xs:integer*", null, null, "TODO"),
			new Query(
					"if ($widget1/unit-cost < $widget2/unit-cost)" + EOL
							+ "then $widget1" + EOL + "else $widget2",
					"if ($widget1/unit-cost < $widget2/unit-cost) then $widget1 else $widget2",
					"if ($widget1/child::unit-cost < $widget2/child::unit-cost) then $widget1 else $widget2",
					"TODO"),
			new Query(
					"if ($part/@discounted)" + EOL + "then $part/wholesale"
							+ EOL + "else $part/retail",
					"if ($part/@discounted) then $part/wholesale else $part/retail",
					"if ($part/attribute::discounted) then $part/child::wholesale else $part/child::retail",
					"TODO"),
			new Query("($x div $y) + xs:decimal($z)", null, null, "TODO"),
			new Query(
					"fn:error(xs:QName(\"app:err057\"), \"Unexpected value\", fn:string($v))",
					null, null, "TODO"),
			new Query("'this is a string with '' apostrophes and \" quotes!'",
					null, null, "TODO"),
			new Query(
					"\"this is a string with ' apostrophes and \"\" quotes!\"",
					null, null, "TODO"),
			new Query(
					"//book[author eq 'Berners-Lee']",
					null,
					"(fn:root(self::node()) treat as document-node())/descendant-or-self::node()/child::book[child::author eq 'Berners-Lee']",
					"TODO"),
			new Query("some $x in $expr1 satisfies $x = 47", null, null, "TODO"),
			new Query("some $x in $expr1, $y in $expr2 satisfies $x = 47",
					null, null, "TODO"),
			new Query("every $x in $expr1 satisfies $x = 47", null, null,
					"TODO"),
			new Query("every $x in $expr1, $y in $expr2 satisfies $x = 47",
					null, null, "TODO"),
			new Query(
					"//product[id = 47]",
					null,
					"(fn:root(self::node()) treat as document-node())/descendant-or-self::node()/child::product[child::id = 47]",
					"TODO"),
			new Query(
					"//product[id = 47]/following::node()/part[id = 48]",
					null,
					"(fn:root(self::node()) treat as document-node())/descendant-or-self::node()/child::product[child::id = 47]/following::node()/child::part[child::id = 48]",
					"TODO"),
			new Query(
					"//part[color eq \"Red\"]",
					null,
					"(fn:root(self::node()) treat as document-node())/descendant-or-self::node()/child::part[child::color eq \"Red\"]",
					"TODO"),
			new Query(
					"//part[color = \"Red\"][color eq \"Red\"]",
					null,
					"(fn:root(self::node()) treat as document-node())/descendant-or-self::node()/child::part[child::color = \"Red\"][child::color eq \"Red\"]",
					"TODO"),
			new Query(
					"$N[@x castable as xs:date][xs:date(@x) gt xs:date(\"2000-01-01\")]",
					null,
					"$N[attribute::x castable as xs:date][xs:date(attribute::x) gt xs:date(\"2000-01-01\")]",
					"TODO"),
			new Query(
					"$N[if (@x castable as xs:date)" + EOL
							+ "then xs:date(@x) gt xs:date(\"2000-01-01\")"
							+ EOL + "else false()]",
					"$N[if (@x castable as xs:date) then xs:date(@x) gt xs:date(\"2000-01-01\") else false()]",
					"$N[if (attribute::x castable as xs:date) then xs:date(attribute::x) gt xs:date(\"2000-01-01\") else false()]",
					"TODO"),
			new Query("\"12.5\"", null, null, "TODO"),
			new Query("12", null, null, "TODO"),
			new Query("12.5", null, null, "TODO"),
			new Query("125E2", null, null, "TODO"),
			new Query("\"He said, \"\"I don't like it.\"\"\"", null, null,
					"TODO"),
			new Query("9 cast as hatsize", null, null, "TODO"),
			new Query("9 cast as hatsize?", null, null, "TODO"),
			new Query(
					"fn:doc(\"bib.xml\")/books/book[fn:count(./author)>1]",
					"fn:doc(\"bib.xml\")/books/book[fn:count(./author) > 1]",
					"fn:doc(\"bib.xml\")/child::books/child::book[fn:count(./child::author) > 1]",
					"TODO"),
			new Query("(1 to 100)[. mod 5 eq 0]", null, null, "TODO"),
			new Query("my:three-argument-function(1, 2, 3)", null, null, "TODO"),
			new Query("my:two-argument-function((1, 2), 3)", null, null, "TODO"),
			new Query("my:two-argument-function(1, ())", null, null, "TODO"),
			new Query("my:one-argument-function((1, 2, 3))", null, null, "TODO"),
			new Query("my:one-argument-function(( ))",
					"my:one-argument-function(())",
					"my:one-argument-function(())", "TODO"),
			new Query("my:zero-argument-function( )",
					"my:zero-argument-function()",
					"my:zero-argument-function()", "TODO"),
			new Query("..", null, "parent::node()", "TODO"),
			new Query("parent::node()", "..", null, "TODO"),
			new Query("child::div1", "div1", null, "TODO"),
			new Query("child::div1/child::para", "div1/para", null, "TODO"),
			new Query("child::chapter[2]", "chapter[2]", null, "TODO"),
			new Query("descendant::toy[attribute::color = \"red\"]",
					"descendant::toy[@color = \"red\"]",
					"descendant::toy[attribute::color = \"red\"]", "TODO"),
			new Query("child::employee[secretary][assistant]",
					"employee[secretary][assistant]",
					"child::employee[child::secretary][child::assistant]",
					"TODO"),
			new Query("div1//para", null,
					"child::div1/descendant-or-self::node()/child::para",
					"TODO"),
			new Query("child::div1/descendant-or-self::node()/child::para",
					"div1//para", null, "TODO"),
			new Query("\"This string was terminated properly!\"", null, null,
					"TODO"),
			new Query("'This string was terminated properly!'", null, null,
					"TODO"),
			new Query("\"This string has \"\"escaping\"\"!\"", null, null,
					"TODO"),
			new Query("'This string has ''escaping''!'", null, null, "TODO"),
			new Query("para", null, "child::para", "TODO"),
			new Query("*", null, "child::*", "TODO"),
			new Query("text()", null, "child::text()", "TODO"),
			new Query("@name", null, "attribute::name", "TODO"),
			new Query("@*", null, "attribute::*", "TODO"),
			new Query("para[1]", null, "child::para[1]", "TODO"),
			new Query("para[fn:last()]", null, "child::para[fn:last()]", "TODO"),
			new Query("*/para", null, "child::*/child::para", "TODO"),
			new Query(
					"/book/chapter[5]/section[2]",
					null,
					"(fn:root(self::node()) treat as document-node())/child::book/child::chapter[5]/child::section[2]",
					"TODO"),
			new Query("chapter//para", null,
					"child::chapter/descendant-or-self::node()/child::para",
					"TODO"),
			new Query(
					"//para",
					null,
					"(fn:root(self::node()) treat as document-node())/descendant-or-self::node()/child::para",
					"TODO"),
			new Query(
					"//@version",
					null,
					"(fn:root(self::node()) treat as document-node())/descendant-or-self::node()/attribute::version",
					"TODO"),
			new Query(
					"//list/member",
					null,
					"(fn:root(self::node()) treat as document-node())/descendant-or-self::node()/child::list/child::member",
					"TODO"),
			new Query(".//para", null,
					"./descendant-or-self::node()/child::para", "TODO"),
			new Query("..", null, "parent::node()", "TODO"),
			new Query("../@lang", null, "parent::node()/attribute::lang",
					"TODO"),
			new Query("para[@type = \"warning\"]", null,
					"child::para[attribute::type = \"warning\"]", "TODO"),
			new Query("para[@type = \"warning\"][5]", null,
					"child::para[attribute::type = \"warning\"][5]", "TODO"),
			new Query("para[5][@type = \"warning\"]", null,
					"child::para[5][attribute::type = \"warning\"]", "TODO"),
			new Query("chapter[title = \"Introduction\"]", null,
					"child::chapter[child::title = \"Introduction\"]", "TODO"),
			new Query("chapter[title]", null, "child::chapter[child::title]",
					"TODO"),
			new Query(
					"employee[@secretary and @assistant]",
					null,
					"child::employee[attribute::secretary and attribute::assistant]",
					"TODO"),
			new Query(
					"employee[@secretary or @assistant]",
					null,
					"child::employee[attribute::secretary or attribute::assistant]",
					"TODO"),
			new Query(
					"book/(chapter | appendix)/section",
					null,
					"child::book/(child::chapter union child::appendix)/child::section",
					"TODO"),
			new Query("E/.", null, "child::E/.", "TODO"),
			new Query("section/@id", null, "child::section/attribute::id",
					"TODO"),
			new Query("section/attribute(id)", null,
					"child::section/attribute::attribute(id)", "TODO"),
			new Query("section/schema-attribute(id)", null,
					"child::section/attribute::schema-attribute(id)", "TODO"),
			new Query("section/schema-element(id)", null,
					"child::section/child::schema-element(id)", "TODO"),
			new Query("element()", null, "child::element()", "TODO"),
			new Query("element(cat)", null, "child::element(cat)", "TODO"),
			new Query("element(cat, cheshire)", null,
					"child::element(cat, cheshire)", "TODO"),
			new Query("element(cat, cheshire?)", null,
					"child::element(cat, cheshire?)", "TODO"),
			new Query("processing-instruction()", null,
					"child::processing-instruction()", "TODO"),
			new Query("processing-instruction('cat')", null,
					"child::processing-instruction('cat')", "TODO"), };

	private static final Query[] INVALID_QUERIES = { new Query("foo- foo"),
			new Query("/*5"), new Query("/ * 5"), new Query("4 + / * 5"),
			new Query("10div 3"), new Query("10 div3"), new Query("10div3"),
			new Query("\"This string was not terminated properly!"),
			new Query("\'This string was not terminated properly!"),
			new Query("\"This string has missing \"escaping\"!\""),
			new Query("'This string has missing 'escaping'!'"),
			new Query("5 (: This is a unterminated comment!"), new Query("//"), };

	public void testValidQueries() {
		for (int i = 0; i < VALID_QUERIES.length; i++) {
			Query query = VALID_QUERIES[i];
			try {
				// Check: Parsable
				ASTXPath xpath = parse(query.getXPath());
				if (AST_DUMP) {
					xpath.dump("");
				}

				// Check: Abbreviated(XPath)
				String abbreviated = PrintXPath2ParserVisitor.abbreviate(query
						.getXPath());
				System.out.println("Abbreviate:               " + abbreviated);
				assertEquals("Abbreviate", query.getAbbreviated(), abbreviated);

				// Check: Unabbreviated(XPath)
				String unabbreviated = PrintXPath2ParserVisitor
						.unabbreviate(query.getXPath());
				System.out
						.println("Unabbreviate:             " + unabbreviated);
				assertEquals("Unabbreviate", query.getUnabbreviated(),
						unabbreviated);

				// Check: Unabbreviate(Abbreviate(XPath))
				String unabbreviateAbbreviate = PrintXPath2ParserVisitor
						.unabbreviate(abbreviated);
				System.out.println("Unabbreviate(Abbreviate): "
						+ unabbreviateAbbreviate);
				assertEquals("Unabbreviate(Abbreviate)", unabbreviated,
						unabbreviateAbbreviate);

				// Check: Abbreviate(Unabbreviate(XPath))
				String abbreviateUnabbreviate = PrintXPath2ParserVisitor
						.abbreviate(unabbreviated);
				System.out.println("Abbreviate(Unabbreviate): "
						+ abbreviateUnabbreviate);
				assertEquals("Abbreviate(Unabbreviate)", abbreviated,
						abbreviateUnabbreviate);

				// Check: Core(XPath)
				String core = CoreXPath2ParserVisitor.toCore(query.getXPath());
				System.out.println("Core:                     " + core);
				assertEquals("Core(XPath)", query.getCore(), core);
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
			Query query = INVALID_QUERIES[i];
			try {
				ASTXPath xpath = parse(query.getXPath());
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
		System.out.println("XPath:                    " + query);
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
