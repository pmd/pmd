/**
 * BSD-style license; for more info see http://pmd.sourceforge.net/license.html
*/
package net.sourceforge.pmd;

import net.sourceforge.pmd.renderers.CSVRenderer;
import net.sourceforge.pmd.renderers.EmacsRenderer;
import net.sourceforge.pmd.renderers.HTMLRenderer;
import net.sourceforge.pmd.renderers.IDEAJRenderer;
import net.sourceforge.pmd.renderers.PapariTextRenderer;
import net.sourceforge.pmd.renderers.Renderer;
import net.sourceforge.pmd.renderers.SummaryHTMLRenderer;
import net.sourceforge.pmd.renderers.TextRenderer;
import net.sourceforge.pmd.renderers.VBHTMLRenderer;
import net.sourceforge.pmd.renderers.XMLRenderer;

import java.io.InputStreamReader;

public class CommandLineOptions {

    private boolean debugEnabled;
    private boolean jdk13;
    private boolean shortNamesEnabled;

    private String inputFileName;
    private String reportFormat;
    private String ruleSets;
    private String encoding = new InputStreamReader(System.in).getEncoding();

    private String[] args;

    public CommandLineOptions(String[] args) {

        if (args == null || args.length < 3) {
            throw new RuntimeException(usage());
        }

        inputFileName = args[0];
        reportFormat = args[1];
        ruleSets = args[2];

        this.args = args;

        for (int i=0; i<args.length; i++) {
            if (args[i].equals("-debug")) {
                debugEnabled = true;
            } else if (args[i].equals("-shortnames")) {
                shortNamesEnabled = true;
            } else if (args[i].equals("-encoding")) {
                encoding = args[i+1];
            } else if (args[i].equals("-jdk13")) {
                jdk13 = true;
            }
        }
    }

    public Renderer createRenderer() {
        if (reportFormat.equals("xml")) {
            return new XMLRenderer();
        } else if (reportFormat.equals("ideaj")) {
            return new IDEAJRenderer(args);
        } else if (reportFormat.equals("papari")) {
            return new PapariTextRenderer();
        } else if (reportFormat.equals("text")) {
            return new TextRenderer();
        } else if (reportFormat.equals("emacs")) {
            return new EmacsRenderer();
        } else if (reportFormat.equals("csv")) {
            return new CSVRenderer();
        } else if (reportFormat.equals("html")) {
            return new HTMLRenderer();
        }
        if (reportFormat.equals("summaryhtml")) {
            return new SummaryHTMLRenderer();
        }
        if (reportFormat.equals("vbhtml")) {
            return new VBHTMLRenderer();
        }
        if (!reportFormat.equals("")) {
            try {
                return (Renderer)Class.forName(reportFormat).newInstance();
            } catch (Exception e) {
                throw new IllegalArgumentException("Can't find the custom format " + reportFormat + ": " + e.getClass().getName());
            }
        }

        throw new IllegalArgumentException("Can't create report with format of " + reportFormat);
    }

    public boolean containsCommaSeparatedFileList() {
        return inputFileName.indexOf(',') != -1;
    }

    public String getInputFileName() {
        return this.inputFileName;
    }

    public String getEncoding() {
        return this.encoding;
    }

    public String getReportFormat() {
        return this.reportFormat;
    }

    public String getRulesets() {
        return this.ruleSets;
    }

    public boolean debugEnabled() {
        return debugEnabled;
    }

    public boolean jdk13() {
        return jdk13;
    }

    public boolean shortNamesEnabled() {
        return shortNamesEnabled;
    }

    public String usage() {
        return PMD.EOL + PMD.EOL +
            "Mandatory arguments:" + PMD.EOL +
            "1) A java source code filename or directory" + PMD.EOL +
            "2) A report format " + PMD.EOL +
            "3) A ruleset filename or a comma-delimited string of ruleset filenames" + PMD.EOL +
            PMD.EOL +
            "For example: " + PMD.EOL +
            "c:\\> java -jar pmd-1.8.jar c:\\my\\source\\code html rulesets/unusedcode.xml,rulesets/imports.xml" + PMD.EOL +
            PMD.EOL +
            "Optional arguments that may be put after the mandatory arguments are: " + PMD.EOL +
            "-debug: prints debugging information " + PMD.EOL +
            "-jdk13: enables PMD to parse source code written using 'assert' as an identifier" + PMD.EOL +
            "-encoding: specifies the character set encoding of the source code files PMD is reading (i.e., UTF-8)" + PMD.EOL +
            "-shortnames: prints shortened filenames in the report" + PMD.EOL +
            PMD.EOL +
            "For example: " + PMD.EOL +
            "c:\\> java -jar pmd-1.8.jar c:\\my\\source\\code html rulesets/unusedcode.xml,rulesets/imports.xml -jdk13 -debug" + PMD.EOL +
            "c:\\> java -jar pmd-1.8.jar c:\\my\\source\\code html rulesets/unusedcode.xml,rulesets/imports.xml -encoding UTF-8" + PMD.EOL +
            PMD.EOL;
    }
}


