package net.sourceforge.pmd.swingui.event;

import java.util.Iterator;
import java.util.List;

/**
 *
 * @author Donald A. Leckie
 * @since December 13, 2002
 * @version $Revision$, $Date$
 */
public class RuleSetEvent
{

    private List m_ruleSetList;
    private Object m_source;

    /**
     *****************************************************************************
     *
     * @param source
     */
    private RuleSetEvent(Object source)
    {
        m_source = source;
    }

    /**
     *****************************************************************************
     *
     * @param source
     * @param ruleSetList
     */
    private RuleSetEvent(Object source, List ruleSetList)
    {
        m_source = source;
        m_ruleSetList = ruleSetList;
    }

    /**
     *****************************************************************************
     *
     * @return
     */
    public List getRuleSetList()
    {
        return m_ruleSetList;
    }

    /**
     *****************************************************************************
     *
     * @return
     */
    public Object getSource()
    {
        return m_source;
    }

    /**
     *****************************************************************************
     *
     * @param registeredRuleSets
     */
    public static final void notifySaveRuleSets(Object source, List ruleSetList)
    {
        RuleSetEvent event = new RuleSetEvent(source, ruleSetList);
        List listenerList = ListenerList.getListeners(RuleSetEventListener.class);
        Iterator listeners = listenerList.iterator();

        while (listeners.hasNext())
        {
            RuleSetEventListener listener;

            listener = (RuleSetEventListener) listeners.next();
            listener.saveRuleSets(event);
        }
    }
}
