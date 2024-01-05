/*
 * BSD-style license; for more info see http://pmd.sourceforge.net/license.html
 */

package net.sourceforge.pmd.cpd;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Set;

import net.sourceforge.pmd.internal.util.FileFinder;
import net.sourceforge.pmd.internal.util.IOUtil;

/**
 * Adapter for PMD 7. This exposes CPD interface of PMD 6 but runs PMD 7 under the hood.
 */
public class CPD {
    private final CPDConfiguration configuration;
    private final List<Path> files = new ArrayList<>();
    private Set<String> current = new HashSet<>();
    private CPDReport report;

    public CPD(CPDConfiguration configuration) {
        this.configuration = configuration;
    }

    public void addAllInDirectory(File dir) throws IOException {
        addDirectory(dir, false);
    }

    public void addRecursively(File dir) throws IOException {
        addDirectory(dir, true);
    }

    public void add(List<File> files) throws IOException {
        for (File f : files) {
            add(f);
        }
    }

    private void addDirectory(File dir, boolean recurse) throws IOException {
        if (!dir.exists()) {
            throw new FileNotFoundException("Couldn't find directory " + dir);
        }
        FileFinder finder = new FileFinder();
        // TODO - could use SourceFileSelector here
        add(finder.findFilesFrom(dir, configuration.filenameFilter(), recurse));
    }

    public void add(File file) throws IOException {
        if (configuration.isSkipDuplicates()) {
            // TODO refactor this thing into a separate class
            String signature = file.getName() + '_' + file.length();
            if (current.contains(signature)) {
                System.err.println("Skipping " + file.getAbsolutePath()
                        + " since it appears to be a duplicate file and --skip-duplicate-files is set");
                return;
            }
            current.add(signature);
        }

        if (!IOUtil.equalsNormalizedPaths(file.getAbsoluteFile().getCanonicalPath(), file.getAbsolutePath())) {
            System.err.println("Skipping " + file + " since it appears to be a symlink");
            return;
        }

        if (!file.exists()) {
            System.err.println("Skipping " + file + " since it doesn't exist (broken symlink?)");
            return;
        }

        files.add(file.toPath());
    }

    public void go() {
        try (CpdAnalysis cpd = CpdAnalysis.create(configuration)) {
            files.forEach(cpd.files()::addFile);
            cpd.performAnalysis(this::collectReport);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    private void collectReport(CPDReport cpdReport) {
        this.report = cpdReport;
    }

    public Iterator<Match> getMatches() {
        return report.getMatches().iterator();
    }
}
