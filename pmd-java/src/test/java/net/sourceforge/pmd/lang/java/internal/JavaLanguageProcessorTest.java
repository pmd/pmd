/*
 * BSD-style license; for more info see http://pmd.sourceforge.net/license.html
 */

package net.sourceforge.pmd.lang.java.internal;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.containsString;
import static org.hamcrest.Matchers.emptyString;
import static org.hamcrest.Matchers.is;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.io.File;
import java.io.IOException;
import java.nio.charset.Charset;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.junit.jupiter.api.io.TempDir;

import net.sourceforge.pmd.lang.JvmLanguagePropertyBundle;
import net.sourceforge.pmd.lang.LanguageProcessorRegistry;
import net.sourceforge.pmd.lang.LanguagePropertyBundle;
import net.sourceforge.pmd.lang.LanguageRegistry;
import net.sourceforge.pmd.lang.java.JavaLanguageModule;
import net.sourceforge.pmd.lang.java.symbols.JClassSymbol;
import net.sourceforge.pmd.util.CollectionUtil;
import net.sourceforge.pmd.util.internal.AuxClasspathUtil;
import net.sourceforge.pmd.util.log.PmdReporter;

import com.github.stefanbirkner.systemlambda.SystemLambda;
import uk.org.webcompere.systemstubs.SystemStubs;
import uk.org.webcompere.systemstubs.environment.EnvironmentVariables;
import uk.org.webcompere.systemstubs.jupiter.SystemStubsExtension;

@ExtendWith(SystemStubsExtension.class)
class JavaLanguageProcessorTest {

    @TempDir
    private Path tempDir;

    @Test
    void expectAuxClasspathWarning() throws Exception {
        String classpath = AuxClasspathUtil.toRawClasspath(AuxClasspathUtil.getRuntimeClasspath());
        JavaLanguageProperties properties =
                (JavaLanguageProperties) JavaLanguageModule.getInstance().newPropertyBundle();
        properties.setProperty(JvmLanguagePropertyBundle.AUX_CLASSPATH, classpath);
        String log = SystemStubs.tapSystemErr(() -> {
            try (JavaLanguageProcessor processor = new JavaLanguageProcessor(properties)) {
                assertNotNull(processor.getTypeSystem());
            }
        });
        assertThat(log, containsString("Adding current platform"));
    }

    @Test
    void classpathListWithJrtFs() throws Exception {
        String auxClasspath = writeClasspathFile("classpath-with-jrtfs.txt", true);
        String log = SystemLambda.tapSystemErr(() -> assertPlatformClassesFound(auxClasspath));
        assertTrue(log.isEmpty(), "unexpected output: " + log);
    }

    @Test
    void classpathListWithoutJrtFs() throws Exception {
        String auxClasspath = writeClasspathFile("classpath-without-jrtfs.txt", false);
        String log = SystemLambda.tapSystemErr(() -> assertPlatformClassesFound(auxClasspath));
        assertThat(log, containsString("Adding current platform"));
    }

    @Test
    void expectNoAuxClasspathWarningViaLanguageProperty() throws Exception {
        String classpath = AuxClasspathUtil.toRawClasspath(AuxClasspathUtil.getRuntimeClasspath());
        JavaLanguageProperties properties =
                (JavaLanguageProperties) JavaLanguageModule.getInstance().newPropertyBundle();
        properties.setProperty(JvmLanguagePropertyBundle.AUX_CLASSPATH, classpath);
        properties.setProperty(JavaLanguageProperties.DISABLE_AUX_CLASSPATH_WARNINGS, true);
        String log = SystemStubs.tapSystemErr(() -> {
            try (JavaLanguageProcessor processor = new JavaLanguageProcessor(properties)) {
                assertNotNull(processor.getTypeSystem());
            }
        });
        assertThat(log, is(emptyString()));
    }

    @Test
    void expectNoAuxClasspathWarningViaEnvironmentVariable(EnvironmentVariables environment) throws Exception {
        environment.set("PMD_JAVA_DISABLE_AUX_CLASSPATH_WARNINGS", "true");

        String classpath = AuxClasspathUtil.toRawClasspath(AuxClasspathUtil.getRuntimeClasspath());
        JavaLanguageModule javaLanguageModule = JavaLanguageModule.getInstance();
        JavaLanguageProperties properties = (JavaLanguageProperties) javaLanguageModule.newPropertyBundle();
        properties.setProperty(JvmLanguagePropertyBundle.AUX_CLASSPATH, classpath);

        try (LanguageProcessorRegistry registry =
                     LanguageProcessorRegistry.create(LanguageRegistry.singleton(javaLanguageModule),
                CollectionUtil.mapOf(javaLanguageModule, properties),
                PmdReporter.quiet())) {
            String log = SystemStubs.tapSystemErr(() -> {
                try (JavaLanguageProcessor processor =
                             (JavaLanguageProcessor) registry.getProcessor(javaLanguageModule)) {
                    assertNotNull(processor.getTypeSystem());
                }
            });
            assertThat(log, is(emptyString()));
        }
    }

