/**
 * BSD-style license; for more info see http://pmd.sourceforge.net/license.html
 */
package net.sourceforge.pmd.cpd;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;

public class CPD {

    private Map source = new HashMap();
    private CPDListener listener = new CPDNullListener();
    private Tokens tokens = new Tokens();
    private int minimumTileSize;
    private MatchAlgorithm matchAlgorithm;
    private Language language;
    private boolean skipDuplicates;

    public CPD(int minimumTileSize, Language language) {
        this.minimumTileSize = minimumTileSize;
        this.language = language;
    }

    public void skipDuplicates() {
        this.skipDuplicates = true;
    }

    public void setCpdListener(CPDListener cpdListener) {
        this.listener = cpdListener;
    }

    public void go() {
        TokenEntry.clearImages();
        matchAlgorithm = new MatchAlgorithm(source, tokens, minimumTileSize, listener);
        matchAlgorithm.findMatches();
    }

    public Iterator getMatches() {
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

    public void add(List files) throws IOException {
        for (Iterator i = files.iterator(); i.hasNext();) {
            add(files.size(), (File) i.next());
        }
    }

    private void addDirectory(String dir, boolean recurse) throws IOException {
        if (!(new File(dir)).exists()) {
            throw new FileNotFoundException("Couldn't find directory " + dir);
        }
        FileFinder finder = new FileFinder();
        // TODO - could use SourceFileSelector here
        add(finder.findFilesFrom(dir, language.getFileFilter(), recurse));
    }

    private Set current = new HashSet();

    private void add(int fileCount, File file) throws IOException {

        if (skipDuplicates) {
            // TODO refactor this thing into a separate class
            String signature = file.getName() + '_' + file.length();
            if (current.contains(signature)) {
                System.out.println("Skipping " + file.getAbsolutePath() + " since it appears to be a duplicate file and --skip-duplicate-files is set");
                return;
            }
            current.add(signature);
        }

        if (!file.getCanonicalPath().equals(file.getAbsolutePath())) {
            System.out.println("Skipping " + file + " since it appears to be a symlink");
            return;
        }

        listener.addedFile(fileCount, file);
        SourceCode sourceCode = new SourceCode(new SourceCode.FileCodeLoader(file));
        language.getTokenizer().tokenize(sourceCode, tokens);
        source.put(sourceCode.getFileName(), sourceCode);
    }

    public static Renderer getRendererFromString(String name) {
        if (name.equalsIgnoreCase("text") || name.equals("")) {
            return new SimpleRenderer();
        }
        try {
            return (Renderer) Class.forName(name).newInstance();
        } catch (Exception e) {
            System.out.println("Can't find class '" + name + "', defaulting to SimpleRenderer.");
        }
        return new SimpleRenderer();
    }

    private static boolean findBooleanSwitch(String[] args, String name) {
        for (int i = 0; i < args.length; i++) {
            if (args[i].equals(name)) {
                return true;
            }
        }
        return false;
    }

    private static String findRequiredStringValue(String[] args, String name) {
        for (int i = 0; i < args.length; i++) {
            if (args[i].equals(name)) {
                return args[i + 1];
            }
        }
        System.out.println("No " + name + " value passed in");
        usage();
        throw new RuntimeException();
    }

    private static String findOptionalStringValue(String[] args, String name, String defaultValue) {
        for (int i = 0; i < args.length; i++) {
            if (args[i].equals(name)) {
                return args[i + 1];
            }
        }
        return defaultValue;
    }

    public static void main(String[] args) {
        if (args.length == 0) {
            usage();
        }

        try {
            boolean skipDuplicateFiles = findBooleanSwitch(args, "--skip-duplicate-files");
            String pathToFiles = findRequiredStringValue(args, "--files");
            String languageString = findOptionalStringValue(args, "--language", "java");
            String formatString = findOptionalStringValue(args, "--format", "text");
            int minimumTokens = Integer.parseInt(findRequiredStringValue(args, "--minimum-tokens"));
            LanguageFactory f = new LanguageFactory();
            Language language = f.createLanguage(languageString);
            Renderer renderer = CPD.getRendererFromString(formatString);
            CPD cpd = new CPD(minimumTokens, language);
            if (skipDuplicateFiles) {
                cpd.skipDuplicates();
            }
            cpd.addRecursively(pathToFiles);
            cpd.go();
            System.out.println(renderer.render(cpd.getMatches()));
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private static void usage() {
        System.out.println("Usage:");
        System.out.println(" java net.sourceforge.pmd.cpd.CPD --minimum-tokens xxx --files xxx [--language xxx] [--format (xml|text|csv)] [--skip-duplicate-files] ");
        System.out.println("i.e: ");
        System.out.println(" java net.sourceforge.pmd.cpd.CPD --minimum-tokens 100 --files c:\\jdk14\\src\\java ");
        System.out.println("or: ");
        System.out.println(" java net.sourceforge.pmd.cpd.CPD --minimum-tokens 100 --files /path/to/c/code --language c ");
        System.out.println("or: ");
        System.out.println(" java net.sourceforge.pmd.cpd.CPD --minimum-tokens 100 --files /path/to/java/code --format xml");
    }

}
