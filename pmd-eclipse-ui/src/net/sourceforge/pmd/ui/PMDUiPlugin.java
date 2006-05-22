package net.sourceforge.pmd.ui;

import net.sourceforge.pmd.ui.nls.StringKeys;
import net.sourceforge.pmd.ui.nls.StringTable;

import org.apache.log4j.Logger;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.Status;
import org.eclipse.jface.dialogs.MessageDialog;
import org.eclipse.jface.resource.ImageDescriptor;
import org.eclipse.jface.resource.ImageRegistry;
import org.eclipse.swt.graphics.Image;
import org.eclipse.swt.widgets.Display;
import org.eclipse.ui.plugin.AbstractUIPlugin;
import org.osgi.framework.BundleContext;

/**
 * The main plugin class to be used in the desktop.
 */
public class PMDUiPlugin extends AbstractUIPlugin {
    private static final Logger log = Logger.getLogger(PMDUiPlugin.class);

	//The shared instance.
	private static PMDUiPlugin plugin;
    
    private StringTable stringTable;
    private String[] priorityLabels;
	
	/**
	 * The constructor.
	 */
	public PMDUiPlugin() {
		plugin = this;
	}

	/**
	 * This method is called upon plug-in activation
	 */
	public void start(BundleContext context) throws Exception {
		super.start(context);
	}

	/**
	 * This method is called when the plug-in is stopped
	 */
	public void stop(BundleContext context) throws Exception {
		super.stop(context);
		plugin = null;
	}

	/**
	 * Returns the shared instance.
	 */
	public static PMDUiPlugin getDefault() {
		return plugin;
	}

	/**
	 * Returns an image descriptor for the image file at the given
	 * plug-in relative path.
	 *
	 * @param path the path
	 * @return the image descriptor
	 */
	public static ImageDescriptor getImageDescriptor(String path) {
		return AbstractUIPlugin.imageDescriptorFromPlugin("net.sourceforge.pmd.ui", path);
	}
    
    /**
     * Get an image corresponding to the severity
     */
    public Image getImage(String key, String iconPath) {
        ImageRegistry registry = getImageRegistry();
        Image image = registry.get(key);
        if (image == null) {
            ImageDescriptor descriptor = getImageDescriptor(iconPath);
            if (descriptor != null) {
                registry.put(key, descriptor);
                image = registry.get(key);
            }
        }

        return image;
    }

    /**
     * Helper method to log error
     * 
     * @see IStatus
     */
    public void logError(String message, Throwable t) {
        getLog().log(new Status(IStatus.ERROR, getBundle().getSymbolicName(), 0, message + t.getMessage(), t));
        if (log != null) {
            log.error(message, t);
        }
    }

    /**
     * Helper method to log error
     * 
     * @see IStatus
     */
    public void logError(IStatus status) {
        getLog().log(status);
        if (log != null) {
            log.error(status.getMessage(), status.getException());
        }
    }

    /**
     * Helper method to display error
     */
    public void showError(final String message, final Throwable t) {
        logError(message, t);
        Display.getDefault().syncExec(new Runnable() {

            public void run() {
                
                MessageDialog.openError(Display.getCurrent().getActiveShell(), getStringTable().getString(StringKeys.MSGKEY_ERROR_TITLE), message
                        + String.valueOf(t));
            }
        });
    }
    
    /**
     * @return an instance of the string table
     */
    public StringTable getStringTable() {
        if (this.stringTable == null) {
            this.stringTable = new StringTable();
        }
        
        return this.stringTable;
    }

    /**
     * @return the priority values
     */
    public Integer[] getPriorityValues() {
        return new Integer[] {
                new Integer(1),
                new Integer(2),
                new Integer(3),
                new Integer(4),
                new Integer(5)
        };
    }

    /**
     * Return the priority labels
     */
    public String[] getPriorityLabels() {
        if (this.priorityLabels == null) {
            StringTable stringTable = getStringTable();
            this.priorityLabels = new String[]{
                stringTable.getString(StringKeys.MSGKEY_PRIORITY_ERROR_HIGH),
                stringTable.getString(StringKeys.MSGKEY_PRIORITY_ERROR),
                stringTable.getString(StringKeys.MSGKEY_PRIORITY_WARNING_HIGH),
                stringTable.getString(StringKeys.MSGKEY_PRIORITY_WARNING),
                stringTable.getString(StringKeys.MSGKEY_PRIORITY_INFORMATION)
            };
        }

        return this.priorityLabels;
    }
}
