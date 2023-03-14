/**
 * BSD-style license; for more info see http://pmd.sourceforge.net/license.html
 */

package net.sourceforge.pmd.cli;

import java.util.Properties;

import net.sourceforge.pmd.PMD;
import net.sourceforge.pmd.PMD.StatusCode;
import net.sourceforge.pmd.PMDVersion;
import net.sourceforge.pmd.annotation.InternalApi;
import net.sourceforge.pmd.lang.Language;
import net.sourceforge.pmd.lang.LanguageRegistry;
import net.sourceforge.pmd.properties.PropertyDescriptor;
import net.sourceforge.pmd.renderers.Renderer;
import net.sourceforge.pmd.renderers.RendererFactory;

import com.beust.jcommander.JCommander;
import com.beust.jcommander.ParameterException;

/**
 * @author Romain Pelisse &lt;belaran@gmail.com&gt;
 * @deprecated Internal API. Use {@link PMD#runPmd(String...)} or {@link PMD#main(String[])},
 *     or {@link PmdParametersParseResult} if you just want to produce a configuration.
 */
@Deprecated
@InternalApi
public final class PMDCommandLineInterface {

    @Deprecated
    public static final String PROG_NAME = "pmd";

    /**
     * @deprecated This is used for testing, but support for it will be removed in PMD 7.
     * Use {@link PMD#runPmd(String...)} or an overload to avoid exiting the VM. In PMD 7,
     * {@link PMD#main(String[])} will call {@link System#exit(int)} always.
     */
    @Deprecated
    public static final String NO_EXIT_AFTER_RUN = "net.sourceforge.pmd.cli.noExit";

    /**
     * @deprecated This is used for testing, but support for it will be removed in PMD 7.
     * Use {@link PMD#runPmd(String...)} or an overload to avoid exiting the VM. In PMD 7,
     * {@link PMD#main(String[])} will call {@link System#exit(int)} always.
     */
    @Deprecated
    public static final String STATUS_CODE_PROPERTY = "net.sourceforge.pmd.cli.status";

    /**
     * @deprecated Use {@link StatusCode#OK}
     */
    @Deprecated
    public static final int NO_ERRORS_STATUS = 0;
    /**
     * @deprecated Use {@link StatusCode#ERROR}
     */
    @Deprecated
    public static final int ERROR_STATUS = 1;
    /**
     * @deprecated Use {@link StatusCode#VIOLATIONS_FOUND}
     */
    @Deprecated
    public static final int VIOLATIONS_FOUND = 4;

    private PMDCommandLineInterface() { }

    /**
     * Note: this may terminate the VM.
     *
     * @deprecated Use {@link PmdParametersParseResult#extractParameters(String...)}
     */
    @Deprecated
    public static PMDParameters extractParameters(PMDParameters arguments, String[] args, String progName) {
        JCommander jcommander = new JCommander(arguments);
        jcommander.setProgramName(progName);

        try {
            jcommander.parse(args);
            if (arguments.isHelp()) {
                jcommander.usage();
                System.out.println(buildUsageText());
                setStatusCodeOrExit(NO_ERRORS_STATUS);
            }
        } catch (ParameterException e) {
            jcommander.usage();
            System.out.println(buildUsageText());
            System.err.println(e.getMessage());
            setStatusCodeOrExit(ERROR_STATUS);
        }
        return arguments;
    }

    public static String buildUsageText() {
        return "\n"
            + "Mandatory arguments:\n"
            + "1) A java source code filename or directory\n"
            + "2) A report format \n"
            + "3) A ruleset filename or a comma-delimited string of ruleset filenames\n"
            + "\n"
            + "For example: \n"
            + getWindowsLaunchCmd() + " -d c:\\my\\source\\code -f html -R java-unusedcode\n\n"
            + supportedVersions() + "\n"
            + "Available report formats and their configuration properties are:" + "\n" + getReports()
            + "\n" + getExamples() + "\n" + "\n" + "\n";

    }

    @Deprecated
    public static String buildUsageText(JCommander jcommander) {
        return buildUsageText();
    }

    private static String getExamples() {
        return getWindowsExample() + getUnixExample();
    }

    private static String getWindowsLaunchCmd() {
        final String WINDOWS_PROMPT = "C:\\>";
        final String launchCmd = "pmd-bin-" + PMDVersion.VERSION + "\\bin\\pmd.bat";
        return WINDOWS_PROMPT + launchCmd;
    }

