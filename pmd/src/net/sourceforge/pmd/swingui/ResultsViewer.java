package net.sourceforge.pmd.swingui;

import net.sourceforge.pmd.PMD;
import net.sourceforge.pmd.PMDException;
import net.sourceforge.pmd.Report;
import net.sourceforge.pmd.RuleContext;
import net.sourceforge.pmd.RuleSet;
import net.sourceforge.pmd.swingui.event.HTMLAnalysisResultsEvent;
import net.sourceforge.pmd.swingui.event.HTMLAnalysisResultsEventListener;
import net.sourceforge.pmd.swingui.event.ListenerList;
import net.sourceforge.pmd.swingui.event.StatusBarEvent;
import net.sourceforge.pmd.swingui.event.TextAnalysisResultsEvent;
import net.sourceforge.pmd.swingui.event.TextAnalysisResultsEventListener;

import javax.swing.*;
import javax.swing.text.html.HTMLEditorKit;
import java.awt.*;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;

/**
 *
 * @author Donald A. Leckie
 * @since August 27, 2002
 * @version $Revision$, $Date$
 */
abstract class ResultsViewer extends JEditorPane {

    private File[] m_sourceFiles;
    private String m_htmlText;
    private PMD m_pmd;
    private RuleSet m_ruleSet;

    /**
     ********************************************************************************
     */
    protected ResultsViewer() {
        super();

        setEditorKit(new HTMLEditorKit());
        setEditable(false);
        setBackground(UIManager.getColor("pmdGray"));

        m_pmd = new PMD();
        m_ruleSet = new RuleSet();

        //
        // Add listeners
        //
        ListenerList.addListener((HTMLAnalysisResultsEventListener) new HTMLAnalysisResultsEventHandler());
        ListenerList.addListener((TextAnalysisResultsEventListener) new TextAnalysisResultsEventHandler());
    }

    /**
     ********************************************************************************
     *
     */
    private void scrollToTop() {
        Component component = getParent();

        while ((component instanceof JScrollPane) == false) {
            component = component.getParent();
        }

        if (component != null) {
            JScrollPane parentScrollPane = (JScrollPane) component;

            parentScrollPane.getHorizontalScrollBar().setValue(0);
            parentScrollPane.getVerticalScrollBar().setValue(0);
            parentScrollPane.repaint();
        }
    }

    /**
     ********************************************************************************
     *
     * @return
     */
    protected String getHTMLText(File file) {

        return m_htmlText;
    }

    /**
     ********************************************************************************
     *
     * @return
     */
    protected String getHTMLText() {
        return m_htmlText;
    }

    /**
     ********************************************************************************
     *
     * @return
     */
    protected String getPlainText() {
        String fullText = "";

        if (m_sourceFiles != null) {
            synchronized (m_sourceFiles) {
                try {
                    RuleContext ruleContext = new RuleContext();
                    TextRenderer renderer = new TextRenderer();
                    renderer.beginRendering(m_sourceFiles.length == 1);

                    for (int n = 0; n < m_sourceFiles.length; n++) {
                        ruleContext.setSourceCodeFilename(m_sourceFiles[n].getPath());
                        ruleContext.setReport(new Report());
                        m_pmd.processFile(new FileInputStream(m_sourceFiles[n]), m_ruleSet, ruleContext);

                        String filePath = m_sourceFiles[n].getPath();
                        Report report = ruleContext.getReport();
                        renderer.render(filePath, report);
                    }

                    fullText = renderer.endRendering();
                } catch (FileNotFoundException exception) {
                    MessageDialog.show(PMDViewer.getViewer(), null, exception);
                } catch (PMDException pmdException) {
                    String message = pmdException.getMessage();
                    Exception exception = pmdException.getReason();
                    MessageDialog.show(PMDViewer.getViewer(), message, exception);
                }
            }
        }

        return fullText;
    }

    /**
     ********************************************************************************
     *
     */
    protected void analyze() {
        if ((m_sourceFiles != null) && (m_ruleSet != null)) {
            (new AnalyzeThread()).start();
        }
    }

