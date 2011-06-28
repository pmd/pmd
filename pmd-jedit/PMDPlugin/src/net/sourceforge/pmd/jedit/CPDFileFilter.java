package net.sourceforge.pmd.jedit;

import java.io.File;
import java.util.regex.*;
import javax.swing.filechooser.FileFilter;
import org.gjt.sp.util.Log;

/**
 * This is a file filter for CPD.  I wanted to use a FileNameExtension filter, but
 * Java 1.5 doesn't have a FileNameExtensionFilter, so this one is pretty close.
 * One difference is I've added "mode" as a parameter to the constructor to make
 * it easy to match a filter against a supported language in CPD.
 */
public class CPDFileFilter extends FileFilter implements Comparable<CPDFileFilter>, java.io.FilenameFilter {
    
    private String mode;
    private String description;
    private String[] extensions;
    private Pattern inclusionsPattern = null;
    private Pattern exclusionsPattern = null;
    
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
    
    public void setInclusions(String regex) {
        if (regex != null && regex.length() > 0) {
            inclusionsPattern = Pattern.compile(regex);
        }
    }
    
    public void setExclusions(String regex) {
        if (regex != null && regex.length() > 0) {
            exclusionsPattern = Pattern.compile(regex);
        }
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
        
        // check full path for inclusions
        if (inclusionsPattern != null) {
            Matcher m = inclusionsPattern.matcher(f.getAbsolutePath());
            if (!m.matches()) {
                return false;   
            }
        }
        
        // check full path for exclusions
        if (exclusionsPattern != null) {
            Matcher m = exclusionsPattern.matcher(f.getAbsolutePath());
            if (m.matches()) {
                return false;   
            }
        }
        
        Log.log(Log.DEBUG, this, "CPD checking: " + f.getAbsolutePath());
        
        // check the extension against acceptable extensions
        String name = f.getName();
        for (String ext : extensions) {
            // The CPD file filter converts all extensions to upper case
            if (name.toUpperCase().endsWith(ext)) {
                return true;   
            }
        }
        
        // no match
        return false;
    }
    
    public boolean accept(File dir, String name) {
        return accept(new File(dir, name));   
    }
    
    public String getDescription() {
        return description;   
    }
    
    public String getMode() {
        return mode;   
    }
    
    public String[] getExtensions() {
        return extensions;   // NOPMD
    }
    
    public int compareTo(CPDFileFilter filter) {
        if (filter == null) {
            return -1;   
        }
        return toString().compareTo(filter.toString());
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