package net.sourceforge.pmd.cli.commands.internal;

import javax.swing.JOptionPane;

import net.sourceforge.pmd.cli.internal.ExecutionResult;
import net.sourceforge.pmd.util.fxdesigner.Designer;
import net.sourceforge.pmd.util.fxdesigner.DesignerStarter;
import picocli.CommandLine.Command;
import picocli.CommandLine.IVersionProvider;
import picocli.CommandLine.Option;

@Command(name = "designer", showDefaultValues = true,
    versionProvider = DesignerVersionProvider.class,
    description = "The PMD visual rule designer")
public class DesignerCommand extends AbstractPmdSubcommand {

    @Option(names = {"-V", "--version"}, versionHelp = true, description = "Print version information and exit.")
    private boolean versionRequested;

    // TODO : Until a Designer version is released making DesignerStarter.launchGui() public we need to copy these…
    // launchGui should probably be changed to take no arguments, and return an int with the exit status code
    private static final String MISSING_JAVAFX =
            "You seem to be missing the JavaFX runtime." + System.lineSeparator()
                + " Please install JavaFX on your system and try again." + System.lineSeparator()
                + " See https://gluonhq.com/products/javafx/";

    private static final int ERROR_EXIT = 1;
    private static final int OK = 0;

    private static boolean isJavaFxAvailable() {
        try {
            DesignerStarter.class.getClassLoader().loadClass("javafx.application.Application");
            return true;
        } catch (ClassNotFoundException | LinkageError e) {
            return false;
        }
    }

    private static int launchGui() {
        String message = null;
        if (!isJavaFxAvailable()) {
            message = MISSING_JAVAFX;
        }

        if (message != null) {
            System.err.println(message);
            JOptionPane.showMessageDialog(null, message);
            return ERROR_EXIT;
        }

        // TODO : add JavaFX to manually launch app…
        //Application.launch(Designer.class, args);
        return OK;
    }

    @Override
    protected ExecutionResult execute() {
        final int status = launchGui();
        return status == OK ? ExecutionResult.OK : ExecutionResult.ERROR;
    }
}

class DesignerVersionProvider implements IVersionProvider {

    // TODO : Since getCurrentVersion is within Designer, we can't ask for the version without JavaFX or we will face a NoClassDefFoundError
    @Override
    public String[] getVersion() throws Exception {
        return new String[] { "PMD Rule Designer " + Designer.getCurrentVersion() };
    }
    
}