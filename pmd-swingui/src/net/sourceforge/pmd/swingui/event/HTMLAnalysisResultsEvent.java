package net.sourceforge.pmd.swingui.event;

import java.util.EventObject;
import java.util.Iterator;
import java.util.List;

public class HTMLAnalysisResultsEvent extends EventObject {
    private String m_htmlText;

    /**
     *****************************************************************************
     *
     * @param source
     */
    private HTMLAnalysisResultsEvent(Object source) {
        super(source);
    }

    /**
     *****************************************************************************
     *
     * @param source
     * @param htmlText
     */
    private HTMLAnalysisResultsEvent(Object source, String htmlText) {
        super(source);

        m_htmlText = htmlText;
    }

    /**
     *****************************************************************************
     *
     * @return
     */
    public String getHTMLText() {
        return m_htmlText;
    }

    /**
     *****************************************************************************
     *
     * @param source
     */
    public static final void notifyRequestHTMLText(Object source) {
        HTMLAnalysisResultsEvent event = new HTMLAnalysisResultsEvent(source);
        List listenerList = ListenerList.getListeners(HTMLAnalysisResultsEventListener.class);
        Iterator listeners = listenerList.iterator();

        while (listeners.hasNext()) {
            HTMLAnalysisResultsEventListener listener;

            listener = (HTMLAnalysisResultsEventListener) listeners.next();
            listener.requestHTMLAnalysisResults(event);
        }
    }

    /**
     *****************************************************************************
     *
     * @param source
     */
    public static final void notifyReturnedHTMLText(Object source, String htmlText) {
        HTMLAnalysisResultsEvent event = new HTMLAnalysisResultsEvent(source, htmlText);
        List listenerList = ListenerList.getListeners(HTMLAnalysisResultsEventListener.class);
        Iterator listeners = listenerList.iterator();

        while (listeners.hasNext()) {
            HTMLAnalysisResultsEventListener listener;

            listener = (HTMLAnalysisResultsEventListener) listeners.next();
            listener.returnedHTMLAnalysisResults(event);
        }
    }
}