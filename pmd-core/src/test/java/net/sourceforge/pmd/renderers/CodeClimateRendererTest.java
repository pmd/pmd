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
        return "{\"type\":\"issue\",\"check_name\":\"Foo\",\"description\":\"blah\",\"content\":{\"body\":\"## Foo" + PMD.EOL + PMD.EOL + "Since: PMD null" + PMD.EOL + PMD.EOL + "Priority: Low" + PMD.EOL + PMD.EOL + "[Categories](https://github.com/codeclimate/spec/blob/master/SPEC.md#categories): Style" + PMD.EOL + PMD.EOL + "[Remediation Points](https://github.com/codeclimate/spec/blob/master/SPEC.md#remediation-points): 50000" + PMD.EOL + PMD.EOL + "desc" + PMD.EOL + PMD.EOL + "This rule has the following properties:" + PMD.EOL + PMD.EOL + "Name | Default Value | Description" + PMD.EOL + "--- | --- | ---" + PMD.EOL + "violationSuppressRegex | null | Suppress violations with messages matching a regular expression" + PMD.EOL + "violationSuppressXPath | null | Suppress violations on nodes which match a given relative XPath expression." + PMD.EOL + "\"},\"categories\":[\"Style\"],\"location\":{\"path\":\"n/a\",\"lines\":{\"begin\":1,\"end\":1}},\"severity\":\"info\",\"remediation_points\":50000}" + "\u0000" + PMD.EOL;
    }

    @Override
    public String getExpectedEmpty() {
        return "";
    }

    @Override
    public String getExpectedMultiple() {
        return "{\"type\":\"issue\",\"check_name\":\"Foo\",\"description\":\"blah\",\"content\":{\"body\":\"## Foo" + PMD.EOL + PMD.EOL + "Since: PMD null" + PMD.EOL + PMD.EOL + "Priority: Low" + PMD.EOL + PMD.EOL + "[Categories](https://github.com/codeclimate/spec/blob/master/SPEC.md#categories): Style" + PMD.EOL + PMD.EOL + "[Remediation Points](https://github.com/codeclimate/spec/blob/master/SPEC.md#remediation-points): 50000" + PMD.EOL + PMD.EOL + "desc" + PMD.EOL + PMD.EOL + "This rule has the following properties:" + PMD.EOL + PMD.EOL + "Name | Default Value | Description" + PMD.EOL + "--- | --- | ---" + PMD.EOL + "violationSuppressRegex | null | Suppress violations with messages matching a regular expression" + PMD.EOL + "violationSuppressXPath | null | Suppress violations on nodes which match a given relative XPath expression." + PMD.EOL + "\"},\"categories\":[\"Style\"],\"location\":{\"path\":\"n/a\",\"lines\":{\"begin\":1,\"end\":1}},\"severity\":\"info\",\"remediation_points\":50000}" + "\u0000" + PMD.EOL +
        	   "{\"type\":\"issue\",\"check_name\":\"Foo\",\"description\":\"blah\",\"content\":{\"body\":\"## Foo" + PMD.EOL + PMD.EOL + "Since: PMD null" + PMD.EOL + PMD.EOL + "Priority: Low" + PMD.EOL + PMD.EOL + "[Categories](https://github.com/codeclimate/spec/blob/master/SPEC.md#categories): Style" + PMD.EOL + PMD.EOL + "[Remediation Points](https://github.com/codeclimate/spec/blob/master/SPEC.md#remediation-points): 50000" + PMD.EOL + PMD.EOL + "desc" + PMD.EOL + PMD.EOL + "This rule has the following properties:" + PMD.EOL + PMD.EOL + "Name | Default Value | Description" + PMD.EOL + "--- | --- | ---" + PMD.EOL + "violationSuppressRegex | null | Suppress violations with messages matching a regular expression" + PMD.EOL + "violationSuppressXPath | null | Suppress violations on nodes which match a given relative XPath expression." + PMD.EOL + "\"},\"categories\":[\"Style\"],\"location\":{\"path\":\"n/a\",\"lines\":{\"begin\":1,\"end\":1}},\"severity\":\"info\",\"remediation_points\":50000}" + "\u0000" + PMD.EOL;
    }

    public static junit.framework.Test suite() {
        return new junit.framework.JUnit4TestAdapter(CodeClimateRendererTest.class);
    }
}
