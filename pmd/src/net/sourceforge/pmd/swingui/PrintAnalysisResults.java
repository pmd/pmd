package net.sourceforge.pmd.swingui;

import net.sourceforge.pmd.swingui.event.ListenerList;
import net.sourceforge.pmd.swingui.event.TextAnalysisResultsEvent;
import net.sourceforge.pmd.swingui.event.TextAnalysisResultsEventListener;

import java.awt.Dimension;
import java.awt.Font;
import java.awt.FontMetrics;
import java.awt.Graphics;
import java.awt.PrintJob;
import java.awt.Toolkit;
import java.text.DateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.Iterator;
import java.util.List;
import java.util.StringTokenizer;

/**
 *
 * @author Donald A. Leckie
 * @since January 5, 2003
 * @version $Revision$, $Date$
 */
class PrintAnalysisResults {
    private PrintJob m_printJob;
    private Graphics m_graphics;
    private int m_printAreaX;
    private int m_printAreaY;
    private int m_printAreaWidth;
    private int m_printAreaHeight;
    private String m_analysisText;
    private String m_filePath;
    private String m_printDate;
    private int m_pageNumber;
    private int m_printLineTop;
    private Font m_font;
    private FontMetrics m_fontMetrics;
    private Font m_boldFont;
    private FontMetrics m_boldFontMetrics;
    private Font m_exampleFont;
    private FontMetrics m_exampleFontMetrics;
    private List m_lineTable;
    private final String EMPTY_STRING = "";
    private boolean m_printingExample;

    /**
     ***********************************************************************
     */
    protected PrintAnalysisResults() {
    }

    /**
     ***********************************************************************
     */
    protected void print() {
        TextAnalysisResultsEventListener textListener = null;

        try {
            Toolkit toolkit;
            PMDViewer viewer;
            String jobTitle;

            textListener = new GetAnalysisResults();
            ListenerList.addListener(textListener);
            toolkit = Toolkit.getDefaultToolkit();
            viewer = PMDViewer.getViewer();
            jobTitle = "Print Analysis Results";
            m_printJob = toolkit.getPrintJob(viewer, jobTitle, null);

            if (m_printJob != null) {
                Dimension pageSize;
                int resolution;
                int margin;

                TextAnalysisResultsEvent.notifyRequestText(this);
                pageSize = m_printJob.getPageDimension();
                resolution = m_printJob.getPageResolution();
                margin = resolution / 2;  // 1/2 inch margin
                m_printAreaX = margin;
                m_printAreaY = margin;
                m_printAreaWidth = pageSize.width - margin - margin;
                m_printAreaHeight = pageSize.height - margin - margin;
                m_boldFont = new Font("Serif", Font.BOLD, 9);
                m_boldFontMetrics = PMDViewer.getViewer().getFontMetrics(m_boldFont);
                m_font = new Font("Serif", Font.PLAIN, 9);
                m_fontMetrics = PMDViewer.getViewer().getFontMetrics(m_font);
                m_exampleFont = new Font("Courier", Font.PLAIN, 9);
                m_exampleFontMetrics = PMDViewer.getViewer().getFontMetrics(m_exampleFont);

                Date date;
                DateFormat dateFormat;

                date = new Date();
                dateFormat = DateFormat.getDateTimeInstance();
                m_printDate = dateFormat.format(date);

                buildLineTable();
                printAnalysisResults();
                m_lineTable.clear();
                m_printJob = null;
            }
        } finally {
            ListenerList.removeListener(textListener);
        }
    }

    /**
     **********************************************************************
     *
     */
    private void buildLineTable() {
        StringTokenizer parser;
        PrintLineInfo blankLine;

        parser = new StringTokenizer(m_analysisText, "\n");
        m_lineTable = new ArrayList(1000);
        m_filePath = parser.nextToken();
        blankLine = new PrintLineInfo();
        blankLine.m_label = EMPTY_STRING;
        blankLine.m_data = EMPTY_STRING;
        blankLine.m_labelFont = m_boldFont;
        blankLine.m_labelFontMetrics = m_boldFontMetrics;
        blankLine.m_dataFont = m_font;
        blankLine.m_dataFontMetrics = m_fontMetrics;

        while (parser.hasMoreTokens()) {
            String textLine = parser.nextToken();
            boolean startingExample = false;

            if (textLine.startsWith("Example:")) {
                m_printingExample = true;
                startingExample = true;
            } else if (textLine.startsWith("Line:")) {
                m_printingExample = false;
                m_lineTable.add(blankLine);
                m_lineTable.add(blankLine);
                m_lineTable.add(blankLine);
            }

            buildPrintLineInfo(textLine, startingExample);
        }
    }

