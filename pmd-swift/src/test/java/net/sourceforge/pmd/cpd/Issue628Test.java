/*
 * BSD-style license; for more info see http://pmd.sourceforge.net/license.html
 */


package net.sourceforge.pmd.cpd;

import java.io.IOException;
import java.nio.charset.StandardCharsets;

import org.apache.commons.io.IOUtils;
import org.junit.Assert;
import org.junit.Test;

public class Issue628Test {

    @Test
    public void tokenize() throws IOException {
        SwiftTokenizer tokenizer = new SwiftTokenizer();
        String code = IOUtils.toString(Issue628Test.class.getResourceAsStream("Issue628.swift"), StandardCharsets.UTF_8);
        SourceCode sourceCode = new SourceCode(new SourceCode.StringCodeLoader(code));
        final Tokens tokenEntries = new Tokens();
        // must not throw an exception
        tokenizer.tokenize(sourceCode, tokenEntries);
        Assert.assertEquals(6, tokenEntries.size());
        Assert.assertEquals(4394, tokenEntries.getTokens().get(3).toString().length());
    }
}
