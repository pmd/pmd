package net.sourceforge.pmd.swingui.event;

import java.util.EventListener;

/**
 *
 * @author Donald A. Leckie
 * @since December 13, 2002
 * @version $Revision$, $Date$
 */
public interface RuleSetChangedEventListener extends EventListener {

    /**
     *********************************************************************************
     *
     * @param ruleSet
     */
    void ruleSetChanged(RuleSetChangedEvent event);

    /**
     *********************************************************************************
     *
     */
    void ruleSetsChanged(RuleSetChangedEvent event);
}