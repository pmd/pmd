package net.sourceforge.pmd.swingui;

import java.awt.Font;
import java.io.FileInputStream;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import javax.swing.event.ListSelectionEvent;
import javax.swing.event.ListSelectionListener;
import javax.swing.text.html.HTMLEditorKit;
import javax.swing.JEditorPane;
import net.sourceforge.pmd.PMD;
import net.sourceforge.pmd.Report;
import net.sourceforge.pmd.RuleContext;
import net.sourceforge.pmd.RuleSet;
import net.sourceforge.pmd.RuleSetFactory;
import net.sourceforge.pmd.RuleSetNotFoundException;

/**
 *
 * @author Donald A. Leckie
 * @since August 27, 2002
 * @version $Revision$, $Date$
 */
class ResultsViewer extends JEditorPane implements ListSelectionListener
{

    private PMDViewer m_pmdViewer;
    private DirectoryTable m_directoryTable;
    private PMD m_pmd;
    private RuleContext m_ruleContext;
    private RuleSet m_ruleSet;
    private String m_htmlText;

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
        m_ruleContext = new RuleContext();
        RuleSetFactory ruleSetFactory = new RuleSetFactory();
        Iterator ruleSets = null;
        m_ruleSet = new RuleSet();

        try
        {
            ruleSets = ruleSetFactory.getRegisteredRuleSets();
        }
        catch (RuleSetNotFoundException exception)
        {
            String message = "Could not get registered rule sets.";

            MessageDialog.show(m_pmdViewer, message, exception);
        }

        if (ruleSets.hasNext() == false)
        {
            String message = "There are no rule sets.";

            MessageDialog.show(m_pmdViewer, message);
        }

        while (ruleSets.hasNext())
        {
            m_ruleSet.addRuleSet((RuleSet) ruleSets.next());
        }

        directoryTable.getSelectionModel().addListSelectionListener(this);
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
            File file = m_directoryTable.getSelectedSourceFile();

            if (file != null)
            {
                m_ruleContext.setSourceCodeFilename(file.getPath());
                m_ruleContext.setReport(new Report());

                AnalyzeThread analyzeThread = new AnalyzeThread("Analyze", file);

                MessageDialog.show(m_pmdViewer, "Analyzing.  Please wait...", analyzeThread);
            }
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
        File file = m_directoryTable.getSelectedSourceFile();
        String filePath = file.getPath();
        TextRenderer renderer = new TextRenderer();

        return renderer.render(filePath, m_ruleContext.getReport());
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

        /**
         ****************************************************************************
         *
         * @param name
         */
        private AnalyzeThread(String threadName, File file)
        {
            super(threadName);

            m_file = file;
            m_resultsViewer = ResultsViewer.this;
        }

        /**
         ***************************************************************************
         *
         */
        protected void process()
        {
            try
            {
                m_pmd.processFile(new FileInputStream(m_file),
                                  m_resultsViewer.m_ruleSet,
                                  m_resultsViewer.m_ruleContext);

                JobThreadEvent event;

                event = new JobThreadEvent(this, "Rendering analysis results into HTML page.  Please wait...");

                notifyJobThreadStatus(event);

                HTMLResultRenderer renderer;

                renderer = new HTMLResultRenderer();
                m_htmlText = renderer.render(m_file.getPath(), m_ruleContext.getReport());
                event = new JobThreadEvent(this, "Storing HTML page into viewer.  Please wait...");

                notifyJobThreadStatus(event);
                setText(m_htmlText);
            }
            catch (FileNotFoundException exception)
            {
                MessageDialog.show(m_pmdViewer, null, exception);
            }
        }
    }
}