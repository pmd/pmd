package net.sourceforge.pmd.swingui.event;

import net.sourceforge.pmd.Rule;

import java.util.EventObject;
import java.util.Iterator;
import java.util.List;

public class SearchRuleEvent extends EventObject
{
    private Rule m_searchRule;

    /**
     *****************************************************************************
     *
     * @param source
     * @param searchRule
     */
    private SearchRuleEvent(Object source, Rule searchRule)
    {
        super(source);

        m_searchRule = searchRule;
    }

    /**
     *****************************************************************************
     *
     * @return
     */
    public Rule getSearchRule()
    {
        return m_searchRule;
    }

    /**
     *****************************************************************************
     *
     * @param source
     */
    public static final void notifySetSearchRule(Object source, Rule searchRule)
    {
        SearchRuleEvent event = new SearchRuleEvent(source, searchRule);
        List listenerList = ListenerList.getListeners(SearchRuleEventListener.class);
        Iterator listeners = listenerList.iterator();

        while (listeners.hasNext())
        {
            SearchRuleEventListener listener;

            listener = (SearchRuleEventListener) listeners.next();
            listener.setSearchRule(event);
        }
    }
}