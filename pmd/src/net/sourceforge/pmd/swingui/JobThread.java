package net.sourceforge.pmd.swingui;

import java.util.List;
import java.util.ArrayList;

/**
 *
 * @author Donald A. Leckie
 * @since August 17, 2002
 * @version $Revision$, $Date$
 */
abstract class JobThread extends Thread
{

    private List m_listeners = new ArrayList();

    /**
     *********************************************************************************
     *
     * @param threadName
     */
    protected JobThread(String threadName)
    {
        super(threadName);
    }

    /**
     *********************************************************************************
     *
     * @param listener
     */
    protected void addListener(JobThreadListener listener)
    {
        m_listeners.add(listener);
    }

    /**
     *********************************************************************************
     *
     */
    private void notifyJobThreadStarted(JobThreadEvent event)
    {
        if (m_listeners != null)
        {
            for (int n = 0; n < m_listeners.size(); n++)
            {
                JobThreadListener listener = (JobThreadListener) m_listeners.get(n);

                listener.jobThreadStarted(event);
            }
        }
    }

    /**
     *********************************************************************************
     *
     */
    private void notifyJobThreadFinished(JobThreadEvent event)
    {
        if (m_listeners != null)
        {
            for (int n = 0; n < m_listeners.size(); n++)
            {
                JobThreadListener listener = (JobThreadListener) m_listeners.get(n);

                listener.jobThreadFinished(event);
            }
        }
    }

    /**
     *********************************************************************************
     *
     */
    protected void notifyJobThreadStatus(JobThreadEvent event)
    {
        if (m_listeners != null)
        {
            for (int n = 0; n < m_listeners.size(); n++)
            {
                JobThreadListener listener = (JobThreadListener) m_listeners.get(n);

                listener.jobThreadStatus(event);
            }
        }
    }

    /**
     *********************************************************************************
     *
     * @param listener
     */
    protected void removeListener(JobThreadListener listener)
    {
        m_listeners.remove(listener);
    }

    /**
     ***************************************************************************
     *
     */
    public void run()
    {
        setup();
        notifyJobThreadStarted(new JobThreadEvent(this));
        process();
        notifyJobThreadFinished(new JobThreadEvent(this));
        cleanup();
    }

    /**
     ***************************************************************************
     *
     */
    protected abstract void setup();

    /**
     ***************************************************************************
     *
     */
    protected abstract void process();

    /**
     ***************************************************************************
     *
     */
    protected abstract void cleanup();
}
