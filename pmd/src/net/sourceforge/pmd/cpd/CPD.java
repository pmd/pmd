/**
 * BSD-style license; for more info see http://pmd.sourceforge.net/license.html
 */
package net.sourceforge.pmd.cpd;

import java.io.File;
import java.io.IOException;
import java.io.FileNotFoundException;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

public class CPD {

    private Map source = new HashMap();
    private CPDListener listener = new CPDNullListener();
    private Tokens tokens = new Tokens();
    private int minimumTileSize;
    private MatchAlgorithm matchAlgorithm;
    private Language language;

    public CPD(int minimumTileSize, Language language) {
        this.minimumTileSize = minimumTileSize;
        this.language = language;
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
        add(finder.findFilesFrom(dir, language.getFileFilter(), recurse));
    }

    private void add(int fileCount, File file) throws IOException {
        listener.addedFile(fileCount, file);
        SourceCode sourceCode = new SourceCode(new SourceCode.FileCodeLoader(file));
        language.getTokenizer().tokenize(sourceCode, tokens);
        source.put(sourceCode.getFileName(), sourceCode);
    }

    public static Renderer getRendererFromString(String name) {
        Renderer renderer = new SimpleRenderer();
        try {
            renderer = (Renderer) Class.forName(name).newInstance();
        } catch (Exception e) {
            System.out.println("Can't find class '" + name + "' so defaulting to SimpleRenderer.");
        }
        return renderer;
    }

    public static void main(String[] args) {
        if (args.length > 4 || args.length < 2) {
            usage();
            System.exit(1);
        }

        try {
            String lang = LanguageFactory.JAVA_KEY;
            Renderer renderer = new SimpleRenderer();
            if (args.length > 2) {
                lang = args[2];
            }
            if (args.length > 3) {
                renderer = CPD.getRendererFromString(args[3]);
            }
            LanguageFactory f = new LanguageFactory();
            Language language = f.createLanguage(lang);
            CPD cpd = new CPD(Integer.parseInt(args[0]), language);
            cpd.addRecursively(args[1]);
            cpd.go();
            System.out.println(renderer.render(cpd.getMatches()));
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private static void usage() {
        System.out.println("Usage:");
        System.out.println(" java net.sourceforge.pmd.cpd.CPD <tile size> <directory> [<language>] [<format>]");
        System.out.println("i.e: ");
        System.out.println(" java net.sourceforge.pmd.cpd.CPD 100 c:\\jdk14\\src\\java ");
        System.out.println("or: ");
        System.out.println(" java net.sourceforge.pmd.cpd.CPD 100 c:\\apache\\src\\ cpp");
        System.out.println("Formats:");
        System.out.println("Simple");
        System.out.println("net.sourceforge.pmd.cpd.CSVRenderer");
        System.out.println("net.sourceforge.pmd.cpd.XMLRenderer");
    }

}
