package net.sourceforge.pmd.swingui.event;

import java.util.EventObject;
import java.util.Iterator;
import java.util.List;

/**
 *
 * @author Donald A. Leckie
 * @since December 13, 2002
 * @version $Revision$, $Date$
 */
public class StatusBarEvent extends EventObject {

    private String m_message;

    /**
     *****************************************************************************
     *
     * @param message
     */
    private StatusBarEvent(Object source) {
        super(source);
    }

    /**
     *****************************************************************************
     *
     * @param message
     */
    private StatusBarEvent(Object source, String message) {
        super(source);

        m_message = message;
    }

    /**
     *****************************************************************************
     *
     * @return
     */
    public String getMessage() {
        return m_message;
    }

    /**
     *****************************************************************************
     *
     */
    public static final void notifyStartAnimation(Object source) {
        StatusBarEvent event = new StatusBarEvent(source);
        List listenerList = ListenerList.getListeners(StatusBarEventListener.class);
        Iterator listeners = listenerList.iterator();

        while (listeners.hasNext()) {
            StatusBarEventListener listener;

            listener = (StatusBarEventListener) listeners.next();
            listener.startAnimation(event);
        }
    }

    /**
     *****************************************************************************
     *
     * @param message
     */
    public static final void notifyShowMessage(Object source, String message) {
        StatusBarEvent event = new StatusBarEvent(source, message);
        List listenerList = ListenerList.getListeners(StatusBarEventListener.class);
        Iterator listeners = listenerList.iterator();

        while (listeners.hasNext()) {
            StatusBarEventListener listener;

            listener = (StatusBarEventListener) listeners.next();
            listener.showMessage(event);
        }
    }

    /**
     *****************************************************************************
     *
     */
    public static final void notifyStopAnimation(Object source) {
        StatusBarEvent event = new StatusBarEvent(source);
        List listenerList = ListenerList.getListeners(StatusBarEventListener.class);
        Iterator listeners = listenerList.iterator();

        while (listeners.hasNext()) {
            StatusBarEventListener listener;

            listener = (StatusBarEventListener) listeners.next();
            listener.stopAnimation(event);
        }
    }
}