package net.sourceforge.pmd.jedit;

import java.io.File;
import javax.swing.filechooser.FileFilter;

/**
 * This is a file filter for CPD.  I wanted to use a FileNameExtension filter, but
 * Java 1.5 doesn't have a FileNameExtensionFilter, so this one is pretty close.
 * One difference is I've added "mode" as a parameter to the constructor to make
 * it easy to match a filter against a supported language in CPD.
 */
public class CPDFileFilter extends FileFilter {
    
    private String mode;
    private String description;
    private String[] extensions;
    
    /**
     * @param mode A language known to CPD.  So far, these line up nicely with
     * the mode names in jEdit, e.g. "java" = "java".
     * @param description Some text to show in the "Files of Type" drop down in
     * a JFileChooser.
     * @param extensions A list of file name extensions supported by this filter.
     */
    public CPDFileFilter(String mode, String description, String... extensions) {
        this.mode = mode;
        this.description = description;
        this.extensions = extensions;
    }
    
    /**
     * @param f A file to check to see if this filter will accept it.
     * @return true if the file is acceptable.
     */
    public boolean accept(File f) {
        if (f == null) {
            return false;   
        }
        
        // always accept directories so JFileChooser works correctly
        if (f.isDirectory()) {
            return true;   
        }
        
        // get the file name extension
        String name = f.getName();
        int index = name.lastIndexOf('.');
        if (index == -1) {
            return false;   
        }
        if (index + 1 >= name.length()) {
            return false;   
        }
        
        // check the extension against acceptable extensions
        String extension = name.substring(index + 1);
        for (String ext : extensions) {
            if (ext.equals(extension)) {
                return true;    
            }
        }
        
        // no match
        return false;
    }
    
    public String getDescription() {
        return description;   
    }
    
    public String getMode() {
        return mode;   
    }
    
    public String[] getExtensions() {
        return extensions;   
    }
    
    public String toString() {
        StringBuilder sb = new StringBuilder();
        sb.append(description);
        sb.append(" (");
        for (String s : extensions) {
            sb.append(s).append(',');   
        }
        sb.deleteCharAt(sb.lastIndexOf(","));
        sb.append(')');
        
        return sb.toString();   
    }
}