    /**
     ********************************************************************************
     *
     */
    protected void analyze(File[] selectedFile, RuleSet ruleSet) {
        if ((selectedFile != null) && (ruleSet != null)) {
            m_sourceFiles = selectedFile;
            m_ruleSet = ruleSet;
            (new AnalyzeThread()).start();
        }
    }

    /**
     ********************************************************************************
     ********************************************************************************
     ********************************************************************************
     */
    private class AnalyzeThread extends Thread {
        private ResultsViewer m_resultsViewer;


        private AnalyzeThread() {
            super("Analyze");

            m_resultsViewer = ResultsViewer.this;
        }

        /**
         ***************************************************************************
         *
         */
        public void run() {
            setup();
            process();
            cleanup();
        }

        /**
         ***************************************************************************
         *
         */
        protected void setup() {
            PMDViewer.getViewer().setEnableViewer(false);
            StatusBarEvent.notifyStartAnimation(this);
        }

        /**
         ***************************************************************************
         *
         */
        protected void process() {
            if (m_sourceFiles == null) {
                return;
            }

            try {
                StatusBarEvent.notifyShowMessage(this, "Analyzing.  Please wait...");

                RuleContext ruleContext;
                HTMLResultRenderer renderer;
                boolean reportNoViolations;

                ruleContext = new RuleContext();
                renderer = new HTMLResultRenderer();
                reportNoViolations = (m_sourceFiles.length == 1);
                renderer.beginRendering(reportNoViolations);

                for (int n = 0; n < m_sourceFiles.length; n++) {
                    ruleContext.setSourceCodeFilename(m_sourceFiles[n].getPath());
                    ruleContext.setReport(new Report());
                    m_pmd.processFile(new FileInputStream(m_sourceFiles[n]), m_ruleSet, ruleContext);

                    StatusBarEvent.notifyShowMessage(this, "Rendering analysis results into HTML page.  Please wait...");

                    String filePath;
                    Report report;

                    filePath = m_sourceFiles[n].getPath();
                    report = ruleContext.getReport();
                    renderer.render(filePath, report);
                }

                m_htmlText = renderer.endRendering();
                StatusBarEvent.notifyShowMessage(this, "Storing HTML page into viewer.  Please wait...");
                setText(m_htmlText);
            } catch (FileNotFoundException exception) {
                MessageDialog.show(PMDViewer.getViewer(), null, exception);
            } catch (PMDException pmdException) {
                String message = pmdException.getMessage();
                Exception exception = pmdException.getReason();
                MessageDialog.show(PMDViewer.getViewer(), message, exception);
            } catch (OutOfMemoryError error) {
                MessageDialog.show(PMDViewer.getViewer(), "Out of memory.");
            } finally {
                m_resultsViewer.scrollToTop();
                StatusBarEvent.notifyShowMessage(this, "Finished");
            }
        }

        /**
         ***************************************************************************
         *
         */
        protected void cleanup() {
            StatusBarEvent.notifyStopAnimation(this);
            PMDViewer.getViewer().setEnableViewer(true);
        }
    }

    /**
     ***********************************************************************************
     ***********************************************************************************
     ***********************************************************************************
     */
    private class HTMLAnalysisResultsEventHandler implements HTMLAnalysisResultsEventListener {

        /**
         ****************************************************************************
         *
         * @param event
         */
        public void requestHTMLAnalysisResults(HTMLAnalysisResultsEvent event) {
            if (m_htmlText == null) {
                m_htmlText = "";
            }

            HTMLAnalysisResultsEvent.notifyReturnedHTMLText(this, m_htmlText);
        }

        /**
         ****************************************************************************
         *
         * @param event
         */
        public void returnedHTMLAnalysisResults(HTMLAnalysisResultsEvent event) {
        }
    }

    /**
     ***********************************************************************************
     ***********************************************************************************
     ***********************************************************************************
     */
    private class TextAnalysisResultsEventHandler implements TextAnalysisResultsEventListener {

        /**
         ****************************************************************************
         *
         * @param event
         */
        public void requestTextAnalysisResults(TextAnalysisResultsEvent event) {
            TextAnalysisResultsEvent.notifyReturnedText(this, getPlainText());
        }

        /**
         ****************************************************************************
         *
         * @param event
         */
        public void returnedTextAnalysisResults(TextAnalysisResultsEvent event) {
        }
    }
}
