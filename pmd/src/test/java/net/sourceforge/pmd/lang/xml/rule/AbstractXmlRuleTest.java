package net.sourceforge.pmd.lang.xml.rule;

import static org.junit.Assert.assertEquals;

import java.io.StringReader;
import java.util.ArrayList;
import java.util.List;

import net.sourceforge.pmd.RuleContext;
import net.sourceforge.pmd.lang.Language;
import net.sourceforge.pmd.lang.Parser;
import net.sourceforge.pmd.lang.ast.Node;
import net.sourceforge.pmd.lang.xml.XmlParserOptions;
import net.sourceforge.pmd.lang.xml.ast.XmlNode;
import net.sourceforge.pmd.lang.xml.rule.AbstractXmlRule;

import org.junit.Test;

public class AbstractXmlRuleTest {

    @Test
    public void testVisit() throws Exception {
	String source = "<?xml version=\"1.0\"?><foo abc=\"abc\"><bar/></foo>";
	XmlParserOptions parserOptions = new XmlParserOptions();
	Parser parser = Language.XML.getDefaultVersion().getLanguageVersionHandler().getParser(parserOptions);
	XmlNode xmlNode = (XmlNode) parser.parse(null, new StringReader(source));
	List<XmlNode> nodes = new ArrayList<XmlNode>();
	nodes.add(xmlNode);

	MyRule rule = new MyRule();
	rule.apply(nodes, null);

	assertEquals(3, rule.visitedNodes.size());
	assertEquals("document", rule.visitedNodes.get(0).toString());
	assertEquals("foo", rule.visitedNodes.get(1).toString());
	assertEquals("bar", rule.visitedNodes.get(2).toString());
    }

    private static class MyRule extends AbstractXmlRule {
	final List<XmlNode> visitedNodes = new ArrayList<XmlNode>();

	public MyRule() {
	}

	@Override
	public void apply(List<? extends Node> nodes, RuleContext ctx) {
	    visitedNodes.clear();
	    super.apply(nodes, ctx);
	}

	@Override
	protected void visit(XmlNode node, RuleContext ctx) {
	    visitedNodes.add(node);
	    super.visit(node, ctx);
	}
    }

    public static junit.framework.Test suite() {
	return new junit.framework.JUnit4TestAdapter(AbstractXmlRuleTest.class);
    }
}
