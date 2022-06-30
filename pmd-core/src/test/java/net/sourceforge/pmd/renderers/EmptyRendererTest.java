/**
 * BSD-style license; for more info see http://pmd.sourceforge.net/license.html
 */

package net.sourceforge.pmd.renderers;

class EmptyRendererTest extends AbstractRendererTest {

    @Override
    Renderer getRenderer() {
        return new EmptyRenderer();
    }

    @Override
    void testNullPassedIn() throws Exception {
        // Overriding test from the super class, this renderer doesn't care, so no NPE.
        getRenderer().renderFileReport(null);
    }

    @Override
    String getExpected() {
        return "";
    }

    @Override
    String getExpectedEmpty() {
        return "";
    }

    @Override
    String getExpectedMultiple() {
        return "";
    }
}
