/**
 * BSD-style license; for more info see http://pmd.sourceforge.net/license.html
 */

package net.sourceforge.pmd.cpd;

import java.io.IOException;
import java.io.StringWriter;
import java.io.Writer;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
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

import net.sourceforge.pmd.cpd.renderer.CPDRenderer;
import net.sourceforge.pmd.cpd.renderer.CPDReportRenderer;
import net.sourceforge.pmd.internal.util.IteratorUtil;
import net.sourceforge.pmd.util.StringUtil;

/**
 * @author Philippe T'Seyen - original implementation
 * @author Romain Pelisse - javax.xml implementation
 *
 */
public final class XMLRenderer implements Renderer, CPDRenderer, CPDReportRenderer {

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
     * @param encoding
     *            the encoding to use or null. If null, default (platform
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

    private void dumpDocToWriter(Document doc, Writer writer) {
        try {
            TransformerFactory tf = TransformerFactory.newInstance();
            Transformer transformer = tf.newTransformer();
            transformer.setOutputProperty(OutputKeys.VERSION, "1.0");
            transformer.setOutputProperty(OutputKeys.METHOD, "xml");
            transformer.setOutputProperty(OutputKeys.ENCODING, encoding);
            transformer.setOutputProperty(OutputKeys.INDENT, "yes");
            transformer.setOutputProperty(OutputKeys.CDATA_SECTION_ELEMENTS, "codefragment");
            transformer.transform(new DOMSource(doc), new StreamResult(writer));
        } catch (TransformerException e) {
            throw new IllegalStateException(e);
        }
    }

    @Override
    public String render(Iterator<Match> matches) {
        StringWriter writer = new StringWriter();
        try {
            render(matches, writer);
        } catch (IOException ignored) {
            // Not really possible with a StringWriter
        }
        return writer.toString();
    }

    @Override
    public void render(Iterator<Match> matches, Writer writer) throws IOException {
        render(new CPDReport(IteratorUtil.toList(matches), Collections.<String, Integer>emptyMap()), writer);
    }

    @Override
    public void render(final CPDReport report, final Writer writer) throws IOException {
        final Document doc = createDocument();
        final Element root = doc.createElement("pmd-cpd");
        final Map<String, Integer> numberOfTokensPerFile = report.getNumberOfTokensPerFile();
        final List<Match> matches = report.getMatches();
        doc.appendChild(root);

        final List<Map.Entry<String, Integer>> entries = new ArrayList<>(numberOfTokensPerFile.entrySet());
        for (final Map.Entry<String, Integer> pair : entries) {
            final Element fileElement = doc.createElement("file");
            fileElement.setAttribute("path", pair.getKey());
            fileElement.setAttribute("totalNumberOfTokens", String.valueOf(pair.getValue()));
            root.appendChild(fileElement);
        }

        for (Match match : matches) {
            root.appendChild(addCodeSnippet(doc,
                    addFilesToDuplicationElement(doc, createDuplicationElement(doc, match), match), match));
        }
        dumpDocToWriter(doc, writer);
        writer.flush();
    }

    private Element addFilesToDuplicationElement(Document doc, Element duplication, Match match) {
        Mark mark;
        for (Iterator<Mark> iterator = match.iterator(); iterator.hasNext();) {
            mark = iterator.next();
            final Element file = doc.createElement("file");
            file.setAttribute("line", String.valueOf(mark.getBeginLine()));
            // only remove invalid characters, escaping is done by the DOM impl.
            String filenameXml10 = StringUtil.removedInvalidXml10Characters(mark.getFilename());
            file.setAttribute("path", filenameXml10);
            file.setAttribute("endline", String.valueOf(mark.getEndLine()));
            final int beginCol = mark.getBeginColumn();
            final int endCol = mark.getEndColumn();
            if (beginCol != -1) {
                file.setAttribute("column", String.valueOf(beginCol));
            }
            if (endCol != -1) {
                file.setAttribute("endcolumn", String.valueOf(endCol));
            }
            final int beginIndex = mark.getBeginTokenIndex();
            final int endIndex = mark.getEndTokenIndex();
            file.setAttribute("begintoken", String.valueOf(beginIndex));
            if (endIndex != -1) {
                file.setAttribute("endtoken", String.valueOf(endIndex));
            }
            duplication.appendChild(file);
        }
        return duplication;
    }

    private Element addCodeSnippet(Document doc, Element duplication, Match match) {
        String codeSnippet = match.getSourceCodeSlice();
        if (codeSnippet != null) {
            // the code snippet has normalized line endings
            String platformSpecific = codeSnippet.replace("\n", System.lineSeparator());
            Element codefragment = doc.createElement("codefragment");
            // only remove invalid characters, escaping is not necessary in CDATA.
            // if the string contains the end marker of a CDATA section, then the DOM impl will
            // create two cdata sections automatically.
            codefragment.appendChild(doc.createCDATASection(StringUtil.removedInvalidXml10Characters(platformSpecific)));
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
