package net.sourceforge.pmd.swingui.event;

import java.util.EventListener;

/**
 *
 * @author Donald A. Leckie
 * @since December 13, 2002
 * @version $Revision$, $Date$
 */
public interface HTMLAnalysisResultsEventListener extends EventListener
{

    /**
     *
     * @param event
     */
    public void requestHTMLAnalysisResults(HTMLAnalysisResultsEvent event);

    /**
     ****************************************************************************
     *
     * @param event
     */
    public void returnedHTMLAnalysisResults(HTMLAnalysisResultsEvent event);
}