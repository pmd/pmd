package net.sourceforge.pmd.swingui.event;

import java.io.File;
import java.util.EventObject;
import java.util.Iterator;
import java.util.List;

/**
 *
 * @author Donald A. Leckie
 * @since December 13, 2002
 * @version $Revision$, $Date$
 */
public class AnalyzeFileEvent extends EventObject
{
    private File m_file;

    /**
     *****************************************************************************
     *
     */
    private AnalyzeFileEvent(Object source)
    {
        super(source);
    }

    /**
     *****************************************************************************
     *
     */
    private AnalyzeFileEvent(Object source, File file)
    {
        super(source);

        m_file = file;
    }

    /**
     *****************************************************************************
     *
     * @return
     */
    public File getFile()
    {
        return m_file;
    }

    /**
     *****************************************************************************
     *
     * @param source
     * @param file
     */
    public static final void notifyStartAnalysis(Object source, File file)
    {
        AnalyzeFileEvent event = new AnalyzeFileEvent(source, file);
        List listenerList = ListenerList.getListeners(AnalyzeFileEventListener.class);
        Iterator listeners = listenerList.iterator();

        while (listeners.hasNext())
        {
            AnalyzeFileEventListener listener;

            listener = (AnalyzeFileEventListener) listeners.next();
            listener.startAnalysis(event);
        }
    }

    /**
     *****************************************************************************
     *
     * @param source
     * @param file
     */
    public static final void notifyStopAnalysis(Object source, File file)
    {
        AnalyzeFileEvent event = new AnalyzeFileEvent(source, file);
        List listenerList = ListenerList.getListeners(AnalyzeFileEventListener.class);
        Iterator listeners = listenerList.iterator();

        while (listeners.hasNext())
        {
            AnalyzeFileEventListener listener;

            listener = (AnalyzeFileEventListener) listeners.next();
            listener.stopAnalysis(event);
        }
    }
}