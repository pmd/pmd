/**
 * BSD-style license; for more info see http://pmd.sourceforge.net/license.html
 */

package net.sourceforge.pmd.lang.vf;

import static net.sourceforge.pmd.util.CollectionUtil.setOf;

import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Collections;

import net.sourceforge.pmd.lang.LanguageProcessorRegistry;
import net.sourceforge.pmd.lang.LanguageRegistry;
import net.sourceforge.pmd.lang.apex.ApexLanguageModule;
import net.sourceforge.pmd.util.log.internal.NoopReporter;

public final class VFTestUtils {
    private VFTestUtils() {
    }

    /**
     * Salesforce metadata is stored in two different formats, the newer sfdx form and the older mdapi format. Used to
     * locate metadata on the file system during unit tests.
     */
    public enum MetadataFormat {
        SFDX("sfdx"),
        MDAPI("mdapi");

        public final String directoryName;

        MetadataFormat(String directoryName) {
            this.directoryName = directoryName;
        }
    }

    /**
     * Represents the metadata types that are referenced from unit tests. Used to locate metadata on the file system
     * during unit tests.
     */
    public enum MetadataType {
        Apex("classes"),
        Objects("objects"),
        Vf("pages");

        public final String directoryName;

        MetadataType(String directoryName) {
            this.directoryName = directoryName;
        }
    }

    public static LanguageProcessorRegistry fakeLpRegistry() {
        LanguageRegistry registry = new LanguageRegistry(setOf(ApexLanguageModule.getInstance(), VfLanguageModule.getInstance()));
        return LanguageProcessorRegistry.create(registry, Collections.emptyMap(), new NoopReporter());
    }

    /**
     * @return the path of the directory that matches the given parameters. The directory path is constructed using the
     * following convention:
     * src/test/resources/_decomposed_test_package_name_/_test_class_name_minus_Test_/metadata/_metadata_format_/_metadata_type_
     */
    public static Path getMetadataPath(Object testClazz, MetadataFormat metadataFormat, MetadataType metadataType) {
        Path path = Paths.get("src", "test", "resources");
        // Decompose the test's package structure into directories
        for (String directory : testClazz.getClass().getPackage().getName().split("\\.")) {
            path = path.resolve(directory);
        }
        // Remove 'Test' from the class name
        path = path.resolve(testClazz.getClass().getSimpleName().replaceFirst("Test$", ""));
        // Append additional directories based on the MetadataFormat and MetadataType
        path = path.resolve("metadata").resolve(metadataFormat.directoryName);
        if (metadataType != null) {
            path = path.resolve(metadataType.directoryName);
        }

        return path.toAbsolutePath();
    }
}
