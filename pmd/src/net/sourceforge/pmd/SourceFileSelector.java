package net.sourceforge.pmd;

import java.io.File;

import net.sourceforge.pmd.lang.Language;

/**
 * Filtering of wanted source files.
 *
 * @author Pieter_Van_Raemdonck - Application Engineers NV/SA - www.ae.be
 */
// FUTURE This needs to be worked into a LanguageFileSelector
public class SourceFileSelector {
    
    private Language language;

    public SourceFileSelector() {
	language = Language.getDefaultLanguage();
    }
    
    public SourceFileSelector (Language language) {
	this.language = language;
    }
    
    /**
     * Check if a file with given fileName should be checked by PMD.
     *
     * @param fileName String
     * @return True if the file must be checked; false otherwise
     */
    public boolean isWantedFile(String fileName) {
        // Any source file should have a '.' in its name...
	int lastDotIndex = fileName.lastIndexOf('.');
        if (lastDotIndex < 0) {
            return false;
        }
        
        return isExtensionValid( fileName.substring( 1 + lastDotIndex).toUpperCase() );
    }

    private boolean isExtensionValid(String fileExtension) {
	if ( fileExtension != null )
	    for ( String extension : language.getExtensions() )
		if ( fileExtension.equalsIgnoreCase(extension) )
		    return true;
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
}
