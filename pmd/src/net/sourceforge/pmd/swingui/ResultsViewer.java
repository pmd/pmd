package net.sourceforge.pmd.swingui;

import java.awt.Component;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;

import javax.swing.JEditorPane;
import javax.swing.JScrollPane;
import javax.swing.text.html.HTMLEditorKit;
import javax.swing.UIManager;

import net.sourceforge.pmd.PMD;
import net.sourceforge.pmd.PMDException;
import net.sourceforge.pmd.Report;
import net.sourceforge.pmd.RuleContext;
import net.sourceforge.pmd.RuleSet;
import net.sourceforge.pmd.swingui.event.AnalyzeFileEvent;
import net.sourceforge.pmd.swingui.event.AnalyzeFileEventListener;
import net.sourceforge.pmd.swingui.event.DirectoryTableEvent;
import net.sourceforge.pmd.swingui.event.DirectoryTableEventListener;
import net.sourceforge.pmd.swingui.event.HTMLAnalysisResultsEvent;
import net.sourceforge.pmd.swingui.event.HTMLAnalysisResultsEventListener;
import net.sourceforge.pmd.swingui.event.ListenerList;
import net.sourceforge.pmd.swingui.event.RuleSetChangedEvent;
import net.sourceforge.pmd.swingui.event.RuleSetChangedEventListener;
import net.sourceforge.pmd.swingui.event.RulesInMemoryEvent;
import net.sourceforge.pmd.swingui.event.RulesInMemoryEventListener;
import net.sourceforge.pmd.swingui.event.StatusBarEvent;

/**
 *
 * @author Donald A. Leckie
 * @since August 27, 2002
 * @version $Revision$, $Date$
 */
class ResultsViewer extends JEditorPane
{

    private File m_selectedFile;
    private String m_htmlText;
    private PMD m_pmd;
    private RuleSet m_ruleSet;
    private Report m_report;

    /**
     ********************************************************************************
     */
    protected ResultsViewer()
    {
        super();

        setEditorKit(new HTMLEditorKit());
        setEditable(false);
        setBackground(UIManager.getColor("pmdGray"));

        m_pmd = new PMD();
        m_ruleSet = new RuleSet();
        ListenerList.addListener((DirectoryTableEventListener) new DirectoryTableEventHandler());
        ListenerList.addListener((RulesInMemoryEventListener) new RulesInMemoryEventHandler());
        ListenerList.addListener((AnalyzeFileEventListener) new AnalyzeFileEventHandler());
        ListenerList.addListener((HTMLAnalysisResultsEventListener) new HTMLAnalysisResultsEventHandler());
    }

    /**
     ********************************************************************************
     *
     * @param event
     */
    private void scrollToTop()
    {
        Component component = getParent();

        while ((component instanceof JScrollPane) == false)
        {
            component = component.getParent();
        }

        if (component != null)
        {
            JScrollPane parentScrollPane = (JScrollPane) component;

            parentScrollPane.getHorizontalScrollBar().setValue(0);
            parentScrollPane.getVerticalScrollBar().setValue(0);
            parentScrollPane.repaint();
        }
    }

    /**
     ********************************************************************************
     *
     * @param event
     */
    private void loadRuleSets()
    throws PMDException
    {
        int priority = Preferences.getPreferences().getLowestPriorityForAnalysis();
        RulesInMemoryEvent.notifyRequestIncludedRules(this, priority);
    }

    /**
     ********************************************************************************
     *
     * @return
     */
    protected String getHTMLText(File file)
    {

        return m_htmlText;
    }

    /**
     ********************************************************************************
     *
     * @return
     */
    protected String getHTMLText()
    {
        return m_htmlText;
    }

    /**
     ********************************************************************************
     *
     * @return
     */
    protected String getPlainText()
    {
        if (m_report != null)
        {
            synchronized(m_report)
            {
                if (m_selectedFile != null)
                {
                    String filePath = m_selectedFile.getPath();
                    TextRenderer renderer = new TextRenderer();

                    return renderer.render(filePath, m_report);
                }
            }
        }

        return "";
    }

