package net.sourceforge.pmd;

/**
 * A convenience exception wrapper.  Contains the original exception, if any.  Also, contains
 * a severity number (int).  Zero implies no severity.  The higher the number the greater the
 * severity.
 *
 * @author Donald A. Leckie
 * @since August 30, 2002
 * @version $Revision$, $Date$
 */
public class PMDException extends Exception
{

    private Exception m_originalException;
    private int m_severity;

    /**
     ********************************************************************************
     *
     * @param message
     */
    public PMDException(String message)
    {
        super(message);
    }

    /**
     *******************************************************************************
     *
     * @param message
     * @param originalException
     */
    public PMDException(String message, Exception originalException)
    {
        super(message);

        m_originalException = originalException;
    }

    /**
     *******************************************************************************
     *
     * @return
     */
    public Exception getOriginalException()
    {
        return m_originalException;
    }

    /**
     *******************************************************************************
     *
     * @param severity
     */
    public void setSeverity(int severity)
    {
        m_severity = severity;
    }

    /**
     *******************************************************************************
     *
     * @return
     */
    public int getSeverity()
    {
        return m_severity;
    }
}