package net.sourceforge.pmd.swingui.event;

import java.util.EventListener;

public interface SetupFilesEventListener extends EventListener
{

    /**
     ****************************************************************************
     *
     * @param event
     */
    void setFileList(SetupFilesEvent event);

    /**
     ****************************************************************************
     *
     * @param event
     */
    void startSetup(SetupFilesEvent event);

    /**
     ****************************************************************************
     *
     * @param event
     */
    void stopSetup(SetupFilesEvent event);
}