    @Test
    void auxClasspathWithJrtFs() throws Exception {
        String auxClasspath = createRawClasspath(true);
        String log = SystemLambda.tapSystemErr(() -> assertPlatformClassesFound(auxClasspath));
        assertTrue(log.isEmpty(), "unexpected output: " + log);
    }

    @Test
    void auxClasspathWithoutJrtFs() throws Exception {
        String auxClasspath = createRawClasspath(false);
        String log = SystemLambda.tapSystemErr(() -> assertPlatformClassesFound(auxClasspath));
        assertThat(log, containsString("Adding current platform"));
    }

    /**
     * This test creates a new AuxClasspathLoader instances for the cache.
     * Need to increase the cache reuse.
     * @throws Exception
     */
    @Test
    void emptyClasspathWithoutJrtFs() throws Exception {
        String log = SystemLambda.tapSystemErr(() -> assertPlatformClassesFound(""));
        assertThat(log, containsString("Adding current platform"));
    }

    /**
     * AuxClasspathLoader uses a static cache. Verify that we use the same classpath
     * as JavaParsingHelper so that we don't create additional new
     * instances the lead to cache eviction and other failing tests.
     *
     * @see net.sourceforge.pmd.lang.java.JavaParsingHelper#TEST_AUX_CLASSPATH_LOADER
     */
    @Test
    void ensureSameClasspath() throws IOException {
        final String javaParsingHelperClasspath = AuxClasspathUtil.toRawClasspath(
                AuxClasspathUtil.getRuntimeClasspath(),
                AuxClasspathUtil.getPlatformClasspath());

        // platform classpath already provided
        String auxClasspath = writeClasspathFile("same-classpath-with-jrtfs.txt", true);
        assertEquals(javaParsingHelperClasspath,
                AuxClasspathUtil.toRawClasspath(AuxClasspathUtil.expandClasspath(auxClasspath)));

        // platform classpath added implicitly
        auxClasspath = writeClasspathFile("same-classpath-test-without-jrtfs.txt", false);
        assertEquals(javaParsingHelperClasspath,
                AuxClasspathUtil.toRawClasspath(AuxClasspathUtil.expandClasspath(auxClasspath), AuxClasspathUtil.getPlatformClasspath()));

        // without a classpath file - platform classes already provided
        auxClasspath = createRawClasspath(true);
        assertEquals(javaParsingHelperClasspath,
                AuxClasspathUtil.toRawClasspath(AuxClasspathUtil.expandClasspath(auxClasspath)));
        // without a classpath file - platform classpath added implicitly
        auxClasspath = createRawClasspath(false);
        assertEquals(javaParsingHelperClasspath,
                AuxClasspathUtil.toRawClasspath(AuxClasspathUtil.expandClasspath(auxClasspath), AuxClasspathUtil.getPlatformClasspath()));
    }

    private void assertPlatformClassesFound(String auxClasspath) throws Exception {
        LanguagePropertyBundle properties = JavaLanguageModule.getInstance().newPropertyBundle();
        properties.setProperty(JvmLanguagePropertyBundle.AUX_CLASSPATH, auxClasspath);
        try (JavaLanguageProcessor processor = new JavaLanguageProcessor((JavaLanguageProperties) properties)) {
            JClassSymbol classSymbol = processor.getTypeSystem().getClassSymbol("java.lang.Object");
            assertNotNull(classSymbol);
        }
    }

    private String writeClasspathFile(String name, boolean addPlatformClasspath) throws IOException {
        List<String> lines = new ArrayList<>();
        lines.add("# classpath file for " + name);
        AuxClasspathUtil.getRuntimeClasspath().forEach(p -> lines.add(p.toString()));
        if (addPlatformClasspath) {
            lines.add(AuxClasspathUtil.getPlatformClasspath().toString());
        }
        Path classpathFile = tempDir.resolve(name);
        Files.write(classpathFile, lines, Charset.defaultCharset());
        return classpathFile.toUri().toString(); // -> file:///.../classpath-with-jrtfs.txt
    }

    private String createRawClasspath(boolean addPlatformClasspath) {
        String rawClasspath = AuxClasspathUtil.getRuntimeClasspath()
                .stream().map(Path::toString).collect(Collectors.joining(File.pathSeparator));
        if (addPlatformClasspath) {
            rawClasspath += File.pathSeparator + AuxClasspathUtil.getPlatformClasspath().toString();
        }
        return rawClasspath;
    }
}
