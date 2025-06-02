/*
 * BSD-style license; for more info see http://pmd.sourceforge.net/license.html
 */

package net.sourceforge.pmd.lang.java.symbols;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.equalTo;
import static org.hamcrest.Matchers.hasProperty;
import static org.hamcrest.Matchers.hasSize;

import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.List;

import org.junit.jupiter.api.Test;

import net.sourceforge.pmd.PMDConfiguration;
import net.sourceforge.pmd.internal.util.ClasspathClassLoader;
import net.sourceforge.pmd.lang.java.types.JClassType;
import net.sourceforge.pmd.lang.java.types.TypeSystem;

/**
 * @author Clément Fournier
 */
class ClassLoadingChildFirstTest {


    /**
     * In this test I packed a jar with a custom version of {@link Void}.
     * The test asserts that when putting that jar on the auxclasspath,
     * a {@link TypeSystem} prefers that custom class to the one on the
     * bootstrap classpath. The functionality under test is actually in
     * {@link ClasspathClassLoader}.
     *
     * <p>The jar file "custom_java_lang.jar" also contains the sources
     * for the custom class {@code java.lang.Void}.
     * </p>
     */
    @Test
    void testClassLoading() {
        Path file = Paths.get("src/test/resources",
                getClass().getPackage().getName().replace('.', '/'),
                "custom_java_lang.jar");

        PMDConfiguration config = new PMDConfiguration();
        config.prependAuxClasspath(file.toAbsolutePath().toString());

        TypeSystem typeSystem = TypeSystem.usingClassLoaderClasspath(config.getClassLoader());

        JClassType voidClass = typeSystem.BOXED_VOID;
        List<JMethodSymbol> declaredMethods = voidClass.getSymbol().getDeclaredMethods();
        assertThat(declaredMethods, hasSize(1));
        assertThat(declaredMethods.get(0), hasProperty("simpleName", equalTo("customMethodOnJavaLangVoid")));
    }

}
