package net.sourceforge.pmd.swingui.event;
//J-
import java.util.EventObject;
import java.util.Iterator;
import java.util.List;

/**
 *
 * @author Donald A. Leckie
 * @since December 13, 2002
 * @version $Revision$, $Date$
 */
public class MessageEvent extends EventObject
{

    private String m_message;
    private Exception m_exception;

    /**
     ***********************************************************
     *
     * @param source
     * @param message
     * @param exception
     */
    private MessageEvent(Object source, String message, Exception exception)
    {
        super(source);

        m_message = message;
        m_exception = exception;
    }

    /**
     ***********************************************************
     *
     * @return
     */
    public String getMessage()
    {
        return m_message;
    }

    /**
     ***********************************************************
     *
     * @return
     */
    public Exception getException()
    {
        return m_exception;
    }

    /**
     ***********************************************************
     *
     * @param source
     * @param message
     * @param exception
     */
    public static final void notifyDisplayMessage(Object source, String message, Exception exception)
    {
        if ((source != null) && (message != null))
        {
            MessageEvent event = new MessageEvent(source, message, exception);
            List listenerList = ListenerList.getListeners(MessageEventListener.class);
            Iterator listeners = listenerList.iterator();

            while (listeners.hasNext())
            {
                MessageEventListener listener;

                listener = (MessageEventListener) listeners.next();
                listener.displayMessage(event);
            }
        }
    }
}