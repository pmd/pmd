package net.sourceforge.pmd.swingui;

import java.util.EventListener;

/**
 *
 * @author Donald A. Leckie
 * @since September 27, 2002
 * @version $Revision$, $Date$
 */
public interface NameChangeListener extends EventListener
{

    /**
     ******************************************************************************
     *
     * @param event
     */
    public void nameChanged(NameChangeEvent event);
}