package net.sourceforge.pmd.swingui;

import java.io.File;
import java.util.ResourceBundle;
import java.util.logging.Logger;
import javax.swing.filechooser.FileFilter;

/**
 * This is a JAR file filter.
 *
 * @author Brant Gurganus
 * @since 0.1
 * @version 0.1
 */
public class JARFileFilter extends FileFilter {
    /**
     * This is the resource bundle for the filter.
     */
    private static final ResourceBundle UI_STRINGS = ResourceBundle
        .getBundle("net.sourceforge.pmd.swingui.l10n.JARFileFilter");
    
    /**
     * This is the logger for the filter.
     */
    private static final Logger LOGGER = Logger.getLogger(
        "net.sourceforge.pmd.swingui.JARFileFilter",
            "net.sourceforge.pmd.swingui.l10n.Logging");
    
    /**
     * Creates the filter.
     */
    public JARFileFilter() {
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
        final boolean acceptable =
            f.getName().endsWith(".jar") || f.isDirectory();
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