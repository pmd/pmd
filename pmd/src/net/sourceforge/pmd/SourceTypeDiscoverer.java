package net.sourceforge.pmd;

import java.io.File;
import java.util.HashMap;
import java.util.Map;

/**
 * This class can give the SourceType of a source file.
 *
 * @author pieter_van_raemdonck - Application Engineers NV/SA - www.ae.be
 */
public class SourceTypeDiscoverer {

    /**
     * Map of (upper-case) file name extensions on the appropriate SourceType
     * object.
     */
    private Map mapExtensionOnSourceType = new HashMap();

    /**
     * Public constructor.
     */
    public SourceTypeDiscoverer() {
        initialize();
    }

    /**
     * Initialization of mapExtensionOnSourceType.
     */
    private void initialize() {
        mapExtensionOnSourceType.put(SourceFileConstants.JSP_EXTENSION_UPPERCASE, SourceType.JSP);
        mapExtensionOnSourceType.put(SourceFileConstants.JSPX_EXTENSION_UPPERCASE, SourceType.JSP);

        // TODO: Do we want a default ??
        mapExtensionOnSourceType.put(SourceFileConstants.JAVA_EXTENSION_UPPERCASE, SourceType.JAVA_14);
    }

    /**
     * Get the SourceType of a given source file.
     *
     * @param sourceFile The File
     * @return a SourceType
     */
    public SourceType getSourceTypeOfFile(File sourceFile) {
        String fileName = sourceFile.getName();
        return getSourceTypeOfFile(fileName);
    }

    /**
     * Get the SourceType of a source file with given name.
     *
     * @param fileName The File
     * @return a SourceType ; null if the fileName is not recognized as a supported source type.
     */
    public SourceType getSourceTypeOfFile(String fileName) {
        SourceType sourceType = null;

        int extensionIndex = 1 + fileName.lastIndexOf('.');
        if (extensionIndex > 0) {
            String extensionUppercase = fileName.substring(extensionIndex).toUpperCase();

            sourceType = (SourceType) mapExtensionOnSourceType
                    .get(extensionUppercase);
        }

        return sourceType;
    }

    /**
     * Set the SourceType of files with ".java" extension. This chooses the Java
     * version.
     *
     * @param sourceType the wanted SourceType
     */
    public void setSourceTypeOfJavaFiles(SourceType sourceType) {
        mapExtensionOnSourceType.put(SourceFileConstants.JAVA_EXTENSION_UPPERCASE, sourceType);
    }

    public SourceType getSourceTypeOfJavaFiles() {
        return (SourceType) mapExtensionOnSourceType.get(SourceFileConstants.JAVA_EXTENSION_UPPERCASE);
    }
}
