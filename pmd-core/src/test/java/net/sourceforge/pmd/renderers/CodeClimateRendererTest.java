/**
 * BSD-style license; for more info see http://pmd.sourceforge.net/license.html
 */
package net.sourceforge.pmd.renderers;

import net.sourceforge.pmd.PMD;

public class CodeClimateRendererTest extends AbstractRendererTst {

    @Override
    public Renderer getRenderer() {
        return new CodeClimateRenderer();
    }

    @Override
    public String getExpected() {
        return "{\"type\":\"issue\",\"check_name\":\"Foo\",\"description\":\"blah\",\"content\":{\"body\":\"desc\"},\"categories\":[\"Style\"],\"location\":{\"path\":\"n/a\",\"lines\":{\"begin\":1,\"end\":1}},\"severity\":\"info\"}" + PMD.EOL;
    }

    @Override
    public String getExpectedEmpty() {
        return "";
    }

    @Override
    public String getExpectedMultiple() {
        return "{\"type\":\"issue\",\"check_name\":\"Foo\",\"description\":\"blah\",\"content\":{\"body\":\"desc\"},\"categories\":[\"Style\"],\"location\":{\"path\":\"n/a\",\"lines\":{\"begin\":1,\"end\":1}},\"severity\":\"info\"}" + PMD.EOL +
            "{\"type\":\"issue\",\"check_name\":\"Foo\",\"description\":\"blah\",\"content\":{\"body\":\"desc\"},\"categories\":[\"Style\"],\"location\":{\"path\":\"n/a\",\"lines\":{\"begin\":1,\"end\":1}},\"severity\":\"info\"}" + PMD.EOL;
    }

    public static junit.framework.Test suite() {
        return new junit.framework.JUnit4TestAdapter(CodeClimateRendererTest.class);
    }
}
