package net.sourceforge.pmd.swingui.event;

import java.util.EventObject;

import net.sourceforge.pmd.swingui.JobThread;

/**
 *
 * @author Donald A. Leckie
 * @since August 27, 2002
 * @version $Revision$, $Date$
 */
public class JobThreadEvent extends EventObject
{

    private String m_message;

    /**
     *********************************************************************************
     *
     * @param source
     */
    public JobThreadEvent(JobThread source)
    {
        super(source);
    }

    /**
     *********************************************************************************
     *
     * @param source
     * @param message
     */
    public JobThreadEvent(JobThread source, String message)
    {
        super(source);

        m_message = message;
    }

    /**
     *********************************************************************************
     *
     * @return
     */
    public String getMessage()
    {
        return (m_message != null) ? m_message : "";
    }
}