package net.sourceforge.pmd.swingui.event;

import java.util.EventListener;

/**
 *
 * @author Donald A. Leckie
 * @since December 13, 2002
 * @version $Revision$, $Date$
 */
public interface RulesEditingEventListener extends EventListener {

    /**
     ****************************************************************************
     *
     * @param event
     */
    void saveData(RulesEditingEvent event);

    /**
     ****************************************************************************
     *
     * @param event
     */
    void loadData(RulesEditingEvent event);
}