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
 * @author Philippe T'Seyen
 */
public final class XMLRenderer implements Renderer {
	
	// FUTURE: Use a XML API - rather than StringBuffer to generate XML Report. 
    // The most convenient would be to use one shipped in the JRE, and this should
    // also allow us to get ride of the encode below, as the XML API choosed will
    // do that for us...
    public String render(Iterator<Match> matches) {
    	Document doc = null;
    	try {
			DocumentBuilderFactory factory = DocumentBuilderFactory
					.newInstance();
			DocumentBuilder parser = factory.newDocumentBuilder();
			doc = parser.newDocument();
		} catch (ParserConfigurationException e) {
			throw new IllegalStateException(e);
		}
		Element root = doc.createElement("pmd-cpd");

		doc.appendChild(root);
/*        StringBuilder buffer = new StringBuilder(300);
        buffer.append("<?xml version=\"1.0\" encoding=\"");
        buffer.append(encoding);
        buffer.append("\"?>").append(PMD.EOL);
        buffer.append("<pmd-cpd>").append(PMD.EOL);*/
        Match match;
        while (matches.hasNext()) {
            match = matches.next();
            Element duplication = doc.createElement("duplication");
            duplication.setAttribute("lines", match.getLabel());
            duplication.setAttribute("tokens", String.valueOf(match.getTokenCount()));
/*            buffer.append("<duplication lines=\"");
            buffer.append(match.getLineCount());
            buffer.append("\" tokens=\"");
            buffer.append(match.getTokenCount());
            buffer.append("\">").append(PMD.EOL);*/

            TokenEntry mark;
            for (Iterator<TokenEntry> iterator = match.iterator(); iterator.hasNext();) {
                mark = iterator.next();
                Element file = doc.createElement("file");
                file.setAttribute("line", String.valueOf(mark.getBeginLine()));
                file.setAttribute("path", mark.getTokenSrcID());
                duplication.appendChild(file);
/*                buffer.append("<file line=\"");
                buffer.append(mark.getBeginLine());
                buffer.append("\" path=\"");
                buffer.append(XMLRenderer.encode(mark.getTokenSrcID()));
                buffer.append("\"/>").append(PMD.EOL);*/
            }
            String codeSnipet = match.getSourceCodeSlice();
            if (codeSnipet != null) {
            	Element codefragment = doc.createElement("codefragment");
 /*               buffer.append("<codefragment>").append(PMD.EOL);
                buffer.append("<![CDATA[").append(PMD.EOL);*/
//                buffer.append(StringUtil.replaceString(codeFragment, "]]>", "]]&gt;")).append(PMD.EOL + "]]>" + PMD.EOL + "</codefragment>" + PMD.EOL);
                codefragment.appendChild(doc.createCDATASection(codeSnipet));
                duplication.appendChild(codefragment);
            }
            //buffer.append("</duplication>").append(PMD.EOL);
            root.appendChild(duplication);
        }
        // 
        try {
        TransformerFactory tf = TransformerFactory.newInstance();
        Transformer transformer = tf.newTransformer();
        transformer.setOutputProperty(OutputKeys.ENCODING, System.getProperty("file.encoding"));
        StringWriter writer = new StringWriter();
        transformer.transform(new DOMSource(doc), new StreamResult(writer));
        return writer.getBuffer().toString().replaceAll("\n|\r", "");
        }  catch (TransformerException e) {
        	throw new IllegalStateException(e);
		}
    }
}
