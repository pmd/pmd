package net.sourceforge.pmd.swingui.event;

import java.util.EventListener;

public interface AnalyzeFileEventListener extends EventListener
{

    /**
     ****************************************************************************
     *
     * @param event
     */
    public void startAnalysis(AnalyzeFileEvent event);

    /**
     ****************************************************************************
     *
     * @param event
     */
    public void stopAnalysis(AnalyzeFileEvent event);
}