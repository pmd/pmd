package net.sourceforge.pmd.swingui.event;

import java.util.EventListener;

/**
 *
 * @author Donald A. Leckie
 * @since January 6, 2003
 * @version $Revision$, $Date$
 */
public interface SearchDirectoryEventListener extends EventListener
{

    /**
     *
     * @param event
     */
    public void setSearchDirectory(SearchDirectoryEvent event);
}