package net.sourceforge.pmd.swingui.event;

import net.sourceforge.pmd.Rule;
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
public class RulesTreeModelEvent extends EventObject
{

    private RulesTreeNode m_parentNode;
    private Rule m_rule;

    /**
     ******************************************************************************
     *
     * @param source
     */
    private RulesTreeModelEvent(Object source)
    {
        super(source);
    }

    /**
     ******************************************************************************
     *
     * @param source
     * @param parentNode
     */
    private RulesTreeModelEvent(Object source, RulesTreeNode parentNode)
    {
        super(source);

        m_parentNode = parentNode;
    }

    /**
     ******************************************************************************
     *
     * @param source
     * @param rule
     */
    private RulesTreeModelEvent(Object source, Rule rule)
    {
        super(source);

        m_rule = rule;
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
     * @return
     */
    public Rule getRule()
    {
        return m_rule;
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

    /**
     ******************************************************************************
     *
     */
    public static void notifyRequestSelectedRule(Object source)
    {
        if (source != null)
        {
            RulesTreeModelEvent event = new RulesTreeModelEvent(source);
            List listenerList = ListenerList.getListeners(RulesTreeModelEventListener.class);
            Iterator listeners = listenerList.iterator();

            while (listeners.hasNext())
            {
                RulesTreeModelEventListener listener;

                listener = (RulesTreeModelEventListener) listeners.next();
                listener.requestSelectedRule(event);
            }
        }
    }

    /**
     ******************************************************************************
     *
     */
    public static void notifyReturnedSelectedRule(Object source, Rule selectedRule)
    {
        if (source != null)
        {
            RulesTreeModelEvent event = new RulesTreeModelEvent(source, selectedRule);
            List listenerList = ListenerList.getListeners(RulesTreeModelEventListener.class);
            Iterator listeners = listenerList.iterator();

            while (listeners.hasNext())
            {
                RulesTreeModelEventListener listener;

                listener = (RulesTreeModelEventListener) listeners.next();
                listener.returnedSelectedRule(event);
            }
        }
    }
}