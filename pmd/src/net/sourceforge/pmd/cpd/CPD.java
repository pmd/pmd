package net.sourceforge.pmd.cpd;

import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

public class CPD {

    public static final String EOL = System.getProperty("line.separator", "\n");

    private Map source = new HashMap();
    private CPDListener listener = new CPDNullListener();
    private Tokens tokens = new Tokens();
    private int minimumTileSize;
    private MatchAlgorithm matchAlgorithm;

    public CPD(int minimumTileSize) {
        this.minimumTileSize = minimumTileSize;
    }

    public void setCpdListener(CPDListener cpdListener) {
        this.listener = cpdListener;
    }

    public void go() {
        matchAlgorithm = new MatchAlgorithm(source, tokens);
        matchAlgorithm.setListener(listener);
        matchAlgorithm.findMatches(minimumTileSize);
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
        FileFinder finder = new FileFinder();
        List list = finder.findFilesFrom(dir, new JavaFileOrDirectoryFilter(), recurse);
        add(list);
    }

    private void add(int fileCount, File file) throws IOException {
        listener.addedFile(fileCount, file);
        Tokenizer tokenizer = new JavaTokensTokenizer();
        SourceCode sourceCode = new SourceCode(file.getAbsolutePath());
        FileReader reader = new FileReader(file);
        tokenizer.tokenize(sourceCode, tokens, reader);
        reader.close();
        source.put(sourceCode.getFileName(), sourceCode);
    }

    public static void main(String[] args) {
        if (args.length != 2) {
            usage();
            System.exit(1);
        }

        try {
            CPD cpd = new CPD(Integer.parseInt(args[0]));
            cpd.addRecursively(args[1]);
            long start = System.currentTimeMillis();
            cpd.go();
            long total = System.currentTimeMillis() - start;
            System.out.println(new SimpleRenderer().render(cpd.getMatches()));
            System.out.println("That took " + total + " milliseconds");
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private static void usage() {
        System.out.println("Usage:");
        System.out.println(" java net.sourceforge.pmd.cpd.CPD <tile size> <directory>");
        System.out.println("i.e: ");
        System.out.println(" java net.sourceforge.pmd.cpd.CPD 100 c:\\jdk14\\src\\java");
    }

}
