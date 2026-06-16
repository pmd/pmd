/*
 * BSD-style license; for more info see http://pmd.sourceforge.net/license.html
 */

package net.sourceforge.pmd.lang.kotlin.rule.xpath.internal;

import java.io.File;
import java.io.IOException;
import java.io.UncheckedIOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.util.Arrays;
import java.util.Collections;
import java.util.LinkedHashMap;
import java.util.Map;

import nl.stokpop.typemapper.analyzer.KotlinTypeMapper;
import nl.stokpop.typemapper.model.TypedAst;

/**
 * Test utility that runs kotlin-type-mapper analysis on Kotlin source files (or inline
 * code strings) and injects the resulting {@link KotlinTypeAnalysisContext} into
 * {@link KotlinTypeAnalysisContextHolder} so XPath functions have type data available.
 */
public class KotlinTypeXPathTestHelper {

    private final Map<String, String> sources;

    private KotlinTypeXPathTestHelper(Map<String, String> sources) {
        this.sources = sources;
    }

    /** Creates a helper that analyzes all .kt files in the given directory. */
    public static KotlinTypeXPathTestHelper forDirectory(File dir) {
        try {
            File[] ktFiles = dir.listFiles((d, name) -> name.endsWith(".kt"));
            Map<String, String> sources = new LinkedHashMap<>();
            if (ktFiles != null) {
                Arrays.sort(ktFiles);
                for (File f : ktFiles) {
                    String content = new String(Files.readAllBytes(f.toPath()), StandardCharsets.UTF_8);
                    sources.put(f.getName(), normalizeLf(content));
                }
            }
            return new KotlinTypeXPathTestHelper(Collections.unmodifiableMap(sources));
        } catch (IOException e) {
            throw new UncheckedIOException(e);
        }
    }

    /** Creates a helper that analyzes a single Kotlin source code string as {@code snippet.kt}. */
    public static KotlinTypeXPathTestHelper forCode(String kotlinCode) {
        return new KotlinTypeXPathTestHelper(Collections.singletonMap("snippet.kt", normalizeLf(kotlinCode)));
    }

    /**
     * Runs kotlin-type-mapper analysis in memory and injects the context into
     * the global holder. Call in {@code @BeforeEach}.
     */
    public void injectContext() {
        TypedAst ast = KotlinTypeMapper.fromSources(sources, Collections.<File>emptyList());
        KotlinTypeAnalysisContextHolder.setGlobal(KotlinTypeAnalysisContext.from(ast));
    }

    private static String normalizeLf(String text) {
        return text.replace("\r\n", "\n").replace("\r", "\n");
    }
}
