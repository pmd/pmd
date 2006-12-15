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
import net.sourceforge.pmd.renderers.YAHTMLRenderer;

import java.io.InputStreamReader;
import java.text.MessageFormat;

public class CommandLineOptions {

    private boolean debugEnabled;
    private String targetJDK = "1.4";
    private boolean shortNamesEnabled;
    private int cpus = Runtime.getRuntime().availableProcessors();

    private String excludeMarker = PMD.EXCLUDE_MARKER;
    private String inputPath;
    private String reportFormat;
    private String reportFile;
    private String ruleSets;
    private String encoding = new InputStreamReader(System.in).getEncoding();
    private String linePrefix;
    private String linkPrefix;
    private int minPriority = Rule.LOWEST_PRIORITY;


    private boolean checkJavaFiles = true;
    private boolean checkJspFiles = false;

    private String[] args;

    public CommandLineOptions(String[] args) {

        if (args == null || args.length < 3) {
            throw new RuntimeException(usage());
        }
        int optIndex = 0;
		if (args[0].charAt(0) == '-') {
			optIndex = args.length - 3;
		}

        inputPath = args[optIndex];
        reportFormat = args[optIndex+1];
        ruleSets = new SimpleRuleSetNameMapper(args[optIndex+2]).getRuleSets();

        this.args = args;

        for (int i = 0; i < args.length; i++) {
            if (args[i].equals("-debug")) {
                debugEnabled = true;
            } else if (args[i].equals("-shortnames")) {
                shortNamesEnabled = true;
            } else if (args[i].equals("-encoding")) {
                encoding = args[++i];
            } else if (args[i].equals("-cpus")) {
                try {
                    cpus = Integer.parseInt(args[++i]);
                } catch (NumberFormatException e) {
                    throw new RuntimeException(MessageFormat.format(
                            "cpus parameter must be a whole number, {0} received",
                            new String[] { args[i] }));
                }
            } else if (args[i].equals("-targetjdk")) {
                targetJDK = args[++i];
            } else if (args[i].equals("-excludemarker")) {
                excludeMarker = args[++i];
            } else if (args[i].equals("-jsp")) {
                checkJspFiles = true;
            } else if (args[i].equals("-nojava")) {
                checkJavaFiles = false;
            } else if (args[i].equals("-lineprefix")) {
                linePrefix = args[++i];
            } else if (args[i].equals("-linkprefix")) {
                linkPrefix = args[++i];
            } else if (args[i].equals("-minimumpriority")) {
                try {
                    minPriority = Integer.parseInt(args[++i]);
                } catch (NumberFormatException e) {
                    throw new RuntimeException(MessageFormat.format(
                            "minimumpriority parameter must be a whole number, {0} received",
                            new String[] { args[i] }));
                }
            } else if (args[i].equals("-reportfile")) {
                reportFile = args[++i];
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
        } else if (reportFormat.equals("yahtml")) {
            return new YAHTMLRenderer();
        } else if (reportFormat.equals("summaryhtml")) {
            return new SummaryHTMLRenderer(linkPrefix, linePrefix);
        } else if (reportFormat.equals("vbhtml")) {
            return new VBHTMLRenderer();
        }
        if (!reportFormat.equals("")) {
            try {
                return (Renderer) Class.forName(reportFormat).newInstance();
            } catch (Exception e) {
                throw new IllegalArgumentException("Can't find the custom format " + reportFormat + ": " + e.getClass().getName());
            }
        }

        throw new IllegalArgumentException("Can't create report with format of " + reportFormat);
    }

    public boolean containsCommaSeparatedFileList() {
        return inputPath.indexOf(',') != -1;
    }

    public String getInputPath() {
        return this.inputPath;
    }

    public String getEncoding() {
        return this.encoding;
    }

    public String getReportFormat() {
        return this.reportFormat;
    }

    public String getReportFile() {
        return this.reportFile;
    }

    public String getRulesets() {
        return this.ruleSets;
    }

    public String getExcludeMarker() {
        return this.excludeMarker;
    }

    public boolean debugEnabled() {
        return debugEnabled;
    }

    public int getCpus() {
        return cpus;
    }

    public String getTargetJDK() {
        return targetJDK;
    }

    public boolean shortNamesEnabled() {
        return shortNamesEnabled;
    }

    public int getMinPriority() {
        return minPriority;
    }

    public String usage() {
        return PMD.EOL + PMD.EOL +
                "Mandatory arguments:" + PMD.EOL +
                "1) A java source code filename or directory" + PMD.EOL +
                "2) A report format " + PMD.EOL +
                "3) A ruleset filename or a comma-delimited string of ruleset filenames" + PMD.EOL +
                PMD.EOL +
                "For example: " + PMD.EOL +
                "c:\\> java -jar pmd-" + PMD.VERSION + ".jar c:\\my\\source\\code html unusedcode" + PMD.EOL +
                PMD.EOL +
                "Optional arguments that may be put before or after the mandatory arguments: " + PMD.EOL +
                "-debug: prints debugging information" + PMD.EOL +
                "-targetjdk: specifies a language version to target - 1.3, 1.4, 1.5 or 1.6" + PMD.EOL +
                "-cpus: specifies the number of threads to create" + PMD.EOL +
                "-encoding: specifies the character set encoding of the source code files PMD is reading (i.e., UTF-8)" + PMD.EOL +
                "-excludemarker: specifies the String that marks the a line which PMD should ignore; default is NOPMD" + PMD.EOL +
                "-shortnames: prints shortened filenames in the report" + PMD.EOL +
                "-linkprefix: path to HTML source, for summary html renderer only" + PMD.EOL +
                "-lineprefix: custom anchor to affected line in the source file, for summary html renderer only" + PMD.EOL +
                "-minimumpriority: rule priority threshold; rules with lower priority than they will not be used" + PMD.EOL +
                "-reportfile: send report output to a file; default to System.out" + PMD.EOL +
                PMD.EOL +
                "For example: " + PMD.EOL +
                "c:\\> java -jar pmd-" + PMD.VERSION + ".jar c:\\my\\source\\code text unusedcode,imports -targetjdk 1.5 -debug" + PMD.EOL +
                "c:\\> java -jar pmd-" + PMD.VERSION + ".jar c:\\my\\source\\code xml basic,design -encoding UTF-8" + PMD.EOL +
                PMD.EOL;
    }

    /**
     * @return Returns the checkJavaFiles.
     */
    public boolean isCheckJavaFiles() {
        return checkJavaFiles;
    }

    /**
     * @return Returns the checkJspFiles.
     */
    public boolean isCheckJspFiles() {
        return checkJspFiles;
    }
}



         
