package net.sourceforge.pmd.swingui.event;

import java.util.EventListener;

public interface RulesInMemoryEventListener extends EventListener
{

    /**
     ****************************************************************************
     *
     * @param event
     */
    public void requestAllRules(RulesInMemoryEvent event);

    /**
     ****************************************************************************
     *
     * @param event
     */
    public void requestIncludedRules(RulesInMemoryEvent event);

    /**
     ****************************************************************************
     *
     * @param event
     */
    public void returnedRules(RulesInMemoryEvent event);
}