package net.sourceforge.pmd.swingui.event;

import java.io.File;
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
    public void fileSelected(DirectoryTableEvent event);
}