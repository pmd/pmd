package net.sourceforge.pmd.swingui.event;

import java.util.EventObject;
import java.util.Iterator;
import java.util.List;

public class SearchDirectoryEvent extends EventObject {
    private String m_searchDirectory;

    /**
     *****************************************************************************
     *
     * @param source
     * @param searchDirectory
     */
    private SearchDirectoryEvent(Object source, String searchDirectory) {
        super(source);

        m_searchDirectory = searchDirectory;
    }

    /**
     *****************************************************************************
     *
     * @return
     */
    public String getSearchDirectory() {
        return m_searchDirectory;
    }

    /**
     *****************************************************************************
     *
     * @param source
     */
    public static final void notifySetSearchDirectory(Object source, String searchDirectory) {
        SearchDirectoryEvent event = new SearchDirectoryEvent(source, searchDirectory);
        List listenerList = ListenerList.getListeners(SearchDirectoryEventListener.class);
        Iterator listeners = listenerList.iterator();

        while (listeners.hasNext()) {
            SearchDirectoryEventListener listener;

            listener = (SearchDirectoryEventListener) listeners.next();
            listener.setSearchDirectory(event);
        }
    }
}