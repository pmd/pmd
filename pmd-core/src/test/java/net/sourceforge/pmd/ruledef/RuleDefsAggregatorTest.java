/**
 * BSD-style license; for more info see http://pmd.sourceforge.net/license.html
 */

package net.sourceforge.pmd.ruledef;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

import java.io.IOException;
import java.io.InputStream;
import java.io.StringWriter;
import java.net.URL;
import java.nio.file.FileSystems;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.transform.TransformerException;

import org.apache.tools.ant.filters.StringInputStream;
import org.junit.Test;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.xml.sax.SAXException;

/**
 * @author Clément Fournier
 * @since 6.0.0
 */
public class RuleDefsAggregatorTest {


    private Document getOutputDocument(List<String> excludes) throws ParserConfigurationException, TransformerException, SAXException, IOException {
        StringWriter writer = new StringWriter();
        URL ruledefsURL = Thread.currentThread().getContextClassLoader().getResource("ruledefs/dummy");
        Path ruledefsPath = FileSystems.getDefault().getPath(ruledefsURL.getFile());

        new RuleDefsAggregator().generateMasterRuledefFrom(ruledefsPath, excludes, writer);

        DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
        factory.setNamespaceAware(true);

        InputStream is = new StringInputStream(writer.toString());

        return factory.newDocumentBuilder().parse(is);
    }


    private List<Element> getRulesElementAfterAggregation(List<String> excludes) throws ParserConfigurationException, TransformerException, SAXException, IOException {
        Document output = getOutputDocument(excludes);

        Element rulesetElt = output.getDocumentElement();

        NodeList rules = rulesetElt.getElementsByTagName("rule");
        List<Element> rulesList = new ArrayList<>();

        for (int i = 0; i < rules.getLength(); i++) {
            if (rules.item(i).getNodeType() == Node.ELEMENT_NODE
                && "rule".equals(rules.item(i).getNodeName())) {
                rulesList.add((Element) rules.item(i));
            }
        }

        return rulesList;
    }


    @Test
    public void testWholeDirAggregation() throws ParserConfigurationException, TransformerException, SAXException, IOException {
        List<Element> rulesList = getRulesElementAfterAggregation(Collections.<String>emptyList());
        assertEquals(4, rulesList.size());
    }


    @Test
    public void testExclude() throws ParserConfigurationException, TransformerException, SAXException, IOException {
        List<Element> rulesList = getRulesElementAfterAggregation(Collections.singletonList("ruledef1.xml"));

        assertEquals(2, rulesList.size());
        for (Element e : rulesList) {
            assertFalse(e.getAttribute("name").startsWith("RDef1"));
        }
    }


    @Test
    public void testNamespace() throws ParserConfigurationException, IOException, SAXException, TransformerException {
        Document doc = getOutputDocument(Collections.<String>emptyList());

        assertTrue(doc.isDefaultNamespace(RuleDefsAggregator.OUTPUT_NAMESPACE));
    }


}
