package net.sourceforge.pmd.swingui;

import java.text.MessageFormat;
import java.util.ResourceBundle;

import javax.swing.UIManager;

/**
 *
 * @author Donald A. Leckie
 * @since September 9, 2002
 * @version $Revision$, $Date$
 */
class Resources
{
    private static ResourceBundle RESOURCES = ResourceBundle.getBundle("net.sourceforge.pmd.swingui.pmdViewer");

    /**
     *********************************************************************************
     *
     * @param name
     *
     * @return
     */
    protected static final String getString(String name)
    {
        return RESOURCES.getString(name);
    }

    /**
     *********************************************************************************
     *
     * @param name
     *
     * @return
     */
    protected static final String getString(String name, String[] parameters)
    {
        String template = RESOURCES.getString(name);
        String message = MessageFormat.format(template, parameters);

        return message;
    }
}