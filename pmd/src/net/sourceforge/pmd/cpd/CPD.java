/*
* User: tom
* Date: Jul 30, 2002
* Time: 9:53:14 AM
 */
package net.sourceforge.pmd.cpd;

import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.io.StringReader;
import java.util.Iterator;
import java.util.List;

public class CPD {

    private TokenSets tokenSets = new TokenSets();
    private CPDListener listener = new CPDNullListener();
    private Results results;
    private int minimumTileSize;


    public void setListener(CPDListener listener) {
        this.listener = listener;
    }


    public void add(List files) throws IOException {
        for (Iterator i = files.iterator(); i.hasNext();) {
            add(files.size(), (File)i.next());
        }
    }

    public void setMinimumTileSize(int tileSize) {
        minimumTileSize = tileSize;
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

    public void go() {
        if (!listener.update("Starting to process " + tokenSets.size() + " files")) return;
        GST gst = new GST(tokenSets, minimumTileSize);
        results = gst.crunch(listener);
        if (results == null) results = new ResultsImpl();  //just ot make sure we don't pass back a null Results
    }

    public Results getResults() {
        return results;
    }

    public int getLineCountFor(Tile tile) {
        return results.getTileLineCount(tile, tokenSets);
    }

    public String getImage(Tile tile) {
        try {
            TokenEntry firstToken = (TokenEntry)results.getOccurrences(tile).next();
            TokenList tl = tokenSets.getTokenList(firstToken);
            int endLine = firstToken.getBeginLine()+ results.getTileLineCount(tile, tokenSets)-1;
            return tl.getSlice(firstToken.getBeginLine()-1, endLine-1);
        } catch (Exception ex) {
            ex.printStackTrace();
        }
        return "";
    }

    private void addDirectory(String dir, boolean recurse) throws IOException {
        FileFinder finder = new FileFinder();
        List list = finder.findFilesFrom(dir, new JavaFileOrDirectoryFilter(), recurse);
        add(list);
    }

    public void add(String id, String input) throws IOException {
        Tokenizer t = new JavaTokensTokenizer();
        TokenList ts = new TokenList(id);
        t.tokenize(ts, new StringReader(input));
        tokenSets.add(ts);
    }

    private void add(int fileCount, File file) throws IOException {
        listener.addedFile(fileCount, file);
        Tokenizer t = new JavaTokensTokenizer();
        //Tokenizer t = new LinesTokenizer();
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
        CPD cpd = new CPD();
        cpd.setListener(new CPDNullListener());

        try {
            cpd.setMinimumTileSize(Integer.parseInt(args[0]));
        } catch (Exception e) {
            usage();
            System.exit(1);
        }

        try {
            cpd.addRecursively(args[1]);
        } catch (Exception e) {
            e.printStackTrace();
            System.exit(1);
        }

        long start = System.currentTimeMillis();
        cpd.go();
        long total = System.currentTimeMillis() - start;
        System.out.println("That took " + total);
        CPDRenderer renderer = new TextRenderer();
        System.out.println(renderer.render(cpd));
        System.out.println("That took " + total);
    }

    private static void usage() {
        System.out.println("Usage:");
        System.out.println(" java net.sourceforge.pmd.cpd.CPD <tile size> <directory>");
        System.out.println("i.e: ");
        System.out.println(" java net.sourceforge.pmd.cpd.CPD 100 c:\\jdk14\\src\\java");
    }
}