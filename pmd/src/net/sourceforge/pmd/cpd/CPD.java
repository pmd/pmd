package net.sourceforge.pmd.cpd;

import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.util.Iterator;
import java.util.List;

public class CPD {

    public static final String EOL = System.getProperty("line.separator", "\n");

    private int minimumTileSize;
    private TokenSets tokenSets = new TokenSets();
    private MatchAlgorithm matchAlgorithm = new MatchAlgorithm();
    private CPDListener listener = new CPDNullListener();

    public CPD(int minimumTileSize) {
        this.minimumTileSize = minimumTileSize;
    }

    public void setCpdListener(CPDListener cpdListener) {
        this.listener = cpdListener;
    }

    public void go() {
        for (Iterator i = tokenSets.iterator(); i.hasNext();) {
            TokenList tl = (TokenList)i.next();
            for (Iterator j = tl.iterator();j.hasNext();) {
                TokenEntry te = (TokenEntry)j.next();
                matchAlgorithm.add(te, listener);
            }
        }
        matchAlgorithm.findMatches(minimumTileSize);
    }

    public String getReport() {
        StringBuffer rpt = new StringBuffer();
        for (Iterator i = matchAlgorithm.matches(); i.hasNext();) {
            Match match = (Match)i.next();
            TokenList tl = tokenSets.getTokenList(match.getStart().getFile());
            rpt.append("=====================================================================");
            rpt.append(EOL);
            rpt.append("Found a " + match.getTokenCount() + " token duplication in the following files: ");
            rpt.append(EOL);
            rpt.append(match.getStart().getFile());
            rpt.append(EOL);
            rpt.append(match.getEnd().getFile());
            rpt.append(EOL);
            rpt.append(tl.getLineSlice(match.getStart().getIndexIntoFile(), match.getTokenCount()));
            rpt.append(EOL);
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
        Tokenizer t = new JavaTokensTokenizer();
        TokenList ts = new TokenList(file.getAbsolutePath());
        FileReader fr = new FileReader(file);
        t.tokenize(ts, fr);
        fr.close();
        tokenSets.add(ts);
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
