package net.sourceforge.pmd.swingui.event;

import net.sourceforge.pmd.swingui.RulesTreeNode;

import java.util.EventObject;
import java.util.Iterator;
import java.util.List;

/**
 *
 * @author Donald A. Leckie
 * @since December 13, 2002
 * @version $Revision$, $Date$
 */
public class RulesEditingEvent extends EventObject
{

    private RulesTreeNode m_dataNode;

    /**
     *******************************************************************************
     *
     * @param source
     * @param dataNode
     */
    private RulesEditingEvent(Object source, RulesTreeNode dataNode)
    {
        super(source);

        m_dataNode = dataNode;
    }

    /**
     *******************************************************************************
     *
     * @return
     */
    public RulesTreeNode getDataNode()
    {
        return m_dataNode;
    }

    /**
     *******************************************************************************
     *
     * @param source
     */
    public static final void notifySaveData(Object source, RulesTreeNode dataNode)
    {
        if ((source != null) && (dataNode != null))
        {
            RulesEditingEvent event = new RulesEditingEvent(source, dataNode);
            List listenerList = ListenerList.getListeners(RulesEditingEventListener.class);
            Iterator listeners = listenerList.iterator();

            while (listeners.hasNext())
            {
                RulesEditingEventListener listener;

                listener = (RulesEditingEventListener) listeners.next();
                listener.saveData(event);
            }
        }
    }

    /**
     *******************************************************************************
     *
     * @param source
     * @param dataNode
     */
    public static void notifyLoadData(Object source, RulesTreeNode dataNode)
    {
        if ((source != null) && (dataNode != null))
        {
            RulesEditingEvent event = new RulesEditingEvent(source, dataNode);
            List listenerList = ListenerList.getListeners(RulesEditingEventListener.class);
            Iterator listeners = listenerList.iterator();

            while (listeners.hasNext())
            {
                RulesEditingEventListener listener;

                listener = (RulesEditingEventListener) listeners.next();
                listener.loadData(event);
            }
        }
    }
}