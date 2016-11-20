/**
 * BSD-style license; for more info see http://pmd.sourceforge.net/license.html
 */

package net.sourceforge.pmd.cpd;

import java.util.HashMap;
import java.util.Map;

import org.junit.Assert;
import org.junit.Test;

public class CPDConfigurationTest {

    @Test
    public void testRenderers() {
        Map<String, Class<? extends Renderer>> renderersToTest = new HashMap<>();
        renderersToTest.put("csv", CSVRenderer.class);
        renderersToTest.put("xml", XMLRenderer.class);
        renderersToTest.put("csv_with_linecount_per_file", CSVWithLinecountPerFileRenderer.class);
        renderersToTest.put("vs", VSRenderer.class);
        renderersToTest.put("text", SimpleRenderer.class);

        for (Map.Entry<String, Class<? extends Renderer>> entry : renderersToTest.entrySet()) {
            Renderer r = CPDConfiguration.getRendererFromString(entry.getKey(), "UTF-8");
            Assert.assertNotNull(r);
            Assert.assertSame(entry.getValue(), r.getClass());
        }
    }
}
