package net.sourceforge.pmd.swingui;

import java.io.File;
import java.util.ResourceBundle;
import java.util.logging.Logger;
import javax.swing.filechooser.FileFilter;

/**
 * This is a Java file filter.
 *
 * @author Brant Gurganus
 * @since 0.1
 * @version 0.1
 */
public class JavaFileFilter extends FileFilter implements java.io.FileFilter {
    /**
     * This is the resource bundle for the filter.
     */
    private static final ResourceBundle UI_STRINGS = ResourceBundle
        .getBundle("net.sourceforge.pmd.swingui.l10n.JavaFileFilter");
    
    /**
     * This is the logger for the filter.
     */
    private static final Logger LOGGER = Logger.getLogger(
        JavaFileFilter.class.getName(),
            "net.sourceforge.pmd.swingui.l10n.Logging");
    
    /**
     * Creates the filter.
     */
    public JavaFileFilter() {
        super();
    }
    
    /**
     * Indicates whether the file is accepted by the filter.
     *
     * @param f file
     * @return acceptability
     */
    public boolean accept(File f) {
        LOGGER.entering(getClass().getName(), "accept", f);
        final boolean acceptableFile =
            !f.isDirectory() && f.getName().endsWith(".java");
        System.out.println(f.isDirectory());
        System.out.println(f);
        final boolean acceptableDir =
            f.isDirectory() && f.listFiles(this).length > 0;
        final boolean acceptable = acceptableFile || acceptableDir;
        LOGGER.exiting(getClass().getName(), "accept",
            Boolean.valueOf(acceptable));
        return acceptable;
    }
    
    /**
     * Gets the description of the filter.
     *
     * @return description of filter
     */
    public String getDescription() {
        LOGGER.entering(getClass().getName(), "getDescription");
        final String description = UI_STRINGS.getString("description");
        LOGGER.exiting(getClass().getName(), "getDescription", description);
        return description;
    }
}