package net.sourceforge.pmd.swingui;

import java.util.ArrayList;
import java.util.List;

import net.sourceforge.pmd.swingui.event.JobThreadEvent;
import net.sourceforge.pmd.swingui.event.JobThreadEventListener;

/**
 *
 * @author Donald A. Leckie
 * @since August 17, 2002
 * @version $Revision$, $Date$
 */
public abstract class JobThread extends Thread
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
    protected void addListener(JobThreadEventListener listener)
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
                JobThreadEventListener listener = (JobThreadEventListener) m_listeners.get(n);

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
                JobThreadEventListener listener = (JobThreadEventListener) m_listeners.get(n);

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
                JobThreadEventListener listener = (JobThreadEventListener) m_listeners.get(n);

                listener.jobThreadStatus(event);
            }
        }
    }

    /**
     *********************************************************************************
     *
     * @param listener
     */
    protected void removeListener(JobThreadEventListener listener)
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
