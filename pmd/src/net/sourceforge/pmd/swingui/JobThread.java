package net.sourceforge.pmd.swingui;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.lang.Thread;
import java.util.List;
import java.util.ArrayList;

abstract class JobThread extends Thread
{

    private List m_listeners;

    //Constants
    public static final String STARTED_JOB_THREAD = "Started Job Thread";
    public static final String FINISHED_JOB_THREAD = "Finished Job Thread";
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
    protected void addListener(ActionListener listener)
    {
        if (m_listeners == null)
        {
            m_listeners = new ArrayList();
        }

        m_listeners.add(listener);
    }

    /**
     *********************************************************************************
     *
     */
    private void notifyListeners(ActionEvent event)
    {
        if (m_listeners != null)
        {
            for (int n = 0; n < m_listeners.size(); n++)
            {
                ActionListener listener = (ActionListener) m_listeners.get(n);

                listener.actionPerformed(event);
            }
        }
    }

    /**
     *********************************************************************************
     *
     * @param listener
     */
    protected void removeListener(ActionListener listener)
    {
        m_listeners.remove(listener);
    }

    /**
     ***************************************************************************
     *
     */
    public void run()
    {
        notifyListeners(new ActionEvent(this, 1, STARTED_JOB_THREAD));
        process();
        notifyListeners(new ActionEvent(this, 2, FINISHED_JOB_THREAD));
    }

    /**
     ***************************************************************************
     *
     */
    protected abstract void process();
}