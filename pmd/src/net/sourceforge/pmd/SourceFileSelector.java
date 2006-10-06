package net.sourceforge.pmd;

import java.io.File;

/**
 * Filtering of wanted source files.
 *
 * @author Pieter_Van_Raemdonck - Application Engineers NV/SA - www.ae.be
 */
public class SourceFileSelector {

    private boolean selectJavaFiles = true;

    // TODO: is false the wanted default option?
    private boolean selectJspFiles = false;

    /**
     * Check if a file with given fileName should be checked by PMD.
     *
     * @param fileName String
     * @return True if the file must be checked; false otherwise
     */
    public boolean isWantedFile(String fileName) {
        int lastDotIndex = fileName.lastIndexOf('.');
        if (lastDotIndex < 0) {
            return false;
        }

        String extensionUppercase = fileName.substring(1 + lastDotIndex)
                .toUpperCase();

        if (selectJavaFiles
                && extensionUppercase
                .equals(SourceFileConstants.JAVA_EXTENSION_UPPERCASE)) {
            return true;
        }

        if (selectJspFiles
                && (extensionUppercase
                .equals(SourceFileConstants.JSP_EXTENSION_UPPERCASE) || extensionUppercase
                .equals(SourceFileConstants.JSPX_EXTENSION_UPPERCASE))) {
            return true;
        }

        return false;
    }

    /**
     * Check if a given file should be checked by PMD.
     *
     * @param file The File
     * @return True if the file must be checked; false otherwise
     */
    public boolean isWantedFile(File file) {
        return isWantedFile(file.getAbsolutePath());
    }

    /**
     * @return Returns the selectJavaFiles.
     */
    public boolean isSelectJavaFiles() {
        return selectJavaFiles;
    }

    /**
     * @param selectJavaFiles The selectJavaFiles to set.
     */
    public void setSelectJavaFiles(boolean selectJavaFiles) {
        this.selectJavaFiles = selectJavaFiles;
    }

    /**
     * @return Returns the selectJspFiles.
     */
    public boolean isSelectJspFiles() {
        return selectJspFiles;
    }

    /**
     * @param selectJspFiles The selectJspFiles to set.
     */
    public void setSelectJspFiles(boolean selectJspFiles) {
        this.selectJspFiles = selectJspFiles;
    }
}
