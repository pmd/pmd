package net.sourceforge.pmd.swingui.event;

import java.util.EventListener;

public interface AnalyzeFileEventListener extends EventListener {

    /**
     ****************************************************************************
     *
     * @param event
     */
    void startAnalysis(AnalyzeFileEvent event);

    /**
     ****************************************************************************
     *
     * @param event
     */
    void stopAnalysis(AnalyzeFileEvent event);
}