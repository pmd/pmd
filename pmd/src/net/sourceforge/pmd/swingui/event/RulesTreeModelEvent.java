package net.sourceforge.pmd.swingui.event;

import java.util.Iterator;
import java.util.List;

import net.sourceforge.pmd.swingui.RulesTreeNode;

/**
 *
 * @author Donald A. Leckie
 * @since December 13, 2002
 * @version $Revision$, $Date$
 */
public class RulesTreeModelEvent
{

    private Object m_source;
    private RulesTreeNode m_parentNode;

    /**
     ******************************************************************************
     *
     * @param parentNode
     */
    private RulesTreeModelEvent(Object source, RulesTreeNode parentNode)
    {
        m_source = source;
        m_parentNode = parentNode;
    }

    /**
     ******************************************************************************
     *
     * @return
     */
    public RulesTreeNode getParentNode()
    {
        return m_parentNode;
    }

    /**
     ******************************************************************************
     *
     */
    public static void notifyReload(Object source, RulesTreeNode parentNode)
    {
        if ((source != null) && (parentNode != null))
        {
            RulesTreeModelEvent event = new RulesTreeModelEvent(source, parentNode);
            List listenerList = ListenerList.getListeners(RulesTreeModelEventListener.class);
            Iterator listeners = listenerList.iterator();

            while (listeners.hasNext())
            {
                RulesTreeModelEventListener listener;

                listener = (RulesTreeModelEventListener) listeners.next();
                listener.reload(event);
            }
        }
    }
}