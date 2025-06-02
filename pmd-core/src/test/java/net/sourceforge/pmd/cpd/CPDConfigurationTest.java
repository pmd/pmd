/**
 * BSD-style license; for more info see http://pmd.sourceforge.net/license.html
 */

package net.sourceforge.pmd.cpd;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.instanceOf;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertSame;
import static org.junit.jupiter.api.Assertions.assertThrows;

import java.nio.charset.StandardCharsets;
import java.util.HashMap;
import java.util.Map;

import org.junit.jupiter.api.Test;

import net.sourceforge.pmd.lang.DummyLanguageNoCapabilities;
import net.sourceforge.pmd.lang.LanguageRegistry;

class CPDConfigurationTest {

    @Test
    void testRenderers() {
        Map<String, Class<? extends CPDReportRenderer>> renderersToTest = new HashMap<>();
        renderersToTest.put("csv", CSVRenderer.class);
        renderersToTest.put("xml", XMLRenderer.class);
        renderersToTest.put("csv_with_linecount_per_file", CSVWithLinecountPerFileRenderer.class);
        renderersToTest.put("vs", VSRenderer.class);
        renderersToTest.put("text", SimpleRenderer.class);

        for (Map.Entry<String, Class<? extends CPDReportRenderer>> entry : renderersToTest.entrySet()) {
            CPDReportRenderer r = CPDConfiguration.createRendererByName(entry.getKey(), StandardCharsets.UTF_8);
            assertNotNull(r);
            assertSame(entry.getValue(), r.getClass());
        }
    }

    @Test
    void testRendererEncoding() {
        CPDConfiguration conf = new CPDConfiguration();
        conf.setRendererName("xml");
        conf.setSourceEncoding(StandardCharsets.UTF_16);

        CPDReportRenderer renderer = conf.getCPDReportRenderer();
        assertNotNull(renderer);
        assertThat(renderer, instanceOf(XMLRenderer.class));
        assertEquals(StandardCharsets.UTF_16.name(), ((XMLRenderer) renderer).getEncoding());
    }

    @Test
    void testRendererEncoding2() {
        CPDConfiguration conf = new CPDConfiguration();
        // here the order of these statements are reversed
        conf.setSourceEncoding(StandardCharsets.UTF_16);
        conf.setRendererName("xml");

        CPDReportRenderer renderer = conf.getCPDReportRenderer();
        assertNotNull(renderer);
        assertThat(renderer, instanceOf(XMLRenderer.class));
        assertEquals(StandardCharsets.UTF_16.name(), ((XMLRenderer) renderer).getEncoding());
    }


    @Test
    void testCpdNotSupported() {
        DummyLanguageNoCapabilities lang = DummyLanguageNoCapabilities.getInstance();
        final CPDConfiguration configuration = new CPDConfiguration(LanguageRegistry.singleton(lang));

        assertThrows(UnsupportedOperationException.class,
            () -> configuration.setOnlyRecognizeLanguage(lang));
        assertThrows(UnsupportedOperationException.class,
            () -> configuration.setDefaultLanguageVersion(lang.getDefaultVersion()));
    }

}
