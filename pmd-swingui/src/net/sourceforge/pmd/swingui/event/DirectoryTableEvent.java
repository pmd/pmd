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
public class DirectoryTableEvent extends EventObject {

    private File m_selectedFile;

    /**
     ****************************************************************************
     *
     */
    private DirectoryTableEvent(Object source, File selectedFile) {
        super(source);

        m_selectedFile = selectedFile;
    }

    /**
     ****************************************************************************
     *
     * @return
     */
    public File getSelectedFile() {
        return m_selectedFile;
    }

    /**
     *******************************************************************************
     *
     * @param dataNode
     */
    public static void notifyRequestFileSelected(Object source) {
        if (source != null) {
            DirectoryTableEvent event = new DirectoryTableEvent(source, null);
            List listenerList = ListenerList.getListeners(DirectoryTableEventListener.class);
            Iterator listeners = listenerList.iterator();

            while (listeners.hasNext()) {
                DirectoryTableEventListener listener;

                listener = (DirectoryTableEventListener) listeners.next();
                listener.requestSelectedFile(event);
            }
        }
    }

    /**
     *******************************************************************************
     *
     * @param dataNode
     */
    public static void notifyFileSelectionChanged(Object source, File newFile) {
        if (source != null) {
            DirectoryTableEvent event = new DirectoryTableEvent(source, newFile);
            List listenerList = ListenerList.getListeners(DirectoryTableEventListener.class);
            Iterator listeners = listenerList.iterator();

            while (listeners.hasNext()) {
                DirectoryTableEventListener listener;

                listener = (DirectoryTableEventListener) listeners.next();
                listener.fileSelectionChanged(event);
            }
        }
    }

    /**
     *******************************************************************************
     *
     * @param dataNode
     */
    public static void notifySelectedFile(Object source, File selectedFile) {
        if ((source != null) && (selectedFile != null)) {
            DirectoryTableEvent event = new DirectoryTableEvent(source, selectedFile);
            List listenerList = ListenerList.getListeners(DirectoryTableEventListener.class);
            Iterator listeners = listenerList.iterator();

            while (listeners.hasNext()) {
                DirectoryTableEventListener listener;

                listener = (DirectoryTableEventListener) listeners.next();
                listener.fileSelected(event);
            }
        }
    }
}