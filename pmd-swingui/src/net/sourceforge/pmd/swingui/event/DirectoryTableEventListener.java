package net.sourceforge.pmd.swingui.event;

import java.util.EventListener;

public interface DirectoryTableEventListener extends EventListener {

    /**
     ******************************************************************************
     *
     * @param event
     */
    void requestSelectedFile(DirectoryTableEvent event);

    /**
     ******************************************************************************
     *
     * @param event
     */
    void fileSelectionChanged(DirectoryTableEvent event);

    /**
     ******************************************************************************
     *
     * @param event
     */
    void fileSelected(DirectoryTableEvent event);
}