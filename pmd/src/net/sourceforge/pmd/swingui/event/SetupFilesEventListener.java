package net.sourceforge.pmd.swingui.event;

import java.util.EventListener;

public interface SetupFilesEventListener extends EventListener
{

    /**
     ****************************************************************************
     *
     * @param event
     */
    public void setFileList(SetupFilesEvent event);

    /**
     ****************************************************************************
     *
     * @param event
     */
    public void startSetup(SetupFilesEvent event);

    /**
     ****************************************************************************
     *
     * @param event
     */
    public void stopSetup(SetupFilesEvent event);
}