package net.sourceforge.pmd.swingui;

import net.sourceforge.pmd.PMD;
import net.sourceforge.pmd.PMDException;
import net.sourceforge.pmd.Report;
import net.sourceforge.pmd.RuleContext;
import net.sourceforge.pmd.RuleSet;
import net.sourceforge.pmd.swingui.event.AnalyzeFileEvent;
import net.sourceforge.pmd.swingui.event.DirectoryTableEvent;
import net.sourceforge.pmd.swingui.event.DirectoryTableEventListener;
import net.sourceforge.pmd.swingui.event.ListenerList;
import net.sourceforge.pmd.swingui.event.PMDDirectoryRequestEvent;
import net.sourceforge.pmd.swingui.event.PMDDirectoryReturnedEvent;
import net.sourceforge.pmd.swingui.event.PMDDirectoryReturnedEventListener;
import net.sourceforge.pmd.swingui.event.RuleSetChangedEvent;
import net.sourceforge.pmd.swingui.event.RuleSetChangedEventListener;
import net.sourceforge.pmd.swingui.event.StatusBarEvent;

import javax.swing.JEditorPane;
import javax.swing.JScrollPane;
import javax.swing.text.html.HTMLEditorKit;
import java.awt.Component;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;

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
    private boolean m_loadRuleSets;

    /**
     ********************************************************************************
     */
    protected ResultsViewer()
    {
        super();

        setEditorKit(new HTMLEditorKit());
        setEditable(false);

        m_pmd = new PMD();
        m_ruleSet = new RuleSet();
        m_loadRuleSets = true;
        ListenerList.addListener((DirectoryTableEventListener) new DirectoryTableEventHandler());
        ListenerList.addListener((PMDDirectoryReturnedEventListener) new PMDDirectoryReturnedEventHandler());
        ListenerList.addListener((RuleSetChangedEventListener) new RuleSetChangedEventHandler());
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
        if (m_loadRuleSets)
        {
            int priority = Preferences.getPreferences().getLowestPriorityForAnalysis();
            PMDDirectoryRequestEvent.notifyRequestIncludedRules(this, priority);
            m_loadRuleSets = false;
        }
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
            AnalyzeFileEvent.notifyStartAnalysis(this, m_file);
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
            AnalyzeFileEvent.notifyStopAnalysis(this, m_file);
            StatusBarEvent.notifyStopAnimation(this);
        }
    }

    /**
     ***************************************************************************
     ***************************************************************************
     ***************************************************************************
     */
    private class PMDDirectoryReturnedEventHandler implements PMDDirectoryReturnedEventListener
    {

        /**
         ***********************************************************************
         *
         * @param event
         */
        public void returnedRuleSetPath(PMDDirectoryReturnedEvent event)
        {
        }

        /**
         ***********************************************************************
         *
         * @param event
         */
        public void returnedAllRuleSets(PMDDirectoryReturnedEvent event)
        {
        }

        /**
         ***********************************************************************
         *
         * @param event
         */
        public void returnedDefaultRuleSets(PMDDirectoryReturnedEvent event)
        {
        }

        /**
         *******************************************************************************
         *
         * @param event
         */
        public void returnedIncludedRules(PMDDirectoryReturnedEvent event)
        {
            m_ruleSet = event.getRuleSet();
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
        public void fileSelected(DirectoryTableEvent event)
        {
            File file = event.getSelectedFile();

            if (file != null)
            {
                (new AnalyzeThread(file)).start();
            }
        }
    }

    /**
     ***********************************************************************************
     ***********************************************************************************
     ***********************************************************************************
     */
    private class RuleSetChangedEventHandler implements RuleSetChangedEventListener
    {

        /**
         *********************************************************************************
         *
         * @param ruleSet
         */
        public void ruleSetChanged(RuleSetChangedEvent event)
        {
            m_loadRuleSets = true;
        }

        /**
         *********************************************************************************
         *
         */
        public void ruleSetsChanged(RuleSetChangedEvent event)
        {
            m_loadRuleSets = true;
        }
    }
}
