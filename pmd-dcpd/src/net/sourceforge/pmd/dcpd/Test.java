package net.sourceforge.pmd.dcpd;

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

    private List files = new ArrayList();
    private TokenSets tokenSets = new TokenSets();

    public Test() {
        try {
            JavaSpace space = getSpace();
            add("C:\\j2sdk1.4.0_01\\src\\java\\lang\\ref", true);
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

    private void add(String dir, boolean recurse) throws IOException {
        FileFinder finder = new FileFinder();
        add(finder.findFilesFrom(dir, new JavaFileOrDirectoryFilter(), recurse));
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
    }

    public static void main(String[] args) {
        new Test();
    }
}