    /**
     **********************************************************************
     *
     */
    private void buildPrintLineInfo(String textLine, boolean startingExample) {
        PrintLineInfo printLineInfo;
        int index;

        printLineInfo = new PrintLineInfo();
        printLineInfo.m_label = EMPTY_STRING;
        printLineInfo.m_data = textLine;
        index = textLine.indexOf(':');

        if (index >= 0) {
            index++;
            printLineInfo.m_label = textLine.substring(0, index);
            printLineInfo.m_data = (index < textLine.length()) ? textLine.substring(index) : EMPTY_STRING;
        }

        if (m_printingExample) {
            printLineInfo.m_dataFont = m_exampleFont;
            printLineInfo.m_dataFontMetrics = m_exampleFontMetrics;
            printLineInfo.m_label = "Example: ";
        } else {
            printLineInfo.m_dataFont = m_font;
            printLineInfo.m_dataFontMetrics = m_fontMetrics;

        }

        printLineInfo.m_labelFont = m_boldFont;
        printLineInfo.m_labelFontMetrics = m_boldFontMetrics;
        printLineInfo.m_labelWidth = printLineInfo.m_labelFontMetrics.stringWidth(printLineInfo.m_label);
        printLineInfo.m_dataWidth = printLineInfo.m_dataFontMetrics.stringWidth(printLineInfo.m_data);
        printLineInfo.m_labelX = m_printAreaX;
        printLineInfo.m_dataX = printLineInfo.m_labelX + printLineInfo.m_labelWidth;

        if (m_printingExample && (startingExample == false)) {
            printLineInfo.m_label = EMPTY_STRING;
        }

        int dataAreaRight = printLineInfo.m_dataX + printLineInfo.m_dataWidth;
        int printAreaRight = m_printAreaX + m_printAreaWidth;

        if (dataAreaRight <= printAreaRight) {
            m_lineTable.add(printLineInfo);
        } else {
            buildPrintLineInfoForMultipleLines(printLineInfo);
        }
    }

    /**
     **********************************************************************
     *
     */
    private void buildPrintLineInfoForMultipleLines(PrintLineInfo basePrintLineInfo) {
        char[] data = basePrintLineInfo.m_data.toCharArray();
        int printAreaRight = m_printAreaX + m_printAreaWidth;
        int dataAreaWidth = printAreaRight - basePrintLineInfo.m_dataX;
        StringBuffer buffer = new StringBuffer(500);
        boolean createPrintLineInfo = false;

        for (int n = 0; n < data.length; n++) {
            char theChar = data[n];
            buffer.append(theChar);

            if ((theChar == ' ') || (theChar == '\n')) {
                String textLine = buffer.toString();

                if (m_fontMetrics.stringWidth(buffer.toString()) >= dataAreaWidth) {
                    for (int n1 = buffer.length() - 1; n1 >= 0; n1--) {
                        if (buffer.charAt(n1) == ' ') {
                            textLine = textLine.substring(0, n1);
                            buffer.delete(0, n1 + 1);
                            break;
                        }
                    }

                    createPrintLineInfo = true;
                } else if (theChar == '\n') {
                    buffer.setLength(0);
                    createPrintLineInfo = true;
                }

                if (createPrintLineInfo) {
                    PrintLineInfo printLineInfo;

                    printLineInfo = new PrintLineInfo();
                    printLineInfo.m_label = basePrintLineInfo.m_label;
                    printLineInfo.m_data = textLine;
                    printLineInfo.m_labelX = basePrintLineInfo.m_labelX;
                    printLineInfo.m_labelWidth = basePrintLineInfo.m_labelWidth;
                    printLineInfo.m_dataX = basePrintLineInfo.m_dataX;
                    printLineInfo.m_dataWidth = basePrintLineInfo.m_dataWidth;
                    printLineInfo.m_labelFont = basePrintLineInfo.m_labelFont;
                    printLineInfo.m_labelFontMetrics = basePrintLineInfo.m_labelFontMetrics;
                    printLineInfo.m_dataFont = basePrintLineInfo.m_dataFont;
                    printLineInfo.m_dataFontMetrics = basePrintLineInfo.m_dataFontMetrics;
                    m_lineTable.add(printLineInfo);

                    // Clear the base label so that it isn't repeated.
                    basePrintLineInfo.m_label = EMPTY_STRING;

                    // Setup for next text line
                    createPrintLineInfo = false;
                }
            }
        }

        if (buffer.length() > 0) {
            PrintLineInfo printLineInfo;

            printLineInfo = new PrintLineInfo();
            printLineInfo.m_label = basePrintLineInfo.m_label;
            printLineInfo.m_data = buffer.toString();
            printLineInfo.m_labelX = basePrintLineInfo.m_labelX;
            printLineInfo.m_labelWidth = basePrintLineInfo.m_labelWidth;
            printLineInfo.m_dataX = basePrintLineInfo.m_dataX;
            printLineInfo.m_dataWidth = basePrintLineInfo.m_dataWidth;
            printLineInfo.m_labelFont = basePrintLineInfo.m_labelFont;
            printLineInfo.m_labelFontMetrics = basePrintLineInfo.m_labelFontMetrics;
            printLineInfo.m_dataFont = basePrintLineInfo.m_dataFont;
            printLineInfo.m_dataFontMetrics = basePrintLineInfo.m_dataFontMetrics;
            m_lineTable.add(printLineInfo);
        }
    }

