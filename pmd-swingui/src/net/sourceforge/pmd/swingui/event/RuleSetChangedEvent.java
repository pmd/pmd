package net.sourceforge.pmd.swingui.event;

import net.sourceforge.pmd.RuleSet;

import java.util.EventObject;
import java.util.Iterator;
import java.util.List;

/**
 *
 * @author Donald A. Leckie
 * @since December 13, 2002
 * @version $Revision$, $Date$
 */
public class RuleSetChangedEvent extends EventObject {

    private RuleSet m_ruleSet;

    /**
     *******************************************************************************
     *
     * @param ruleSet
     */
    private RuleSetChangedEvent(Object source, RuleSet ruleSet) {
        super(source);

        m_ruleSet = ruleSet;
    }

    /**
     *******************************************************************************
     *
     * @return
     */
    public boolean allRuleSetsChanged() {
        return m_ruleSet == null;
    }

    /**
     *******************************************************************************
     *
     * @return
     */
    public RuleSet getRuleSet() {
        return m_ruleSet;
    }

    /**
     *******************************************************************************
     *
     * @param ruleSet
     */
    public static void notifyRuleSetChanged(Object source, RuleSet ruleSet) {
        if ((source != null) && (ruleSet != null)) {
            RuleSetChangedEvent event = new RuleSetChangedEvent(source, ruleSet);
            List listenerList = ListenerList.getListeners(RuleSetChangedEventListener.class);
            Iterator listeners = listenerList.iterator();

            while (listeners.hasNext()) {
                RuleSetChangedEventListener listener;

                listener = (RuleSetChangedEventListener) listeners.next();
                listener.ruleSetChanged(event);
            }
        }
    }

    /**
     *******************************************************************************
     *
     */
    public static void notifyRuleSetsChanged(Object source) {
        if (source != null) {
            RuleSetChangedEvent event = new RuleSetChangedEvent(source, null);
            List listenerList = ListenerList.getListeners(RuleSetChangedEventListener.class);
            Iterator listeners = listenerList.iterator();

            while (listeners.hasNext()) {
                RuleSetChangedEventListener listener;

                listener = (RuleSetChangedEventListener) listeners.next();
                listener.ruleSetsChanged(event);
            }
        }
    }
}