package net.sourceforge.pmd.swingui;

import java.util.EventObject;

/**
 *
 * @author Donald A. Leckie
 * @since August 27, 2002
 * @version $Revision$, $Date$
 */
class JobThreadEvent extends EventObject
{

    private String m_message;

    /**
     *********************************************************************************
     *
     * @param source
     */
    protected JobThreadEvent(JobThread source)
    {
        super(source);
    }

    /**
     *********************************************************************************
     *
     * @param source
     * @param message
     */
    protected JobThreadEvent(JobThread source, String message)
    {
        super(source);

        m_message = message;
    }

    /**
     *********************************************************************************
     *
     * @return
     */
    protected String getMessage()
    {
        return (m_message != null) ? m_message : "";
    }
}