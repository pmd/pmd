package net.sourceforge.pmd.cpd;

import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

public class CPD {

    public static final String EOL = System.getProperty("line.separator", "\n");

    private int minimumTileSize;
    private Map tokenSets = new HashMap();
    private MatchAlgorithm matchAlgorithm = new MatchAlgorithm();
    private CPDListener listener = new CPDNullListener();
    private Tokens tokens = new Tokens();

    public CPD(int minimumTileSize) {
        this.minimumTileSize = minimumTileSize;
    }

    public void setCpdListener(CPDListener cpdListener) {
        this.listener = cpdListener;
    }

    public void go() {
        matchAlgorithm.setListener(listener);
        for (Iterator i = tokens.iterator(); i.hasNext();) {
            matchAlgorithm.add((TokenEntry)i.next());
        }
        matchAlgorithm.findMatches(minimumTileSize);
    }

    public Iterator getMatches() {
        return matchAlgorithm.matches();
    }

    public String getReport() {
        // yikes.  there's a dire need for refactoring here.
        StringBuffer rpt = new StringBuffer();
        for (Iterator i = matchAlgorithm.matches(); i.hasNext();) {
            Match match = (Match)i.next();

            rpt.append("=====================================================================");
            rpt.append(EOL);

            boolean printedHeader = false;
            for (Iterator occurrences = match.iterator(); occurrences.hasNext();) {
                Mark mark = (Mark)occurrences.next();

                SourceCode sourceCode = (SourceCode)tokenSets.get(mark.getFile());
                if (!printedHeader) {
                    rpt.append("Found a " + tokens.getLineCount(mark, match) + " line (" + match.getTokenCount() + " tokens) duplication in the following files: ");
                    rpt.append(EOL);
                    printedHeader = true;
                }

                rpt.append("Starting at line " + mark.getBeginLine() + " of " + mark.getFile());
                rpt.append(EOL);

                if (!occurrences.hasNext()) {
                    rpt.append(sourceCode.getSlice(mark.getBeginLine()-1, mark.getBeginLine() + tokens.getLineCount(mark, match)));
                    rpt.append(EOL);
                }
            }
        }
        return rpt.toString();
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
        tokenSets.put(sourceCode.getFileName(), sourceCode);
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
            System.out.println(cpd.getReport());
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
