package net.sourceforge.pmd.swingui;

import java.util.EventListener;
import java.util.EventObject;

/**
 *
 * @author Donald A. Leckie
 * @since August 27, 2002
 * @version $Revision$, $Date$
 */
interface JobThreadListener extends EventListener
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