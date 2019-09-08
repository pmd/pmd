/**
 * BSD-style license; for more info see http://pmd.sourceforge.net/license.html
 */

package net.sourceforge.pmd.scala;

import java.io.StringReader;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.atomic.AtomicInteger;

import org.apache.commons.io.IOUtils;
import org.junit.Assert;
import org.junit.Test;

import net.sourceforge.pmd.lang.LanguageRegistry;
import net.sourceforge.pmd.lang.LanguageVersionHandler;
import net.sourceforge.pmd.lang.Parser;
import net.sourceforge.pmd.lang.scala.ScalaLanguageModule;
import net.sourceforge.pmd.lang.scala.ast.ScalaNode;
import net.sourceforge.pmd.lang.scala.ast.ScalaParserVisitorAdapter;

public class ScalaParserTest {
    private static final String SCALA_TEST = "/parserFiles/helloworld.scala";

    @Test
    public void testLineNumbersAccuracy() throws Exception {
        LanguageVersionHandler scalaVersionHandler = LanguageRegistry.getLanguage(ScalaLanguageModule.NAME)
                .getDefaultVersion().getLanguageVersionHandler();
        Parser parser = scalaVersionHandler.getParser(scalaVersionHandler.getDefaultParserOptions());
        ScalaNode<?> root = (ScalaNode<?>) parser.parse(null,
                new StringReader(IOUtils.toString(getClass().getResourceAsStream(SCALA_TEST), "UTF-8")));

        final List<NodeLineMatcher> nodeLineTargets = new ArrayList<NodeLineMatcher>();
        nodeLineTargets.add(new NodeLineMatcher("TermName", "Main", 1));
        nodeLineTargets.add(new NodeLineMatcher("TypeName", "App", 1));
        nodeLineTargets.add(new NodeLineMatcher("TermName", "println", 2));

        ScalaParserVisitorAdapter<Void, Void> visitor = new ScalaParserVisitorAdapter<Void, Void>() {
            public Void visit(ScalaNode<?> node, Void data) {
                for (NodeLineMatcher nodeLine : nodeLineTargets) {
                    if (nodeLine.nodeName.equals(node.getXPathNodeName()) && nodeLine.nodeValue.equals(node.getImage())
                            && nodeLine.lineNumber == node.getBeginLine()) {
                        nodeLine.matched = true;
                    }
                }
                return super.visit(node, data);
            }
        };
        visitor.visit(root, null);

        for (NodeLineMatcher nodeLine : nodeLineTargets) {
            Assert.assertTrue("Did not successfully find the node " + nodeLine, nodeLine.matched);
        }
    }

    class NodeLineMatcher {
        String nodeName;
        String nodeValue;
        Integer lineNumber;
        boolean matched = false;

        NodeLineMatcher(String nodeName, String nodeValue, Integer lineNumber) {
            this.nodeName = nodeName;
            this.nodeValue = nodeValue;
            this.lineNumber = lineNumber;
        }

        public String toString() {
            return "NodeName: " + nodeName + " NodeValue: " + nodeValue + " Line Number: " + lineNumber;
        }
    }

    @Test
    public void testCountNodes() throws Exception {
        LanguageVersionHandler scalaVersionHandler = LanguageRegistry.getLanguage(ScalaLanguageModule.NAME)
                .getDefaultVersion().getLanguageVersionHandler();
        Parser parser = scalaVersionHandler.getParser(scalaVersionHandler.getDefaultParserOptions());
        ScalaNode<?> root = (ScalaNode<?>) parser.parse(null,
                new StringReader(IOUtils.toString(getClass().getResourceAsStream(SCALA_TEST), "UTF-8")));

        final AtomicInteger nodeCount = new AtomicInteger();
        ScalaParserVisitorAdapter<Void, Void> visitor = new ScalaParserVisitorAdapter<Void, Void>() {
            @Override
            public Void visit(ScalaNode<?> node, Void data) {
                nodeCount.incrementAndGet();
                return super.visit(node, data);
            }
        };
        visitor.visit(root, null);
        Assert.assertEquals(12, nodeCount.get());
    }
}
