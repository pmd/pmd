import net.jini.space.JavaSpace;
import net.jini.core.lease.Lease;
import net.jini.core.discovery.LookupLocator;
import net.jini.core.lookup.ServiceRegistrar;
import net.jini.core.lookup.ServiceMatches;
import net.jini.core.lookup.ServiceTemplate;
import net.jini.core.entry.Entry;
import net.jini.core.entry.UnusableEntryException;
import net.sourceforge.pmd.cpd.*;

import java.rmi.*;
import java.io.*;
import java.util.List;
import java.util.ArrayList;
import java.util.Iterator;

public class Test {


    public static class JavaFileOrDirectoryFilter implements FilenameFilter {
      public boolean accept(File dir, String filename) {
          return filename.endsWith("java") || (new File(dir.getAbsolutePath() + System.getProperty("file.separator") + filename).isDirectory());
      }
    }

    private List files = new ArrayList();
    private TokenSets tokenSets = new TokenSets();

    public Test() {
        try {
            JavaSpace space = getSpace();
            addRecursively("C:\\j2sdk1.4.0_01\\src\\java\\lang\\ref");
            Entry wrapper = convertTSS();
            System.out.println("token count = " + tokenSets.tokenCount());

            long start = System.currentTimeMillis();
            System.out.println("WRITING");
            space.write(wrapper, null, Lease.FOREVER);
            long stop = System.currentTimeMillis();
            System.out.println("that took " + (stop - start) + " milliseconds");

            start = System.currentTimeMillis();
            System.out.println("TAKING");
            TSSWrapper result = (TSSWrapper)space.take(new TSSWrapper(), null, Long.MAX_VALUE);
            stop = System.currentTimeMillis();
            System.out.println("that took " + (stop - start) + " milliseconds");
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private Entry convertTSS() {
        return new TSSWrapper(tokenSets);
    }
    private  void add(List files) throws IOException {
        for (Iterator i = files.iterator(); i.hasNext();) {
            add(files.size(), (File)i.next());
        }
    }

    private void add(int fileCount, File file) throws IOException {
        Tokenizer t = new JavaTokensTokenizer();
        TokenList ts = new TokenList(file.getAbsolutePath());
        FileReader fr = new FileReader(file);
        t.tokenize(ts, fr);
        fr.close();
        tokenSets.add(ts);
    }

    private void add(File file) throws IOException {
        add(1, file);
    }

    private void addRecursively(String dir) throws IOException {
        addDirectory(dir, true);
    }

    private void addDirectory(String dir, boolean recurse) throws IOException {
        File root = new File(dir);
        List list = new ArrayList();
        scanDirectory(root, list, recurse);
        add(list);
    }

    private void scanDirectory(File dir, List list, boolean recurse) {
     FilenameFilter filter = new JavaFileOrDirectoryFilter();
     String[] possibles = dir.list(filter);
     for (int i=0; i<possibles.length; i++) {
        File tmp = new File(dir + System.getProperty("file.separator") + possibles[i]);
        if (tmp.isDirectory()) {
            if (recurse) {
                scanDirectory(tmp, list, true);
            }
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


    private JavaSpace getSpace() throws Exception {
        ServiceRegistrar registrar = (new LookupLocator("jini://mordor")).getRegistrar();
        ServiceMatches sm = registrar.lookup(new ServiceTemplate(null, new Class[] {JavaSpace.class}, new Entry[] {}),  1);
        return (JavaSpace)sm.items[0].service;
/*
        for (int i=0; i<sm.totalMatches; i++) {
            if (sm.items[i].service instanceof JavaSpace) {
                return (JavaSpace)sm.items[i].service;
            }
        }
*/
        //throw new RuntimeException("Couldn't find a JavaSpace on mordor!");
/*
        JavaSpace space = null;
        try {
          LookupDiscovery lDisc = new LookupDiscovery(groups);
          ServiceDiscoveryManager sdm = new ServiceDiscoveryManager(lDisc, null);
          ServiceItem si = null;

          si = sdm.lookup(new ServiceTemplate(null, serviceTypes, null),
                null,
                MAX_WAIT);

          if (si != null) {
            space = (JavaSpace si.service;
          }

        } catch (Exception e) {
          e.printStackTrace();
        }

        return space;
*/
    }

    public static void main(String[] args) {
        new Test();
    }
}



/*

public Test() {
    try {
        Message msg = new Message();
        msg.content = "Hello World Foo";
        JavaSpace space = getSpace();

        long start = System.currentTimeMillis();
        for (int i=0; i<10; i++) {
            space.write(msg, null, Lease.FOREVER);
            Message result = (Message)space.take(new Message(), null, Long.MAX_VALUE);
        }
        long stop = System.currentTimeMillis();
        System.out.println("that took " + (stop - start) + " milliseconds");
    } catch (Exception e) {
        e.printStackTrace();
    }
}
*/
