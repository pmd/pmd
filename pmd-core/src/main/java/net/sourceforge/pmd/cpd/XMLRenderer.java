/**
 * BSD-style license; for more info see http://pmd.sourceforge.net/license.html
 */
package net.sourceforge.pmd.cpd;

import java.io.StringWriter;
import java.util.Iterator;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.transform.OutputKeys;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerException;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamResult;

import org.w3c.dom.Document;
import org.w3c.dom.Element;

/**
 * @author Philippe T'Seyen - original implementation
 * @author Romain Pelisse - javax.xml implementation
 * 
 */
public final class XMLRenderer implements Renderer {

    private String encoding;

    /**
     * Creates a XML Renderer with the default (platform dependent) encoding.
     */
    public XMLRenderer() {
        this(null);
    }

    /**
     * Creates a XML Renderer with a specific output encoding.
     * 
     * @param encoding the encoding to use or null. If null, default (platform
     *            dependent) encoding is used.
     */
    public XMLRenderer(String encoding) {
        setEncoding(encoding);
    }

    public void setEncoding(String encoding) {
        if (encoding != null) {
            this.encoding = encoding;
        } else {
            this.encoding = System.getProperty("file.encoding");
        }
    }

    public String getEncoding() {
        return this.encoding;
    }

    private Document createDocument() {
        try {
            DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
            DocumentBuilder parser = factory.newDocumentBuilder();
            return parser.newDocument();
        } catch (ParserConfigurationException e) {
            throw new IllegalStateException(e);
        }
    }

    private String xmlDocToString(Document doc) {
        try {
            TransformerFactory tf = TransformerFactory.newInstance();
            Transformer transformer = tf.newTransformer();
            transformer.setOutputProperty(OutputKeys.METHOD, "xml");
            transformer.setOutputProperty(OutputKeys.ENCODING, encoding);
            transformer.setOutputProperty(OutputKeys.INDENT, "yes");
            transformer.setOutputProperty(OutputKeys.CDATA_SECTION_ELEMENTS, "codefragment");
            StringWriter writer = new StringWriter();
            transformer.transform(new DOMSource(doc), new StreamResult(writer));
            return writer.toString();
        } catch (TransformerException e) {
            throw new IllegalStateException(e);
        }
    }

    public String render(Iterator<Match> matches) {
        Document doc = createDocument();
        Element root = doc.createElement("pmd-cpd");
        doc.appendChild(root);

        Match match;
        while (matches.hasNext()) {
            match = matches.next();
            root.appendChild(addCodeSnippet(doc,
                    addFilesToDuplicationElement(doc, createDuplicationElement(doc, match), match), match));
        }
        return xmlDocToString(doc);
    }

    private Element addFilesToDuplicationElement(Document doc, Element duplication, Match match) {
        Mark mark;
        for (Iterator<Mark> iterator = match.iterator(); iterator.hasNext();) {
            mark = iterator.next();
            Element file = doc.createElement("file");
            file.setAttribute("line", String.valueOf(mark.getBeginLine()));
            file.setAttribute("path", mark.getFilename());
            duplication.appendChild(file);
        }
        return duplication;
    }

    private Element addCodeSnippet(Document doc, Element duplication, Match match) {
        String codeSnipet = match.getSourceCodeSlice();
        if (codeSnipet != null) {
            Element codefragment = doc.createElement("codefragment");
            codefragment.appendChild(doc.createCDATASection(codeSnipet));
            duplication.appendChild(codefragment);
        }
        return duplication;
    }

    private Element createDuplicationElement(Document doc, Match match) {
        Element duplication = doc.createElement("duplication");
        duplication.setAttribute("lines", String.valueOf(match.getLineCount()));
        duplication.setAttribute("tokens", String.valueOf(match.getTokenCount()));
        return duplication;
    }
}