    private static String getWindowsExample() {
        final String launchCmd = getWindowsLaunchCmd();
        final String WINDOWS_PATH_TO_CODE = "c:\\my\\source\\code ";

        return "For example on windows: " + "\n"
            + launchCmd + " --dir " + WINDOWS_PATH_TO_CODE
            + "--format text -R rulesets/java/quickstart.xml --use-version java-1.5 --debug" + "\n"
            + launchCmd + " -dir " + WINDOWS_PATH_TO_CODE
            + "-f xml --rulesets rulesets/java/quickstart.xml,category/java/codestyle.xml --encoding UTF-8" + "\n"
            + launchCmd + " --d " + WINDOWS_PATH_TO_CODE
            + "--rulesets rulesets/java/quickstart.xml --aux-classpath lib\\commons-collections.jar;lib\\derby.jar"
            + "\n"
            + launchCmd + " -d " + WINDOWS_PATH_TO_CODE
            + "-f html -R rulesets/java/quickstart.xml --aux-classpath file:///C:/my/classpathfile" + "\n" + "\n";
    }

    private static String getUnixExample() {
        final String launchCmd = "$ pmd-bin-" + PMDVersion.VERSION + "/bin/run.sh pmd";
        return "For example on *nix: " + "\n"
            + launchCmd
            + " --dir /home/workspace/src/main/java/code -f html --rulesets rulesets/java/quickstart.xml,category/java/codestyle.xml"
            + "\n"
            + launchCmd
            + " -d ./src/main/java/code -R rulesets/java/quickstart.xml -f xslt --property xsltFilename=my-own.xsl"
            + "\n"
            + launchCmd
            + " -d ./src/main/java/code -R rulesets/java/quickstart.xml -f xslt --property xsltFilename=html-report-v2.xslt"
            + "\n"
            + " - html-report-v2.xslt is at https://github.com/pmd/pmd/tree/master/pmd-core/etc/xslt/html-report-v2.xslt"
            + launchCmd
            + " -d ./src/main/java/code -f html -R rulesets/java/quickstart.xml --aux-classpath commons-collections.jar:derby.jar"
            + "\n";
    }

    private static String supportedVersions() {
        return "Languages and version supported:" + "\n"
            + LanguageRegistry.PMD.commaSeparatedList(Language::getId)
            + "\n";
    }

    /**
     * For testing purpose only...
     *
     * @param args
     *
     * @deprecated Use {@link PMD#runPmd(String...)}
     */
    @Deprecated
    public static void main(String[] args) {
        System.out.println(PMDCommandLineInterface.buildUsageText());
    }

    public static String jarName() {
        return "pmd-" + PMDVersion.VERSION + ".jar";
    }

    private static String getReports() {
        StringBuilder buf = new StringBuilder();
        for (String reportName : RendererFactory.supportedRenderers()) {
            Renderer renderer = RendererFactory.createRenderer(reportName, new Properties());
            buf.append("   ").append(reportName).append(": ");
            if (!reportName.equals(renderer.getName())) {
                buf.append(" Deprecated alias for '").append(renderer.getName()).append("\n");
                continue;
            }
            buf.append(renderer.getDescription()).append("\n");

            for (PropertyDescriptor<?> property : renderer.getPropertyDescriptors()) {
                buf.append("        ").append(property.name()).append(" - ");
                buf.append(property.description());
                Object deflt = property.defaultValue();
                if (deflt != null) {
                    buf.append("   default: ").append(deflt);
                }
                buf.append("\n");
            }

        }
        return buf.toString();
    }

    public static void setStatusCodeOrExit(int status) {
        if (isExitAfterRunSet()) {
            System.exit(status);
        } else {
            setStatusCode(status);
        }
    }

    private static boolean isExitAfterRunSet() {
        String noExit = System.getenv(NO_EXIT_AFTER_RUN);
        if (noExit == null) {
            noExit = System.getProperty(NO_EXIT_AFTER_RUN);
        }
        return noExit == null;
    }

    private static void setStatusCode(int statusCode) {
        System.setProperty(STATUS_CODE_PROPERTY, Integer.toString(statusCode));
    }

    public static void printJcommanderUsageOnConsole() {
        new JCommander(new PMDParameters()).usage();
    }
}
