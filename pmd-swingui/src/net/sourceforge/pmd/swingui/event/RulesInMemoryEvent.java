package net.sourceforge.pmd.swingui.event;

import net.sourceforge.pmd.RuleSet;

import java.util.EventObject;
import java.util.Iterator;
import java.util.List;

public class RulesInMemoryEvent extends EventObject {

    private int m_lowestPriorityForAnalysis;
    private RuleSet m_rules;

    /**
     *****************************************************************************
     *
     */
    private RulesInMemoryEvent(Object source) {
        super(source);
    }

    /**
     *****************************************************************************
     *
     */
    private RulesInMemoryEvent(Object source, int lowestPriorityForAnalysis) {
        super(source);

        m_lowestPriorityForAnalysis = lowestPriorityForAnalysis;
    }

    /**
     *****************************************************************************
     *
     */
    private RulesInMemoryEvent(Object source, RuleSet rules) {
        super(source);

        m_rules = rules;
    }

    /**
     *****************************************************************************
     *
     */
    public RuleSet getRules() {
        return m_rules;
    }

    /**
     *****************************************************************************
     *
     * @param source
     */
    public static final void notifyRequestAllRules(Object source) {
        RulesInMemoryEvent event = new RulesInMemoryEvent(source);
        List listenerList = ListenerList.getListeners(RulesInMemoryEventListener.class);
        Iterator listeners = listenerList.iterator();

        while (listeners.hasNext()) {
            RulesInMemoryEventListener listener;

            listener = (RulesInMemoryEventListener) listeners.next();
            listener.requestAllRules(event);
        }
    }

    /**
     *****************************************************************************
     *
     * @param source
     */
    public static final void notifyRequestIncludedRules(Object source, int lowestPriorityForAnalysis) {
        RulesInMemoryEvent event = new RulesInMemoryEvent(source, lowestPriorityForAnalysis);
        List listenerList = ListenerList.getListeners(RulesInMemoryEventListener.class);
        Iterator listeners = listenerList.iterator();

        while (listeners.hasNext()) {
            RulesInMemoryEventListener listener;

            listener = (RulesInMemoryEventListener) listeners.next();
            listener.requestIncludedRules(event);
        }
    }

    /**
     *****************************************************************************
     *
     * @param source
     */
    public static final void notifyReturnedRules(Object source, RuleSet rules) {
        RulesInMemoryEvent event = new RulesInMemoryEvent(source, rules);
        List listenerList = ListenerList.getListeners(RulesInMemoryEventListener.class);
        Iterator listeners = listenerList.iterator();

        while (listeners.hasNext()) {
            RulesInMemoryEventListener listener;

            listener = (RulesInMemoryEventListener) listeners.next();
            listener.returnedRules(event);
        }
    }
}