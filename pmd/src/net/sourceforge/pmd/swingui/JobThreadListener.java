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
     * @parameter event
     */
    public void jobThreadStarted(JobThreadEvent event);

    /**
     *******************************************************************************
     *
     * @parameter event
     */
    public void jobThreadFinished(JobThreadEvent event);

    /**
     *******************************************************************************
     *
     * @parameter event
     */
    public void jobThreadStatus(JobThreadEvent event);
}