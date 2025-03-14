/*
 * BSD-style license; for more info see http://pmd.sourceforge.net/license.html
 */

package net.sourceforge.pmd.cli.commands.internal;

import java.util.ArrayList;
import java.util.List;

import net.sourceforge.pmd.PMDVersion;
import net.sourceforge.pmd.cli.internal.PmdBanner;

import picocli.CommandLine;

class PMDVersionProvider implements CommandLine.IVersionProvider {
    @Override
    public String[] getVersion() throws Exception {
        List<String> lines = new ArrayList<>(PmdBanner.loadBanner());
        lines.add(PMDVersion.getFullVersionName());
        lines.add("Java version: " + System.getProperty("java.version") + ", vendor: " + System.getProperty("java.vendor") + ", runtime: " + System.getProperty("java.home"));
        return lines.toArray(new String[0]);
    }
}
