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
    private Occurrences occ = new Occurrences();
    private Occurrences results;

    public void add(File file) throws IOException {
        Tokenizer t = new Tokenizer();
        TokenSet ts = new TokenSet(file.getAbsolutePath());
        FileReader fr = new FileReader(file);
        t.tokenize(ts, fr);
        fr.close();
        tokenSets.add(ts);
    }

    public void add(String id, String input) throws IOException {
        Tokenizer t = new Tokenizer();
        TokenSet ts = new TokenSet(id);
        t.tokenize(ts, new StringReader(input));
        tokenSets.add(ts);
    }

    public void go(int minimumTileSize) {
        occ.addInitial(tokenSets);
        GST gst = new GST(this.tokenSets, this.occ, minimumTileSize);
        gst.crunch();
        results = gst.getResults();
    }

    public Occurrences getResults() {
        return results;
    }

    public String toString() {
        return tokenSets.toString();
    }

    public static void main(String[] args) {
        CPD cpd = new CPD();
        try {
/*
            cpd.add("1", "helloworld");
            cpd.add("2", "hellothere");
*/
/*
            cpd.add(new File("c:\\data\\pmd\\pmd\\test-data\\Unused1.java"));
            cpd.add(new File("c:\\data\\pmd\\pmd\\test-data\\Unused2.java"));
            cpd.add(new File("c:\\data\\pmd\\pmd\\test-data\\Unused3.java"));
*/
/*
            List files = findFilesRecursively("c:\\data\\cougaar\\core\\src\\org\\cougaar\\core\\adaptivity");
            files.addAll(findFilesRecursively("c:\\data\\cougaar\\core\\src\\org\\cougaar\\core\\agent"));
            files.addAll(findFilesRecursively("c:\\data\\cougaar\\core\\src\\org\\cougaar\\core\\blackboard"));
*/
            List files = findFilesRecursively("c:\\data\\cougaar\\core\\src\\org\\");
            for (Iterator i = files.iterator(); i.hasNext();) {
                cpd.add((File)i.next());
            }
        } catch (IOException ioe) {
            ioe.printStackTrace();
            return;
        }
        cpd.go(150);
        for (Iterator i = cpd.getResults().getTiles(); i.hasNext();) {
            Tile tile = (Tile)i.next();
            System.out.println(tile.getImage());
            System.out.println("[Source,Location]");
            for (Iterator j = cpd.getResults().getOccurrences(tile); j.hasNext();) {
                System.out.println(j.next());
            }
        }
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

}
