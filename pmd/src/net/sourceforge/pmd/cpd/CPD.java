/*
 * User: tom
 * Date: Jul 30, 2002
 * Time: 9:53:14 AM
 */
package net.sourceforge.pmd.cpd;

import java.io.*;
import java.util.Iterator;
import java.util.List;
import java.util.ArrayList;

public class CPD {

    public static class JavaFileOrDirectoryFilter implements FilenameFilter {
      public boolean accept(File dir, String filename) {
          return filename.endsWith("java") || (new File(dir.getAbsolutePath() + System.getProperty("file.separator") + filename).isDirectory());
      }
    }
		
    private TokenSets tokenSets = new TokenSets();
    private CPDListener listener = new CPDNullListener();
    private Results results;
    private int minimumTileSize;


    public void setListener(CPDListener listener) {
        this.listener = listener;
    }


    public void add(List files) throws IOException {
        for (Iterator i = files.iterator(); i.hasNext();) {
            add((File)i.next());
        }
    }

    public void add(File file) throws IOException {
		listener.addedFile(file);
        Tokenizer t = new JavaTokensTokenizer();
        TokenList ts = new TokenList(file.getAbsolutePath());
        FileReader fr = new FileReader(file);
        t.tokenize(ts, fr);
        fr.close();
        tokenSets.add(ts);
    }

    public void addAllInDirectory(String dir) throws IOException {
        addDirectory(dir, false);
    }

    public void addRecursively(String dir) throws IOException {
        addDirectory(dir, true);
    }

    private void addDirectory(String dir, boolean recurse) throws IOException {
        File root = new File(dir);
        List list = new ArrayList();
        scanDirectory(root, list, recurse);
        add(list);
    }

    public void setMinimumTileSize(int tileSize) {
        minimumTileSize = tileSize;
    }

    private void scanDirectory(File dir, List list, boolean recurse) {
     FilenameFilter filter = new JavaFileOrDirectoryFilter();
     String[] possibles = dir.list(filter);
     for (int i=0; i<possibles.length; i++) {
        File tmp = new File(dir + System.getProperty("file.separator") + possibles[i]);
        if (recurse && tmp.isDirectory()) {
           scanDirectory(tmp, list, true);
        } else {
           list.add(new File(dir + System.getProperty("file.separator") + possibles[i]));
        }
     }
    }

    public void add(String id, String input) throws IOException {
        Tokenizer t = new JavaTokensTokenizer();
        TokenList ts = new TokenList(id);
        t.tokenize(ts, new StringReader(input));
        tokenSets.add(ts);
    }


    public void go() {
		listener.update("Starting to process " + tokenSets.size() + " files");
        GST gst = new GST(tokenSets, minimumTileSize);
        results = gst.crunch(listener);
    }

    public Results getResults() {
        return results;
    }

    public int getLineCountFor(Tile tile) {
        return results.getTileLineCount(tile, tokenSets);
    }

    public String getImage(Tile tile) {
        try {
            Iterator i = results.getOccurrences(tile);
            TokenEntry firstToken = (TokenEntry)i.next();
            TokenList tl = tokenSets.getTokenList(firstToken);
            return tl.getSlice(firstToken.getBeginLine(), firstToken.getBeginLine()+ results.getTileLineCount(tile, tokenSets));
        } catch (Exception ex) {ex.printStackTrace(); }
        return "";
    }

    public static void main(String[] args) {
        CPD cpd = new CPD();
        //cpd.addListener(new ListenerImpl());
        cpd.setListener(new CPDNullListener());
        try {
/*
            cpd.add("1", "public class Foo {}");
            cpd.add("2", "public class Bar {}");
*/
/*
            cpd.add(new File("c:\\data\\pmd\\pmd\\test-data\\Unused1.java"));
            cpd.add(new File("c:\\data\\pmd\\pmd\\test-data\\Unused2.java"));
            cpd.add(new File("c:\\data\\pmd\\pmd\\test-data\\Unused3.java"));
            cpd.add(new File("c:\\data\\pmd\\pmd\\test-data\\Unused4.java"));
            cpd.add(new File("c:\\data\\pmd\\pmd\\test-data\\Unused5.java"));
            cpd.add(new File("c:\\data\\pmd\\pmd\\test-data\\Unused6.java"));
            cpd.add(new File("c:\\data\\pmd\\pmd\\test-data\\Unused7.java"));
*/
            //cpd.add(findFilesRecursively("c:\\data\\pmd\\pmd-cpd\\src\\net\\sourceforge\\pmd\\cpd"));
            //cpd.add(new File("c:\\data\\cougaar\\core\\src\\org\\cougaar\\core\\adaptivity\\PlayHelper.java"));
            cpd.addRecursively("c:\\data\\cougaar\\core\\src\\org\\cougaar\\core\\adaptivity\\");
            //cpd.add(findFilesRecursively("c:\\data\\cougaar\\core\\src\\org\\"));
        } catch (IOException ioe) {
            ioe.printStackTrace();
            return;
        }
        long start = System.currentTimeMillis();
        cpd.setMinimumTileSize(26);
        cpd.go();
        System.out.println((System.currentTimeMillis() - start));
        Results results = cpd.getResults();
        for (Iterator i = results.getTiles(); i.hasNext();) {
            Tile tile = (Tile)i.next();
            System.out.println("=============================================================");
            System.out.println("A " + cpd.getLineCountFor(tile) + " line (" + tile.getTokenCount() + " tokens) duplication in these files:");
            for (Iterator j = cpd.getResults().getOccurrences(tile); j.hasNext();) {
                TokenEntry tok = (TokenEntry)j.next();
                System.out.println(tok.getBeginLine() + "\t" + tok.getTokenSrcID());
            }
            System.out.println(cpd.getImage(tile));
        }
    }

}
