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
        return "{\"type\":\"issue\",\"check_name\":\"Foo\",\"description\":\"blah\",\"content\":{\"body\":\"## Foo\\n\\nSince: PMD null\\n\\nPriority: Low\\n\\n[Categories](https://github.com/codeclimate/spec/blob/master/SPEC.md#categories): Style\\n\\n[Remediation Points](https://github.com/codeclimate/spec/blob/master/SPEC.md#remediation-points): 50000\\n\\ndesc\\n\\n### [PMD properties](http://pmd.github.io/pmd-5.1.3/pmd-developer.html)\\n\\nName | Value | Description\\n--- | --- | ---\\nviolationSuppressRegex | null | Suppress violations with messages matching a regular expression\\nviolationSuppressXPath | null | Suppress violations on nodes which match a given relative XPath expression.\\n\"},\"categories\":[\"Style\"],\"location\":{\"path\":\"n/a\",\"lines\":{\"begin\":1,\"end\":1}},\"severity\":\"info\",\"remediation_points\":50000}" + "\u0000" + PMD.EOL;
    }

    @Override
    public String getExpectedEmpty() {
        return "";
    }

    @Override
    public String getExpectedMultiple() {
        return "{\"type\":\"issue\",\"check_name\":\"Foo\",\"description\":\"blah\",\"content\":{\"body\":\"## Foo\\n\\nSince: PMD null\\n\\nPriority: Low\\n\\n[Categories](https://github.com/codeclimate/spec/blob/master/SPEC.md#categories): Style\\n\\n[Remediation Points](https://github.com/codeclimate/spec/blob/master/SPEC.md#remediation-points): 50000\\n\\ndesc\\n\\n### [PMD properties](http://pmd.github.io/pmd-5.1.3/pmd-developer.html)\\n\\nName | Value | Description\\n--- | --- | ---\\nviolationSuppressRegex | null | Suppress violations with messages matching a regular expression\\nviolationSuppressXPath | null | Suppress violations on nodes which match a given relative XPath expression.\\n\"},\"categories\":[\"Style\"],\"location\":{\"path\":\"n/a\",\"lines\":{\"begin\":1,\"end\":1}},\"severity\":\"info\",\"remediation_points\":50000}" + "\u0000" + PMD.EOL +
        	   "{\"type\":\"issue\",\"check_name\":\"Foo\",\"description\":\"blah\",\"content\":{\"body\":\"## Foo\\n\\nSince: PMD null\\n\\nPriority: Low\\n\\n[Categories](https://github.com/codeclimate/spec/blob/master/SPEC.md#categories): Style\\n\\n[Remediation Points](https://github.com/codeclimate/spec/blob/master/SPEC.md#remediation-points): 50000\\n\\ndesc\\n\\n### [PMD properties](http://pmd.github.io/pmd-5.1.3/pmd-developer.html)\\n\\nName | Value | Description\\n--- | --- | ---\\nviolationSuppressRegex | null | Suppress violations with messages matching a regular expression\\nviolationSuppressXPath | null | Suppress violations on nodes which match a given relative XPath expression.\\n\"},\"categories\":[\"Style\"],\"location\":{\"path\":\"n/a\",\"lines\":{\"begin\":1,\"end\":1}},\"severity\":\"info\",\"remediation_points\":50000}" + "\u0000" + PMD.EOL;
    }

    public static junit.framework.Test suite() {
        return new junit.framework.JUnit4TestAdapter(CodeClimateRendererTest.class);
    }
}
