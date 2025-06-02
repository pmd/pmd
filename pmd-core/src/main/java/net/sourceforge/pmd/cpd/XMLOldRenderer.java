/**
 * BSD-style license; for more info see http://pmd.sourceforge.net/license.html
 */

package net.sourceforge.pmd.cpd;

import java.io.IOException;
import java.io.Writer;

/**
 * Provides backwards compatible XML renderer, which doesn't use namespaces, schema and
 * doesn't output error information.
 *
 * <p>This renderer is available as "xmlold".
 *
 * @deprecated Update your tools to use the standard XML renderer "xml" again.
 */
@Deprecated
public class XMLOldRenderer implements CPDReportRenderer {
    private final XMLRenderer xmlRenderer;

    public XMLOldRenderer() {
        this(null);
    }

    public XMLOldRenderer(String encoding) {
        this.xmlRenderer = new XMLRenderer(encoding, false);
    }

    @Override
    public void render(CPDReport report, Writer writer) throws IOException {
        xmlRenderer.render(report, writer);
    }
}
