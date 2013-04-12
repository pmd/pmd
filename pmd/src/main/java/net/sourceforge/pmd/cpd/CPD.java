/**
 * BSD-style license; for more info see http://pmd.sourceforge.net/license.html
 */
package net.sourceforge.pmd.cpd;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.TreeMap;

import net.sourceforge.pmd.util.FileFinder;

import org.apache.commons.io.FilenameUtils;

public class CPD {

	private CPDConfiguration configuration;

	private Map<String, SourceCode> source = new TreeMap<String, SourceCode>();
    private CPDListener listener = new CPDNullListener();
    private Tokens tokens = new Tokens();
    private MatchAlgorithm matchAlgorithm;

    public CPD(CPDConfiguration theConfiguration) {
    	configuration = theConfiguration;
        // before we start any tokenizing (add(File...)), we need to reset the static TokenEntry status
        TokenEntry.clearImages();
    }

    public void setCpdListener(CPDListener cpdListener) {
        this.listener = cpdListener;
    }

    public void go() {
        matchAlgorithm = new MatchAlgorithm(source, tokens,configuration.getMinimumTileSize(),listener);
        matchAlgorithm.findMatches();
    }

    public Iterator<Match> getMatches() {
        return matchAlgorithm.matches();
    }

    public void add(File file) throws IOException {
        add(1, file);
    }

    public void addAllInDirectory(String dir) throws IOException {
        addDirectory(dir, false);
    }

    public void addRecursively(String dir) throws IOException {
        addDirectory(dir, true);
    }

    public void add(List<File> files) throws IOException {
        for (File f: files) {
            add(files.size(), f);
        }
    }

    private void addDirectory(String dir, boolean recurse) throws IOException {
        if (!(new File(dir)).exists()) {
            throw new FileNotFoundException("Couldn't find directory " + dir);
        }
        FileFinder finder = new FileFinder();
        // TODO - could use SourceFileSelector here
        add(finder.findFilesFrom(dir, configuration.filenameFilter(), recurse));
    }

    private Set<String> current = new HashSet<String>();

    private void add(int fileCount, File file) throws IOException {

        if (configuration.isSkipDuplicates()) {
            // TODO refactor this thing into a separate class
            String signature = file.getName() + '_' + file.length();
            if (current.contains(signature)) {
                System.err.println("Skipping " + file.getAbsolutePath() + " since it appears to be a duplicate file and --skip-duplicate-files is set");
                return;
            }
            current.add(signature);
        }

        if (!FilenameUtils.equalsNormalizedOnSystem(file.getAbsoluteFile().getCanonicalPath(), file.getAbsolutePath())) {
            System.err.println("Skipping " + file + " since it appears to be a symlink");
            return;
        }

        if (!file.exists()) {
            System.err.println("Skipping " + file + " since it doesn't exist (broken symlink?)");
            return;
        }

        listener.addedFile(fileCount, file);
        SourceCode sourceCode = configuration.sourceCodeFor(file);
        configuration.tokenizer().tokenize(sourceCode, tokens);
        source.put(sourceCode.getFileName(), sourceCode);
    }

	public static void main(String[] args) {
		CPDCommandLineInterface.main(args);
	}
}
