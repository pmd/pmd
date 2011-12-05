/**
 * BSD-style license; for more info see http://pmd.sourceforge.net/license.html
 */
package net.sourceforge.pmd.build.xml;

import java.io.File;
import java.util.Map;

import javax.xml.transform.dom.DOMSource;

import org.w3c.dom.Document;
import org.w3c.dom.Element;

public interface XmlFileTemplater {

	Document doTemplate(Document doc, Element root);	
	
	void transform(File source, File result, String xsl);
	
	void transform(File source, File result, String xsl, Map<String, String> parameters);
	
	void transform(DOMSource source, File result, String xsl);
	
	void transform(DOMSource source, File result, String xsl, Map<String, String> parameters);
}
