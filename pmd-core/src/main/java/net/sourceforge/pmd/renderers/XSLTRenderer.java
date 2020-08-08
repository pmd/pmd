/**
 * BSD-style license; for more info see http://pmd.sourceforge.net/license.html
 */

package net.sourceforge.pmd.renderers;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.io.StringReader;
import java.io.StringWriter;
import java.io.Writer;
import java.nio.file.Files;
import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerConfigurationException;
import javax.xml.transform.TransformerException;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamResult;
import javax.xml.transform.stream.StreamSource;

import org.apache.commons.lang3.StringUtils;
import org.w3c.dom.Document;
import org.xml.sax.InputSource;
import org.xml.sax.SAXException;

import net.sourceforge.pmd.properties.PropertyDescriptor;
import net.sourceforge.pmd.properties.PropertyFactory;

/**
 * Renderer to XML format with a XSL Transformation applied.
 *
 * @author Romain Pelisse, belaran@gmail.com
 */
public class XSLTRenderer extends XMLRenderer {

    public static final String NAME = "xslt";

    // TODO 7.0.0 use PropertyDescriptor<Optional<File>>
    public static final PropertyDescriptor<String> XSLT_FILENAME = PropertyFactory.stringProperty("xsltFilename").desc("The XSLT file name.").defaultValue("").build();

    private Transformer transformer;
    private String xsltFilename = "/pmd-nicerhtml.xsl";
    private Writer outputWriter;
    private StringWriter stringWriter;

    public XSLTRenderer() {
        super();
        setName(NAME);
        setDescription("XML with a XSL Transformation applied.");
        definePropertyDescriptor(XSLT_FILENAME);
    }

    @Override
    public String defaultFileExtension() {
        return "xsl";
    }

    @Override
    public void start() throws IOException {
        String xsltFilenameProperty = getProperty(XSLT_FILENAME);
        if (StringUtils.isNotBlank(xsltFilenameProperty)) {
            File file = new File(xsltFilenameProperty);
            if (file.exists() && file.canRead()) {
                this.xsltFilename = xsltFilenameProperty;
            }
        }

        // We keep the inital writer to put the final html output
        this.outputWriter = getWriter();
        // We use a new one to store the XML...
        this.stringWriter = new StringWriter();
        setWriter(stringWriter);
        // If don't find the xsl no need to bother doing the all report,
        // so we check this here...
        InputStream xslt = null;
        File file = new File(this.xsltFilename);
        if (file.exists() && file.canRead()) {
            xslt = Files.newInputStream(file.toPath());
        } else {
            xslt = this.getClass().getResourceAsStream(this.xsltFilename);
        }
        if (xslt == null) {
            throw new FileNotFoundException("Can't find XSLT file: " + this.xsltFilename);
        }

        try (InputStream stream = xslt) {
            this.prepareTransformer(stream);
        }
        // Now we build the XML file
        super.start();
    }

    /**
     * Prepare the transformer, doing the proper "building"...
     *
     * @param xslt
     *            The stylesheet provided as an InputStream
     */
    private void prepareTransformer(InputStream xslt) {
        try {
            // Get a TransformerFactory object
            TransformerFactory factory = TransformerFactory.newInstance();
            StreamSource src = new StreamSource(xslt);
            // Get an XSL Transformer object
            this.transformer = factory.newTransformer(src);
        } catch (TransformerConfigurationException e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public void end() throws IOException {
        // First we finish the XML report
        super.end();
        // Now we transform it using XSLT
        Document doc = this.getDocument(stringWriter.toString());
        this.transform(doc);
    }

    private void transform(Document doc) {
        DOMSource source = new DOMSource(doc);
        StreamResult result = new StreamResult(this.outputWriter);
        try {
            transformer.transform(source, result);
        } catch (TransformerException e) {
            throw new RuntimeException(e);
        }
    }

    private Document getDocument(String xml) {
        try {
            DocumentBuilder parser = DocumentBuilderFactory.newInstance().newDocumentBuilder();
            return parser.parse(new InputSource(new StringReader(xml)));
        } catch (ParserConfigurationException | SAXException | IOException e) {
            throw new RuntimeException(e);
        }
    }
}
