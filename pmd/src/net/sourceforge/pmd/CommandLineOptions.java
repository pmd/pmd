package net.sourceforge.pmd;

public class CommandLineOptions {

    private boolean debugEnabled;
    private boolean shortNamesEnabled;

    private String inputFileName;
    private String reportFormat;
    private String ruleSets;

    public CommandLineOptions(String[] args) {

        if (args == null || args.length < 3) {
            throw new RuntimeException(usage());
        }

        inputFileName = args[0];
        reportFormat = args[1];
        ruleSets = args[2];

        for (int i=0; i<args.length; i++) {
            if (args[i].equals("-debug")) {
                debugEnabled = true;
            }
            if (args[i].equals("-shortnames")) {
                shortNamesEnabled = true;
            }
        }
    }

    public boolean containsCommaSeparatedFileList() {
        return inputFileName.indexOf(',') != -1;
    }

    public String getInputFileName() {
        return this.inputFileName;
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

    public boolean shortNamesEnabled() {
        return shortNamesEnabled;
    }

    private String usage() {
        return PMD.EOL +
            "Please pass in a java source code filename or directory, a report format, " + PMD.EOL +
            "and a ruleset filename or a comma-delimited string of ruleset filenames." + PMD.EOL +
            "For example: " + PMD.EOL +
            "c:\\> java -jar pmd-1.2.1.jar c:\\my\\source\\code html rulesets/unusedcode.xml,rulesets/imports.xml" + PMD.EOL;
    }
}


