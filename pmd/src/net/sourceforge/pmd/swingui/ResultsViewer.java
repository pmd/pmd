package net.sourceforge.pmd.swingui;

import java.io.FileInputStream;
import java.io.File;
import java.io.FileNotFoundException;

import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;
import javax.swing.event.ListSelectionEvent;
import javax.swing.event.ListSelectionListener;
import javax.swing.JEditorPane;
import javax.swing.JScrollPane;
import javax.swing.text.html.HTMLEditorKit;

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
class ResultsViewer extends JEditorPane implements ListSelectionListener, ChangeListener
{

    private PMDViewer m_pmdViewer;
    private DirectoryTable m_directoryTable;
    private String m_htmlText;
    private PMD m_pmd;
    private RuleSet m_ruleSet;
    private Report m_report;
    private boolean m_loadRuleSets;
    private AnalyzeThreadInUse m_analyzeThreadInUse;
    private JScrollPane m_parentScrollPane;

    /**
     ********************************************************************************
     */
    protected ResultsViewer(PMDViewer pmdViewer, DirectoryTable directoryTable)
    {
        super();

        setDoubleBuffered(true);
        setEditorKit(new HTMLEditorKit());
        setEditable(false);

        m_pmdViewer = pmdViewer;
        m_directoryTable = directoryTable;
        m_pmd = new PMD();
        m_ruleSet = new RuleSet();
        m_loadRuleSets = true;
        m_analyzeThreadInUse = new AnalyzeThreadInUse();

        m_directoryTable.getSelectionModel().addListSelectionListener(this);
        m_pmdViewer.addRuleSetChangeListener(this);
    }

    /**
     ********************************************************************************
     *
     * @param parentScrollPane
     */
    protected void setParentScrollPane(JScrollPane parentScrollPane)
    {
        m_parentScrollPane = parentScrollPane;
    }

    /**
     ********************************************************************************
     *
     * @param event
     */
    private void scrollToTop()
    {
        m_parentScrollPane.getHorizontalScrollBar().setValue(0);
        m_parentScrollPane.getVerticalScrollBar().setValue(0);
        m_parentScrollPane.repaint();
    }

    /**
     ********************************************************************************
     *
     * @param event
     */
    public void valueChanged(ListSelectionEvent event)
    {
        // Swing may generate a changing event more than once.  All changing events, except
        // the last event, with have the "value is adjusting" flag set true.  We want only
        // the last event.
        if (event.getValueIsAdjusting() == false)
        {
            if (m_analyzeThreadInUse.inUse() == false)
            {
                File file = m_directoryTable.getSelectedFile();

                if (file != null)
                {
                    (new AnalyzeThread(file, m_analyzeThreadInUse)).start();
                }
            }
        }
    }

    /**
     ********************************************************************************
     *
     * @param event
     */
    public void stateChanged(ChangeEvent event)
    {
        m_loadRuleSets = true;
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
            m_ruleSet = m_pmdViewer.getPMDDirectory().getIncludedRules();
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
                File file = m_directoryTable.getSelectedFile();
                String filePath = file.getPath();
                TextRenderer renderer = new TextRenderer();

                return renderer.render(filePath, m_report);
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
            m_pmdViewer.setEnableViewer(false);
            m_directoryTable.getSelectionModel().removeListSelectionListener(m_resultsViewer);
            addListener(m_pmdViewer);
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
                MessageDialog.show(m_pmdViewer, null, exception);
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
            removeListener(m_pmdViewer);
            m_directoryTable.getSelectionModel().addListSelectionListener(m_resultsViewer);
            m_pmdViewer.setEnableViewer(true);
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
         ***************************************************************************
         *
         * @param inUse
         */
        protected void setInUse(boolean inUse)
        {
            m_inUse = inUse;
        }

        /**
         ***************************************************************************
         *
         * @return
         */
        protected boolean inUse()
        {
            return m_inUse;
        }
    }
}