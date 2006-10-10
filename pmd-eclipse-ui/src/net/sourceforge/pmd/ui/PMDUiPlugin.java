/*
 * Created on may 2006
 *
 * Copyright (c) 2006, PMD for Eclipse Development Team
 * All rights reserved.
 *
 * Redistribution and use in source and binary forms, with or without
 * modification, are permitted provided that the following conditions are
 * met:
 * 
 *     * Redistributions of source code must retain the above copyright
 *       notice, this list of conditions and the following disclaimer.
 *     * Redistributions in binary form must reproduce the above copyright
 *       notice, this list of conditions and the following disclaimer in the
 *       documentation and/or other materials provided with the distribution.
 *     * The end-user documentation included with the redistribution, if
 *       any, must include the following acknowledgement:
 *       "This product includes software developed in part by support from
 *        the Defense Advanced Research Project Agency (DARPA)"
 *     * Neither the name of "PMD for Eclipse Development Team" nor the names of its
 *       contributors may be used to endorse or promote products derived from
 *       this software without specific prior written permission.
 * 
 * THIS SOFTWARE IS PROVIDED BY THE COPYRIGHT HOLDERS AND CONTRIBUTORS "AS
 * IS" AND ANY EXPRESS OR IMPLIED WARRANTIES, INCLUDING, BUT NOT LIMITED
 * TO, THE IMPLIED WARRANTIES OF MERCHANTABILITY AND FITNESS FOR A
 * PARTICULAR PURPOSE ARE DISCLAIMED. IN NO EVENT SHALL THE COPYRIGHT OWNER
 * OR CONTRIBUTORS BE LIABLE FOR ANY DIRECT, INDIRECT, INCIDENTAL, SPECIAL,
 * EXEMPLARY, OR CONSEQUENTIAL DAMAGES (INCLUDING, BUT NOT LIMITED TO,
 * PROCUREMENT OF SUBSTITUTE GOODS OR SERVICES; LOSS OF USE, DATA, OR
 * PROFITS; OR BUSINESS INTERRUPTION) HOWEVER CAUSED AND ON ANY THEORY OF
 * LIABILITY, WHETHER IN CONTRACT, STRICT LIABILITY, OR TORT (INCLUDING
 * NEGLIGENCE OR OTHERWISE) ARISING IN ANY WAY OUT OF THE USE OF THIS
 * SOFTWARE, EVEN IF ADVISED OF THE POSSIBILITY OF SUCH DAMAGE.
 */

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
 * 
 * @author Philippe Herlin
 * @version $Revision$
 * 
 * $Log$
 * Revision 1.3  2006/10/10 22:31:01  phherlin
 * Fix other PMD warnings
 *
 * Revision 1.2  2006/10/09 13:26:40  phherlin
 * Review Sebastian code... and fix most PMD warnings
 *
 */
public class PMDUiPlugin extends AbstractUIPlugin {
    private static final Logger log = Logger.getLogger(PMDUiPlugin.class);

	//The shared instance.
	private static PMDUiPlugin plugin; // NOPMD by Herlin on 11/10/06 00:21
    
    private StringTable stringTable; // NOPMD by Herlin on 11/10/06 00:22
    private String[] priorityLabels; // NOPMD by Herlin on 11/10/06 00:22
	
	/**
	 * The constructor.
	 */
	public PMDUiPlugin() {
        super();
		plugin = this;
	}

	/**
	 * This method is called when the plug-in is stopped
	 */
	public void stop(BundleContext context) throws Exception { // NOPMD by Herlin on 11/10/06 00:21
		super.stop(context);
		plugin = null; // NOPMD by Herlin on 11/10/06 00:21
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
        final ImageRegistry registry = getImageRegistry();
        Image image = registry.get(key);
        if (image == null) {
            final ImageDescriptor descriptor = getImageDescriptor(iconPath);
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
            final StringTable stringTable = getStringTable();
            this.priorityLabels = new String[]{
                stringTable.getString(StringKeys.MSGKEY_PRIORITY_ERROR_HIGH),
                stringTable.getString(StringKeys.MSGKEY_PRIORITY_ERROR),
                stringTable.getString(StringKeys.MSGKEY_PRIORITY_WARNING_HIGH),
                stringTable.getString(StringKeys.MSGKEY_PRIORITY_WARNING),
                stringTable.getString(StringKeys.MSGKEY_PRIORITY_INFORMATION)
            };
        }

        return this.priorityLabels; // NOPMD by Herlin on 11/10/06 00:22
    }
}
