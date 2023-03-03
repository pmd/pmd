/**
 * BSD-style license; for more info see http://pmd.sourceforge.net/license.html
 */

package net.sourceforge.pmd.cli.commands.internal;

import net.sourceforge.pmd.cpd.GUI;

import picocli.CommandLine.Command;

@Command(name = "cpd-gui",
    description = "GUI for the Copy/Paste Detector%n  Warning: May not support the full CPD feature set")
public class CpdGuiCommand implements Runnable {

    @Override
    public void run() {
        new GUI();
    }

}
