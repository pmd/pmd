package net.sourceforge.pmd.swingui;

import net.sourceforge.pmd.PMDException;
import net.sourceforge.pmd.RuleSet;
import net.sourceforge.pmd.swingui.event.DirectoryTableEvent;
import net.sourceforge.pmd.swingui.event.DirectoryTableEventListener;
import net.sourceforge.pmd.swingui.event.ListenerList;
import net.sourceforge.pmd.swingui.event.RulesInMemoryEvent;
import net.sourceforge.pmd.swingui.event.RulesInMemoryEventListener;

import java.io.File;

/**
 *
 * @author Donald A. Leckie
 * @since August 27, 2002
 * @version $Revision$, $Date$
 */
class AnalysisResultsViewer extends ResultsViewer
{

    private RuleSet m_ruleSet;

    /**
     ********************************************************************************
     */
    protected AnalysisResultsViewer()
    {
        super();

        //
        // Add listeners
        //
        ListenerList.addListener((DirectoryTableEventListener) new DirectoryTableEventHandler());
        ListenerList.addListener((RulesInMemoryEventListener) new RulesInMemoryEventHandler());
    }

    /**
     ***********************************************************************************
     ***********************************************************************************
     ***********************************************************************************
     */
    private class DirectoryTableEventHandler implements DirectoryTableEventListener
    {

        /**
         ***************************************************************************
         *
         * @param event
         */
        public void requestSelectedFile(DirectoryTableEvent event)
        {
        }

        /**
         ***************************************************************************
         *
         * @param event
         */
        public void fileSelectionChanged(DirectoryTableEvent event)
        {
            try
            {
                File[] file = {event.getSelectedFile()};
                int priority = Preferences.getPreferences().getLowestPriorityForAnalysis();
                RulesInMemoryEvent.notifyRequestIncludedRules(this, priority);
                AnalysisResultsViewer.this.analyze(file, m_ruleSet);
            }
            catch (PMDException pmdException)
            {
                MessageDialog.show(PMDViewer.getViewer(), pmdException.getMessage(), pmdException.getReason());
            }
        }

        /**
         ***************************************************************************
         *
         * @param event
         */
        public void fileSelected(DirectoryTableEvent event)
        {
        }
    }

    /**
     ***************************************************************************
     ***************************************************************************
     ***************************************************************************
     */
    private class RulesInMemoryEventHandler implements RulesInMemoryEventListener
    {

        /**
         ***********************************************************************
         *
         * @param event
         */
        public void requestAllRules(RulesInMemoryEvent event)
        {
        }

        /**
         ***********************************************************************
         *
         * @param event
         */
        public void requestIncludedRules(RulesInMemoryEvent event)
        {
        }

        /**
         ***********************************************************************
         *
         * @param event
         */
        public void returnedRules(RulesInMemoryEvent event)
        {
            m_ruleSet = event.getRules();
        }
    }
}
