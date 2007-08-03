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
					"{((typeswitch(fn:root(fs:apply-ordering-mode(self::node()))) case $fs:new as document-node() return $fs:new default $fs:new return fn:error()))}"),
			new Query(
					"foo -foo",
					"foo - foo",
					"child::foo - child::foo",
					"{fs:minus(fs:convert-operand(fn:data((fs:apply-ordering-mode(child::foo))), 1.0E0), fs:convert-operand(fn:data((fs:apply-ordering-mode(child::foo))), 1.0E0))}"),
			new Query(
					"foo(: This is a comment :)- foo",
					"foo - foo",
					"child::foo - child::foo",
					"{fs:minus(fs:convert-operand(fn:data((fs:apply-ordering-mode(child::foo))), 1.0E0), fs:convert-operand(fn:data((fs:apply-ordering-mode(child::foo))), 1.0E0))}"),
			new Query("foo-foo", null, "child::foo-foo",
					"{fs:apply-ordering-mode(child::foo-foo)}"),
			new Query(
					"foo (: commenting out a (: comment :) may be confusing, but often helpful :)",
					"foo", "child::foo", "{fs:apply-ordering-mode(child::foo)}"),
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
					"{(typeswitch(fn:root(fs:apply-ordering-mode(self::node()))) case $fs:new as document-node() return $fs:new default $fs:new return fn:error())}"),
			new Query(
					"/*",
					null,
					"(fn:root(self::node()) treat as document-node())/child::*",
					"{fs:apply-ordering-mode(fs:distinct-doc-order-or-atomic-sequence(let $fs:sequence as node()* := (typeswitch(fn:root(fs:apply-ordering-mode(self::node()))) case $fs:new as document-node() return $fs:new default $fs:new return fn:error()) return let $fs:last := fn:count($fs:sequence) return for $fs:dot at $fs:position in $fs:sequence return fs:apply-ordering-mode(child::*)))}"),
			new Query(
					"/ *",
					"/*",
					"(fn:root(self::node()) treat as document-node())/child::*",
					"{fs:apply-ordering-mode(fs:distinct-doc-order-or-atomic-sequence(let $fs:sequence as node()* := (typeswitch(fn:root(fs:apply-ordering-mode(self::node()))) case $fs:new as document-node() return $fs:new default $fs:new return fn:error()) return let $fs:last := fn:count($fs:sequence) return for $fs:dot at $fs:position in $fs:sequence return fs:apply-ordering-mode(child::*)))}"),
			new Query(
					"(/) * 5",
					null,
					"((fn:root(self::node()) treat as document-node())) * 5",
					"{fs:times(fs:convert-operand(fn:data((((typeswitch(fn:root(fs:apply-ordering-mode(self::node()))) case $fs:new as document-node() return $fs:new default $fs:new return fn:error())))), 1.0E0), fs:convert-operand(fn:data((5)), 1.0E0))}"),
			new Query("4", null, null, "{4}"),
			new Query("-4", null, null,
					"{fs:unary-minus(fs:convert-operand(fn:data((4)), 1.0E0))}"),
			new Query("+4", null, null,
					"{fs:unary-plus(fs:convert-operand(fn:data((4)), 1.0E0))}"),
			new Query(
					"+--4",
					null,
					null,
					"{fs:unary-plus(fs:convert-operand(fn:data((fs:unary-minus(fs:convert-operand(fn:data((fs:unary-minus(fs:convert-operand(fn:data((4)), 1.0E0)))), 1.0E0)))), 1.0E0))}"),
			new Query(
					"-++4",
					null,
					null,
					"{fs:unary-minus(fs:convert-operand(fn:data((fs:unary-plus(fs:convert-operand(fn:data((fs:unary-plus(fs:convert-operand(fn:data((4)), 1.0E0)))), 1.0E0)))), 1.0E0))}"),
			new Query(
					"-+-+--+4",
					null,
					null,
					"{fs:unary-minus(fs:convert-operand(fn:data((fs:unary-plus(fs:convert-operand(fn:data((fs:unary-minus(fs:convert-operand(fn:data((fs:unary-plus(fs:convert-operand(fn:data((fs:unary-minus(fs:convert-operand(fn:data((fs:unary-minus(fs:convert-operand(fn:data((fs:unary-plus(fs:convert-operand(fn:data((4)), 1.0E0)))), 1.0E0)))), 1.0E0)))), 1.0E0)))), 1.0E0)))), 1.0E0)))), 1.0E0))}"),
			new Query(
					"4 + (/) * 5",
					null,
					"4 + ((fn:root(self::node()) treat as document-node())) * 5",
					"{fs:plus(fs:convert-operand(fn:data((4)), 1.0E0), fs:convert-operand(fn:data((fs:times(fs:convert-operand(fn:data((((typeswitch(fn:root(fs:apply-ordering-mode(self::node()))) case $fs:new as document-node() return $fs:new default $fs:new return fn:error())))), 1.0E0), fs:convert-operand(fn:data((5)), 1.0E0)))), 1.0E0))}"),
			new Query(
					"4 + /",
					null,
					"4 + (fn:root(self::node()) treat as document-node())",
					"{fs:plus(fs:convert-operand(fn:data((4)), 1.0E0), fs:convert-operand(fn:data(((typeswitch(fn:root(fs:apply-ordering-mode(self::node()))) case $fs:new as document-node() return $fs:new default $fs:new return fn:error()))), 1.0E0))}"),
			new Query("4 | 5", null, "4 union 5",
					"{fs:apply-ordering-mode(op:union(4, 5))}"),
			new Query("4 union 5", "4 | 5", null,
					"{fs:apply-ordering-mode(op:union(4, 5))}"),
			new Query("4 intersect 5", null, null,
					"{fs:apply-ordering-mode(op:intersect(4, 5))}"),
			new Query("4 except 5", null, null,
					"{fs:apply-ordering-mode(op:except(4, 5))}"),
			new Query(
					"4 treat as xs:integer",
					null,
					null,
					"{typeswitch(4) case $fs:new as xs:integer return $fs:new default $fs:new return fn:error()}"),
			new Query(
					"4 treat as xs:integer?",
					null,
					null,
					"{typeswitch(4) case $fs:new as xs:integer? return $fs:new default $fs:new return fn:error()}"),
			new Query(
					"4 treat as xs:integer+",
					null,
					null,
					"{typeswitch(4) case $fs:new as xs:integer+ return $fs:new default $fs:new return fn:error()}"),
			new Query(
					"4 treat as xs:integer*",
					null,
					null,
					"{typeswitch(4) case $fs:new as xs:integer* return $fs:new default $fs:new return fn:error()}"),
			new Query(
					"if ($widget1/unit-cost < $widget2/unit-cost)" + EOL
							+ "then $widget1" + EOL + "else $widget2",
					"if ($widget1/unit-cost < $widget2/unit-cost) then $widget1 else $widget2",
					"if ($widget1/child::unit-cost < $widget2/child::unit-cost) then $widget1 else $widget2",
					"{if (fn:boolean((some $v1 in fn:data((fs:apply-ordering-mode(fs:distinct-doc-order-or-atomic-sequence(let $fs:sequence as node()* := $widget1 return let $fs:last := fn:count($fs:sequence) return for $fs:dot at $fs:position in $fs:sequence return fs:apply-ordering-mode(child::unit-cost))))) satisfies some $v2 in fn:data((fs:apply-ordering-mode(fs:distinct-doc-order-or-atomic-sequence(let $fs:sequence as node()* := $widget2 return let $fs:last := fn:count($fs:sequence) return for $fs:dot at $fs:position in $fs:sequence return fs:apply-ordering-mode(child::unit-cost))))) satisfies let $u1 := fs:convert-operand($v1, $v2) return let $u2 := fs:convert-operand($v2, $v1) return fs:lt($u1, $u2)))) then $widget1 else $widget2}"),
			new Query(
					"if ($part/@discounted)" + EOL + "then $part/wholesale"
							+ EOL + "else $part/retail",
					"if ($part/@discounted) then $part/wholesale else $part/retail",
					"if ($part/attribute::discounted) then $part/child::wholesale else $part/child::retail",
					"{if (fn:boolean((fs:apply-ordering-mode(fs:distinct-doc-order-or-atomic-sequence(let $fs:sequence as node()* := $part return let $fs:last := fn:count($fs:sequence) return for $fs:dot at $fs:position in $fs:sequence return fs:apply-ordering-mode(attribute::discounted)))))) then fs:apply-ordering-mode(fs:distinct-doc-order-or-atomic-sequence(let $fs:sequence as node()* := $part return let $fs:last := fn:count($fs:sequence) return for $fs:dot at $fs:position in $fs:sequence return fs:apply-ordering-mode(child::wholesale))) else fs:apply-ordering-mode(fs:distinct-doc-order-or-atomic-sequence(let $fs:sequence as node()* := $part return let $fs:last := fn:count($fs:sequence) return for $fs:dot at $fs:position in $fs:sequence return fs:apply-ordering-mode(child::retail)))}"),
			new Query(
					"($x div $y) + xs:decimal($z)",
					null,
					null,
					"{fs:plus(fs:convert-operand(fn:data(((fs:div(fs:convert-operand(fn:data(($x)), 1.0E0), fs:convert-operand(fn:data(($y)), 1.0E0))))), 1.0E0), fs:convert-operand(fn:data((xs:decimal($z))), 1.0E0))}"),
			new Query(
					"fn:error(xs:QName(\"app:err057\"), \"Unexpected value\", fn:string($v))",
					null, null,
					"{fn:error(xs:QName(\"app:err057\"), \"Unexpected value\", fn:string($v))}"),
			new Query("'this is a string with '' apostrophes and \" quotes!'",
					null, null,
					"{'this is a string with '' apostrophes and \" quotes!'}"),
			new Query(
					"\"this is a string with ' apostrophes and \"\" quotes!\"",
					null, null,
					"{\"this is a string with ' apostrophes and \"\" quotes!\"}"),
			new Query(
					"//book[author eq 'Berners-Lee']",
					null,
					"(fn:root(self::node()) treat as document-node())/descendant-or-self::node()/child::book[child::author eq 'Berners-Lee']",
					"{fs:apply-ordering-mode(fs:distinct-doc-order-or-atomic-sequence(let $fs:sequence as node()* := fs:apply-ordering-mode(fs:distinct-doc-order-or-atomic-sequence(let $fs:sequence as node()* := (typeswitch(fn:root(fs:apply-ordering-mode(self::node()))) case $fs:new as document-node() return $fs:new default $fs:new return fn:error()) return let $fs:last := fn:count($fs:sequence) return for $fs:dot at $fs:position in $fs:sequence return fs:apply-ordering-mode(descendant-or-self::node()))) return let $fs:last := fn:count($fs:sequence) return for $fs:dot at $fs:position in $fs:sequence return let $fs:sequence := fs:apply-ordering-mode(fs:distinct-doc-order(fs:apply-ordering-mode(child::book) return let $fs:last := fn:count($fs:sequence) return for $fs:dot at $fs:position in $fs:sequence return if (fs:eq(fs:convert-operand(fn:data((fs:apply-ordering-mode(child::author))), string), fs:convert-operand(fn:data(('Berners-Lee')), string))) then $fs:dot else ()))}"),
			new Query(
					"some $x in $expr1 satisfies $x = 47",
					null,
					null,
					"{some $x in $expr1 satisfies fn:boolean((some $v1 in fn:data(($x)) satisfies some $v2 in fn:data((47)) satisfies let $u1 := fs:convert-operand($v1, $v2) return let $u2 := fs:convert-operand($v2, $v1) return fs:eq($u1, $u2)))}"),
			new Query(
					"some $x in $expr1, $y in $expr2 satisfies $x = 47",
					null,
					null,
					"{some $x in $expr1some $y in $expr2 satisfies fn:boolean((some $v1 in fn:data(($x)) satisfies some $v2 in fn:data((47)) satisfies let $u1 := fs:convert-operand($v1, $v2) return let $u2 := fs:convert-operand($v2, $v1) return fs:eq($u1, $u2)))}"),
			new Query(
					"every $x in $expr1 satisfies $x = 47",
					null,
					null,
					"{every $x in $expr1 satisfies fn:boolean((some $v1 in fn:data(($x)) satisfies some $v2 in fn:data((47)) satisfies let $u1 := fs:convert-operand($v1, $v2) return let $u2 := fs:convert-operand($v2, $v1) return fs:eq($u1, $u2)))}"),
			new Query(
					"every $x in $expr1, $y in $expr2 satisfies $x = 47",
					null,
					null,
					"{every $x in $expr1every $y in $expr2 satisfies fn:boolean((some $v1 in fn:data(($x)) satisfies some $v2 in fn:data((47)) satisfies let $u1 := fs:convert-operand($v1, $v2) return let $u2 := fs:convert-operand($v2, $v1) return fs:eq($u1, $u2)))}"),
			new Query(
					"//product[id = 47]",
					null,
					"(fn:root(self::node()) treat as document-node())/descendant-or-self::node()/child::product[child::id = 47]",
					"{fs:apply-ordering-mode(fs:distinct-doc-order-or-atomic-sequence(let $fs:sequence as node()* := fs:apply-ordering-mode(fs:distinct-doc-order-or-atomic-sequence(let $fs:sequence as node()* := (typeswitch(fn:root(fs:apply-ordering-mode(self::node()))) case $fs:new as document-node() return $fs:new default $fs:new return fn:error()) return let $fs:last := fn:count($fs:sequence) return for $fs:dot at $fs:position in $fs:sequence return fs:apply-ordering-mode(descendant-or-self::node()))) return let $fs:last := fn:count($fs:sequence) return for $fs:dot at $fs:position in $fs:sequence return let $fs:sequence := fs:apply-ordering-mode(fs:distinct-doc-order(fs:apply-ordering-mode(child::product) return let $fs:last := fn:count($fs:sequence) return for $fs:dot at $fs:position in $fs:sequence return if (some $v1 in fn:data((fs:apply-ordering-mode(child::id))) satisfies some $v2 in fn:data((47)) satisfies let $u1 := fs:convert-operand($v1, $v2) return let $u2 := fs:convert-operand($v2, $v1) return fs:eq($u1, $u2)) then $fs:dot else ()))}"),
			new Query(
					"//product[id = 47]/following::node()/part[id = 48]",
					null,
					"(fn:root(self::node()) treat as document-node())/descendant-or-self::node()/child::product[child::id = 47]/following::node()/child::part[child::id = 48]",
					"{fs:apply-ordering-mode(fs:distinct-doc-order-or-atomic-sequence(let $fs:sequence as node()* := fs:apply-ordering-mode(fs:distinct-doc-order-or-atomic-sequence(let $fs:sequence as node()* := fs:apply-ordering-mode(fs:distinct-doc-order-or-atomic-sequence(let $fs:sequence as node()* := fs:apply-ordering-mode(fs:distinct-doc-order-or-atomic-sequence(let $fs:sequence as node()* := (typeswitch(fn:root(fs:apply-ordering-mode(self::node()))) case $fs:new as document-node() return $fs:new default $fs:new return fn:error()) return let $fs:last := fn:count($fs:sequence) return for $fs:dot at $fs:position in $fs:sequence return fs:apply-ordering-mode(descendant-or-self::node()))) return let $fs:last := fn:count($fs:sequence) return for $fs:dot at $fs:position in $fs:sequence return let $fs:sequence := fs:apply-ordering-mode(fs:distinct-doc-order(fs:apply-ordering-mode(child::product) return let $fs:last := fn:count($fs:sequence) return for $fs:dot at $fs:position in $fs:sequence return if (some $v1 in fn:data((fs:apply-ordering-mode(child::id))) satisfies some $v2 in fn:data((47)) satisfies let $u1 := fs:convert-operand($v1, $v2) return let $u2 := fs:convert-operand($v2, $v1) return fs:eq($u1, $u2)) then $fs:dot else ())) return let $fs:last := fn:count($fs:sequence) return for $fs:dot at $fs:position in $fs:sequence return fs:apply-ordering-mode(ancestor-or-self::node()/following-sibling::node()/descendant-or-self::node()))) return let $fs:last := fn:count($fs:sequence) return for $fs:dot at $fs:position in $fs:sequence return let $fs:sequence := fs:apply-ordering-mode(fs:distinct-doc-order(fs:apply-ordering-mode(child::part) return let $fs:last := fn:count($fs:sequence) return for $fs:dot at $fs:position in $fs:sequence return if (some $v1 in fn:data((fs:apply-ordering-mode(child::id))) satisfies some $v2 in fn:data((48)) satisfies let $u1 := fs:convert-operand($v1, $v2) return let $u2 := fs:convert-operand($v2, $v1) return fs:eq($u1, $u2)) then $fs:dot else ()))}"),
			new Query(
					"//part[color eq \"Red\"]",
					null,
					"(fn:root(self::node()) treat as document-node())/descendant-or-self::node()/child::part[child::color eq \"Red\"]",
					"{fs:apply-ordering-mode(fs:distinct-doc-order-or-atomic-sequence(let $fs:sequence as node()* := fs:apply-ordering-mode(fs:distinct-doc-order-or-atomic-sequence(let $fs:sequence as node()* := (typeswitch(fn:root(fs:apply-ordering-mode(self::node()))) case $fs:new as document-node() return $fs:new default $fs:new return fn:error()) return let $fs:last := fn:count($fs:sequence) return for $fs:dot at $fs:position in $fs:sequence return fs:apply-ordering-mode(descendant-or-self::node()))) return let $fs:last := fn:count($fs:sequence) return for $fs:dot at $fs:position in $fs:sequence return let $fs:sequence := fs:apply-ordering-mode(fs:distinct-doc-order(fs:apply-ordering-mode(child::part) return let $fs:last := fn:count($fs:sequence) return for $fs:dot at $fs:position in $fs:sequence return if (fs:eq(fs:convert-operand(fn:data((fs:apply-ordering-mode(child::color))), string), fs:convert-operand(fn:data((\"Red\")), string))) then $fs:dot else ()))}"),
			new Query(
					"//part[color = \"Red\"][color eq \"Red\"]",
					null,
					"(fn:root(self::node()) treat as document-node())/descendant-or-self::node()/child::part[child::color = \"Red\"][child::color eq \"Red\"]",
					"{fs:apply-ordering-mode(fs:distinct-doc-order-or-atomic-sequence(let $fs:sequence as node()* := fs:apply-ordering-mode(fs:distinct-doc-order-or-atomic-sequence(let $fs:sequence as node()* := (typeswitch(fn:root(fs:apply-ordering-mode(self::node()))) case $fs:new as document-node() return $fs:new default $fs:new return fn:error()) return let $fs:last := fn:count($fs:sequence) return for $fs:dot at $fs:position in $fs:sequence return fs:apply-ordering-mode(descendant-or-self::node()))) return let $fs:last := fn:count($fs:sequence) return for $fs:dot at $fs:position in $fs:sequence return let $fs:sequence := fs:apply-ordering-mode(fs:distinct-doc-order(let $fs:sequence := fs:apply-ordering-mode(fs:distinct-doc-order(fs:apply-ordering-mode(child::part) return let $fs:last := fn:count($fs:sequence) return for $fs:dot at $fs:position in $fs:sequence return if (some $v1 in fn:data((fs:apply-ordering-mode(child::color))) satisfies some $v2 in fn:data((\"Red\")) satisfies let $u1 := fs:convert-operand($v1, $v2) return let $u2 := fs:convert-operand($v2, $v1) return fs:eq($u1, $u2)) then $fs:dot else () return let $fs:last := fn:count($fs:sequence) return for $fs:dot at $fs:position in $fs:sequence return if (fs:eq(fs:convert-operand(fn:data((fs:apply-ordering-mode(child::color))), string), fs:convert-operand(fn:data((\"Red\")), string))) then $fs:dot else ()))}"),
			new Query(
					"$N[@x castable as xs:date][xs:date(@x) gt xs:date(\"2000-01-01\")]",
					null,
					"$N[attribute::x castable as xs:date][xs:date(attribute::x) gt xs:date(\"2000-01-01\")]",
					"{let $fs:sequence := let $fs:sequence := $N return let $fs:last := fn:count($fs:sequence) return for $fs:dot at $fs:position in $fs:sequence return if(fs:apply-ordering-mode(attribute::x) castable as xs:date) then $fs:dot else () return let $fs:last := fn:count($fs:sequence) return for $fs:dot at $fs:position in $fs:sequence return if(fs:gt(fs:convert-operand(fn:data((xs:date(fs:apply-ordering-mode(attribute::x)))), string), fs:convert-operand(fn:data((xs:date(\"2000-01-01\"))), string))) then $fs:dot else ()}"),
			new Query(
					"$N[if (@x castable as xs:date)" + EOL
							+ "then xs:date(@x) gt xs:date(\"2000-01-01\")"
							+ EOL + "else false()]",
					"$N[if (@x castable as xs:date) then xs:date(@x) gt xs:date(\"2000-01-01\") else false()]",
					"$N[if (attribute::x castable as xs:date) then xs:date(attribute::x) gt xs:date(\"2000-01-01\") else false()]",
					"{let $fs:sequence := $N return let $fs:last := fn:count($fs:sequence) return for $fs:dot at $fs:position in $fs:sequence return if(if (fn:boolean((fs:apply-ordering-mode(attribute::x) castable as xs:date))) then fs:gt(fs:convert-operand(fn:data((xs:date(fs:apply-ordering-mode(attribute::x)))), string), fs:convert-operand(fn:data((xs:date(\"2000-01-01\"))), string)) else false()) then $fs:dot else ()}"),
			new Query("\"12.5\"", null, null, "{\"12.5\"}"),
			new Query("12", null, null, "{12}"),
			new Query("12.5", null, null, "{12.5}"),
			new Query("125E2", null, null, "{125E2}"),
			new Query("\"He said, \"\"I don't like it.\"\"\"", null, null,
					"{\"He said, \"\"I don't like it.\"\"\"}"),
			new Query("9 cast as hatsize", null, null,
					"{let $v as xs:anyAtomicType := fn:data((9)) return $v cast as hatsize}"),
			new Query(
					"9 cast as hatsize?",
					null,
					null,
					"{let $v as xs:anyAtomicType := fn:data((9)) return typeswitch ($v) case $fs:new as empty-sequence() return () default $fs:new return $v cast as hatsize}"),
			new Query(
					"fn:doc(\"bib.xml\")/books/book[fn:count(./author)>1]",
					"fn:doc(\"bib.xml\")/books/book[fn:count(./author) > 1]",
					"fn:doc(\"bib.xml\")/child::books/child::book[fn:count(./child::author) > 1]",
					"{fs:apply-ordering-mode(fs:distinct-doc-order-or-atomic-sequence(let $fs:sequence as node()* := fs:apply-ordering-mode(fs:distinct-doc-order-or-atomic-sequence(let $fs:sequence as node()* := fn:doc(\"bib.xml\") return let $fs:last := fn:count($fs:sequence) return for $fs:dot at $fs:position in $fs:sequence return fs:apply-ordering-mode(child::books))) return let $fs:last := fn:count($fs:sequence) return for $fs:dot at $fs:position in $fs:sequence return let $fs:sequence := fs:apply-ordering-mode(fs:distinct-doc-order(fs:apply-ordering-mode(child::book) return let $fs:last := fn:count($fs:sequence) return for $fs:dot at $fs:position in $fs:sequence return if (some $v1 in fn:data((fn:count(fs:apply-ordering-mode(fs:distinct-doc-order-or-atomic-sequence(let $fs:sequence as node()* := fs:dot return let $fs:last := fn:count($fs:sequence) return for $fs:dot at $fs:position in $fs:sequence return fs:apply-ordering-mode(child::author)))))) satisfies some $v2 in fn:data((1)) satisfies let $u1 := fs:convert-operand($v1, $v2) return let $u2 := fs:convert-operand($v2, $v1) return fs:gt($u1, $u2)) then $fs:dot else ()))}"),
			new Query(
					"(1 to 100)[. mod 5 eq 0]",
					null,
					null,
					"{let $fs:sequence := (fs:to((1), (100))) return let $fs:last := fn:count($fs:sequence) return for $fs:dot at $fs:position in $fs:sequence return if(fs:eq(fs:convert-operand(fn:data((fs:mod(fs:convert-operand(fn:data((fs:dot)), 1.0E0), fs:convert-operand(fn:data((5)), 1.0E0)))), string), fs:convert-operand(fn:data((0)), string))) then $fs:dot else ()}"),
			new Query("my:three-argument-function(1, 2, 3)", null, null,
					"{my:three-argument-function(1, 2, 3)}"),
			new Query("my:two-argument-function((1, 2), 3)", null, null,
					"{my:two-argument-function((1, 2), 3)}"),
			new Query("my:two-argument-function(1, ())", null, null,
					"{my:two-argument-function(1, ())}"),
			new Query("my:one-argument-function((1, 2, 3))", null, null,
					"{my:one-argument-function((1, 2, 3))}"),
			new Query("my:one-argument-function(( ))",
					"my:one-argument-function(())",
					"my:one-argument-function(())",
					"{my:one-argument-function(())}"),
			new Query("my:zero-argument-function( )",
					"my:zero-argument-function()",
					"my:zero-argument-function()",
					"{my:zero-argument-function()}"),
			new Query("..", null, "parent::node()",
					"{fs:apply-ordering-mode(parent::node())}"),
			new Query("parent::node()", "..", null,
					"{fs:apply-ordering-mode(parent::node())}"),
			new Query("child::div1", "div1", null,
					"{fs:apply-ordering-mode(child::div1)}"),
			new Query(
					"child::div1/child::para",
					"div1/para",
					null,
					"{fs:apply-ordering-mode(fs:distinct-doc-order-or-atomic-sequence(let $fs:sequence as node()* := fs:apply-ordering-mode(child::div1) return let $fs:last := fn:count($fs:sequence) return for $fs:dot at $fs:position in $fs:sequence return fs:apply-ordering-mode(child::para)))}"),
			new Query(
					"child::chapter[2]",
					"chapter[2]",
					null,
					"{let $fs:sequence := fs:apply-ordering-mode(fs:distinct-doc-order(fs:apply-ordering-mode(child::chapter) return let $fs:last := fn:count($fs:sequence) return for $fs:dot at $fs:position in $fs:sequence return if (2) then $fs:dot else ()}"),
			new Query(
					"descendant::toy[attribute::color = \"red\"]",
					"descendant::toy[@color = \"red\"]",
					"descendant::toy[attribute::color = \"red\"]",
					"{let $fs:sequence := fs:apply-ordering-mode(fs:distinct-doc-order(fs:apply-ordering-mode(descendant::toy) return let $fs:last := fn:count($fs:sequence) return for $fs:dot at $fs:position in $fs:sequence return if (some $v1 in fn:data((fs:apply-ordering-mode(attribute::color))) satisfies some $v2 in fn:data((\"red\")) satisfies let $u1 := fs:convert-operand($v1, $v2) return let $u2 := fs:convert-operand($v2, $v1) return fs:eq($u1, $u2)) then $fs:dot else ()}"),
			new Query(
					"child::employee[secretary][assistant]",
					"employee[secretary][assistant]",
					"child::employee[child::secretary][child::assistant]",
					"{let $fs:sequence := fs:apply-ordering-mode(fs:distinct-doc-order(let $fs:sequence := fs:apply-ordering-mode(fs:distinct-doc-order(fs:apply-ordering-mode(child::employee) return let $fs:last := fn:count($fs:sequence) return for $fs:dot at $fs:position in $fs:sequence return if (fs:apply-ordering-mode(child::secretary)) then $fs:dot else () return let $fs:last := fn:count($fs:sequence) return for $fs:dot at $fs:position in $fs:sequence return if (fs:apply-ordering-mode(child::assistant)) then $fs:dot else ()}"),
			new Query(
					"div1//para",
					null,
					"child::div1/descendant-or-self::node()/child::para",
					"{fs:apply-ordering-mode(fs:distinct-doc-order-or-atomic-sequence(let $fs:sequence as node()* := fs:apply-ordering-mode(fs:distinct-doc-order-or-atomic-sequence(let $fs:sequence as node()* := fs:apply-ordering-mode(child::div1) return let $fs:last := fn:count($fs:sequence) return for $fs:dot at $fs:position in $fs:sequence return fs:apply-ordering-mode(descendant-or-self::node()))) return let $fs:last := fn:count($fs:sequence) return for $fs:dot at $fs:position in $fs:sequence return fs:apply-ordering-mode(child::para)))}"),
			new Query(
					"child::div1/descendant-or-self::node()/child::para",
					"div1//para",
					null,
					"{fs:apply-ordering-mode(fs:distinct-doc-order-or-atomic-sequence(let $fs:sequence as node()* := fs:apply-ordering-mode(fs:distinct-doc-order-or-atomic-sequence(let $fs:sequence as node()* := fs:apply-ordering-mode(child::div1) return let $fs:last := fn:count($fs:sequence) return for $fs:dot at $fs:position in $fs:sequence return fs:apply-ordering-mode(descendant-or-self::node()))) return let $fs:last := fn:count($fs:sequence) return for $fs:dot at $fs:position in $fs:sequence return fs:apply-ordering-mode(child::para)))}"),
			new Query("\"This string was terminated properly!\"", null, null,
					"{\"This string was terminated properly!\"}"),
			new Query("'This string was terminated properly!'", null, null,
					"{'This string was terminated properly!'}"),
			new Query("\"This string has \"\"escaping\"\"!\"", null, null,
					"{\"This string has \"\"escaping\"\"!\"}"),
			new Query("'This string has ''escaping''!'", null, null,
					"{'This string has ''escaping''!'}"),
			new Query("para", null, "child::para",
					"{fs:apply-ordering-mode(child::para)}"),
			new Query("*", null, "child::*",
					"{fs:apply-ordering-mode(child::*)}"),
			new Query("text()", null, "child::text()",
					"{fs:apply-ordering-mode(child::text())}"),
			new Query("@name", null, "attribute::name",
					"{fs:apply-ordering-mode(attribute::name)}"),
			new Query("@*", null, "attribute::*",
					"{fs:apply-ordering-mode(attribute::*)}"),
			new Query(
					"para[1]",
					null,
					"child::para[1]",
					"{let $fs:sequence := fs:apply-ordering-mode(fs:distinct-doc-order(fs:apply-ordering-mode(child::para) return let $fs:last := fn:count($fs:sequence) return for $fs:dot at $fs:position in $fs:sequence return if (1) then $fs:dot else ()}"),
			new Query(
					"para[fn:last()]",
					null,
					"child::para[fn:last()]",
					"{let $fs:sequence := fs:apply-ordering-mode(fs:distinct-doc-order(fs:apply-ordering-mode(child::para) return let $fs:last := fn:count($fs:sequence) return for $fs:dot at $fs:position in $fs:sequence return if (fn:last()) then $fs:dot else ()}"),
			new Query(
					"*/para",
					null,
					"child::*/child::para",
					"{fs:apply-ordering-mode(fs:distinct-doc-order-or-atomic-sequence(let $fs:sequence as node()* := fs:apply-ordering-mode(child::*) return let $fs:last := fn:count($fs:sequence) return for $fs:dot at $fs:position in $fs:sequence return fs:apply-ordering-mode(child::para)))}"),
			new Query(
					"/book/chapter[5]/section[2]",
					null,
					"(fn:root(self::node()) treat as document-node())/child::book/child::chapter[5]/child::section[2]",
					"{fs:apply-ordering-mode(fs:distinct-doc-order-or-atomic-sequence(let $fs:sequence as node()* := fs:apply-ordering-mode(fs:distinct-doc-order-or-atomic-sequence(let $fs:sequence as node()* := fs:apply-ordering-mode(fs:distinct-doc-order-or-atomic-sequence(let $fs:sequence as node()* := (typeswitch(fn:root(fs:apply-ordering-mode(self::node()))) case $fs:new as document-node() return $fs:new default $fs:new return fn:error()) return let $fs:last := fn:count($fs:sequence) return for $fs:dot at $fs:position in $fs:sequence return fs:apply-ordering-mode(child::book))) return let $fs:last := fn:count($fs:sequence) return for $fs:dot at $fs:position in $fs:sequence return let $fs:sequence := fs:apply-ordering-mode(fs:distinct-doc-order(fs:apply-ordering-mode(child::chapter) return let $fs:last := fn:count($fs:sequence) return for $fs:dot at $fs:position in $fs:sequence return if (5) then $fs:dot else ())) return let $fs:last := fn:count($fs:sequence) return for $fs:dot at $fs:position in $fs:sequence return let $fs:sequence := fs:apply-ordering-mode(fs:distinct-doc-order(fs:apply-ordering-mode(child::section) return let $fs:last := fn:count($fs:sequence) return for $fs:dot at $fs:position in $fs:sequence return if (2) then $fs:dot else ()))}"),
			new Query(
					"chapter//para",
					null,
					"child::chapter/descendant-or-self::node()/child::para",
					"{fs:apply-ordering-mode(fs:distinct-doc-order-or-atomic-sequence(let $fs:sequence as node()* := fs:apply-ordering-mode(fs:distinct-doc-order-or-atomic-sequence(let $fs:sequence as node()* := fs:apply-ordering-mode(child::chapter) return let $fs:last := fn:count($fs:sequence) return for $fs:dot at $fs:position in $fs:sequence return fs:apply-ordering-mode(descendant-or-self::node()))) return let $fs:last := fn:count($fs:sequence) return for $fs:dot at $fs:position in $fs:sequence return fs:apply-ordering-mode(child::para)))}"),
			new Query(
					"//para",
					null,
					"(fn:root(self::node()) treat as document-node())/descendant-or-self::node()/child::para",
					"{fs:apply-ordering-mode(fs:distinct-doc-order-or-atomic-sequence(let $fs:sequence as node()* := fs:apply-ordering-mode(fs:distinct-doc-order-or-atomic-sequence(let $fs:sequence as node()* := (typeswitch(fn:root(fs:apply-ordering-mode(self::node()))) case $fs:new as document-node() return $fs:new default $fs:new return fn:error()) return let $fs:last := fn:count($fs:sequence) return for $fs:dot at $fs:position in $fs:sequence return fs:apply-ordering-mode(descendant-or-self::node()))) return let $fs:last := fn:count($fs:sequence) return for $fs:dot at $fs:position in $fs:sequence return fs:apply-ordering-mode(child::para)))}"),
			new Query(
					"//@version",
					null,
					"(fn:root(self::node()) treat as document-node())/descendant-or-self::node()/attribute::version",
					"{fs:apply-ordering-mode(fs:distinct-doc-order-or-atomic-sequence(let $fs:sequence as node()* := fs:apply-ordering-mode(fs:distinct-doc-order-or-atomic-sequence(let $fs:sequence as node()* := (typeswitch(fn:root(fs:apply-ordering-mode(self::node()))) case $fs:new as document-node() return $fs:new default $fs:new return fn:error()) return let $fs:last := fn:count($fs:sequence) return for $fs:dot at $fs:position in $fs:sequence return fs:apply-ordering-mode(descendant-or-self::node()))) return let $fs:last := fn:count($fs:sequence) return for $fs:dot at $fs:position in $fs:sequence return fs:apply-ordering-mode(attribute::version)))}"),
			new Query(
					"//list/member",
					null,
					"(fn:root(self::node()) treat as document-node())/descendant-or-self::node()/child::list/child::member",
					"{fs:apply-ordering-mode(fs:distinct-doc-order-or-atomic-sequence(let $fs:sequence as node()* := fs:apply-ordering-mode(fs:distinct-doc-order-or-atomic-sequence(let $fs:sequence as node()* := fs:apply-ordering-mode(fs:distinct-doc-order-or-atomic-sequence(let $fs:sequence as node()* := (typeswitch(fn:root(fs:apply-ordering-mode(self::node()))) case $fs:new as document-node() return $fs:new default $fs:new return fn:error()) return let $fs:last := fn:count($fs:sequence) return for $fs:dot at $fs:position in $fs:sequence return fs:apply-ordering-mode(descendant-or-self::node()))) return let $fs:last := fn:count($fs:sequence) return for $fs:dot at $fs:position in $fs:sequence return fs:apply-ordering-mode(child::list))) return let $fs:last := fn:count($fs:sequence) return for $fs:dot at $fs:position in $fs:sequence return fs:apply-ordering-mode(child::member)))}"),
			new Query(
					".//para",
					null,
					"./descendant-or-self::node()/child::para",
					"{fs:apply-ordering-mode(fs:distinct-doc-order-or-atomic-sequence(let $fs:sequence as node()* := fs:apply-ordering-mode(fs:distinct-doc-order-or-atomic-sequence(let $fs:sequence as node()* := fs:dot return let $fs:last := fn:count($fs:sequence) return for $fs:dot at $fs:position in $fs:sequence return fs:apply-ordering-mode(descendant-or-self::node()))) return let $fs:last := fn:count($fs:sequence) return for $fs:dot at $fs:position in $fs:sequence return fs:apply-ordering-mode(child::para)))}"),
			new Query("..", null, "parent::node()",
					"{fs:apply-ordering-mode(parent::node())}"),
			new Query(
					"../@lang",
					null,
					"parent::node()/attribute::lang",
					"{fs:apply-ordering-mode(fs:distinct-doc-order-or-atomic-sequence(let $fs:sequence as node()* := fs:apply-ordering-mode(parent::node()) return let $fs:last := fn:count($fs:sequence) return for $fs:dot at $fs:position in $fs:sequence return fs:apply-ordering-mode(attribute::lang)))}"),
			new Query(
					"para[@type = \"warning\"]",
					null,
					"child::para[attribute::type = \"warning\"]",
					"{let $fs:sequence := fs:apply-ordering-mode(fs:distinct-doc-order(fs:apply-ordering-mode(child::para) return let $fs:last := fn:count($fs:sequence) return for $fs:dot at $fs:position in $fs:sequence return if (some $v1 in fn:data((fs:apply-ordering-mode(attribute::type))) satisfies some $v2 in fn:data((\"warning\")) satisfies let $u1 := fs:convert-operand($v1, $v2) return let $u2 := fs:convert-operand($v2, $v1) return fs:eq($u1, $u2)) then $fs:dot else ()}"),
			new Query(
					"para[@type = \"warning\"][5]",
					null,
					"child::para[attribute::type = \"warning\"][5]",
					"{let $fs:sequence := fs:apply-ordering-mode(fs:distinct-doc-order(let $fs:sequence := fs:apply-ordering-mode(fs:distinct-doc-order(fs:apply-ordering-mode(child::para) return let $fs:last := fn:count($fs:sequence) return for $fs:dot at $fs:position in $fs:sequence return if (some $v1 in fn:data((fs:apply-ordering-mode(attribute::type))) satisfies some $v2 in fn:data((\"warning\")) satisfies let $u1 := fs:convert-operand($v1, $v2) return let $u2 := fs:convert-operand($v2, $v1) return fs:eq($u1, $u2)) then $fs:dot else () return let $fs:last := fn:count($fs:sequence) return for $fs:dot at $fs:position in $fs:sequence return if (5) then $fs:dot else ()}"),
			new Query(
					"para[5][@type = \"warning\"]",
					null,
					"child::para[5][attribute::type = \"warning\"]",
					"{let $fs:sequence := fs:apply-ordering-mode(fs:distinct-doc-order(let $fs:sequence := fs:apply-ordering-mode(fs:distinct-doc-order(fs:apply-ordering-mode(child::para) return let $fs:last := fn:count($fs:sequence) return for $fs:dot at $fs:position in $fs:sequence return if (5) then $fs:dot else () return let $fs:last := fn:count($fs:sequence) return for $fs:dot at $fs:position in $fs:sequence return if (some $v1 in fn:data((fs:apply-ordering-mode(attribute::type))) satisfies some $v2 in fn:data((\"warning\")) satisfies let $u1 := fs:convert-operand($v1, $v2) return let $u2 := fs:convert-operand($v2, $v1) return fs:eq($u1, $u2)) then $fs:dot else ()}"),
			new Query(
					"chapter[title = \"Introduction\"]",
					null,
					"child::chapter[child::title = \"Introduction\"]",
					"{let $fs:sequence := fs:apply-ordering-mode(fs:distinct-doc-order(fs:apply-ordering-mode(child::chapter) return let $fs:last := fn:count($fs:sequence) return for $fs:dot at $fs:position in $fs:sequence return if (some $v1 in fn:data((fs:apply-ordering-mode(child::title))) satisfies some $v2 in fn:data((\"Introduction\")) satisfies let $u1 := fs:convert-operand($v1, $v2) return let $u2 := fs:convert-operand($v2, $v1) return fs:eq($u1, $u2)) then $fs:dot else ()}"),
			new Query(
					"chapter[title]",
					null,
					"child::chapter[child::title]",
					"{let $fs:sequence := fs:apply-ordering-mode(fs:distinct-doc-order(fs:apply-ordering-mode(child::chapter) return let $fs:last := fn:count($fs:sequence) return for $fs:dot at $fs:position in $fs:sequence return if (fs:apply-ordering-mode(child::title)) then $fs:dot else ()}"),
			new Query(
					"employee[@secretary and @assistant]",
					null,
					"child::employee[attribute::secretary and attribute::assistant]",
					"{let $fs:sequence := fs:apply-ordering-mode(fs:distinct-doc-order(fs:apply-ordering-mode(child::employee) return let $fs:last := fn:count($fs:sequence) return for $fs:dot at $fs:position in $fs:sequence return if (fn:boolean((fs:apply-ordering-mode(attribute::secretary))) and fn:boolean((fs:apply-ordering-mode(attribute::assistant)))) then $fs:dot else ()}"),
			new Query(
					"employee[@secretary or @assistant]",
					null,
					"child::employee[attribute::secretary or attribute::assistant]",
					"{let $fs:sequence := fs:apply-ordering-mode(fs:distinct-doc-order(fs:apply-ordering-mode(child::employee) return let $fs:last := fn:count($fs:sequence) return for $fs:dot at $fs:position in $fs:sequence return if (fn:boolean((fs:apply-ordering-mode(attribute::secretary))) or fn:boolean((fs:apply-ordering-mode(attribute::assistant)))) then $fs:dot else ()}"),
			new Query(
					"book/(chapter | appendix)/section",
					null,
					"child::book/(child::chapter union child::appendix)/child::section",
					"{fs:apply-ordering-mode(fs:distinct-doc-order-or-atomic-sequence(let $fs:sequence as node()* := fs:apply-ordering-mode(fs:distinct-doc-order-or-atomic-sequence(let $fs:sequence as node()* := fs:apply-ordering-mode(child::book) return let $fs:last := fn:count($fs:sequence) return for $fs:dot at $fs:position in $fs:sequence return (fs:apply-ordering-mode(op:union(fs:apply-ordering-mode(child::chapter), fs:apply-ordering-mode(child::appendix)))))) return let $fs:last := fn:count($fs:sequence) return for $fs:dot at $fs:position in $fs:sequence return fs:apply-ordering-mode(child::section)))}"),
			new Query(
					"E/.",
					null,
					"child::E/.",
					"{fs:apply-ordering-mode(fs:distinct-doc-order-or-atomic-sequence(let $fs:sequence as node()* := fs:apply-ordering-mode(child::E) return let $fs:last := fn:count($fs:sequence) return for $fs:dot at $fs:position in $fs:sequence return fs:dot))}"),
			new Query(
					"section/@id",
					null,
					"child::section/attribute::id",
					"{fs:apply-ordering-mode(fs:distinct-doc-order-or-atomic-sequence(let $fs:sequence as node()* := fs:apply-ordering-mode(child::section) return let $fs:last := fn:count($fs:sequence) return for $fs:dot at $fs:position in $fs:sequence return fs:apply-ordering-mode(attribute::id)))}"),
			new Query(
					"section/attribute(id)",
					null,
					"child::section/attribute::attribute(id)",
					"{fs:apply-ordering-mode(fs:distinct-doc-order-or-atomic-sequence(let $fs:sequence as node()* := fs:apply-ordering-mode(child::section) return let $fs:last := fn:count($fs:sequence) return for $fs:dot at $fs:position in $fs:sequence return fs:apply-ordering-mode(attribute::attribute(id))))}"),
			new Query(
					"section/schema-attribute(id)",
					null,
					"child::section/attribute::schema-attribute(id)",
					"{fs:apply-ordering-mode(fs:distinct-doc-order-or-atomic-sequence(let $fs:sequence as node()* := fs:apply-ordering-mode(child::section) return let $fs:last := fn:count($fs:sequence) return for $fs:dot at $fs:position in $fs:sequence return fs:apply-ordering-mode(attribute::schema-attribute(id))))}"),
			new Query(
					"section/schema-element(id)",
					null,
					"child::section/child::schema-element(id)",
					"{fs:apply-ordering-mode(fs:distinct-doc-order-or-atomic-sequence(let $fs:sequence as node()* := fs:apply-ordering-mode(child::section) return let $fs:last := fn:count($fs:sequence) return for $fs:dot at $fs:position in $fs:sequence return fs:apply-ordering-mode(child::schema-element(id))))}"),
			new Query("element()", null, "child::element()",
					"{fs:apply-ordering-mode(child::element())}"),
			new Query("element(cat)", null, "child::element(cat)",
					"{fs:apply-ordering-mode(child::element(cat))}"),
			new Query("element(cat, cheshire)", null,
					"child::element(cat, cheshire)",
					"{fs:apply-ordering-mode(child::element(cat, cheshire))}"),
			new Query("element(cat, cheshire?)", null,
					"child::element(cat, cheshire?)",
					"{fs:apply-ordering-mode(child::element(cat, cheshire?))}"),
			new Query("processing-instruction()", null,
					"child::processing-instruction()",
					"{fs:apply-ordering-mode(child::processing-instruction())}"),
			new Query("processing-instruction('cat')", null,
					"child::processing-instruction('cat')",
					"{fs:apply-ordering-mode(child::processing-instruction('cat'))}"),
	};

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
				// TODO Uncomment line below to test translation to XPath2 Core
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
