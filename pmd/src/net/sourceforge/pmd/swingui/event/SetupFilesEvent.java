package net.sourceforge.pmd.swingui.event;

import java.util.EventObject;
import java.util.Iterator;
import java.util.List;

/**
 *
 * @author Donald A. Leckie
 * @since December 13, 2002
 * @version $Revision$, $Date$
 */
public class SetupFilesEvent extends EventObject
{

    /**
     *****************************************************************************
     *
     */
    private SetupFilesEvent(Object source)
    {
        super(source);
    }

    /**
     *****************************************************************************
     *
     * @param file
     */
    public static final void notifyStartSetup(Object source)
    {
        SetupFilesEvent event = new SetupFilesEvent(source);
        List listenerList = ListenerList.getListeners(SetupFilesEventListener.class);
        Iterator listeners = listenerList.iterator();

        while (listeners.hasNext())
        {
            SetupFilesEventListener listener;

            listener = (SetupFilesEventListener) listeners.next();
            listener.startSetup(event);
        }
    }

    /**
     *****************************************************************************
     *
     * @param file
     */
    public static final void notifyStopSetup(Object source)
    {
        SetupFilesEvent event = new SetupFilesEvent(source);
        List listenerList = ListenerList.getListeners(SetupFilesEventListener.class);
        Iterator listeners = listenerList.iterator();

        while (listeners.hasNext())
        {
            SetupFilesEventListener listener;

            listener = (SetupFilesEventListener) listeners.next();
            listener.stopSetup(event);
        }
    }
}