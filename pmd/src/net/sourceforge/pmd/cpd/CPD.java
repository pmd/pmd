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
    private Results results;

    public void add(File file) throws IOException {
        Tokenizer t = new Tokenizer();
        TokenList ts = new TokenList(file.getAbsolutePath());
        FileReader fr = new FileReader(file);
        t.tokenize(ts, fr);
        fr.close();
        tokenSets.add(ts);
    }

    public void add(String id, String input) throws IOException {
        Tokenizer t = new Tokenizer();
        TokenList ts = new TokenList(id);
        t.tokenize(ts, new StringReader(input));
        tokenSets.add(ts);
    }

    public void add(List files) throws IOException {
        for (Iterator i = files.iterator(); i.hasNext();) {
            add((File)i.next());
        }
    }

    public void go(int minimumTileSize) {
        GST gst = new GST(this.tokenSets, minimumTileSize);
        results = gst.crunch();
    }

    public Results getResults() {
        return results;
    }

    public String toString() {
        return tokenSets.toString();
    }

    public static void main(String[] args) {
        boolean timing = Boolean.valueOf(args[0]).booleanValue();
        long start = System.currentTimeMillis();


        CPD cpd = new CPD();
        try {
/*
            cpd.add("1", "helloworld");
            cpd.add("2", "hellothere");
*/
            cpd.add(new File("c:\\data\\pmd\\pmd\\test-data\\Unused1.java"));
            cpd.add(new File("c:\\data\\pmd\\pmd\\test-data\\Unused2.java"));
            cpd.add(new File("c:\\data\\pmd\\pmd\\test-data\\Unused3.java"));
            cpd.add(new File("c:\\data\\pmd\\pmd\\test-data\\Unused4.java"));
            cpd.add(new File("c:\\data\\pmd\\pmd\\test-data\\Unused5.java"));
            cpd.add(new File("c:\\data\\pmd\\pmd\\test-data\\Unused6.java"));
            cpd.add(new File("c:\\data\\pmd\\pmd\\test-data\\Unused7.java"));
/*
            List files = findFilesRecursively("c:\\data\\cougaar\\core\\src\\org\\cougaar\\core\\adaptivity");
            files.addAll(findFilesRecursively("c:\\data\\cougaar\\core\\src\\org\\cougaar\\core\\agent"));
            files.addAll(findFilesRecursively("c:\\data\\cougaar\\core\\src\\org\\cougaar\\core\\blackboard"));
*/
            //cpd.add(findFilesRecursively("c:\\data\\cougaar\\core\\src\\org\\"));
        } catch (IOException ioe) {
            ioe.printStackTrace();
            return;
        }
        cpd.go(150);
        long stop = System.currentTimeMillis();
        if (timing) {
            System.out.println("Elapsed time = " + (stop-start) + " ms");
        }
        for (Iterator i = cpd.getResults().getTiles(); i.hasNext();) {
            Tile tile = (Tile)i.next();
            System.out.println(tile.getImage());
            System.out.println("[Source,Location]");
            for (Iterator j = cpd.getResults().getOccurrences(tile); j.hasNext();) {
                System.out.println(j.next());
            }
        }

/*
        try {
        CPD cpd = new CPD();
        cpd.add("1", "helloworld");
        cpd.add("2", "hellothere");
        cpd.add("3", "elloworld");
        cpd.add("4", "ahelloworld");
        cpd.go(4);
        Results results = cpd.getResults();
        for (Iterator i = results.getTiles(); i.hasNext();) {
            Tile tile = (Tile)i.next();
            System.out.println("NEW TILE");

            for (Iterator j = results.getOccurrences(tile); j.hasNext();) {
                j.next();
                System.out.println(tile.getImage());
            }
        }

        } catch (Exception e) {
            e.printStackTrace();
        }
*/
    }

    private static List findFilesRecursively(String dir) {
     File root = new File(dir);
     List list = new ArrayList();
     scanDirectory(root, list, true);
     return list;
   }

   private static void scanDirectory(File dir, List list, boolean recurse) {
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

    private static List getHelloTokens() {
        List tokens = new ArrayList();
        Token tok = new Token('h', 0, "1");
        tokens.add(tok);
        Token tok1 = new Token('e', 1, "1");
        tokens.add(tok1);
        Token tok3 = new Token('l', 2, "1");
        tokens.add(tok3);
        Token tok4 = new Token('l', 3, "1");
        tokens.add(tok4);
        Token tok5 = new Token('o', 4, "1");
        tokens.add(tok5);
        return tokens;
    }

}
