/**
 * BSD-style license; for more info see http://pmd.sourceforge.net/license.html
 */

package net.sourceforge.pmd;

import static junit.framework.TestCase.assertEquals;
import static junit.framework.TestCase.assertTrue;
import static junit.framework.TestCase.fail;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;

import org.apache.commons.lang3.StringUtils;
import org.junit.Test;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.NodeList;
import org.xml.sax.SAXException;

import net.sourceforge.pmd.ruledef.RuleDefsAggregator;

/**
 * @author Clément Fournier
 * @since 6.0.0
 */
public class AbstractMasterRuleDefTest {

    private final String langName;
    private final URL indexURL;


    public AbstractMasterRuleDefTest(String langName) {
        this.langName = langName;
        indexURL = Thread.currentThread().getContextClassLoader()
                         .getResource(RuleDefsAggregator.getGeneratedResourceFileName(langName));
    }


    @Test
    public void testMasterRuledefExists() {
        assertTrue(new File(indexURL.getFile()).exists());
    }


    @Test
    public void testMasterRuleDefHasNoNameConflicts() throws ParserConfigurationException, SAXException {
        DocumentBuilderFactory documentBuilderFactory = DocumentBuilderFactory.newInstance();
        documentBuilderFactory.setNamespaceAware(true);
        DocumentBuilder builder = documentBuilderFactory.newDocumentBuilder();

        File master = new File(indexURL.getFile());

        if (!master.exists()) {
            fail();
        }

        Document document = null;
        try (InputStream stream = new FileInputStream(master)) {
            document = builder.parse(stream);
        } catch (IOException e) {
            fail();
        }


        NodeList rules = document.getElementsByTagName("rule");
        List<String> ruleNames = new ArrayList<>(rules.getLength());

        for (int i = 0; i < rules.getLength(); i++) {
            String name = ((Element) rules.item(i)).getAttribute("name");
            if (StringUtils.isNotBlank(name)) {
                ruleNames.add(name);
            }
        }
        assertEquals(ruleNames.size(), new HashSet<>(ruleNames).size());

    }
}
