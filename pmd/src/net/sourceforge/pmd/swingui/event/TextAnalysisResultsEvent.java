package net.sourceforge.pmd.swingui.event;

import java.util.EventObject;
import java.util.Iterator;
import java.util.List;

public class TextAnalysisResultsEvent extends EventObject {
    private String m_text;

    /**
     *****************************************************************************
     *
     * @param source
     */
    private TextAnalysisResultsEvent(Object source) {
        super(source);
    }

    /**
     *****************************************************************************
     *
     * @param source
     * @param text
     */
    private TextAnalysisResultsEvent(Object source, String text) {
        super(source);

        m_text = text;
    }

    /**
     *****************************************************************************
     *
     * @return
     */
    public String getText() {
        return m_text;
    }

    /**
     *****************************************************************************
     *
     * @param source
     */
    public static final void notifyRequestText(Object source) {
        TextAnalysisResultsEvent event = new TextAnalysisResultsEvent(source);
        List listenerList = ListenerList.getListeners(TextAnalysisResultsEventListener.class);
        Iterator listeners = listenerList.iterator();

        while (listeners.hasNext()) {
            TextAnalysisResultsEventListener listener;

            listener = (TextAnalysisResultsEventListener) listeners.next();
            listener.requestTextAnalysisResults(event);
        }
    }

    /**
     *****************************************************************************
     *
     * @param source
     */
    public static final void notifyReturnedText(Object source, String htmlText) {
        TextAnalysisResultsEvent event = new TextAnalysisResultsEvent(source, htmlText);
        List listenerList = ListenerList.getListeners(TextAnalysisResultsEventListener.class);
        Iterator listeners = listenerList.iterator();

        while (listeners.hasNext()) {
            TextAnalysisResultsEventListener listener;

            listener = (TextAnalysisResultsEventListener) listeners.next();
            listener.returnedTextAnalysisResults(event);
        }
    }
}