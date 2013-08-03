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
import java.util.Properties;
import java.util.Set;
import java.util.TreeMap;

import net.sourceforge.pmd.util.FileFinder;

import org.apache.commons.io.FilenameUtils;

public class CPD {

    private static final int MISSING_FILES = 1;
	private static final int MISSING_ARGS = 2;
	private static final int DUPLICATE_CODE_FOUND = 4;

	static boolean dontExitForTests = false;

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
        matchAlgorithm = new MatchAlgorithm(
        		source, tokens, 
        		configuration.minimumTileSize(), 
        		listener
        		);
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

        if (configuration.skipDuplicates()) {
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

    private static void setSystemProperties(String[] args, CPDConfiguration config) {
        boolean ignoreLiterals = CPDConfiguration.findBooleanSwitch(args, "--ignore-literals");
        boolean ignoreIdentifiers = CPDConfiguration.findBooleanSwitch(args, "--ignore-identifiers");
        boolean ignoreAnnotations = CPDConfiguration.findBooleanSwitch(args, "--ignore-annotations");
        Properties properties = System.getProperties();
        if (ignoreLiterals) {
            properties.setProperty(JavaTokenizer.IGNORE_LITERALS, "true");
        }
        if (ignoreIdentifiers) {
            properties.setProperty(JavaTokenizer.IGNORE_IDENTIFIERS, "true");
        }
        if (ignoreAnnotations) {
            properties.setProperty(JavaTokenizer.IGNORE_ANNOTATIONS, "true");
        }
        System.setProperties(properties);
        config.language().setProperties(properties);
    }

    public static void main(String[] args) {
        if (args.length == 0) {
        	showUsage();
            System.exit(MISSING_ARGS);
        }

        try {
        	CPDConfiguration config = new CPDConfiguration(args);

            // Pass extra parameters as System properties to allow language
            // implementation to retrieve their associate values...
            setSystemProperties(args, config);
           
            CPD cpd = new CPD(config);
            
            /* FIXME: Improve this !!!	*/
            boolean missingFiles = true;
            for (int position = 0; position < args.length; position++) {
                if (args[position].equals("--files")) {
                	cpd.addRecursively(args[position + 1]);
                	if ( missingFiles ) {
                        missingFiles = false;
                    }
                }
            }

            if ( missingFiles ) {
	            System.out.println("No " + "--files" + " value passed in");
	            showUsage();
	            System.exit(MISSING_FILES);
            }

            cpd.go();
            if (cpd.getMatches().hasNext()) {
                System.out.println(config.renderer().render(cpd.getMatches()));
                if (!dontExitForTests) {
                    System.exit(DUPLICATE_CODE_FOUND);
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public static void showUsage() {
        System.out.println("Usage:");
        System.out.println(" java net.sourceforge.pmd.cpd.CPD --minimum-tokens xxx --files xxx [--language xxx] [--encoding xxx] [--format (xml|text|csv|vs)] [--skip-duplicate-files] ");
        System.out.println("i.e: ");
        System.out.println(" java net.sourceforge.pmd.cpd.CPD --minimum-tokens 100 --files c:\\jdk14\\src\\java ");
        System.out.println("or: ");
        System.out.println(" java net.sourceforge.pmd.cpd.CPD --minimum-tokens 100 --files /path/to/c/code --language c ");
        System.out.println("or: ");
        System.out.println(" java net.sourceforge.pmd.cpd.CPD --minimum-tokens 100 --encoding UTF-16LE --files /path/to/java/code --format xml");
    }

}
