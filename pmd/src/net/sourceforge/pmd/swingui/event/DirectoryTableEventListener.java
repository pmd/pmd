package net.sourceforge.pmd.swingui.event;

import java.util.EventListener;

public interface DirectoryTableEventListener extends EventListener
{

    /**
     ******************************************************************************
     *
     * @param event
     */
    public void requestSelectedFile(DirectoryTableEvent event);

    /**
     ******************************************************************************
     *
     * @param event
     */
    public void fileSelectionChanged(DirectoryTableEvent event);

    /**
     ******************************************************************************
     *
     * @param event
     */
    public void fileSelected(DirectoryTableEvent event);
}