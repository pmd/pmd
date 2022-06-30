/**
 * BSD-style license; for more info see http://pmd.sourceforge.net/license.html
 */

package net.sourceforge.pmd.cpd;

import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertSame;

import java.util.HashMap;
import java.util.Map;

import org.junit.jupiter.api.Test;

import net.sourceforge.pmd.cpd.renderer.CPDRenderer;

class CPDConfigurationTest {

    @Test
    void testRenderers() {
        Map<String, Class<? extends CPDRenderer>> renderersToTest = new HashMap<>();
        renderersToTest.put("csv", CSVRenderer.class);
        renderersToTest.put("xml", XMLRenderer.class);
        renderersToTest.put("csv_with_linecount_per_file", CSVWithLinecountPerFileRenderer.class);
        renderersToTest.put("vs", VSRenderer.class);
        renderersToTest.put("text", SimpleRenderer.class);

        for (Map.Entry<String, Class<? extends CPDRenderer>> entry : renderersToTest.entrySet()) {
            Renderer r = CPDConfiguration.getRendererFromString(entry.getKey(), "UTF-8");
            assertNotNull(r);
            assertSame(entry.getValue(), r.getClass());
        }
    }

    @Test
    void testCPDRenderers() {
        Map<String, Class<? extends CPDRenderer>> renderersToTest = new HashMap<>();
        renderersToTest.put("csv", CSVRenderer.class);
        renderersToTest.put("xml", XMLRenderer.class);
        renderersToTest.put("csv_with_linecount_per_file", CSVWithLinecountPerFileRenderer.class);
        renderersToTest.put("vs", VSRenderer.class);
        renderersToTest.put("text", SimpleRenderer.class);

        for (Map.Entry<String, Class<? extends CPDRenderer>> entry : renderersToTest.entrySet()) {
            CPDRenderer r = CPDConfiguration.getCPDRendererFromString(entry.getKey(), "UTF-8");
            assertNotNull(r);
            assertSame(entry.getValue(), r.getClass());
        }
    }
}
