package net.sourceforge.pmd.swingui;

import java.awt.Component;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;

import javax.swing.JEditorPane;
import javax.swing.JScrollPane;
import javax.swing.text.html.HTMLEditorKit;

import net.sourceforge.pmd.swingui.event.DirectoryTableEvent;
import net.sourceforge.pmd.swingui.event.DirectoryTableEventListener;
import net.sourceforge.pmd.swingui.event.JobThreadEvent;
import net.sourceforge.pmd.swingui.event.ListenerList;
import net.sourceforge.pmd.swingui.event.RuleSetChangedEvent;
import net.sourceforge.pmd.swingui.event.RuleSetChangedEventListener;
import net.sourceforge.pmd.swingui.event.PMDDirectoryRequestEvent;
import net.sourceforge.pmd.swingui.event.PMDDirectoryReturnedEvent;
import net.sourceforge.pmd.swingui.event.PMDDirectoryReturnedEventListener;
import net.sourceforge.pmd.PMD;
import net.sourceforge.pmd.PMDException;
import net.sourceforge.pmd.Report;
import net.sourceforge.pmd.RuleContext;
import net.sourceforge.pmd.RuleSet;

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
    private AnalyzeThreadInUse m_analyzeThreadInUse;
    private JScrollPane m_parentScrollPane;
    private PMDDirectoryReturnedEventHandler m_pmdDirectoryReturnedEventHandler;
    private DirectoryTableEventHandler m_directoryTableEventHandler;
    private RuleSetChangedEventHandler m_ruleSetChangedEventHandler;

    /**
     ********************************************************************************
     */
    protected ResultsViewer()
    {
        super();

        setDoubleBuffered(true);
        setEditorKit(new HTMLEditorKit());
        setEditable(false);

        m_pmd = new PMD();
        m_ruleSet = new RuleSet();
        m_loadRuleSets = true;
        m_analyzeThreadInUse = new AnalyzeThreadInUse();
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
            PMDDirectoryRequestEvent.notifyRequestIncludedRules(this);
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
    private class AnalyzeThread extends JobThread
    {
        private File m_file;
        private ResultsViewer m_resultsViewer;
        private AnalyzeThreadInUse m_analyzeThreadInUse;

        /**
         ****************************************************************************
         *
         * @param threadName
         */
        private AnalyzeThread(File file, AnalyzeThreadInUse analyzeThreadInUse)
        {
            super("Analyze");

            m_file = file;
            m_analyzeThreadInUse = analyzeThreadInUse;
            m_analyzeThreadInUse.setInUse(true);
            m_resultsViewer = ResultsViewer.this;
        }

        /**
         ***************************************************************************
         *
         */
        protected void setup()
        {
            PMDViewer.getViewer().setEnableViewer(false);
            addListener(PMDViewer.getViewer().getJobThreadEventListener());
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
                JobThreadEvent event;

                event = new JobThreadEvent(this, "Analyzing.  Please wait...");
                notifyJobThreadStatus(event);

                loadRuleSets();
                RuleContext ruleContext = new RuleContext();
                ruleContext.setSourceCodeFilename(m_file.getPath());
                ruleContext.setReport(new Report());
                m_pmd.processFile(new FileInputStream(m_file), m_ruleSet, ruleContext);

                event = new JobThreadEvent(this, "Rendering analysis results into HTML page.  Please wait...");
                notifyJobThreadStatus(event);

                HTMLResultRenderer renderer;

                renderer = new HTMLResultRenderer();
                m_htmlText = renderer.render(m_file.getPath(), ruleContext.getReport());

                event = new JobThreadEvent(this, "Storing HTML page into viewer.  Please wait...");
                notifyJobThreadStatus(event);
                setText(m_htmlText);

                m_resultsViewer.scrollToTop();
                event = new JobThreadEvent(this, "Finished");
                notifyJobThreadStatus(event);
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
            removeListener(PMDViewer.getViewer().getJobThreadEventListener());
            PMDViewer.getViewer().setEnableViewer(true);
            m_analyzeThreadInUse.setInUse(false);
        }
    }

    /**
     ***************************************************************************
     ***************************************************************************
     ***************************************************************************
     */
    class AnalyzeThreadInUse
    {
        private boolean m_inUse;

        /**
         ***********************************************************************
         *
         * @param inUse
         */
        protected void setInUse(boolean inUse)
        {
            m_inUse = inUse;
        }

        /**
         ***********************************************************************
         *
         * @return
         */
        protected boolean inUse()
        {
            return m_inUse;
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
            if (m_analyzeThreadInUse.inUse() == false)
            {
                File file = event.getSelectedFile();

                if (file != null)
                {
                    (new AnalyzeThread(file, m_analyzeThreadInUse)).start();
                }
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
