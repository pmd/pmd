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
public class PMDDirectoryReturnedEvent extends EventObject {

    private List m_ruleSetList;
    private RuleSet m_ruleSet;
    private String m_ruleSetPath;

    /**
     *****************************************************************************
     *
     * @param source
     */
    private PMDDirectoryReturnedEvent(Object source) {
        super(source);
    }

    /**
     *****************************************************************************
     *
     * @param source
     * @param ruleSetList
     */
    private PMDDirectoryReturnedEvent(Object source, String ruleSetPath) {
        super(source);

        m_ruleSetPath = ruleSetPath;
    }

    /**
     *****************************************************************************
     *
     * @param source
     * @param ruleSetList
     */
    private PMDDirectoryReturnedEvent(Object source, List ruleSetList) {
        super(source);

        m_ruleSetList = ruleSetList;
    }

    /**
     *****************************************************************************
     *
     * @param source
     * @param ruleSet
     */
    private PMDDirectoryReturnedEvent(Object source, RuleSet ruleSet) {
        super(source);

        m_ruleSet = ruleSet;
    }

    /**
     *****************************************************************************
     *
     * @return
     */
    public String getRuleSetPath() {
        return m_ruleSetPath;
    }

    /**
     *****************************************************************************
     *
     * @return
     */
    public List getRuleSetList() {
        return m_ruleSetList;
    }

    /**
     *****************************************************************************
     *
     * @return
     */
    public RuleSet getRuleSet() {
        return m_ruleSet;
    }

    /**
     *****************************************************************************
     *
     * @param source
     * @param ruleSetPath
     */
    public static final void notifyReturnedRuleSetPath(Object source, String ruleSetPath) {
        PMDDirectoryReturnedEvent event = new PMDDirectoryReturnedEvent(source, ruleSetPath);
        List listenerList = ListenerList.getListeners(PMDDirectoryReturnedEventListener.class);
        Iterator listeners = listenerList.iterator();

        while (listeners.hasNext()) {
            PMDDirectoryReturnedEventListener listener;

            listener = (PMDDirectoryReturnedEventListener) listeners.next();
            listener.returnedRuleSetPath(event);
        }
    }

    /**
     *****************************************************************************
     *
     * @param source
     * @param ruleSets
     */
    public static final void notifyReturnedAllRuleSets(Object source, List ruleSets) {
        PMDDirectoryReturnedEvent event = new PMDDirectoryReturnedEvent(source, ruleSets);
        List listenerList = ListenerList.getListeners(PMDDirectoryReturnedEventListener.class);
        Iterator listeners = listenerList.iterator();

        while (listeners.hasNext()) {
            PMDDirectoryReturnedEventListener listener;

            listener = (PMDDirectoryReturnedEventListener) listeners.next();
            listener.returnedAllRuleSets(event);
        }
    }

    /**
     *****************************************************************************
     *
     * @param source
     * @param ruleSets
     */
    public static final void notifyReturnedDefaultRuleSets(Object source, List ruleSets) {
        PMDDirectoryReturnedEvent event = new PMDDirectoryReturnedEvent(source, ruleSets);
        List listenerList = ListenerList.getListeners(PMDDirectoryReturnedEventListener.class);
        Iterator listeners = listenerList.iterator();

        while (listeners.hasNext()) {
            PMDDirectoryReturnedEventListener listener;

            listener = (PMDDirectoryReturnedEventListener) listeners.next();
            listener.returnedDefaultRuleSets(event);
        }
    }

    /**
     *****************************************************************************
     *
     * @param source
     * @param ruleSet
     */
    public static final void notifyReturnedIncludedRules(Object source, RuleSet ruleSet) {
        PMDDirectoryReturnedEvent event = new PMDDirectoryReturnedEvent(source, ruleSet);
        List listenerList = ListenerList.getListeners(PMDDirectoryReturnedEventListener.class);
        Iterator listeners = listenerList.iterator();

        while (listeners.hasNext()) {
            PMDDirectoryReturnedEventListener listener;

            listener = (PMDDirectoryReturnedEventListener) listeners.next();
            listener.returnedIncludedRules(event);
        }
    }
}
