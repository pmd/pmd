package net.sourceforge.pmd.swingui.event;

import java.util.Iterator;
import java.util.List;

import net.sourceforge.pmd.RuleSet;

/**
 *
 * @author Donald A. Leckie
 * @since December 13, 2002
 * @version $Revision$, $Date$
 */
public class PMDDirectoryRequestEvent
{

    private List m_ruleSetList;
    private Object m_source;
    private RuleSet m_ruleSet;

    /**
     *****************************************************************************
     *
     * @param source
     */
    private PMDDirectoryRequestEvent(Object source)
    {
        m_source = source;
    }

    /**
     *****************************************************************************
     *
     * @param source
     * @param ruleSetList
     */
    private PMDDirectoryRequestEvent(Object source, List ruleSetList)
    {
        m_source = source;
        m_ruleSetList = ruleSetList;
    }

    /**
     *****************************************************************************
     *
     * @param source
     * @param ruleSet
     */
    private PMDDirectoryRequestEvent(Object source, RuleSet ruleSet)
    {
        m_source = source;
        m_ruleSet = ruleSet;
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
    public RuleSet getRuleSet()
    {
        return m_ruleSet;
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
     * @param source
     */
    public static final void notifyRequestRuleSetPath(Object source)
    {
        PMDDirectoryRequestEvent event = new PMDDirectoryRequestEvent(source);
        List listenerList = ListenerList.getListeners(PMDDirectoryRequestEventListener.class);
        Iterator listeners = listenerList.iterator();

        while (listeners.hasNext())
        {
            PMDDirectoryRequestEventListener listener;

            listener = (PMDDirectoryRequestEventListener) listeners.next();
            listener.requestRuleSetPath(event);
        }
    }

    /**
     *****************************************************************************
     *
     * @param source
     */
    public static final void notifyRequestAllRuleSets(Object source)
    {
        PMDDirectoryRequestEvent event = new PMDDirectoryRequestEvent(source);
        List listenerList = ListenerList.getListeners(PMDDirectoryRequestEventListener.class);
        Iterator listeners = listenerList.iterator();

        while (listeners.hasNext())
        {
            PMDDirectoryRequestEventListener listener;

            listener = (PMDDirectoryRequestEventListener) listeners.next();
            listener.requestAllRuleSets(event);
        }
    }

    /**
     *****************************************************************************
     *
     * @param source
     */
    public static final void notifyRequestDefaultRuleSets(Object source)
    {
        PMDDirectoryRequestEvent event = new PMDDirectoryRequestEvent(source);
        List listenerList = ListenerList.getListeners(PMDDirectoryRequestEventListener.class);
        Iterator listeners = listenerList.iterator();

        while (listeners.hasNext())
        {
            PMDDirectoryRequestEventListener listener;

            listener = (PMDDirectoryRequestEventListener) listeners.next();
            listener.requestDefaultRuleSets(event);
        }
    }

    /**
     *****************************************************************************
     *
     * @param source
     */
    public static final void notifyRequestIncludedRules(Object source)
    {
        PMDDirectoryRequestEvent event = new PMDDirectoryRequestEvent(source);
        List listenerList = ListenerList.getListeners(PMDDirectoryRequestEventListener.class);
        Iterator listeners = listenerList.iterator();

        while (listeners.hasNext())
        {
            PMDDirectoryRequestEventListener listener;

            listener = (PMDDirectoryRequestEventListener) listeners.next();
            listener.requestIncludedRules(event);
        }
    }
}
