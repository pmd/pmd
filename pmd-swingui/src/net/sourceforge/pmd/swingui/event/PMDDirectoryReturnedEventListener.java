package net.sourceforge.pmd.swingui.event;


import java.util.EventListener;


/**
 *
 * @author Donald A. Leckie
 * @since December 13, 2002
 * @version 0.1
 */
public interface PMDDirectoryReturnedEventListener extends EventListener {

    /**
     *******************************************************************************
     *
     * @param event
     */
    void returnedRuleSetPath(PMDDirectoryReturnedEvent event);

    /**
     *******************************************************************************
     *
     * @param event
     */
    void returnedAllRuleSets(PMDDirectoryReturnedEvent event);

    /**
     *******************************************************************************
     *
     * @param event
     */
    void returnedDefaultRuleSets(PMDDirectoryReturnedEvent event);

    /**
     *******************************************************************************
     *
     * @param event
     */
    void returnedIncludedRules(PMDDirectoryReturnedEvent event);
}
