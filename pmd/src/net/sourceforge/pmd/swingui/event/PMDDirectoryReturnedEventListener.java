package net.sourceforge.pmd.swingui.event;

import java.util.EventListener;

/**
 *
 * @author Donald A. Leckie
 * @since December 13, 2002
 * @version $Revision$, $Date$
 */
public interface PMDDirectoryReturnedEventListener extends EventListener
{

    /**
     *******************************************************************************
     *
     * @param event
     */
    public void returnedRuleSetPath(PMDDirectoryReturnedEvent event);

    /**
     *******************************************************************************
     *
     * @param event
     */
    public void returnedAllRuleSets(PMDDirectoryReturnedEvent event);

    /**
     *******************************************************************************
     *
     * @param event
     */
    public void returnedDefaultRuleSets(PMDDirectoryReturnedEvent event);

    /**
     *******************************************************************************
     *
     * @param event
     */
    public void returnedIncludedRules(PMDDirectoryReturnedEvent event);
}
