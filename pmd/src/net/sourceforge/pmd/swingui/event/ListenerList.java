package net.sourceforge.pmd.swingui.event;

import java.util.ArrayList;
import java.util.EventListener;
import java.util.Iterator;
import java.util.List;

/**
 *
 * @author Donald A. Leckie
 * @since December 13, 2002
 * @version $Revision$, $Date$
 */
public class ListenerList
{

    private static List m_listeners = new ArrayList();

    /**
     *****************************************************************************
     *
     * @param listener
     */
    public static void addListener(EventListener listener)
    {
        if (listener != null)
        {
            if (m_listeners.contains(listener) == false)
            {
                m_listeners.add(listener);
            }
        }
    }

    /**
     *****************************************************************************
     *
     * @param listener
     */
    public static void removeListener(EventListener listener)
    {
        if (listener != null)
        {
            m_listeners.remove(listener);
        }
    }

    /**
     ****************************************************************************
     *
     * @param event
     */
    public static List getListeners(Class listenerType)
    {
        List list = new ArrayList();

        if (listenerType != null)
        {
            Iterator listeners = m_listeners.iterator();

            while (listeners.hasNext())
            {
                Object listener = listeners.next();

                if (listenerType.isInstance(listener))
                {
                    list.add(listener);
                }
            }
        }

        return list;
    }
}