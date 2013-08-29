package net.sourceforge.pmd.jdeveloper;

import java.util.Arrays;

import oracle.ide.Ide;
import oracle.ide.log.LogManager;

import oracle.javatools.dialogs.ExceptionDialog;


final class Util {
    private Util() {
    }

    public static void logMessage(final String msg) {
        LogManager.getLogManager().showLog();
        LogManager.getLogManager().getMsgPage().log(msg + "\n");
    }

    public static void logMessage(final StackTraceElement[] stack) {
        LogManager.getLogManager().showLog();
        // TODO Output as Stack
        LogManager.getLogManager().getMsgPage().log(Arrays.toString(stack) + 
                                                    "\n");
    }

    public static void showError(final Exception exc, final String title) {
        //        JOptionPane.showMessageDialog(null, 
        //                                      "Error while running " + title + ": " + 
        //                                      "\n" + exc.getMessage(), title, 
        //                                      JOptionPane.ERROR_MESSAGE);
        ExceptionDialog.showExceptionDialog(Ide.getMainWindow(), exc);
    }

}
