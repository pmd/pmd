package net.sourceforge.pmd.swingui.event;

import java.util.EventListener;

/**
 *
 * @author Donald A. Leckie
 * @since August 27, 2002
 * @version $Revision$, $Date$
 */
public interface JobThreadEventListener extends EventListener
{

    /**
     *******************************************************************************
     *
     * @param event
     */
    public void jobThreadStarted(JobThreadEvent event);

    /**
     *******************************************************************************
     *
     * @param event
     */
    public void jobThreadFinished(JobThreadEvent event);

    /**
     *******************************************************************************
     *
     * @param event
     */
    public void jobThreadStatus(JobThreadEvent event);
}