/*
 * BSD-style license; for more info see http://pmd.sourceforge.net/license.html
 */

package net.sourceforge.pmd.internal.util;

import static org.junit.jupiter.api.Assertions.assertEquals;

import java.net.MalformedURLException;
import java.net.URI;
import java.net.URISyntaxException;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;

public final class ClasspathClassLoaderTestUtil {

    private ClasspathClassLoaderTestUtil() {

    }

    public static void assertClasspathContainsExactly(ClasspathClassLoader.ParsedClassPath cp, String... expectedUrlStrings) throws MalformedURLException, URISyntaxException {
        List<URL> expectedUrls = new ArrayList<>();
        for (String urlString : expectedUrlStrings) {
            expectedUrls.add(new URI("file", null, urlString, null).toURL());
        }
        assertEquals(expectedUrls, cp.getUrls());
    }
}
