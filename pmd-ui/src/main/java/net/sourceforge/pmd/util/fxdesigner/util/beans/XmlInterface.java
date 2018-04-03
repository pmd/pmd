/**
 * BSD-style license; for more info see http://pmd.sourceforge.net/license.html
 */

package net.sourceforge.pmd.util.fxdesigner.util.beans;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.Optional;
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
 * Represents a version of the Xml format used to store settings. The
 * parser and serializer must understand each other, so they're kept
 * together.
 *
 * @author Clément Fournier
 * @since 6.1.0
 */
public abstract class XmlInterface {

    // modifying these will break compatibility
    private static final String SCHEMA_MODEL_VERSION = "revision";
    private static final String SCHEMA_DOCUMENT_ELEMENT = "designer-settings";

    private final int revisionNumber;


    public XmlInterface(int rev) {
        this.revisionNumber = rev;
    }


    public int getRevisionNumber() {
        return revisionNumber;
    }


    /**
     * Parses a XML document produced by {@link #writeModelToXml(File, SimpleBeanModelNode)}
     * into a settings node.
     *
     * @param document The document to parse
     *
     * @return The root of the model hierarchy, or empty if the revision is not supported
     */
    public final Optional<SimpleBeanModelNode> parseXml(Document document) {
        if (canParse(document)) {
            Element rootNodeElement = (Element) document.getDocumentElement().getChildNodes().item(1);
            return Optional.ofNullable(parseSettingsOwnerNode(rootNodeElement));
        }
        return Optional.empty();
    }


    /**
     * Returns true if the document can be read by this object.
     *
     * @param document Document to test
     */
    public boolean canParse(Document document) {
        int docVersion = Integer.parseInt(document.getDocumentElement().getAttribute(SCHEMA_MODEL_VERSION));
        return docVersion == getRevisionNumber();
    }


    private Document initDocument() throws IOException {
        DocumentBuilderFactory documentBuilderFactory = DocumentBuilderFactory.newInstance();
        DocumentBuilder documentBuilder;
        try {
            documentBuilder = documentBuilderFactory.newDocumentBuilder();
        } catch (ParserConfigurationException e) {
            throw new IOException("Failed to create settings document builder", e);
        }
        Document document = documentBuilder.newDocument();

        Element settingsElement = document.createElement(SCHEMA_DOCUMENT_ELEMENT);
        settingsElement.setAttribute(SCHEMA_MODEL_VERSION, "" + getRevisionNumber());
        document.appendChild(settingsElement);
        return document;
    }


    /** Saves parameters to disk. */
    private void save(Document document, File outputFile) throws IOException {
        try {
            TransformerFactory transformerFactory = TransformerFactory.newInstance();
            Transformer transformer = transformerFactory.newTransformer();

            transformer.setOutputProperty(OutputKeys.METHOD, "xml");
            transformer.setOutputProperty(OutputKeys.INDENT, "yes");
            transformer.setOutputProperty("{http://xml.apache.org/xslt}indent-amount", "4");
            transformer.setOutputProperty(OutputKeys.ENCODING, "UTF-8");

            Source source = new DOMSource(document);
            outputFile.getParentFile().mkdirs();
            Result result = new StreamResult(new FileWriter(outputFile));
            transformer.transform(source, result);
        } catch (TransformerException e) {
            throw new IOException("Failed to save settings", e);
        }
    }


    /**
     * Writes the model to a file.
     *
     * @param output The output file
     * @param model  The model to serialize
     *
     * @throws IOException If saving the settings failed
     */
    public final void writeModelToXml(File output, SimpleBeanModelNode model) throws IOException {
        Document document = initDocument();
        model.accept(getDocumentMakerVisitor(), document.getDocumentElement());
        save(document, output);
    }


    /**
     * Parses a settings node and its descendants recursively.
     *
     * @param nodeElement Element to parse
     *
     * @return The model described by the element
     */
    protected abstract SimpleBeanModelNode parseSettingsOwnerNode(Element nodeElement);


    /**
     * Gets a visitor which populates xml elements with corresponding nodes.
     *
     * @return A visitor
     */
    protected abstract BeanNodeVisitor<Element> getDocumentMakerVisitor();

}
