/**
 * BSD-style license; for more info see http://pmd.sourceforge.net/license.html
 */

package net.sourceforge.pmd.util.fxdesigner.util.settings;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.Collections;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;
import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;

import org.apache.commons.io.IOUtils;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.xml.sax.SAXException;

/**
 * Loads settings stored in the format of {@link XMLSettingsSaver}.
 *
 * @author Clément Fournier
 * @since 6.0.0
 */
public class XMLSettingsLoader {

    private final String fileName;


    public XMLSettingsLoader(String fileName) {
        this.fileName = fileName;
    }


    private Set<Element> getSettingNodes(Document document) {
        NodeList nodes = document.getElementsByTagName("setting");
        Set<Element> set = new HashSet<>();
        for (int i = 0; i < nodes.getLength(); i++) {
            set.add((Element) nodes.item(i));
        }

        return set;
    }


    public Map<String, String> getSettings() throws IOException {
        InputStream stream = null;
        try {
            File file = new File(fileName);

            if (file.exists()) {

                DocumentBuilder builder = DocumentBuilderFactory.newInstance().newDocumentBuilder();
                stream = new FileInputStream(file);
                Document document = builder.parse(stream);

                Set<Element> settings = getSettingNodes(document);

                return settings.stream()
                               .collect(Collectors.toMap((elt) -> elt.getAttribute("key"),
                                                         Node::getTextContent));

            }
        } catch (SAXException | ParserConfigurationException | IOException e) {
            throw new IOException("Failed to load settings", e);
        } finally {
            IOUtils.closeQuietly(stream);

        }

        return Collections.emptyMap();
    }
}
