/**
 * BSD-style license; for more info see http://pmd.sourceforge.net/license.html
 */

package net.sourceforge.pmd.scm;

import java.io.IOException;
import java.net.URL;
import java.nio.charset.Charset;
import java.nio.file.Files;
import java.nio.file.Path;

import org.junit.Assert;

public final class Helper {
    private Helper() { }

    public static void assertResultedSourceEquals(Charset charset, URL expected, Path actual) throws IOException {
        byte[] data = new byte[65536];
        int length = expected.openStream().read(data);
        String expectedContents = new String(data, 0, length, charset);
        String actualContents = new String(Files.readAllBytes(actual), charset);
        Assert.assertEquals(expectedContents, actualContents);
    }
}
