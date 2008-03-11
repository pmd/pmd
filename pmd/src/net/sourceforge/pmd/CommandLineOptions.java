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
import net.sourceforge.pmd.renderers.XSLTRenderer;
import net.sourceforge.pmd.renderers.YAHTMLRenderer;

import java.io.InputStreamReader;
import java.text.MessageFormat;

public class CommandLineOptions {

    private boolean debugEnabled;
    private boolean stressTestEnabled;
    private String targetJDK = "1.5";
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
    private boolean benchmark;
	private String xsltFilename;
	private String auxClasspath;

    private boolean checkJavaFiles = true;
    private boolean checkJspFiles;

    private String[] args;
	
    public CommandLineOptions(String[] args) {

        this.args = args; // needed by createRenderer
        
        if (args == null || args.length < 3) {
            throw new IllegalArgumentException(usage());
        }
        int optIndex = 0;
        if (args[0].charAt(0) == '-') {
            optIndex = args.length - 3;
        }

        inputPath = args[optIndex];
        reportFormat = args[optIndex+1];
        ruleSets = new SimpleRuleSetNameMapper(args[optIndex+2]).getRuleSets();

        for (int i = 0; i < args.length; i++) {
            if (args[i].equals("-debug")) {
                debugEnabled = true;
            } else if (args[i].equals("-stress")) {
                stressTestEnabled = true;
            } else if (args[i].equals("-shortnames")) {
                shortNamesEnabled = true;
            } else if (args[i].equals("-encoding")) {
                encoding = args[++i];
            } else if (args[i].equals("-cpus")) {
                try {
                    cpus = Integer.parseInt(args[++i]);
                } catch (NumberFormatException e) {
                    throw new IllegalArgumentException(MessageFormat.format(
                            "cpus parameter must be a whole number, {0} received", args[i]));
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
                    throw new IllegalArgumentException(MessageFormat.format(
                            "minimumpriority parameter must be a whole number, {0} received", args[i]));
                }
            } else if (args[i].equals("-reportfile")) {
                reportFile = args[++i];
            } else if (args[i].equals("-benchmark")) {
                benchmark = true;
            } else if ( args[i].equals("-xslt") ) {
            	i++;
            	if ( i >= args.length ) {
            		 throw new IllegalArgumentException(usage());
            	}
            	this.xsltFilename = args[i];
            } else if (args[i].equals("-auxclasspath")) {
            	i++;
            	if ( i >= args.length ) {
            		throw new IllegalArgumentException(usage());
	           	}
                this.auxClasspath = args[i];            	
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
        } else if (reportFormat.equals("nicehtml")) {
            return new XSLTRenderer(this.xsltFilename);
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

    public boolean stressTestEnabled() {
        return stressTestEnabled;
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

    public boolean benchmark() {
        return benchmark;
    }

    public String getAuxClasspath() {
    	return auxClasspath;
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
                "-targetjdk: specifies a language version to target - 1.3, 1.4, 1.5, 1.6 or 1.7; default is 1.5" + PMD.EOL +
                "-cpus: specifies the number of threads to create" + PMD.EOL +
                "-encoding: specifies the character set encoding of the source code files PMD is reading (i.e., UTF-8)" + PMD.EOL +
                "-excludemarker: specifies the String that marks the a line which PMD should ignore; default is NOPMD" + PMD.EOL +
                "-shortnames: prints shortened filenames in the report" + PMD.EOL +
                "-linkprefix: path to HTML source, for summary html renderer only" + PMD.EOL +
                "-lineprefix: custom anchor to affected line in the source file, for summary html renderer only" + PMD.EOL +
                "-minimumpriority: rule priority threshold; rules with lower priority than they will not be used" + PMD.EOL +
                "-nojava: do not check Java files; default to check Java files" + PMD.EOL +
                "-jsp: check JSP/JSF files; default to do not check JSP/JSF files" + PMD.EOL +
                "-reportfile: send report output to a file; default to System.out" + PMD.EOL +
                "-benchmark: output a benchmark report upon completion; default to System.err" + PMD.EOL +
                "-xslt: override default xslt for 'nicehtml' output." + PMD.EOL +
                "-auxclasspath: specifies the classpath for libraries used by the source code (used by type resolution)" + PMD.EOL +
                "   (alternatively, a 'file://' URL to a text file containing path elements on consecutive lines)" + PMD.EOL +
                PMD.EOL +
                "For example on windows: " + PMD.EOL +
                "c:\\> java -jar pmd-" + PMD.VERSION + ".jar c:\\my\\source\\code text unusedcode,imports -targetjdk 1.5 -debug" + PMD.EOL +
                "c:\\> java -jar pmd-" + PMD.VERSION + ".jar c:\\my\\source\\code xml basic,design -encoding UTF-8" + PMD.EOL +
                "c:\\> java -jar pmd-" + PMD.VERSION + ".jar c:\\my\\source\\code html typeresolution -auxclasspath commons-collections.jar;derby.jar" + PMD.EOL +                
                "c:\\> java -jar pmd-" + PMD.VERSION + ".jar c:\\my\\source\\code html typeresolution -auxclasspath file:///C:/my/classpathfile" + PMD.EOL +
                PMD.EOL +
                "For example on *nix: " + PMD.EOL +
                "$ java -jar pmd-" + PMD.VERSION + ".jar /home/workspace/src/main/java/code nicehtml basic,design" + PMD.EOL +
                "$ java -jar pmd-" + PMD.VERSION + ".jar /home/workspace/src/main/java/code nicehtml basic,design -xslt my-own.xsl" + PMD.EOL +
                "$ java -jar pmd-" + PMD.VERSION + ".jar /home/workspace/src/main/java/code nicehtml typeresolution -auxclasspath commons-collections.jar:derby.jar" + PMD.EOL +
                PMD.EOL;
    }

    public boolean isCheckJavaFiles() {
        return checkJavaFiles;
    }

    public boolean isCheckJspFiles() {
        return checkJspFiles;
    }
}




