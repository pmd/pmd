package net.sourceforge.pmd.swingui.event;

import java.util.EventListener;

/**
 *
 * @author Donald A. Leckie
 * @since December 13, 2002
 * @version $Revision$, $Date$
 */
public interface RuleSetChangedEventListener extends EventListener
{

    /**
     *********************************************************************************
     *
     * @param ruleSet
     */
    public void ruleSetChanged(RuleSetChangedEvent event);

    /**
     *********************************************************************************
     *
     */
    public void ruleSetsChanged(RuleSetChangedEvent event);
}