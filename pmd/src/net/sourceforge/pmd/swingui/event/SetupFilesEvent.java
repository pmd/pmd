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
public class SetupFilesEvent extends EventObject
{

    private File[] m_fileList;

    /**
     *****************************************************************************
     *
     */
    private SetupFilesEvent(Object source)
    {
        super(source);
    }

    /**
     *****************************************************************************
     *
     */
    private SetupFilesEvent(Object source, File[] fileList)
    {
        super(source);

        m_fileList = fileList;
    }

    /**
     ****************************************************************************
     *
     * @return
     */
    public File[] getFileList()
    {
        return m_fileList;
    }

    /**
     *****************************************************************************
     *
     * @param file
     */
    public static final void notifySetFileList(Object source, File[] fileList)
    {
        SetupFilesEvent event = new SetupFilesEvent(source, fileList);
        List listenerList = ListenerList.getListeners(SetupFilesEventListener.class);
        Iterator listeners = listenerList.iterator();

        while (listeners.hasNext())
        {
            SetupFilesEventListener listener;

            listener = (SetupFilesEventListener) listeners.next();
            listener.setFileList(event);
        }
    }

    /**
     *****************************************************************************
     *
     * @param file
     */
    public static final void notifyStartSetup(Object source)
    {
        SetupFilesEvent event = new SetupFilesEvent(source);
        List listenerList = ListenerList.getListeners(SetupFilesEventListener.class);
        Iterator listeners = listenerList.iterator();

        while (listeners.hasNext())
        {
            SetupFilesEventListener listener;

            listener = (SetupFilesEventListener) listeners.next();
            listener.startSetup(event);
        }
    }

    /**
     *****************************************************************************
     *
     * @param file
     */
    public static final void notifyStopSetup(Object source)
    {
        SetupFilesEvent event = new SetupFilesEvent(source);
        List listenerList = ListenerList.getListeners(SetupFilesEventListener.class);
        Iterator listeners = listenerList.iterator();

        while (listeners.hasNext())
        {
            SetupFilesEventListener listener;

            listener = (SetupFilesEventListener) listeners.next();
            listener.stopSetup(event);
        }
    }
}