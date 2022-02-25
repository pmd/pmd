/**
 * BSD-style license; for more info see http://pmd.sourceforge.net/license.html
 */

package net.sourceforge.pmd.renderers;


import java.io.IOException;
import javax.xml.stream.XMLStreamException;

import net.sourceforge.pmd.RuleViolation;

public class XMLVerboseRenderer extends XMLRenderer {
    public static final String NAME = "xml-verbose";

    public XMLVerboseRenderer() {
        super();
    }

    public XMLVerboseRenderer(String encoding) {
        super(encoding);
    }

    @Override
    protected void renderFileViolationsBody(RuleViolation rv) throws XMLStreamException, IOException {
        super.renderFileViolationsBody(rv);
        try {
            String sourceCode = rv.getSourceCode(getProperty(ENCODING));
            if (sourceCode != null) {
                getXmlWriter().writeStartElement("codesnippet");
                writeNewLine();
                getXmlWriter().writeCharacters(sourceCode);
                writeNewLine();
                getXmlWriter().writeEndElement();
                writeNewLine();
            }
        } catch (RuntimeException ex) {
            throw new IOException(ex);
        }
    }
}
