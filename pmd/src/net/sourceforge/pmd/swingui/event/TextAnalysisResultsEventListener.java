package net.sourceforge.pmd.swingui.event;

import java.util.EventListener;

/**
 *
 * @author Donald A. Leckie
 * @since December 13, 2002
 * @version $Revision$, $Date$
 */
public interface TextAnalysisResultsEventListener extends EventListener
{

    /**
     ****************************************************************************
     *
     * @param event
     */
    public void requestTextAnalysisResults(TextAnalysisResultsEvent event);

    /**
     ****************************************************************************
     *
     * @param event
     */
    public void returnedTextAnalysisResults(TextAnalysisResultsEvent event);
}