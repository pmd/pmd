/**
 * BSD-style license; for more info see http://pmd.sourceforge.net/license.html
 */

package net.sourceforge.pmd.util.fxdesigner.util.settings;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.transform.OutputKeys;
import javax.xml.transform.Result;
import javax.xml.transform.Source;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerException;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamResult;

import org.w3c.dom.Document;
import org.w3c.dom.Element;

/**
 * Saves settings to disk as key-value pairs. This implementation stores them into an XML file.
 *
 * @author Clément Fournier
 * @since 6.0.0
 */
public class XMLSettingsSaver {

    private final String fileName;
    private Document document;


    private XMLSettingsSaver(String fileName) throws IOException {
        this.fileName = fileName;

        DocumentBuilderFactory documentBuilderFactory = DocumentBuilderFactory.newInstance();
        DocumentBuilder documentBuilder;
        try {
            documentBuilder = documentBuilderFactory.newDocumentBuilder();
        } catch (ParserConfigurationException e) {
            throw new IOException("Failed to create settings document builder", e);
        }
        document = documentBuilder.newDocument();

        Element settingsElement = document.createElement("settings");
        document.appendChild(settingsElement);
    }


    /**
     * Saves a key value pair.
     */
    public XMLSettingsSaver put(String key, String value) {
        Element settingElement = document.createElement("setting");
        settingElement.setAttribute("key", key);
        settingElement.appendChild(document.createCDATASection(value));
        document.getDocumentElement().appendChild(settingElement);

        return this;
    }


    /** Saves parameters to disk. */
    public void save() throws IOException {
        try {
            TransformerFactory transformerFactory = TransformerFactory.newInstance();
            Transformer transformer = transformerFactory.newTransformer();

            transformer.setOutputProperty(OutputKeys.METHOD, "xml");
            transformer.setOutputProperty(OutputKeys.INDENT, "yes");
            transformer.setOutputProperty("{http://xml.apache.org/xslt}indent-amount", "4");
            transformer.setOutputProperty(OutputKeys.ENCODING, "UTF-8");

            Source source = new DOMSource(document);
            Result result = new StreamResult(new FileWriter(new File(fileName)));
            transformer.transform(source, result);
        } catch (TransformerException e) {
            throw new IOException("Failed to save settings", e);

        }
    }


    /**
     * Get an instance.
     *
     * @throws IOException if initialisation fails
     */
    public static XMLSettingsSaver forFile(String fileName) throws IOException {
        return new XMLSettingsSaver(fileName);
    }


}
