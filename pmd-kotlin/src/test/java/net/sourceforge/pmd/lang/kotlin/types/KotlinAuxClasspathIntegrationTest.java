/*
 * BSD-style license; for more info see http://pmd.sourceforge.net/license.html
 */

package net.sourceforge.pmd.lang.kotlin.types;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.io.File;
import java.util.List;

import org.junit.jupiter.api.Test;

import net.sourceforge.pmd.lang.JvmLanguagePropertyBundle;
import net.sourceforge.pmd.lang.kotlin.ast.KotlinParser.KtFunctionDeclaration;
import net.sourceforge.pmd.lang.kotlin.ast.KotlinParser.KtKotlinFile;
import net.sourceforge.pmd.lang.kotlin.ast.KotlinParsingHelper;
import net.sourceforge.pmd.lang.kotlin.ast.KotlinTerminalNode;

/**
 * Integration test: verifies that the auxClasspath language property flows
 * through KotlinAuxClasspathResolver → KotlinTypeMapper and enables type
 * resolution for third-party library classes.
 *
 * <p>JUnit's {@code @Test} annotation ({@code org.junit.jupiter.api.Test})
 * is used as the test type because it is:
 * <ul>
 *   <li>always available on the test classpath (via the junit-jupiter-api JAR), and</li>
 *   <li>not part of the JDK or Kotlin stdlib, so it requires explicit auxClasspath
 *       to be resolved by the Kotlin compiler embedded in kotlin-type-mapper.</li>
 * </ul>
 */
class KotlinAuxClasspathIntegrationTest {

    private static final String SNIPPET =
            "import org.junit.jupiter.api.Test\n"
            + "@Test fun annotated() {}\n"
            + "fun unannotated() {}\n";

    private static final KotlinParsingHelper PARSER = KotlinParsingHelper.DEFAULT;

    @Test
    void annotationFqNameUnresolvedWithoutAuxClasspath() {
        KtKotlinFile root = PARSER.parse(SNIPPET);
        assertTrue(KotlinNodeTypeData.isTypeInfoAvailable(root));
        KtFunctionDeclaration fn = firstFunctionNamed(root, "annotated");
        assertNotNull(fn);
        List<String> annotations = KotlinNodeTypeData.getAnnotationFqNames(fn);
        // Without auxClasspath the Kotlin compiler cannot resolve org.junit.jupiter.api.Test
        assertFalse(annotations.contains("org.junit.jupiter.api.Test"),
                "Expected @Test to be unresolved without auxClasspath, but got: " + annotations);
    }

    @Test
    void annotationFqNameResolvedWithAuxClasspath() throws Exception {
        File junitJar = new File(
                Test.class.getProtectionDomain().getCodeSource().getLocation().toURI());
        String jarPath = junitJar.getAbsolutePath();

        KotlinParsingHelper parser = PARSER.withLanguageProperties(bundle -> {
            bundle.setProperty(JvmLanguagePropertyBundle.AUX_CLASSPATH, jarPath);
            return kotlin.Unit.INSTANCE;
        });

        KtKotlinFile root = parser.parse(SNIPPET);
        assertTrue(KotlinNodeTypeData.isTypeInfoAvailable(root));
        KtFunctionDeclaration fn = firstFunctionNamed(root, "annotated");
        assertNotNull(fn);
        List<String> annotations = KotlinNodeTypeData.getAnnotationFqNames(fn);
        assertTrue(annotations.contains("org.junit.jupiter.api.Test"),
                "Expected @Test to resolve with auxClasspath=" + jarPath + ", but got: " + annotations);
    }

    private static KtFunctionDeclaration firstFunctionNamed(KtKotlinFile root, String name) {
        for (KtFunctionDeclaration fn : root.descendants(KtFunctionDeclaration.class)) {
            KotlinTerminalNode id = fn.simpleIdentifier().children(KotlinTerminalNode.class).first();
            if (id != null && name.equals(id.getText())) {
                return fn;
            }
        }
        return null;
    }
}
