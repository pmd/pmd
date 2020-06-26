/**
 * BSD-style license; for more info see http://pmd.sourceforge.net/license.html
 */

package net.sourceforge.pmd.lang.xml.rule;

import static org.junit.Assert.assertEquals;

import java.util.ArrayList;
import java.util.List;

import org.junit.Test;

import net.sourceforge.pmd.RuleContext;
import net.sourceforge.pmd.lang.xml.XmlParsingHelper;
import net.sourceforge.pmd.lang.xml.ast.XmlNode;

public class AbstractXmlRuleTest {

    @Test
    public void testVisit() throws Exception {
        String source = "<?xml version=\"1.0\"?><foo abc=\"abc\"><bar/></foo>";
        XmlNode xmlNode = XmlParsingHelper.XML.parse(source);
        MyRule rule = new MyRule();
        rule.apply(xmlNode, null);

        assertEquals(3, rule.visitedNodes.size());
        assertEquals("document", rule.visitedNodes.get(0).toString());
        assertEquals("foo", rule.visitedNodes.get(1).toString());
        assertEquals("bar", rule.visitedNodes.get(2).toString());
    }

    private static class MyRule extends AbstractXmlRule {
        final List<XmlNode> visitedNodes = new ArrayList<>();

        MyRule() {
        }

        @Override
        public void start(RuleContext ctx) {
            visitedNodes.clear();
        }

        @Override
        protected void visit(XmlNode node, RuleContext ctx) {
            visitedNodes.add(node);
            super.visit(node, ctx);
        }
    }
}
