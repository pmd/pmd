package net.sourceforge.pmd.swingui.viewer;

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
import javax.swing.JEditorPane;
import net.sourceforge.pmd.PMD;
import net.sourceforge.pmd.renderers.XMLRenderer;
import net.sourceforge.pmd.Report;
import net.sourceforge.pmd.RuleContext;
import net.sourceforge.pmd.RuleSet;
import net.sourceforge.pmd.RuleSetFactory;
import net.sourceforge.pmd.RuleSetNotFoundException;

/**
 *
 * @author Donald A. Leckie
 * @since August 17, 2002
 * @version $Revision$, $Date$
 */
class ResultsEditorPane extends JEditorPane implements ListSelectionListener
{

    private PMDViewer m_pmdViewer;
    private SourceFileList m_sourceFileList;
    private PMD m_pmd;
    private RuleContext m_ruleContext;
    private RuleSet m_ruleSet;

    /**
     ********************************************************************************
     */
    protected ResultsEditorPane(PMDViewer pmdViewer)
    {
        super();

        setFont(new Font("Monospaced", Font.PLAIN, 12));

        RuleSetFactory ruleSetFactory;
        Iterator ruleSets;

        m_pmdViewer = pmdViewer;
        m_pmd = new PMD();
        m_ruleContext = new RuleContext();
        ruleSetFactory = new RuleSetFactory();
        ruleSets = null;
        m_ruleSet = new RuleSet();

        try
        {
            ruleSets = ruleSetFactory.getRegisteredRuleSets();
        }
        catch (RuleSetNotFoundException exception)
        {
            String message = "Could not get registered rule sets.";

            MessageDialog.show(m_pmdViewer, message, exception);
            m_pmdViewer.setVisible(false);
        }

        if (ruleSets.hasNext() == false)
        {
            String message = "There are no rule sets.";

            MessageDialog.show(m_pmdViewer, message);
            m_pmdViewer.setVisible(false);
        }

        while (ruleSets.hasNext())
        {
            m_ruleSet.addRuleSet((RuleSet) ruleSets.next());
        }
    }

    /**
     ********************************************************************************
     *
     * @param sourceFileList
     */
    protected void setSourceFileList(SourceFileList sourceFileList)
    {
        m_sourceFileList = sourceFileList;

        m_sourceFileList.addListSelectionListener(this);
    }

    /**
     ********************************************************************************
     *
     * @param event
     */
    public void valueChanged(ListSelectionEvent event)
    {
        int index = event.getFirstIndex();
        File file = m_sourceFileList.getFile(index);

        // Swing may generate a changing event more than once.  All changing events, except
        // the last event, with have the "value is adjusting" flag set true.  We want only
        // the last event.
        if (event.getValueIsAdjusting() == false)
        {
            return;
        }

        if (file == null)
        {
            return;
        }

        m_ruleContext.setSourceCodeFilename(file.getPath());
        m_ruleContext.setReport(new Report());

        AnalyzeThread analyzeThread = new AnalyzeThread("Analyze", file);

        MessageDialog.show(m_pmdViewer, "Analyzing.  Please wait...", analyzeThread);
/*
        try
        {
            setText("Analyzing.  Please wait...");
            paintImmediately(getBounds());
            m_pmd.processFile(new FileInputStream(file), m_ruleSet, m_ruleContext);
            setText((new TextRenderer()).render(fileName, m_ruleContext.getReport()));
        }
        catch (FileNotFoundException exception)
        {
            MessageDialog.show(m_pmdViewer, null, exception);
        }
*/
    }

    /**
     ********************************************************************************
     ********************************************************************************
     ********************************************************************************
     */
    private class AnalyzeThread extends JobThread
    {
        private File m_file;
        private ResultsEditorPane m_resultsEditorPane;

        /**
         ****************************************************************************
         *
         * @param name
         */
        private AnalyzeThread(String threadName, File file)
        {
            super(threadName);

            m_file = file;
            m_resultsEditorPane = ResultsEditorPane.this;
        }

        /**
         ***************************************************************************
         *
         */
        public void run()
        {
            try
            {
                m_pmd.processFile(new FileInputStream(m_file),
                                  m_resultsEditorPane.m_ruleSet,
                                  m_resultsEditorPane.m_ruleContext);
                setText((new TextRenderer()).render(m_file.getPath(),
                        m_ruleContext.getReport()));
            }
            catch (FileNotFoundException exception)
            {
                MessageDialog.show(m_pmdViewer, null, exception);
            }

            // This is very important; otherwise, the message window would remain open
            // with no way to close it because the "close icon" was made non-functional.
            closeWindow();
        }
    }
}