    /**
     ********************************************************************************
     *
     */
    protected void analyze()
    {
        if (m_selectedFile != null)
        {
            (new AnalyzeThread(m_selectedFile)).start();
        }
    }

    /**
     ********************************************************************************
     ********************************************************************************
     ********************************************************************************
     */
    private class AnalyzeThread extends Thread
    {
        private File m_file;
        private ResultsViewer m_resultsViewer;

        /**
         ****************************************************************************
         *
         * @param threadName
         */
        private AnalyzeThread(File file)
        {
            super("Analyze");

            m_file = file;
            m_resultsViewer = ResultsViewer.this;
        }

        /**
         ***************************************************************************
         *
         */
        public void run()
        {
            setup();
            process();
            cleanup();
        }

        /**
         ***************************************************************************
         *
         */
        protected void setup()
        {
            PMDViewer.getViewer().setEnableViewer(false);
            StatusBarEvent.notifyStartAnimation(this);
        }

        /**
         ***************************************************************************
         *
         */
        protected void process()
        {
            if (m_file == null)
            {
                return;
            }

            try
            {
                StatusBarEvent.notifyShowMessage(this, "Analyzing.  Please wait...");

                loadRuleSets();
                RuleContext ruleContext = new RuleContext();
                ruleContext.setSourceCodeFilename(m_file.getPath());
                ruleContext.setReport(new Report());
                m_pmd.processFile(new FileInputStream(m_file), m_ruleSet, ruleContext);

                StatusBarEvent.notifyShowMessage(this, "Rendering analysis results into HTML page.  Please wait...");

                HTMLResultRenderer renderer;

                renderer = new HTMLResultRenderer();
                m_htmlText = renderer.render(m_file.getPath(), ruleContext.getReport());

                StatusBarEvent.notifyShowMessage(this, "Storing HTML page into viewer.  Please wait...");
                setText(m_htmlText);

                m_resultsViewer.scrollToTop();
                StatusBarEvent.notifyShowMessage(this, "Finished");
            }
            catch (FileNotFoundException exception)
            {
                MessageDialog.show(PMDViewer.getViewer(), null, exception);
            }
            catch (Throwable throwable)
            {
            }
        }

        /**
         ***************************************************************************
         *
         */
        protected void cleanup()
        {
            StatusBarEvent.notifyStopAnimation(this);
            PMDViewer.getViewer().setEnableViewer(true);
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
            m_selectedFile = event.getSelectedFile();

            if (m_selectedFile != null)
            {
                (new AnalyzeThread(m_selectedFile)).start();
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
     ***********************************************************************************
     ***********************************************************************************
     ***********************************************************************************
     */
    private class HTMLAnalysisResultsEventHandler implements HTMLAnalysisResultsEventListener
    {

        /**
         ****************************************************************************
         *
         * @param event
         */
        public void requestHTMLAnalysisResults(HTMLAnalysisResultsEvent event)
        {
            if (m_htmlText == null)
            {
                m_htmlText = "";
            }

            HTMLAnalysisResultsEvent.notifyReturnedHTMLText(this, m_htmlText);
        }

        /**
         ****************************************************************************
         *
         * @param event
         */
        public void returnedHTMLAnalysisResults(HTMLAnalysisResultsEvent event)
        {
        }
    }

    /**
     ***********************************************************************************
     ***********************************************************************************
     ***********************************************************************************
     */
    private class AnalyzeFileEventHandler implements AnalyzeFileEventListener
    {

        /**
         ****************************************************************************
         *
         * @param event
         */
        public void startAnalysis(AnalyzeFileEvent event)
        {
        }

        /**
         ****************************************************************************
         *
         * @param event
         */
        public void stopAnalysis(AnalyzeFileEvent event)
        {
        }
    }
}