    /**
     **********************************************************************
     *
     */
    private void printAnalysisResults() {
        int lineHeight;
        int printAreaBottom;
        Iterator lineTableIterator;

        lineTableIterator = m_lineTable.iterator();
        m_printLineTop = m_printAreaY + m_printAreaHeight;
        lineHeight = m_fontMetrics.getHeight();
        printAreaBottom = m_printAreaY + m_printAreaHeight;

        while (lineTableIterator.hasNext()) {
            PrintLineInfo printLineInfo = (PrintLineInfo) lineTableIterator.next();

            if ((m_printLineTop + lineHeight) > printAreaBottom) {
                endPage();
                beginPage();
                printHeader();
            }

            printBody(printLineInfo);
        }

        m_printJob.end();
    }

    /**
     **********************************************************************
     *
     */
    private void beginPage() {
        m_graphics = m_printJob.getGraphics();
    }

    /**
     **********************************************************************
     *
     * <file path>        Page 1          <print date>
     */
    private void printHeader() {
        int baseline;

        m_printLineTop = m_printAreaY;
        baseline = m_printLineTop + m_boldFontMetrics.getAscent();
        m_graphics.setFont(m_boldFont);

        //
        // Draw the file path
        //
        int fileNameX = m_printAreaX;
        int fileNameWidth = m_boldFontMetrics.stringWidth(m_filePath);
        m_graphics.drawString(m_filePath, fileNameX, baseline);

        //
        // Draw the page number
        //
        int pageTextWidth;
        int pageTextX;
        String pageText;

        m_pageNumber++;
        pageText = "Page " + m_pageNumber;
        pageTextWidth = m_boldFontMetrics.stringWidth(pageText);
        pageTextX = m_printAreaX + (m_printAreaWidth / 2) - (pageTextWidth / 2);

        if (pageTextX <= (m_printAreaX + fileNameWidth)) {
            pageTextX = m_printAreaX + fileNameWidth + 10;
        }

        m_graphics.drawString(pageText, pageTextX, baseline);

        //
        // Draw the print date
        //
        int printDateWidth;
        int printDateX;

        printDateWidth = m_boldFontMetrics.stringWidth(m_printDate);
        printDateX = m_printAreaX + m_printAreaWidth - printDateWidth;
        m_graphics.drawString(m_printDate, printDateX, baseline);

        //
        // Draw single horizontal line with space above and below.
        //
        int x1;
        int x2;

        m_printLineTop += m_boldFontMetrics.getHeight() + 3;
        x1 = m_printAreaX;
        x2 = m_printAreaX + m_printAreaWidth;
        m_graphics.drawLine(x1, m_printLineTop, x2, m_printLineTop);
        m_printLineTop += 3;
    }

    /**
     **********************************************************************
     *
     * @param printLineInfo
     */
    private void printBody(PrintLineInfo printLineInfo) {
        if ((printLineInfo.m_label.length() > 0) || (printLineInfo.m_data.length() > 0)) {
            int x;
            int y;
            int baseline;

            baseline = m_printLineTop + printLineInfo.m_labelFontMetrics.getAscent();

            // Print label
            if (printLineInfo.m_label.length() > 0) {
                m_graphics.setFont(printLineInfo.m_labelFont);
                x = printLineInfo.m_labelX;
                y = baseline;
                m_graphics.drawString(printLineInfo.m_label, x, y);
            }

            // Print data
            m_graphics.setFont(printLineInfo.m_dataFont);
            x = printLineInfo.m_dataX;
            y = baseline;
            m_graphics.drawString(printLineInfo.m_data, x, y);
        }

        m_printLineTop += printLineInfo.m_dataFontMetrics.getHeight();
    }

    /**
     **********************************************************************
     *
     */
    private void endPage() {
        m_graphics = null;
    }

    /**
     ***********************************************************************
     ***********************************************************************
     ***********************************************************************
     */
    private class GetAnalysisResults implements TextAnalysisResultsEventListener {

        /**
         ****************************************************************************
         *
         * @param event
         */
        public void requestTextAnalysisResults(TextAnalysisResultsEvent event) {
        }

        /**
         ****************************************************************************
         *
         * @param event
         */
        public void returnedTextAnalysisResults(TextAnalysisResultsEvent event) {
            m_analysisText = event.getText();
        }
    }

    /**
     ***********************************************************************
     ***********************************************************************
     ***********************************************************************
     */
    private class PrintLineInfo {
        public String m_label;
        public String m_data;
        public int m_labelWidth;
        public int m_dataWidth;
        public int m_labelX;
        public int m_dataX;
        public Font m_labelFont;
        public FontMetrics m_labelFontMetrics;
        public Font m_dataFont;
        public FontMetrics m_dataFontMetrics;
    }
}