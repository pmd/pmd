/**
 * 
 */
package net.sourceforge.pmd.cpd;

import java.io.File;
import java.io.FilenameFilter;

import net.sourceforge.pmd.SourceFileSelector;

/**
 * Filtering of directories en wanted source files.
 */
public class SourceFileOrDirectoryFilter implements FilenameFilter {
    private SourceFileSelector fileSelector;

    /**
     * Public constructor
     * @param fileSelector the FileSelector that knows what source files to accept
     */
    public SourceFileOrDirectoryFilter(SourceFileSelector fileSelector) {
    	this.fileSelector = fileSelector;
    }
    
	public boolean accept(File dir, String filename) {
        return (fileSelector.isWantedFile(filename) 
    		|| (new File(dir.getAbsolutePath() + System.getProperty("file.separator") + filename).isDirectory())) && !filename.equals("SCCS");
        // Remark: Why not use "new File(dir, filename).isDirectory()" ?
    }
}
