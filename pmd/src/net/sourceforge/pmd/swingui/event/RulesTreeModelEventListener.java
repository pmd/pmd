package net.sourceforge.pmd.swingui.event;

import java.util.EventListener;

/**
 *
 * @author Donald A. Leckie
 * @since December 13, 2002
 * @version $Revision$, $Date$
 */
public interface RulesTreeModelEventListener extends EventListener {

    /**
     ****************************************************************************
     *
     * @param event
     */
    void reload(RulesTreeModelEvent event);

    /**
     ****************************************************************************
     *
     * @param event
     */
    void requestSelectedRule(RulesTreeModelEvent event);

    /**
     ****************************************************************************
     *
     * @param event
     */
    void returnedSelectedRule(RulesTreeModelEvent event);
}