package net.sourceforge.pmd.swingui.event;

import java.util.EventListener;

/**
 *
 * @author Donald A. Leckie
 * @since December 13, 2002
 * @version $Revision$, $Date$
 */
public interface StatusBarEventListener extends EventListener
{

    /**
     **************************************************************************
     *
     * @param event
     */
    public void startAnimation(StatusBarEvent event);

    /**
     **************************************************************************
     *
     * @param event
     */
    public void showMessage(StatusBarEvent event);

    /**
     **************************************************************************
     *
     * @param event
     */
    public void stopAnimation(StatusBarEvent event);
}