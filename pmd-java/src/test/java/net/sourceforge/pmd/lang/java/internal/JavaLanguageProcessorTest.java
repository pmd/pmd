/*
 * BSD-style license; for more info see http://pmd.sourceforge.net/license.html
 */

package net.sourceforge.pmd.lang.java.internal;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.containsString;
import static org.hamcrest.Matchers.emptyString;
import static org.hamcrest.Matchers.is;
import static org.junit.jupiter.api.Assertions.assertNotNull;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;

import net.sourceforge.pmd.lang.JvmLanguagePropertyBundle;
import net.sourceforge.pmd.lang.LanguageProcessorRegistry;
import net.sourceforge.pmd.lang.LanguageRegistry;
import net.sourceforge.pmd.lang.java.JavaLanguageModule;
import net.sourceforge.pmd.util.CollectionUtil;
import net.sourceforge.pmd.util.internal.AuxClasspathUtil;
import net.sourceforge.pmd.util.log.PmdReporter;

import uk.org.webcompere.systemstubs.SystemStubs;
import uk.org.webcompere.systemstubs.environment.EnvironmentVariables;
import uk.org.webcompere.systemstubs.jupiter.SystemStubsExtension;

@ExtendWith(SystemStubsExtension.class)
class JavaLanguageProcessorTest {

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
}
