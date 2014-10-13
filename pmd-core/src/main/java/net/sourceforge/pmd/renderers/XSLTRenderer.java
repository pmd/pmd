/**
 * BSD-style license; for more info see http://pmd.sourceforge.net/license.html
 */

package net.sourceforge.pmd.renderers;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.io.StringReader;
import java.io.StringWriter;
import java.io.Writer;

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

import net.sourceforge.pmd.lang.rule.properties.StringProperty;

import org.w3c.dom.Document;
import org.xml.sax.InputSource;
import org.xml.sax.SAXException;

/**
 * Renderer to XML format with a XSL Transformation applied.
 * 
 * @author Romain Pelisse, belaran@gmail.com
 */
public class XSLTRenderer extends XMLRenderer {

    public static final String NAME = "xslt";

    public static final StringProperty XSLT_FILENAME = new StringProperty("xsltFilename", "The XSLT file name.", null, 0);

    private Transformer transformer;
    private String xsltFilename = "/pmd-nicerhtml.xsl";
    private Writer outputWriter;

    public XSLTRenderer() {
	super();
	setName(NAME);
	setDescription("XML with a XSL Transformation applied.");
	definePropertyDescriptor(XSLT_FILENAME);
    }

    public String defaultFileExtension() { return "xsl"; }
    
    /**
     * {@inheritDoc}
     */
    @Override
    public void start() throws IOException {
	String xsltFilenameProperty = getProperty(XSLT_FILENAME);
	if (xsltFilenameProperty != null) {
	    File file = new File(xsltFilenameProperty);
	    if (file.exists() && file.canRead()) {
		this.xsltFilename = xsltFilenameProperty;
	    }
	}

	// We keep the inital writer to put the final html output
	this.outputWriter = getWriter();
	// We use a new one to store the XML...
	Writer w = new StringWriter();
	setWriter(w);
	// If don't find the xsl no need to bother doing the all report,
	// so we check this here...
	InputStream xslt = null;
	File file = new File(this.xsltFilename);
	if (file.exists() && file.canRead()) {
	    xslt = new FileInputStream(file);
	} else {
	    xslt = this.getClass().getResourceAsStream(this.xsltFilename);
	}
	if (xslt == null) {
	    throw new FileNotFoundException("Can't file XSLT sheet :" + this.xsltFilename);
	}
	this.prepareTransformer(xslt);
	// Now we build the XML file
	super.start();
    }

    /**
     * Prepare the transformer, doing the proper "building"...
     *
     * @param xslt The stylesheet provided as an InputStream
     */
    private void prepareTransformer(InputStream xslt) {
	if (xslt != null) {
	    try {
		//Get a TransformerFactory object
		TransformerFactory factory = TransformerFactory.newInstance();
		StreamSource src = new StreamSource(xslt);
		//Get an XSL Transformer object
		this.transformer = factory.newTransformer(src);
	    } catch (TransformerConfigurationException e) {
		e.printStackTrace();
	    }
	}
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void end() throws IOException {
	// First we finish the XML report
	super.end();
	// Now we transform it using XSLT
	Writer writer = super.getWriter();
	if (writer instanceof StringWriter) {
	    StringWriter w = (StringWriter) writer;
	    StringBuffer buffer = w.getBuffer();
	    Document doc = this.getDocument(buffer.toString());
	    this.transform(doc);
	} else {
	    // Should not happen !
	    throw new RuntimeException("Wrong writer");
	}

    }

    private void transform(Document doc) {
	DOMSource source = new DOMSource(doc);
	this.setWriter(new StringWriter());
	StreamResult result = new StreamResult(this.outputWriter);
	try {
	    transformer.transform(source, result);
	} catch (TransformerException e) {
	    e.printStackTrace();
	}
    }

    private Document getDocument(String xml) {
	try {
	    DocumentBuilder parser = DocumentBuilderFactory.newInstance().newDocumentBuilder();
	    return parser.parse(new InputSource(new StringReader(xml)));
	} catch (ParserConfigurationException e) {
	    e.printStackTrace();
	} catch (SAXException e) {
	    e.printStackTrace();
	} catch (IOException e) {
	    e.printStackTrace();
	}
	return null;
    }
}
