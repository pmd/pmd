/**
 * BSD-style license; for more info see http://pmd.sourceforge.net/license.html
 */

package net.sourceforge.pmd.renderers;

import net.sourceforge.pmd.PMDVersion;
import net.sourceforge.pmd.reporting.Report.ConfigurationError;
import net.sourceforge.pmd.reporting.Report.ProcessingError;

class MetricsRendererTest extends AbstractRendererTest {

    @Override
    Renderer getRenderer() {
        return new MetricsRenderer();
    }

    @Override
    String getExpected() {
        return getHeader() + "</metrics>" + EOL;
    }

    @Override
    String getExpectedEmpty() {
        return getHeader() + "</metrics>" + EOL;
    }

    @Override
    String getExpectedMultiple() {
        return getHeader() + "</metrics>" + EOL;
    }

    @Override
    String getExpectedError(ProcessingError error) {
        return getHeader() + "<error filename=\"file\" msg=\"RuntimeException: Error\">"
               + EOL + "<![CDATA[" + error.getDetail() + "]]>" + EOL + "</error>" + EOL + "</metrics>" + EOL;
    }

    @Override
    String getExpectedErrorWithoutMessage(ProcessingError error) {
        return getHeader() + "<error filename=\"file\" msg=\"NullPointerException: null\">"
               + EOL + "<![CDATA[" + error.getDetail() + "]]>" + EOL + "</error>" + EOL + "</metrics>" + EOL;
    }

    @Override
    String getExpectedError(ConfigurationError error) {
        return getHeader() + "<error filename=\"Foo\" msg=\"a configuration error\"/>"
               + EOL + "</metrics>" + EOL;
    }

    @Override
    String filter(String expected) {
        return expected.replaceAll(" timestamp=\"[^\"]+\"", " timestamp=\"\"");
    }

    String getHeader() {
        return "<?xml version=\"1.0\" encoding=\"UTF-8\"?>" + EOL
               + "<metrics version=\"" + PMDVersion.VERSION + "\" timestamp=\"2024-08-12T10:54:00.000\" source=\"PMD\">" + EOL;
    }
}
